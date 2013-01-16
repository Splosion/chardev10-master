package org.chardev.cjt.spelldescriptionparser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.chardev.cjt.entity.Spell;
import org.chardev.cjt.entity.SpellEffectScaling;


public class Environment {
	
	protected final Connection db;
	protected final Spell context;
	protected final Map<String, String> variables;

	public Environment(Connection db, Spell context) {
		this.db = db;
		this.context = context;
		
		this.variables = new HashMap<String, String>();
		
		int descId = context.getDescriptionVariablesId();
		if( descId > 0 ) {
			try {
				PreparedStatement stmt = db.prepareStatement("SELECT * FROM `SpellDescriptionVariables` WHERE `ID`=?");
				stmt.setInt(1, descId);
				
				ResultSet result = stmt.executeQuery();
				if( ! result.next() ) {
					System.err.println("Unable to retrieve spell description variables with id: " + descId);
				}
				
				String definitions = result.getString("Definitions");
				String[] defs = definitions.split("\\r\\n");
				
				for( String def : defs ) {
					Matcher m = Pattern.compile("^\\s*\\$(\\w+)\\s*=\\s*(.+)\\s*$").matcher(def);
					if(m.find()) {
						variables.put(m.group(1), m.group(2));
					}
					else {
						throw new RuntimeException("Unable to match definition: "+def);
					}
				}
			}
			catch( SQLException e ) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public Spell getSpellContext() {
		return context;
	}

	public Environment switchContext(Spell ref) {
		return new Environment(db,ref);
	}
	
	public String lookup( String var ) {
		return variables.get(var);
	}

	public static class NotSetException extends Exception {
		private static final long serialVersionUID = -3119205983178029584L;
		public NotSetException( String msg ) {
			super(msg);
		}
	}
	
	protected ResultSet getSpellResult( int spellId ) throws SQLException, NotSetException {
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM `spell` WHERE `ID`=?");
		stmt.setInt(1, spellId);
		
		ResultSet result = stmt.executeQuery();
		if( ! result.next() ) {
			throw new NotSetException("Result is empty: " + stmt.toString());
		}
		return result;
	}
	
	protected ResultSet getSpellIconResult( int spellId ) throws SQLException, NotSetException {
		PreparedStatement stmt = db.prepareStatement("SELECT i.* FROM `spellicon` i INNER JOIN `spellmisc` m ON i.`ID` = m.`SpellIconID` INNER JOIN `spell` s ON m.`ID` = s.`SpellMiscID` WHERE s.`ID`=?");
		stmt.setInt(1, spellId);
		
		ResultSet result = stmt.executeQuery();
		if( ! result.next() ) {
			throw new NotSetException("Result is empty: " + stmt.toString());
		}
		return result;
	}
	
	protected ResultSet getSpellAuraOptions( int spellId ) throws SQLException, NotSetException {
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM `spellauraoptions` o INNER JOIN `spell` s ON s.`SpellAuraOptionsID` = o.`ID` WHERE s.`ID`=?");
		stmt.setInt(1, spellId);
		
		ResultSet result = stmt.executeQuery();
		if( ! result.next() ) {
			throw new NotSetException("Result is empty: " + stmt.toString());
		}
		return result;
	}
	
	protected ResultSet getRangeResult( int spellId ) throws SQLException, NotSetException {
		PreparedStatement stmt = db.prepareStatement("SELECT r.* FROM `spellrange` r INNER JOIN `spellmisc` m ON r.`ID` = m.`SpellRangeID` INNER JOIN `spell` s ON m.`ID` = s.`SpellMiscID` WHERE s.`ID`=?");
		stmt.setInt(1, spellId);
		
		ResultSet result = stmt.executeQuery();
		if( ! result.next() ) {
			throw new NotSetException("Result is empty: " + stmt.toString());
		}
		return result;
	}
	
	protected ResultSet getSpellDurationResult( int spellId ) throws SQLException, NotSetException {
		PreparedStatement stmt = db.prepareStatement("SELECT d.* FROM `spell` s INNER JOIN `SpellMisc` m ON m.`ID` = s.`SpellMiscID` INNER JOIN `spellduration` d ON m.`SpellDurationID` = d.`ID` WHERE s.`ID`=?");
		stmt.setInt(1, spellId);
		
		ResultSet result = stmt.executeQuery();
		if( ! result.next() ) {
			throw new NotSetException("Result is empty: " + stmt.toString());
		}
		return result;
	}
	
	protected ResultSet getSpellEffectResult( int spellId, Integer index ) throws SQLException, NotSetException {
			if( index == null ) {
				System.err.println("Index was null, falling back to 1 (one based)");
				index = 1;
			}
			
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM `spelleffect` WHERE `SpellID`=? AND `Index`=?");
			stmt.setInt(1, spellId);
			stmt.setInt(2, index - 1);
			
			ResultSet result = stmt.executeQuery();
			if( ! result.next() ) {
				throw new NotSetException("Result is empty: " + stmt.toString());
			}
			return result;
	}

	public int getStacks( int spellId ) throws NotSetException {
		try {
			ResultSet result = getSpellAuraOptions(spellId);
			int i = result.getInt("Stacks");
			result.getStatement().close();
			return i;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getProcCharges( int spellId ) throws NotSetException {
		try {
			ResultSet result = getSpellAuraOptions(spellId);
			int i = result.getInt("ProcCharges");
			result.getStatement().close();
			return i;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public double getProcRate( int spellId ) throws NotSetException {
		try {
			ResultSet result = getSpellAuraOptions(spellId);
			double d = result.getInt("ProcRate");
			result.getStatement().close();
			return d;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getProcValue( int spellId, Integer index ) throws NotSetException {
		try {
			ResultSet result = getSpellEffectResult(spellId, index);
			int i = result.getInt("ProcValue");
			result.getStatement().close();
			return i;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getValue( int spellId, Integer index ) throws NotSetException {
		try {
			ResultSet result = getSpellEffectResult(spellId, index);
			int i = result.getInt("Value");
			result.getStatement().close();
			return i;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getSecondaryEffect( int spellId, Integer index ) throws NotSetException {
		try {
			ResultSet result = getSpellEffectResult(spellId, index);
			int i = result.getInt("SecondaryEffect");
			result.getStatement().close();
			return i;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getTargets( int spellId, Integer index ) throws NotSetException {
		try {
			ResultSet result = getSpellEffectResult(spellId, index);
			int i = result.getInt("Targets");
			result.getStatement().close();
			return i;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public double getPeriod( int spellId, Integer index ) throws NotSetException {
		try {
			ResultSet result = getSpellEffectResult(spellId, index);
			double d = result.getDouble("Period") / 1000d;
			result.getStatement().close();
			return d;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public double getProcChance( int spellId, Integer index ) throws NotSetException {
		try {
			ResultSet result = getSpellEffectResult(spellId, index);
			double d = result.getDouble("ProcChance");
			result.getStatement().close();
			return d;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public double getF8( int spellId, Integer index ) throws NotSetException {
		try {
			ResultSet result = getSpellEffectResult(spellId, index);
			double d = result.getDouble("f8");
			result.getStatement().close();
			return d;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getSpellDescription( int spellId ) throws NotSetException {
		try {
			ResultSet result = getSpellResult(spellId);
			String s = result.getString("Description");
			result.getStatement().close();
			return s;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getSpellName( int spellId) throws NotSetException {
		try {
			ResultSet result = getSpellResult(spellId);
			String s = result.getString("Name");
			result.getStatement().close();
			return s;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Spell getSpell( int spellId ) {
		try {
			return new Spell(db, spellId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Double getDuration( int spellId ) throws NotSetException {
		try {
			ResultSet result = getSpellDurationResult(spellId);
			double d = result.getDouble("Duration") / 1000d;
			result.getStatement().close();
			return d;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getScalingId( int spellId) throws NotSetException {
		try {
			ResultSet result = getSpellResult(spellId);
			int i = result.getInt("SpellScalingID");
			result.getStatement().close();
			return i;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public SpellEffectScaling getSpellEffectScaling( int spellEffectId ) throws NotSetException {
		try {
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM `SpellEffectScaling` WHERE `SpellEffectID`=?");
			stmt.setInt(1, spellEffectId);
			
			ResultSet result = stmt.executeQuery();
			if( ! result.next() ) {
				throw new NotSetException("Result is empty: " + stmt.toString());
			}
			
			SpellEffectScaling s = new SpellEffectScaling(
					result.getDouble("Coefficient"),
					result.getDouble("Dice")
			);
			result.getStatement().close();
			
			return s;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getSpellRadius( int spellId, Integer index ) throws NotSetException {
		try {
			if( index == null ) {
				System.err.println("Index was null, falling back to 1 (one based)");
				index = 1;
			}
			
			PreparedStatement stmt = db.prepareStatement("SELECT r.Radius FROM spellradius r, spelleffect e WHERE e.spellid=? and r.ID = e.SpellRadiusID and e.Index = ?");
			stmt.setInt(1, spellId);
			stmt.setInt(2, index - 1);
			
			ResultSet result = stmt.executeQuery();
			if( ! result.next() ) {
				throw new NotSetException("Result is empty: " + stmt.toString());
			}
			int r = result.getInt("Radius");
			result.getStatement().close();
			return r;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getEffectRadius( int spellId, Integer index ) throws NotSetException {
		try {
			if( index == null ) {
				System.err.println("Index was null, falling back to 1 (one based)");
				index = 1;
			}
			
			PreparedStatement stmt = db.prepareStatement("SELECT r.Radius FROM spellradius r, spelleffect e WHERE e.spellid=? and r.ID = e.EffectSpellRadiusID and e.Index = ?");
			stmt.setInt(1, spellId);
			stmt.setInt(2, index - 1);
			
			ResultSet result = stmt.executeQuery();
			if( ! result.next() ) {
				throw new NotSetException("Result is empty: " + stmt.toString());
			}
			int r = result.getInt("Radius");
			result.getStatement().close();
			return r;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getMaximumTargets( int spellId ) throws NotSetException {
		try {
			PreparedStatement stmt = db.prepareStatement("SELECT r.* FROM `spelltargetrestrictions` r INNER JOIN `spell` s ON s.`SpellTargetRestrictionsID` = r.`ID` WHERE s.`ID`=?");
			stmt.setInt(1, spellId);
			
			ResultSet result = stmt.executeQuery();
			if( ! result.next() ) {
				throw new NotSetException("Result is empty: " + stmt.toString());
			}
			int t = result.getInt("Targets");
			result.getStatement().close();
			return t;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getTargetLevel( int spellId ) throws NotSetException {
		try {
			PreparedStatement stmt = db.prepareStatement("SELECT r.* FROM `spelltargetrestrictions` r INNER JOIN `spell` s ON s.`SpellTargetRestrictionsID` = r.`ID` WHERE s.`ID`=?");
			stmt.setInt(1, spellId);
			
			ResultSet result = stmt.executeQuery();
			if( ! result.next() ) {
				throw new NotSetException("Result is empty: " + stmt.toString());
			}
			int t = result.getInt("Level");
			result.getStatement().close();
			return t;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getSpellIcon(int spellId) throws NotSetException {
		try {
			ResultSet result = getSpellIconResult(spellId);
			String s = result.getString("Icon").toLowerCase().replaceAll("interface\\\\icons\\\\", "").replaceAll("\\w", "");
			result.getStatement().close();
			return s;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public double getMinimumHostileRange( int spellId ) throws NotSetException {
		try {
			ResultSet result = getRangeResult(spellId);
			double d = result.getInt("MinimumHostile");
			result.getStatement().close();
			return d;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public double getMaximumHostileRange( int spellId ) throws NotSetException {
		try {
			ResultSet result = getRangeResult(spellId);
			double d = result.getInt("MaximumHostile");
			result.getStatement().close();
			return d;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
