package org.chardev.cjt.spelldescriptionparser.ast.node;

import org.chardev.cjt.entity.Spell;
import org.chardev.cjt.spelldescriptionparser.DescriptionParser;
import org.chardev.cjt.spelldescriptionparser.Environment;
import org.chardev.cjt.spelldescriptionparser.Environment.NotSetException;
import org.chardev.cjt.spelldescriptionparser.ParserStream.ParserException;
import org.chardev.cjt.spelldescriptionparser.ast.Expression;
import org.chardev.cjt.spelldescriptionparser.ast.leaf.Plain;

public class TextRef implements Expression {
	
	public final String refName;
	public final int refId;
	
	public TextRef( String refName, int refId ) {
		this.refName = refName;
		this.refId = refId;
	}
	
	@Override
	public Expression evaluate(Environment e) {
		if( this.refName.compareToIgnoreCase("spellname") == 0 ) {
			try {
				return new Plain("|cFFFFFFFF" + e.getSpellName(refId) + "|r");
			}
			catch(NotSetException nse) {
				return this;
			}
		}
		if( this.refName.compareToIgnoreCase("spellicon") == 0 ) {
			try {
				return new Plain(e.getSpellIcon(refId));
			}
			catch(NotSetException nse) {
				return this;
			}
		}
		else if( this.refName.compareToIgnoreCase("spelldesc") == 0 ) {
			Spell context = e.getSpellContext();
			if( refId == context.getId() ) {
				return new Plain("{circular reference}");
			}
			Spell ref = e.getSpell(refId);
			try {
				return new DescriptionParser(ref.getDescription()).parse().evaluate(e.switchContext(ref)).evaluate(e);
			} catch (ParserException ex) {
				throw new RuntimeException(ex);
			}
		}
		else {
			System.err.println("Unhandled ref: "+refName+", ignoring it");
			return new Plain("@"+refName+refName);
		}
	}
}
