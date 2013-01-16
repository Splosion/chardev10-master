package org.chardev.cjt.spelldescriptionparser.ast;

public class DecimalNumber extends Expression {
	
	double number;
	
	public DecimalNumber( double number ) {
		this.number = number;
	}
	
	@Override
	public Result evaluate() {
		return new ImmediateResult(number);
	}
}
