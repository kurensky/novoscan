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


if ($argc != 1) {
	print "Usage: load_gis_data.pl gis_data_file.txt\n";
	exit 1;
}
open (LoadData, "<$ARGV[0]") || die "Error open file \"$ARGV[0]\": $!\n";
open (BadFile, ">$ARGV[0]".".bad") || die "Error open file \"$ARGV[0]\": $!\n";
open (LogFile, ">>$ARGV[0]".".log") || die "Error open file \"$ARGV[0]\": $!\n";
print "Load data from file : $ARGV[0] \n";
while (<LoadData>) {
# Code_dor,XField,YField,Hfield,Piket,Isso,Info
# 54110010,55.016963072924270,82.181563756812764,148.851,4.861,0,8.Oct.04
  if ($_ =~ /^([0-9]+),([0-9.]+),([0-9.]+),([0-9.]+),([0-9.]+),(\d+),(.+)$/i) {
	my $cod   = $1;
	my $x     = $2;
	my $y     = $3;
	my $z     = $4;
	my $piket = $5;
	my $type  = $6;
	my $info  = $7;
  	&insert_data ($cod, $type, $x, $y, $z, $piket, $info);
  	print "code=$cod, type=$type, x=$x, y=$y, z=$z, p=$piket, i=$info\n";
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
  my $SQL_QUERY = "select add_gis_point_3d(get_road_by_code('$_[0]'),$_[1],'$_[2]','$_[3]','$_[4]',4326,$_[5],'$_[6]');";
  my $sth = $conn->prepare($SQL_QUERY);
  $sth->execute();
}
