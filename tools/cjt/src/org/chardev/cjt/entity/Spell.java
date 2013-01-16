package org.chardev.cjt.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class Spell {
	
	private final Connection con;
	private boolean retrievedScaling = false;
	
	protected final String desc;
	protected final int id, descriptionVariablesId, scalingId;
	protected final Map<Integer,SpellEffect> effects;
	protected final boolean[] effectRetrieved;

	protected SpellScaling scaling;
	
	public Spell( Connection con, int id ) throws SQLException {
		this.con = con;
		
		PreparedStatement spellStmt = con.prepareStatement(
				"SELECT *, s.`ID` FROM `Spell` s LEFT JOIN `SpellMisc` m ON s.`SpellMiscID` = m.`ID` WHERE s.`ID` = ?"
		);
		
		spellStmt.setInt(1, id);
		ResultSet result = spellStmt.executeQuery();
		
		if( ! result.next()) {
			spellStmt.close();
			throw new IllegalArgumentException("No spell with id: " + id);
		}
		
		this.desc = result.getString("Description");
		this.id = result.getInt("ID");
		this.scalingId = result.getInt("SpellScalingID");
		this.descriptionVariablesId = result.getInt("SpellDescriptionVariablesID");
		
		this.effects = new HashMap<Integer, SpellEffect>();
		this.effectRetrieved = new boolean[]{false,false,false};
		
		spellStmt.close();
	}
	
	public int getDescriptionVariablesId() {
		return descriptionVariablesId;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public int getId() {
		return id;
	}

	public SpellScaling getScaling() {
		if( ! retrievedScaling ) {
			
			try {
				PreparedStatement stmt = con.prepareStatement("SELECT * FROM `SpellScaling` WHERE `ID`=?");
				stmt.setInt(1, scalingId);
				
				ResultSet result = stmt.executeQuery();
				if( result.next() ) {
					scaling = new SpellScaling(
							result.getInt("CastTimeStart"),
							result.getInt("CastTimeEnd"),
							result.getInt("Intervals"),
							result.getInt("Distribution")
					);
				}
				
				stmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			retrievedScaling = true;
		}
		return scaling;
	}

	public SpellEffect getSpellEffect(Integer index) {
		if( index == null ) {
			System.err.println("Index was null, falling back to 1 (one based)");
			index = 1;
		}
		
		index -= 1; // so that everywhere one based indices are used
		
		if( ! effects.containsKey(index) ) {
			SpellEffect effect = new SpellEffect( con, id, index );
			
			effects.put(index, effect);
		}
		return effects.get(index);
	}
}
