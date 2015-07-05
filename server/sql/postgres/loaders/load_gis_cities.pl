#!/bin/perl -w
use DBI;
use DBD::Pg qw(:pg_types);




use strict;
my $load_cnt = 0;
my $load_bad = 0;
my $PgDB     = 'postgres';
my $PgUSER     = 'owner_track';
my $argc     = @ARGV;


if ($argc != 1) {
	print "Usage: load_gis_cities.pl gis_data_file.txt\n";
	exit 1;
}
open (LoadData, "<$ARGV[0]") || die "Error open file \"$ARGV[0]\": $!\n";
open (BadFile, ">$ARGV[0]".".bad") || die "Error open file \"$ARGV[0]\": $!\n";
open (LogFile, ">>$ARGV[0]".".log") || die "Error open file \"$ARGV[0]\": $!\n";
print "Load data from file : $ARGV[0] \n";
while (<LoadData>) {
# 918	Волоколамск     Московская область      Центральный федеральный округ   56.045670       35.943951
# Seq	City	Area	Region		Lat	Lon
  if ($_ =~ /^([0-9]+)\t(.+)\t(.+)\t(.+)\t([0-9.]+)\t([0-9.]+)$/i) {
	my $seq   = $1;
	my $city     = $2;
	my $area     = $3;
	my $region     = $4;
	my $lat = $5;
	my $lon  = $6;
  	#&insert_data ($cod, $type, $x, $y, $z, $piket, $info);
  	print "seq=$seq, city=$city, area=$area, region=$region, lat=$lat, lon=$lon\n";
	$load_cnt = $load_cnt + 1;
  } else {
	$load_bad = $load_bad + 1;
	print BadFile "$_";
  }
}
print LogFile "End load data from file : $ARGV[0]\n";
print LogFile "Load strings: \"$load_cnt\"\n";
print LogFile "Bad  strings: \"$load_bad\"\n";
close LoadData;
close LogFile;
close BadFile;

sub insert_data {
#SELECT add_gis_point_3d
# (1003
# ,0
# ,'55.017001089098343'
# ,'82.181601121806565'
# ,'148.851'
# ,4326
# ,'123.34'
# ,'INFO'
# )
  my $conn = DBI->connect("dbi:Pg:dbname=$PgDB", "$PgUSER", "");
  my $SQL_QUERY = "select add_gis_point_3d(get_road_by_code('$_[0]'),$_[1],'$_[2]','$_[3]','$_[4]',4326,$_[5],'$_[6]');";
  my $sth = $conn->prepare($SQL_QUERY);
  $sth->execute();
}
