package sintatico;

import lexico.Lexico;
import lexico.ResultadoLexico;
import lexico.TabelaSimbolos;
import lexico.Token;
import util.FilePosition;

public class Sintatico {
	
	private static Token token;

	public static Token readToken() {
		ResultadoLexico result = Lexico.getToken();

		//System.out.println( "lex: "+result.getLexema()+" tok: "+result.getToken()+" linha: "+result.getLine() );
		
		return result.getToken();
	}

	public static void casaToken(Token tokenEsperado) {

		if( token == tokenEsperado ) {
			//System.out.println("CASOU TOKEN "+token);
		}else {
			error();
		}
		
		token = readToken();
	}
	
	public static void error() {
		System.out.println( "ERRO! TOKEN "+token+" INESPERADO! NA LINHA "+FilePosition.getInstance().getLine() );
		System.exit(0);
	}

	public static void principal() {
		token = readToken();
		S();
		casaToken(Token.EOF);
	}
	
	public static void S() {

		// VARIAVEL ou CONSTANTE
		while( token == Token.INT || token == Token.CHAR || token == Token.FINAL ) {
			if( token == Token.INT || token == Token.CHAR) {
				VARIAVEL();
			}else if( token == Token.FINAL ) {
				CONSTANTE();
			}
		}
		
		while( token == Token.FOR || token == Token.IF || token == Token.READLN ||
				  token == Token.WRITE || token == Token.WRITELN || 
				  token == Token.ID | token == Token.SEMICOLON ) {
			
				COMANDO();
		}
		
	}
	
	public static void VARIAVEL() {
		
		if( token == Token.INT ) {
			casaToken(Token.INT);
		}else if( token == Token.CHAR ) {
			casaToken(Token.CHAR);
		}
		
		casaToken(Token.ID);
		
		if( token == Token.OPEN_BRACKET ) {
			casaToken(Token.OPEN_BRACKET);
			EXP();
			casaToken(Token.CLOSE_BRACKET);
		}else if( token == Token.ATTR ) {
			ATRIBUICAO();
		}
		
		while( token == Token.COMMA ) {
			casaToken(Token.COMMA);
			casaToken(Token.ID);
			
			if( token == Token.OPEN_BRACKET ) {
				casaToken(Token.OPEN_BRACKET);
				EXP();
				casaToken(Token.CLOSE_BRACKET);
			}else if( token == Token.ATTR ) {
				ATRIBUICAO();
			}
		}
		
		casaToken(Token.SEMICOLON);
	}
	
	public static void ATRIBUICAO() {
		casaToken(Token.ATTR);
		EXP();
	}
	
	public static void CONSTANTE() {
		casaToken(Token.FINAL);
		casaToken(Token.ID);
		casaToken(Token.EQUAL);
		
		if( token == Token.SUM) {
			casaToken(Token.SUM);
		}else if( token == Token.MINUS ) {
			casaToken(Token.MINUS);
		}
		
		casaToken( Token.CONST );
		casaToken( Token.SEMICOLON );
	}
	
	public static void COMANDO() {
		
		while ( token == Token.FOR || token == Token.IF ||
				token == Token.READLN || token == Token.WRITE || token == Token.WRITELN ||
				token == Token.ID || token == Token.SEMICOLON ) {
			
			if( token == Token.FOR ) {
				casaToken(Token.FOR);
				casaToken(Token.ID);
				casaToken(Token.ATTR);
				EXP();
				casaToken(Token.TO);
				EXP();
				
				if( token == Token.STEP ) {
					casaToken(Token.STEP);
					casaToken(Token.CONST);
				}
				
				casaToken(Token.DO);
				BLOCO();
				
			}else if( token == Token.IF ) {
				casaToken(Token.IF);
				EXP();
				casaToken(Token.THEN);
				BLOCO();
				
				if( token == Token.ELSE ) {
					casaToken(Token.ELSE);
					BLOCO();
				}
			
			}else if( token == Token.READLN ||
					  token == Token.WRITE || token == Token.WRITELN || 
					  token == Token.ID || token == Token.SEMICOLON ) {
				CMD();
			}
		}
	}
	
	public static void BLOCO() {
		
		if( token == Token.BEGIN ) {
			casaToken(Token.BEGIN);
			COMANDO();
			casaToken(Token.END);
		}else {
			COMANDO();
		}
	}
	
	public static void CMD() {
		
		do {
			
			if( token == Token.READLN ) {
				casaToken(Token.READLN);
				casaToken(Token.OPEN_PARENTHESIS);
				casaToken(Token.ID);
				casaToken(Token.CLOSE_PARENTHESIS);
			}else if( token == Token.WRITE ) {
				casaToken(Token.WRITE);
				casaToken(Token.OPEN_PARENTHESIS);
				EXP();
				
				if( token == Token.COMMA ) {
					casaToken(Token.COMMA);
					EXP();
				}
				
				casaToken(Token.CLOSE_PARENTHESIS);
				
			}else if( token == Token.WRITELN ) {
				casaToken(Token.WRITELN);
				casaToken(Token.OPEN_PARENTHESIS);
				EXP();
				
				if( token == Token.COMMA ) {
					casaToken(Token.COMMA);
					EXP();
				}
				
				casaToken(Token.CLOSE_PARENTHESIS);
				
			}else if( token == Token.ID ) {
				casaToken(Token.ID);
				ATRIBUICAO();
			}
			
			casaToken(Token.SEMICOLON);
			
		}while( token == Token.SEMICOLON || token == Token.READLN ||
			   token == Token.WRITE || token == Token.WRITELN ||
			   	token == Token.ATTR );
	}
	
	public static void EXP() {
		EXPS();
		
		if( token == Token.LESS ) {
			casaToken(Token.LESS);
			EXPS();
		}else if( token == Token.GREATHER ) {
			casaToken(Token.GREATHER);
			EXPS();
		}else if( token == Token.LESS_EQUALS ) {
			casaToken(Token.LESS_EQUALS);
			EXPS();
		}else if( token == Token.GREATHER_EQUAL ) {
			casaToken(Token.GREATHER_EQUAL);
			EXPS();
		}else if( token == Token.EQUAL ) {
			casaToken(Token.EQUAL);
			EXPS();
		}else if( token == Token.DIFFERENT ) {
			casaToken(Token.DIFFERENT);
			EXPS();
		}
	}
	
	public static void EXPS() {
		
		if( token == Token.SUM ) {
			casaToken(Token.SUM);
		}else if( token == Token.MINUS ) {
			casaToken(Token.MINUS);
		}
		
		T();
		
		while( token == Token.SUM || token == Token.MINUS || token == Token.OR ) {
			
			if( token == Token.SUM ) {
				casaToken(Token.SUM);
			}else if( token == Token.MINUS ) {
				casaToken(Token.MINUS);
			}else if( token == Token.OR ) {
				casaToken(Token.OR);
			}
			
			T();
		}
		
	}
	
	public static void T() {
		F();
		
		while( token == Token.MULTIPLY || token == Token.DIVIDE || token == Token.MOD ||
			   token == Token.AND ) {
			
			if( token == Token.MULTIPLY ) {
				casaToken(Token.MULTIPLY);
			}else if( token == Token.DIVIDE ) {
				casaToken(Token.DIVIDE);
			}else if( token == Token.MOD ) {
				casaToken(Token.MOD);
			}else if( token == Token.AND ) {
				casaToken(Token.AND);
			}
			
			F();
		}
	}
	
	public static void F() {
		
		if( token == Token.OPEN_PARENTHESIS ) {
			casaToken(Token.OPEN_PARENTHESIS);
			EXP();
			casaToken(Token.CLOSE_PARENTHESIS);
			
		}else if( token == Token.NOT ) {
			casaToken(Token.NOT);
			F();
			
		}else if( token == Token.CONST ) {
			casaToken(Token.CONST);
			
		}else if( token == Token.ID ) {
			casaToken(Token.ID);
			
			if( token == Token.OPEN_BRACKET ) {
				casaToken(Token.OPEN_BRACKET);
				EXP();
				casaToken(Token.CLOSE_BRACKET);
			}
		}else {
			// OLHAR
			error();
		}
	}
	
}