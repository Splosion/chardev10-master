package org.chardev.cjt.spelldescriptionparser.ast;

public class Parenthesis extends Expression {
	
	protected Expression e;
	
	public Parenthesis( Expression e ) {
		this.e = e;
	}
	
	@Override
	public Result evaluate() {
		return e.evaluate();
	}
}
