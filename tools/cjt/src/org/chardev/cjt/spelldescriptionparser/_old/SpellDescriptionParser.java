package org.chardev.cjt.spelldescriptionparser._old;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import org.chardev.cjt.util.Database;

public class SpellDescriptionParser {
	
	protected Connection db;
	
	public SpellDescriptionParser( Connection db ) {
		this.db = db;
	}
	
	public void parseSpell( int spellId ) throws Throwable {
		PreparedStatement spellStmt = db.prepareStatement(
				"SELECT *, s.`ID` FROM `Spell` s INNER JOIN `SpellMisc` m ON s.`SpellMiscID` = m.`ID` WHERE s.`ID` = ?"
		);
		
		spellStmt.setInt(1, spellId);
		ResultSet result = spellStmt.executeQuery();
		
		if( result.next()) {
			String desc = result.getString("Description");
			if( desc == null || ! desc.contains("$") ) {
				return;
			}
			
			try {
				Description description = new Description(desc);
			}
			catch (Exception e) {
				System.out.println("Error parsing description of spell " + result.getInt("ID") + "\n" + e);
			}
		}
		
		spellStmt.close();
	}
	
	public void parseDatabase() throws Throwable {
		
		ResultSet result = db.createStatement().executeQuery("SELECT `ID` FROM `Spell` LIMIT 0,10000");
		
		while( result.next()) {
			parseSpell(result.getInt(1));
		}
	}
	
	public static void main(String[] args) {
		try {
			new SpellDescriptionParser(Database.connectToDatabase(Database.CHARDEV_MOP)).parseDatabase();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
	}
}
