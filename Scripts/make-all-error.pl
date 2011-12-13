#!/usr/bin/perl

use strict;

(my $src, my $dest) = @ARGV;

if ($src eq "ac") {
    foreach(`ls $src`) {
	if (/(.*)\.$src$/) {
	    print `make $dest/$1.$dest FILE=$1`;
	    if (`cat $dest/$1.$dest`) {
		print ("*** EXPECTED COMPILE TO FAIL ***\n");
		exit(1);
	    }
	}
    }
}
