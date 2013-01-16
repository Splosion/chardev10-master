package org.chardev.cjt.spelldescriptionparser.ast;


public class Operation extends Expression{
	
	protected Expression left, right;
	protected BinaryOperator op;
	
	public Operation( Expression left, BinaryOperator op, Expression right ) {
		this.left = left;
		this.right = right;
		this.op = op;
	}
	
	@Override
	public Result evaluate() {
		Result lr = left.evaluate(), rr = right.evaluate();
		if( lr.isDiffered() || rr.isDiffered() ) {
			return new DifferedResult(this);
		}
		else {
			switch( op ) {
			case PLUS: return new ImmediateResult(lr.toDouble()+rr.toDouble());
			case MINUS: return new ImmediateResult(lr.toDouble()-rr.toDouble());
			case TIMES: return new ImmediateResult(lr.toDouble()*rr.toDouble());
			case DIVIDE: return new ImmediateResult(lr.toDouble()/rr.toDouble());
			default: throw new RuntimeException("Unhandled binary operator: "+op);
			}
		}
	}
	
	public static enum BinaryOperator {
		PLUS, MINUS, TIMES, DIVIDE
	}
}
