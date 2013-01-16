package org.chardev.cjt.entity;

public class SpellScaling {
	public final int castTimeStart, castTimeEnd, intervals, distribution;
	
	public SpellScaling( int castTimeStart, int castTimeEnd, int intervals, int distribution ) {
		this.intervals = intervals;
		this.castTimeEnd = castTimeEnd;
		this.castTimeStart = castTimeStart;
		this.distribution = distribution;
	}
}
