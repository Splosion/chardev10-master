package org.chardev.cjt.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpellEffect {
	
	private final Connection con;
	private boolean retrievedScaling = false;
	
	protected SpellEffectScaling scaling;
	protected final int id;
	protected final double value;
	protected final double f8;
	
	public SpellEffect( Connection con, int spellId, int index ) {
		
		this.con = con;
		
		try {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM `SpellEffect` WHERE `SpellID` = ? AND `Index` = ?");
			stmt.setInt(1, spellId);
			stmt.setInt(2, index);
			ResultSet result = stmt.executeQuery();
			
			if( ! result.next()) {
				throw new IllegalArgumentException("No spell effect with spell id: "+spellId+" and index: " + index);
			}
			
			this.id = result.getInt("ID");
			this.value = result.getDouble("Value");
			this.f8 = result.getDouble("f8");
			
			stmt.close();
		}
		catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public SpellEffectScaling getScaling() {
		if( ! retrievedScaling ) {
			try {
				PreparedStatement stmt = con.prepareStatement("SELECT * FROM `SpellEffectScaling` WHERE `SpellEffectID` = ? ");
				stmt.setInt(1, id);

				ResultSet result = stmt.executeQuery();
				if( result.next() ) {
					scaling = new SpellEffectScaling(result.getDouble("Coefficient"), result.getDouble("Dice"));
				}
				
				stmt.close();
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
			retrievedScaling = true;
		}
		return scaling;
	}

	public double getValue() {
		return this.value;
	}
	
	public double getF8() {
		return f8;
	}
}
