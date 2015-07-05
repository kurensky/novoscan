#!/bin/perl -w
use DBI;
use DBD::Pg qw(:pg_types);




use strict;
my $load_cnt = 0;
my $load_bad = 0;
my $PgDB     = 'postgres';
my $PgUSER     = 'owner_track';
my $argc     = @ARGV;
my $conn = DBI->connect("dbi:Pg:dbname=$PgDB", "$PgUSER", "");
my $c_dor;
my $name;
my $desc;
my $stat;
my $beg;
my $end;
my $cod;


if ($argc != 1) {
	print "Usage: load_gis_road.pl gis_road_file.txt\n";
	exit 1;
}
open (LoadData, "<$ARGV[0]") || die "Error open file \"$ARGV[0]\": $!\n";
open (BadFile, ">$ARGV[0]".".bad") || die "Error open file \"$ARGV[0]\": $!\n";
print "Load data from file : $ARGV[0] \n";
while (<LoadData>) {
#3;К-14п1;Подъезд  к с. Крутишка\13 км\;0;0,596;54000141;0
  if ($_ =~ /^([0-9]*);(.*);(.*);([0-9,]*);([0-9,]*);([0-9.]+);([0-9]*);$/i) {
	
	$c_dor = $1;
	$name  = $2;
	$desc  = $3;
	$stat  = $7;
	$beg   = $4;
	$end   = $5;
	$cod   = $6;
        if ($beg) {
	  $beg   =~ s/,/./;
        } else {
	  $beg   = 'NULL';
        };

        if ($end) {
	  $end   =~ s/,/./;
        } else {
	  $end   = 'NULL';
        };
	if (!$stat) {
	  $stat = 'NULL';
	}

  	print STDERR "$c_dor, $name, $desc, $stat, $beg, $end, $cod\n";
  	&insert_road ($c_dor, $name, $desc, $stat, $beg, $end, $cod);
	$load_cnt = $load_cnt + 1;
  } else {
	$load_bad = $load_bad + 1;
	print BadFile "$_";
  }
}
close LoadData;
print "End load data from file : $ARGV[0]\n";
print "Load strings: \"$load_cnt\"\n";
print "Bad  strings: \"$load_bad\"\n";

sub insert_road {
  my $SQL_QUERY = "select add_road('$name','$desc',$stat,$beg,$end,$cod);";
  my $sth = $conn->prepare($SQL_QUERY);
  $sth->execute();
  $SQL_QUERY = "select set_road_code_ctyp($cod,$c_dor);";
  $sth = $conn->prepare($SQL_QUERY);
  $sth->execute();
#  my $sth = $conn->prepare($SQL_QUERY);
#  $sth->execute();
}
