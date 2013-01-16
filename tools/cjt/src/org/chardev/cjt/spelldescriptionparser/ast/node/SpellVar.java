package org.chardev.cjt.spelldescriptionparser.ast.node;

import org.chardev.cjt.entity.Spell;
import org.chardev.cjt.entity.SpellEffectScaling;
import org.chardev.cjt.entity.SpellScaling;
import org.chardev.cjt.spelldescriptionparser.Environment;
import org.chardev.cjt.spelldescriptionparser.Environment.NotSetException;
import org.chardev.cjt.spelldescriptionparser.ast.Expression;
import org.chardev.cjt.spelldescriptionparser.ast.Variable;
import org.chardev.cjt.spelldescriptionparser.ast.leaf.Decimal;
import org.chardev.cjt.spelldescriptionparser.ast.leaf.Plain;
import org.chardev.cjt.spelldescriptionparser.ast.leaf.Time;

public class SpellVar implements Variable {
	public final Integer spellId, index;
	public final String abbr;
	
	public SpellVar(Integer spellId, String abbr, Integer index) {
		this.spellId = spellId;
		this.index = index;
		this.abbr = abbr;
	}
	
	@Override
	public Expression evaluate(Environment e) {
		try {
			int codePoint = abbr.codePointAt(0);
			int spellId = this.spellId == null ? e.getSpellContext().getId() : this.spellId;
			
			switch (codePoint) {
			case 'A':
				return new Decimal(e.getEffectRadius(spellId, index));
			case 'a':
				return new Decimal(e.getSpellRadius(spellId, index));
			case 'o':
				return new Decimal(-1); //TODO: implement me
			case 's':
			case 'S':
			case 'm':
			case 'M': {
				try {
					Spell s = e.getSpell(spellId);
					
					SpellScaling sc = s.getScaling(); 
					
					if( sc != null ) {
						
						SpellEffectScaling sec = s.getSpellEffect(index).getScaling();
						if( sec != null && sec.coefficient > 0 ) {
							return new ScalingValue( abbr, sc, sec ); 
						}
					}
					
					return new Decimal(s.getSpellEffect(index).getValue());
				}
				catch (IllegalArgumentException iae) {
					return new Decimal(0);
				}
			}
			case 'D':
			case 'd': {
				Spell s = e.getSpell(spellId);
				SpellScaling sc = s.getScaling(); 
				
				if( sc != null ) {
					if( sc.castTimeStart != sc.castTimeEnd && sc.intervals > 0 ) {
						return new ScalingTime(sc); 
					}
				}
				
				return new Time(e.getDuration(spellId));
			}
			case 'b':
				return new Decimal(e.getProcChance(spellId,index));
			case 'e':
				return new Decimal(e.getProcValue(spellId,index));
			case 'F':
			case 'f':
				return new Decimal(e.getF8(spellId,index));
			case 'H':
			case 'h':
				return new Decimal(e.getProcRate(spellId));
			case 'i':
				return new Decimal(e.getMaximumTargets(spellId));
			case 'n':
				return new Decimal(e.getProcCharges(spellId));
			case 'q':
				return new Decimal(e.getSecondaryEffect(spellId,index));
			case 'R':
				return new Decimal(e.getMaximumHostileRange(spellId));
			case 'r':
				return new Decimal(e.getMinimumHostileRange(spellId));
			case 'U':
			case 'u':
				return new Decimal(e.getStacks(spellId));
			case 'w':
				return new Decimal(e.getValue(spellId, index));
			case 'p':
			case 'T':
			case 't':
				return new Decimal(e.getPeriod(spellId,index));
			case 'v':
				//TODO: Implement SpellTargetRestrictions, f3
				return new Decimal(e.getTargetLevel(spellId));
			case 'x':
				return new Decimal(e.getTargets(spellId,index));
			case 'z':
				return new Plain("$place");
			default:
				throw new RuntimeException("Unhandled abbr:" + new StringBuffer().appendCodePoint(codePoint).toString());
			}
		}
		catch( NotSetException nse ) {
			return this;
		}
	}
	
	@Override
	public String toString() {
		return spellId + abbr + ( index != null ? index : "") ;
	}
}

