package org.chardev.cjt.spelldescriptionparser._old;

import java.util.LinkedList;
import java.util.List;

public class Description extends StringParser {
	
	protected String unmodified;
	protected String stripped;
	protected List<LiteralExpression> expressions;
	
	public Description( String desc ) throws Exception {
		super(desc);
		
		this.unmodified = desc;
		this.expressions = new LinkedList<LiteralExpression>();
		
		parse();
	}
	
	protected void parse() throws Exception {
		int start = 0;
		
		cursor = 0;
		
		while(true) {
			readUntil("$");
			
			if( cursor == string.length() ) {
				break;
			}
			
			if( cursor >= string.length() - 1 ) {
				throw new Exception("Invalid Expression in " + string + ", unexpected end of string");
			}
			
			start = this.cursor;
			
			cursor ++;
			
			if( utfEquals("{") ) {			
				readParenthesis();
				//
				// move cursor behind closing '}'
				cursor ++;
			}
			else if( utfEquals("@") ) {
				cursor ++;
				while(readLetter()){};
				while(readDigit()){};
			}
			else if( utfEquals("?") ) {
				readCondition();
			}
			else {
				
				if( utfEquals("<") ) {
					readParenthesis(); cursor++;
				}
				else {
					if( utfEquals("/") || utfEquals("*") ) {
						cursor++;
						
						System.out.println(readDecimalNumber());
						
						if( ! read(";")){
							throw new DescriptionParsingException("Malformed mathematical expression");
						}
					}
					
					readVariable();
				}
			}
			expressions.add(new LiteralExpression(start, cursor));
		}
		
		stripped = "";
		int current = 0;
		for( LiteralExpression exp : expressions ) {
			stripped += string.substring(current,exp.start) + "?";
			current = exp.end;
		}
		
		if( stripped.contains("$")) {
			throw new DescriptionParsingException("Unhandled expression (Stripped description: " + stripped + ")");
		}
	}
	
	protected Variable readVariable() throws Exception {
		int start = cursor;
		boolean ref = false;
		
		int refId = 0;
		String varName = "";
		int refEffectIndex = 0;
		
		if( readDigit()) {
			ref = true;
			while(readDigit());
			refId = Integer.valueOf(string.substring(start, cursor));
			start = cursor;
		}
		//
		// read a first letter
		if( utfEquals("g") || utfEquals("G") || utfEquals("l") || utfEquals("L") ) {
			String option1, option2;
			//
			// advance behind var name and set start
			start = ++cursor; 
			readUntil(":");
			option1 = string.substring(start,cursor);
			//
			// advance behind colon and set start
			start = ++cursor;
			readUntil(";");
			option2 = string.substring(start,cursor);
			//
			// advance behind semicolon
			cursor ++;
			return new TextOptionVariable(refId, varName, option1, option2);
		}
		else {
			if( ! readLetter()) {
				throw new DescriptionParsingException("Malformed variable");
			}
			//
			// read a second letter
			if( readLetter()) {
				if( ref ) {
					throw new DescriptionParsingException("Malformed variable");
				}
				else {
					while(readLetter());
				}
			}
			else {
				ref = true;
			}
			varName = string.substring(start, cursor);
			start = cursor;
			//
			// read a ref effect id 
			if( readDigit()) {
				if( ref) {
					while(readDigit());
					refEffectIndex = Integer.valueOf(string.substring(start, cursor));
				}
				else {
					throw new DescriptionParsingException("Malformed variable");
				}
			}
			
			if( ! ref ) {
				return new StatVariable(varName);
			}
			else {
				return new ReferenceVariable(refId, varName, refEffectIndex);
			}
		}
	}
	
	protected float readDecimalNumber() {
		int start = cursor;
		boolean decimalPoint = false;
		do {
			if( read(".")) {
				if( decimalPoint ) {
					break;
				}
				decimalPoint = true;
			}
			else if( ! readDigit()) {
				break;
			}
		}
		while( cursor < string.length());
		
		return Float.valueOf(string.substring(start,cursor));
	}
	
	protected void readCondition() throws Exception {
		if( ! utfEquals("?") ) {
			throw new Exception("Condition has to start with a '?'!");
		}
		//
		// IF
		do {
			cursor ++;
			
			if( utfEquals("(") ) {
				readParenthesis();
				//
				// move cursor behind closing ')'
				cursor ++;
			}
			else {
				read("!");
				while(readLetter()){};
				while(readDigit()){};
				while(readWhitespace()){};
			}
		}	
		while( utfEquals("&") || utfEquals("|"));
		//
		// THEN
		if( utfEquals("[")) {
			readParenthesis(); cursor++;
		}
		else if( utfEquals("?")) {
			readCondition();
		}
		else {
			throw new Exception("Malformed condition: "+this.string);
		}
		//
		// ELSE
		if( utfEquals("[")) {
			readParenthesis(); cursor++;
		}
		else if( utfEquals("?")) {
			readCondition();
		}
		else {
			throw new Exception("Malformed condition: "+this.string);
		}
	}
	
	public List<LiteralExpression> getExpressions() {
		return expressions;
	}
	
	public String getUnmodified() {
		return unmodified;
	}
	
	public String getStripped() {
		return stripped;
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append(this.unmodified);
		for( LiteralExpression exp : this.expressions ) {
			s.append(" >>" + exp);
		}
		return s.toString();
	}
	
	public class LiteralExpression {
		public final int start, end;
		public LiteralExpression( int start, int end) {
			this.start = start;
			this.end = end;
		}
		public String toString() {
			return Description.this.string.substring(start,end);
		}
	}
	
	public class DescriptionParsingException extends Exception {
		private static final long serialVersionUID = 1L;

		public DescriptionParsingException( String cause ) {
			super( cause + "\nString was: " + Description.this.unmodified + " Cursor at: " + Description.this.cursor);
		}
	}
}
