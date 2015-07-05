<?php
error_reporting(E_ALL);
$time_diff  = 7;			# +3 GMT
$basepath   = 'data/';			# sub-directory with LOG collection for every vehicle
$ext        = '.txt';
$param      = parse_params();
$date       = '000000';
$this_year  = 7;			# !
$da_prev    = '';
$handle     = null;			# output file handle
$packet     = '';
$vehicle_id = -1;
$dbconn;
$pgconn = "host=localhost dbname=postgres user=owner_track password=dctvgjrtlf";

if( $param==NULL ) {
    print_hello();			# no params
    exit;
}
$vehicle_id = sprintf( "%08ld",$param['id'] );
$key_len    = $param['len']*2+2;
$data       = $param['data'];

#echo $vehicle_id." ".$key_len." ".$data."<br>\n";
if( wrong_data_length() ) {
    echo "ERR".strlen($data);		# wrong data length
}
else {
    $file_dir   = $basepath.$vehicle_id;
//    echo $file_dir."<br>";
// Open connect to Database
    $dbconn = pg_connect($pgconn) or die('Could not connect: ' . pg_last_error());
    for( $n=0;$n<strlen($data)/$key_len;$n++ )
    {
	get_packet( $n );
	parse_packet( );
	file_open( );
	fwrite( $handle, $packet );
    }
    fclose( $handle );
// Close connect to Database
    pg_close($dbconn);
// Connecting, selecting database
    echo "OK\r\n";
}
exit;

#############################################################################
#############################################################################

function file_open( ) {
   global $handle;
   global $date;
   global $da_prev;
   global $ext;
   global $file_dir;

   if( !$handle ) {
	if( !is_dir($file_dir) ) {
	    mkdir( $file_dir,01777 );
	    umask( 0 );
	    chmod( $file_dir,01777 );
	    clearstatcache( );
	}
	$handle = fopen( $file_dir.'/'.$date.$ext,'a+' );
	if( !$handle ) {
	    echo "fopen";
	    exit;
	}
	$da_prev = $date;
   }
   if( $date!=$da_prev ) {
	fclose( $handle );
	$handle = fopen( $file_dir.'/'.$date.$ext,'a+' );
	if( !$handle ) {
	    echo "fopen";
	    exit;
	}
	$da_prev = $date;
   }
}

function parse_packet( ) {
   global $time_diff;
   global $date;
   global $packet;
   global $this_year;
   global $vehicle_id;
   global $dbconn;
# 0         1         2         3         4         5
# 01234567890123456789012345678901234567890123456789012345678
# --
# 00000241DBF74C72C793CA73B0D618108507004183599C0B014405860078		577
# 0000000AFD864C7CA34DE57AA2399597850400416645EE02008A000E003EC000000007
# 0000004C095A8C6DA34D96CCA239A2AE85040040154D990247B8409100aaaaaaaaaa
# 00000009A1EF4D84A266D002A033B5738D0580401F468D015B0D4205000000000000000000007C
# 0000007100004000000000000000000000000000000000000040000039C3F0000000000000
# --------rec_id  --------lat     --status  ----course    --io
#         ----time        --------long  ----speed     ----hmet
#             ----date              --nsat      --hdop      ----------analog
#                                     --macro_id  ----hgeo
#
   $error      = 0;
   $outputdata = "# ".$packet."\r\n";

   $ri = get_4b(0);				# record id
   $ti = get_2b(8);				# time
   $da = get_2b(12);				# date
	if( $da&0x8000 ) $ti = $ti + 0x10000;
	if( $ti+$time_diff*3600 >= 24*3600 ) { $da++; $ti = $ti + $time_diff*3600 - 24*3600; }
	else $ti += $time_diff*3600;
	$ti   = c_time( $ti );
	$da   = c_date( $da );
	$date = substr($da,4,2).substr($da,2,2).substr($da,0,2);
   $la = get_ll(16);				# latitude
   $lo = get_ll(24);				# longitude
   $st = get_1b(32);				# status
	$fx = $st & 0xC3;			# fix indicator (03h) and valid or valid last (C0h)
	if( !($st&4) ) $la *= -1;		# south
	if( $st&8 )    $lo *= -1;		# west
   $sr = ($st & 0x30) >> 4;			# system run flag
   $su = get_1b(34) & 0x0F;			# satellites used
   $za = get_1b(34) >> 4;			# zone alarm status
   $ma = get_1b(36)&0x7F;			# macro id
   $sp = fractal(get_2b(38));			# speed (SOG)
   $co = fractal(get_2b(42));			# course
   $hd = get_1b(46);				# hdop
   $ge = fractal(get_2b(48));			# hgeo
   $hm = fractal(get_2b(52));			# hmet
   $zs = get_1b(56);				# zone status
   $an = get_5b(58);				# analog

	if( int(substr($da,4,2))<$this_year )	{ $error |= 0x01; }
	if( int(substr($da,4,2))>$this_year+5 )	{ $error |= 0x01; }
	if( (!$la || !$lo) && $fx )		{ $error |= 0x02; }
	if( $su<0 || $su>16 )			{ $error |= 0x04; }
	if( $sp<0 || $sp>100 )			{ $error |= 0x08; }
	if( $co>360 || $co<0 )			{ $error |= 0x10; }
	if( $ge>5000 || $ge<-1000 )		{ $error |= 0x20; }
	if( $hm>1000 )				{ $error |= 0x40; }

   $packet = $outputdata;
   if( $sr )    $packet.= "# \r\n";
   if( $error ) $packet.= "# ERROR".sprintf("%02X",$error).": ";
   $packet.= $ri.",".$sr.",".$da.",".$ti.",".$la.",".$lo.",".$fx.",".$su.",".$sp.",".$zs.",".$za.",".$co.",".$hd.",".$ge.",".$ma.",".$hm.",".$an."!\r\n";
#            0       1       2       3       4       5       6       7       8       9       10      11      12      13      14      15      16-19   20
#            10,     0,      280306, 210142, 55.72   37.54   1,      4,      35.8,   0,      0,      151.8,  2,      138,    0,      14,     0,0,0,0!
#            403,    0,      170306, 095815, 55.793,37.61    1,      14,     8.9,    0,      7,      359.9   22,     1196.9, 0,              76!
#	     787,    0,      180306, 224011, 467415 37.4441  1,      13,     33.7,   0,      6,      115.7,  11,     12071,  0,              7B!
#	     908,    0,      180306, 235657, 157726 37.5674  1,      8,      12.4,   0,      0,      112.2,  11,    -1444.1, 0,              04!
#	     914,    0,      180306, 133056, 55.715 37.5661  1,      8,      24.1,   0,      0,      218.2,  11,    -1444.2, 0,              70!
    $datetime = $da."-".$ti;
    echo $datetime."\n";
    $query = "SELECT
add_data_sensor
($vehicle_id::varchar
,$ri::int8
,to_timestamp('$datetime','DDMMYY-HH24MISS')::timestamp
,$la::float8
,$lo::float8
,$fx::int4
,$su::int4
,$za::int4
,$ma::int4
,0::int4
,$sp::float8
,$co::float8
,$hd::float8
,$ge::float8
,$hm::float8
,NULL::int4
,$an::int8
,NULL::float8
,1::int4 
,'<INFO><REC>11</REC><ID>123</ID></INFO>'::text 
,now()::timestamp
);";
    $result = pg_query($dbconn, $query) or die('Query failed: ' . pg_last_error());
#    echo $result."<br>";
    pg_free_result($result);

}

#############################################################################

function fractal( $val ) {
   $ret = $val&0x3FFF;
   if( $val&0x4000 ) $ret = $ret / 10;
   if( $val&0x8000 ) $ret = $ret * (-1);
   return $ret;
}

function c_time( $val ) {
	$hh  = int( $val/3600 );
	$mm  = int( ($val - $hh*3600)/60 );
	$ss  = $val - $hh*3600 - $mm*60;
	return sprintf( "%02s%02s%02s", $hh, $mm, $ss );
}

# converts word in DDMMYY format
# word is xxYY YYYM MMMD DDDD
function c_date( $val ) {
	$dd  = $val & 0x1F;
	$mm  = ($val>>5) & 0x0F;
	$yy  = ($val>>9) & 0x1F;
	return sprintf( "%02d%02d%02d", $dd, $mm, $yy );
}

#############################################################################
# SOURCE: 0xE0000000
# coverts given coordinate in GGG.DDDDDDDD format
# source format: GGGMMSS.DDDD or GGMMSS.DDDD
# MM*60 + SS.DDDD  -- X
# 3600"            -- 1 grad.
function get_ll( $offset ) {
	global $packet;
	$dotc = substr( $packet,$offset,1 );
    if( $dotc!='A' ) { return NULL; }
	$dot = (hex2int($dotc)>>1 ) - 1;
	$pos = hexs2int(substr($packet,$offset,8),8) & 0x1FFFFFFF;	# GGGMMDDDD
#print "dot=$dot pos=$pos<br>";
	$pos1 = $pos / (exp10($dot+2));
	$gg   = int($pos1);						# GG
	$mm   = ($pos1 - $gg)*5/3;					# MM
	$pos  = $gg + $mm;
#print $gg.'-'.$mm."<br>";
#print $pos."=<br>";
	return $pos;
}

function exp10( $val ) {
	$ret = 1;
	for( $i=0;$i<$val;$i++ ) { $ret *= 10; }
	return $ret;
}

function get_5b( $val ) {
   global $packet;
   global $key_len;
   if( int($key_len)<70 ) return "0000000000";
   $a0 = (hexs2int(substr($packet,$val,3),3)  >>2)&0x03FF;		# xxx0000000
   $a1 = (hexs2int(substr($packet,$val+2,3),3))   &0x03FF;		# 00xxx00000
   $a2 = (hexs2int(substr($packet,$val+5,3),3)>>2)&0x03FF;		# 00000xxx00
   $a3 = (hexs2int(substr($packet,$val+7,3),3))   &0x03FF;		# 0000000xxx
   $a0 = sprintf( "%03ld",$a0 );
   $a1 = sprintf( "%03ld",$a1 );
   $a2 = sprintf( "%03ld",$a2 );
   $a3 = sprintf( "%03ld",$a3 );
   return $a0.$a1.$a2.$a3;
}

function get_4b( $val ) {
   global $packet;
   return hexs2int( substr($packet,$val,8),8 );
}

function get_2b( $val ) {
   global $packet;
   return hexs2int( substr($packet,$val,4),4 );
}

function get_1b( $val ) {
   global $packet;
   $data = substr( $packet,$val,2 );
   return hexs2int( $data,2 );
}

function hexs2int( $val,$len ) {
   $ret = 0;
   $i   = 0;
   while( $i < $len ) {
	$ret = $ret * 16.0 + hex2int( $val{$i} );
	$i++;
   }
   return $ret;
}

function hex2int( $val ) {
   switch( $val ) {
	case '0' : $ret = 0; break;
	case '1' : $ret = 1; break;
	case '2' : $ret = 2; break;
	case '3' : $ret = 3; break;
	case '4' : $ret = 4; break;
	case '5' : $ret = 5; break;
	case '6' : $ret = 6; break;
	case '7' : $ret = 7; break;
	case '8' : $ret = 8; break;
	case '9' : $ret = 9; break;
	case 'a' : ;
	case 'A' : $ret = 10; break;
	case 'b' : ;
	case 'B' : $ret = 11; break;
	case 'c' : ;
	case 'C' : $ret = 12; break;
	case 'd' : ;
	case 'D' : $ret = 13; break;
	case 'e' : ;
	case 'E' : $ret = 14; break;
	case 'f' : ;
	case 'F' : $ret = 15; break;
	default  : $ret = 0;
   }
   return $ret;
}

#############################################################################
#
function int( $str ) {
   $ret = 0;
   for($i=0;$i<strlen($str);$i++) {
	$ch = substr( $str,$i,1 );
      if( $ch=='.' ) break;
	$ret = $ret * 10 + $ch;
   }
   return $ret;
}

#############################################################################
#
function get_packet( $index ) {
   global $data;
   global $key_len;
   global $packet;
   $packet = substr( $data,$index*$key_len,$key_len );
}

#############################################################################
#
function wrong_data_length( ) {
   global $key_len;
   global $data;
   return ($data==null) || (strlen($data)==0) || (strlen($data)%$key_len);
}

#############################################################################
#
function parse_params( ) {
   $param   = NULL;
   $key_num = 0;
   if( strlen($_SERVER["QUERY_STRING"])>0 ) {
	$params = split( "&", $_SERVER["QUERY_STRING"] );
	foreach( $params as $pair ) {
	   $tmp  = split( "=",$pair );
	   if( $tmp[0]=='id' || $tmp[0]=='len' || $tmp[0]=='data' ) $key_num++;
	   $param[$tmp[0]] = $tmp[1];
	}
   }
   if( $key_num!=3 ) return NULL;
   return $param;
}

function print_hello( ) {
   echo <<< END
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1251">
<title>G-Map Welcome</title>
<style type="text/css">
  @import 'styles.css';
</style>
</head>
<body>
<hr>
	message
<hr>
</body>
</html>

END;
}

#############################################################################
#     00000010TTTTDDDDXXXXXXXXYYYYYYYYSSUUSSSSCCCCHHGGGGMMMMIIAAAAAAAAAA
#	~~~~~~~~----====~~~~~~~~========~~==~~~~====~~====~~~~==~~~~~~~~~~
#	0         1	        2         3         4         5         6
#	012345678901234567890123456789012345678901234567890123456789012345
#----------------------------------------------------------------------------
#	u32_t	id;			// serial number of current record
#	u16_t	time;			// seconds, start from midnight
#	u16_t	date;			// xfYY YYYM MMMD DDDD (bits)
#					// |\_ NEW_RECORD_FLAG
#					// \__ HIGHBIT_TIME
#	u32_t	Latitude;
#	u32_t Longitude;
#	u8_t	status;		// bit LONGITUDE_W(x08) LATITUDE_N(x04)
#					//	GPS_FIX_INDICATOR_MASK(x03)
#					//	GPS_VALID(x80) + GPS_VALID_LAST(x40)
#					// 	SYSTEM_START_MASK(x30)
#	u8_t	satellites_used;	// bits 0x0F - satellite number
#					// bits 0xF0 - zone alarm status
#	u16_t	speed;		// speed (SOG, mile/h)
#	u16_t	course;		// cource (degrees)
#	u8_t	hdop;			// 
#	u16_t	hgeo;			// 
#	u16_t	hmet;			// 
#--
#	u8_t	gpio;			// I/O status
#	u8_t	analog[5];		// 5 bytes ( 4 * 10 = 40 bits )
#############################################################################

?>
