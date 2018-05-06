package lexico;

import enums.Token;
import java.util.HashMap;

public class TabelaSimbolos {
	private HashMap<String, Token> tabela = new HashMap<>();
	private static TabelaSimbolos instancia;
	
 	private TabelaSimbolos() {
		tabela.put( "and", Token.AND );
		tabela.put( "<-", Token.ATTR );
		tabela.put( "begin", Token.BEGIN );
		tabela.put( "char", Token.CHAR );
		tabela.put( "]", Token.CLOSE_BRACKET );
		tabela.put( ")", Token.CLOSE_PARENTHESIS );
		tabela.put( ",", Token.COMMA );
		tabela.put( "<>", Token.DIFFERENT );
		tabela.put( "/", Token.DIVIDE );
		tabela.put( "do", Token.DO );
		tabela.put( "else", Token.ELSE );
		tabela.put( "end", Token.END );
		tabela.put( "=", Token.EQUAL );
		tabela.put( "final", Token.FINAL );
		tabela.put( "for", Token.FOR );
		tabela.put( "if", Token.IF );
		tabela.put( "int", Token.INT );
		tabela.put( "<", Token.LESS );
		tabela.put( "<=", Token.LESS_EQUALS );
		tabela.put( "-", Token.MINUS );
		tabela.put( ">", Token.GREATHER );
		tabela.put( ">=", Token.GREATHER_EQUAL );
		tabela.put( "*", Token.MULTIPLY );
		tabela.put( "not", Token.NOT );
		tabela.put( "[", Token.OPEN_BRACKET );
		tabela.put( "(", Token.OPEN_PARENTHESIS );
		tabela.put( "or", Token.OR );
		tabela.put( "%", Token.MOD );
		tabela.put( "readln", Token.READLN );
		tabela.put( ";", Token.SEMICOLON );
		tabela.put( "step", Token.STEP );
		tabela.put( "+", Token.SUM );
		tabela.put( "then", Token.THEN );
		tabela.put( "to", Token.TO );
		tabela.put( "write", Token.WRITE );
		tabela.put( "writeln", Token.WRITELN );
	}

	public static TabelaSimbolos getInstance() {
		
		if( instancia == null ) {
			instancia = new TabelaSimbolos();
		}
		
		return instancia;
	}
	
	public void add( String key, Token value ) {
		
		if( this.tabela.containsKey( key ) == false ) {
			this.tabela.put( key, value );
		}
	}
	
	public Token getToken( String lex ) {
		return this.tabela.get( lex );
	}
	
	public void print() {
		this.tabela.forEach( (k,v)->System.out.println("key: "+k+" value: "+v) );
	}
}