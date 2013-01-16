package org.chardev.cjt.spelldescriptionparser.ast;

public class CharacterStat extends Expression {
	
	protected String statName;
	
	public CharacterStat( String statName ) {
		this.statName = statName;
	}
	
	@Override
	public Result evaluate() {
		return new DifferedResult(this);
	}
}
