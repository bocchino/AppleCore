#!/usr/bin/perl

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
