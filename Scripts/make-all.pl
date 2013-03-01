#!/usr/bin/perl
#
# Script for AppleCore builds
#
# Usage: make-all src dest
#
# Builds each file src/X.src into dest/X.dest.  For example, if src=ac
# and dest=avm, then this script builds each file ac/FILE.ac into
# avm/FILE.avm.
#
use strict;

(my $src, my $dest) = @ARGV;

foreach(`ls $src`) {
    if (/(.*)\.$src$/) {
	print `make $dest/$1.$dest FILE=$1`;
	if (!(`cat $dest/$1.$dest`)) {
	    print ("*** COMPILE ERROR ***\n");
	    exit(1);
	}
    }
}
