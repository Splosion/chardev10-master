package org.chardev.cjt.spelldescriptionparser.ast;

public class ImmediateResult extends Result {
	
	protected final double result;
	
	public ImmediateResult( final double result ) {
		this.result = result;
	}
	
	@Override
	public boolean isDiffered() {
		return false;
	}
	
	@Override
	public double toDouble() {
		return result;
	}
}
