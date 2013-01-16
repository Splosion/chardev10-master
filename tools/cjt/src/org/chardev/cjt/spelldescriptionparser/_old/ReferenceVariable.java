package org.chardev.cjt.spelldescriptionparser._old;

public class ReferenceVariable implements Variable {
	
	public final int spellId, effectIndex;
	public final String varName;
	
	public ReferenceVariable( int spellId, String varName, int effectIndex ) {
		this.spellId = spellId;
		this.varName = varName;
		this.effectIndex = effectIndex;
	}
}
