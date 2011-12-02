#!/usr/bin/perl

use strict;

die "usage: $0 A B size" if (@ARGV != 3);

(my $A, my $B, my $size) = @ARGV;

my $numBits = 8*$size;
my $mask=(1<<$numBits)-1;

my $result = 0;
my $negateResult = 0;

if ($A < 0) {
    $A = -$A;
    if ($B > 0) {
	$negateResult = 1;
    }
    else {
	$B = -$B;
    }
}
elsif ($B < 0) {
    $B = -$B;
    $negateResult = 1;
}

foreach(1..$numBits) {
#    print "loop $_\n";
    my $bit=$A & 1;
    print "...bit=$bit\n";
    if ($bit) {
	print "...adding $B\n";
	$result+=$B;
	print "...result=$result\n";
    }
    $A=shiftRight($A);
    $B=shiftLeft($B);
#    print "...carry=$carry\n";
}
if ($negateResult) {
    $result = -$result;
}
print "=$result\n";

sub shiftLeft(@) {
    (my $val)=@_;
    return ($val<<1) & $mask;
}

sub shiftRight(@) {
    (my $val)=@_;
    return ($val>>1) & $mask;
}

