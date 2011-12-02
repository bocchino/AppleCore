#!/usr/bin/perl

use strict;

die "usage: $0 A B size" if (@ARGV != 3);

(my $A, my $B, my $size) = @ARGV;

my $numBits = 8*$size;

my $quotient=0;
my $remainder=0;
my $negateQuotient = 0;
my $negateRemainder = 0;

if ($A < 0) {
    $A = -$A;
    $negateRemainder = 1;
    if ($B < 0) {
	$B = -$B;
    }
    else {
	$negateQuotient = 1;
    }
}
else {
    if ($B < 0) {
	$B = -$B;
	$negateQuotient = 1;
    }
}

foreach(1..$numBits) {
#    print "loop $_\n";
    my $carry=($A>>($numBits-1)) & 1;
    $A=shiftLeft($A);
    $quotient=shiftLeft($quotient);
    $remainder=shiftLeft($remainder) | $carry;
    if ($remainder >= $B) {
	$remainder -= $B;
	$quotient |= 1;
    }
#    print "...carry=$carry\n";
#    print "...quotient=$quotient\n";
#    print "...remainder=$remainder\n";
}
if ($negateQuotient) {
    $quotient=-$quotient;
}
if ($negateRemainder) {
    $remainder=-$remainder;
}
print "=$quotient";
if ($remainder != 0) {
    print "r$remainder";
}
print "\n";

sub shiftLeft(@) {
    (my $val)=@_;
    return ($val<<1) & ((1<<$numBits)-1)
}
