package org.chardev.cjt.spelldescriptionparser._old;

public class StringParser {
	protected int cursor;
	protected String string;
	
	public StringParser( String string ) {
		this.string = string;
	}
	
	public void readUntil( String c ) {
		readUntil(c.codePointAt(0));
	}
	
	public void readUntil( int codePoint) {
		while( cursor < string.length() && string.codePointAt(cursor) != codePoint ) {
			cursor ++;
		}
	}
	
	public boolean readLetter() {
		if( cursor < string.length() && Character.isLetter(string.codePointAt(cursor)) ) {
			cursor ++;
			return true;
		}
		return false;
	}
	
	public boolean readDigit() {
		if( cursor < string.length() && Character.isDigit(string.codePointAt(cursor)) ) {
			cursor ++;
			return true;
		}
		return false;
	}
	
	public boolean readWhitespace() {
		if( cursor < string.length() && Character.isWhitespace(string.codePointAt(cursor)) ) {
			cursor ++;
			return true;
		}
		return false;
	}
	
	public boolean read( String c ) {
		return read(c.codePointAt(0));
	}
	
	public boolean read( int codePoint ) {
		if( cursor < string.length() && string.codePointAt(cursor) == codePoint ) {
			cursor ++;
			return true;
		}
		return false;
	}
	
	public boolean utfEquals( String c ) {
		return utfEquals(c.codePointAt(0));
	}
	
	public boolean utfEquals( int codePoint ) {
		return string.codePointAt(cursor) == codePoint;
	}
	
	public void readParenthesis() throws Exception {
		int opening = string.codePointAt(cursor);
		int closing;
		switch( opening ) {
		case '{': closing = '}'; break;
		case '(': closing = ')'; break;
		case '[': closing = ']'; break;
		case '<': closing = '>'; break;
		default: throw new Exception("Unable to determine closing parenthesis for: " + new StringBuffer().appendCodePoint(opening).toString());
		}
		
		cursor ++;
		while(  cursor < string.length() && string.codePointAt(cursor) != closing ) {
			if( string.codePointAt(cursor) == opening ) {
				readParenthesis();
			}
			else {
				cursor ++;
			}
		}
	}
	
	public String getString() {
		return string;
	}
	
	public int getCursor() {
		return cursor;
	}
}
