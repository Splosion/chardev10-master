package org.chardev.cjt.spelldescriptionparser.ast;

public class DifferedResult extends Result {
	
	private Expression expression;
	
	public DifferedResult( Expression expression ) {
		this.expression = expression;
	}
	
	@Override
	public boolean isDiffered() {
		return true;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	@Override
	public double toDouble() {
		throw new RuntimeException("Unable to convered a DifferedResult to double!");
	}
}
