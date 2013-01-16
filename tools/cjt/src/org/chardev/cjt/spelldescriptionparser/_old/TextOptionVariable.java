package org.chardev.cjt.spelldescriptionparser._old;

public class TextOptionVariable implements Variable {
	public final int spellId;
	public final String varName, option1, option2;
	
	public TextOptionVariable(int spellId, String varName, String option1, String option2) {
		this.spellId = spellId;
		this.varName = varName;
		this.option1 = option1;
		this.option2 = option2;
	}
}
