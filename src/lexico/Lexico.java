package lexico;

import util.FilePosition;
import util.Programa;

public class Lexico{

	public static boolean isAritimetic( char c ){
		return(
			c == '+' || c == '-' || c == '/' || c == '*'
		);
	}

	public static boolean isLetter( char c ){
		return(
			( c >= 'a' && c <= 'z' ) ||
			( c >= 'A' && c <= 'Z' )
		);
	}
	
	public static boolean isDigit( char c ){
		return ( c >= '0' && c <= '9' );
	}

	public static boolean isHexLetter( char c ){
		return( 
			(c >= 'a' && c <= 'f') ||
			(c >= 'A' && c <= 'F')
		);
	}

	public static boolean isPrintable( char c ){
		
		char[] printables = {
				'A','B','C','D','E','F','G','H','I','J','K','L',
				'M','N','O','P','Q','R','S','T','U','V','W','X','Y',
				'Z','a','b','c','d','e','f','g','h','i','j','k','l',
				'm','n','o','p','q','r','s','t','u','v','w','x','y',
				'z','^','0','1','2','3','4','5','6','7','8','9',' ',
				'_','.','\'','"',';','&',':','(',')','[',']','{','}',
				'+','-','*','/','%',',','@','!','?','>','<','=','\n'};
		
		for( int i = 0; i < printables.length; i++ ) {
			if( printables[i] == c )
				return true;
		}
		
		return false;
	}

	public static boolean eof( String programa, FilePosition pos ){
		return( FilePosition.getInstance().getFilePos() == programa.length() );
	}
	
	public static ResultadoLexico getToken( ){

		final int initial_state = 0;
		final int final_state = 4;
		
		String programa = Programa.getInstance().getProgram();
		FilePosition pos = Programa.getInstance().getPosition();

		int state = initial_state;
		char c;
		String lex = "";
		Token token = Token.EOF;
		do{

			if( eof(programa, pos) ){
	
				if( state != 0 ){
					System.out.println("ERRO LEXICO - FIM DE ARQUIVO INESPERADO! NA LINHA "+pos.getLineNumber() );
					System.exit(0);
				}
				break;
			}
			c = programa.charAt( pos.getFilePos() );

			if( isPrintable(c) == false ){
				System.out.println("ERRO LEXICO - CARACTERE '"+c+"' INVALIDO! "+(int)c+" NA LINHA "+pos.getLineNumber());
				System.exit(0);
				break;
			}
			
			if( c == '\n' ) {
				pos.sumLine();
			}
			
			switch( state ){
				case 0:

					if( isDigit(c) ){
						if( c == '0' ){
							state = 1;
							lex += c;
						}else{
							state = 7;
							lex += c;
						}
					}else if( isAritimetic(c) && c != '/' ){
						
						switch( c ) {
							case '+':
								lex += c;
								token = Token.SUM;
								state = final_state;
								break;
							case '-':
								lex += c;
								token = Token.MINUS;
								state = final_state;
								break;
							case '*':
								lex += c;
								token = Token.MULTIPLY;
								state = final_state;
								break;
						}

					}else if( isLetter(c) ){
						state = 8;
						lex += c;

					}else{

						switch( c ){
							case '>':
								state = 9;
								lex += c;
								break;
							case '<':
								state = 10;
								lex += c;
								break;
							case '/':
								state = 11;
								lex += c;
								break;
							case '\'':
								state = 14;
								break;
							case '"':
								state = 16;
								break;
							case ';':
								state = final_state;
								lex += c;
								token = Token.SEMICOLON;
								break;
							case '(':
								state = final_state;
								lex += c;
								token = Token.OPEN_PARENTHESIS;
								break;
							case ')':
								state = final_state;
								lex += c;
								token = Token.CLOSE_PARENTHESIS;
								break;
							case '[':
								state = final_state;
								lex += c;
								token = Token.CLOSE_PARENTHESIS;
								break;
							case ']':
								state = final_state;
								lex += c;
								token = Token.CLOSE_BRACKET;
								break;
							case ',':
								state = final_state;
								lex += c;
								token = Token.COMMA;
								break;
							case '=':
								state= final_state;
								lex += c;
								token = Token.EQUAL;
								break;
							case ' ':
								break;
							case '\n':
								break;
							case '%':
								state = final_state;
								lex += c;
								token = Token.MOD;
								break;
							case '_':
								state = 17;
								lex += c;
								break;
							default:
								System.out.println("ERRO LEXICO - CARACTERE "+c+" INVALIDO! "+(int)c+" NA LINHA "+pos.getLineNumber());
								System.exit(0);
								break;
						}

					}
					break;

				case 1:

					if( isDigit(c) ){
						state = 2;
						lex += c;
					}else if( isHexLetter(c) ){
						state = 5;
						lex += c;
					}else {
						token = Token.CONST;
						pos.devolveChar(c);
						state = final_state;
					}

					break;
				case 2:
					if( isDigit(c) ){
						state = 3;
						lex += c;
					}else if( isHexLetter(c) ){
						state = 6;
						lex += c;
					}else {
						state = final_state;
						token = Token.CONST;
						pos.devolveChar(c);
					}

					break;
				case 3:
					if( isDigit(c) ){
						state = 7;
						lex += c;
					}else{
						
						if( c == 'h' ) {
							token = Token.CONST;
							state = final_state;
						}else {
							token = Token.CONST;
							pos.devolveChar(c);
							state = final_state;
						}
					}

					break;
				case 5:

					if( isDigit(c) || isHexLetter(c) ){
						state = 6;
						lex += c;
					}else{
						System.out.println("ERRO LEXICO - CONSTANTE "+(lex+c)+" INVALIDA! ");
						System.exit(0);
					}

					break;
				case 6:
					if( c == 'h' ){
						state = final_state;
						lex += c;
						token = Token.CONST;
					}else{
						System.out.println("ERRO LEXICO - CONSTANTE "+(lex+c)+" INVALIDA! NA LINHA "+pos.getLineNumber() );
						System.exit(0);
						break;
					}

					break;
				case 7:
					if( isDigit(c) ){
						lex += c;
					}else {
						state = final_state;
						token = Token.CONST;
						pos.devolveChar(c);
					}
					/*
					//  if( c == '(' || c == ')' || c == '[' || c == ']' || c == ' ' || c == '\n' || c == ',' || c == ';' || isAritimetic(c) ){
					}else {
						System.out.println("ERRO LEXICO - CONSTANTE "+(lex+c)+" INVALIDA! NA LINHA "+pos.getLineNumber() );
						System.exit(0);
					}
					*/

					break;
				case 8:
					if( isLetter(c) || isDigit(c) || c == '_' ){
						lex += c;
					}else{
						state = final_state;
						token = Token.ID;
						pos.devolveChar(c);
					}

					break;
				case 9:
					if( c == '=' ){
						lex += c;
					}else{
						pos.devolveChar(c);
					}

					state = final_state;
					token = Token.EQUAL;

					break;
				case 10:
					if( c == '>' ){
						token = Token.DIFFERENT;
						lex += c;
					}else if( c == '=' ) {
						token = Token.LESS_EQUALS;
					}else if( c == '-' ){
						token = Token.ATTR;
						lex += c;
					}else{
						token = Token.LESS;
						pos.devolveChar(c);
					}

					state = final_state;

					break;
				case 11:
					if( c == '*' ){
						state = 12;
						lex = "";
					}else{
						state = final_state;
						token = Token.DIVIDE;
						pos.devolveChar(c);
					}

					break;
				case 12:
					if( c == '*' ){
						state = 13;
					}

					break;
				case 13:
					if( c == '/' ){
						state = 0;
					}

					break;
				case 14:
					if( c != '\'' ){
						state = 15;
						lex += c;
					}

					break;
				case 15:
					if( c == '\'' ){
						state = final_state;
						token = Token.CONST;
					}else {
						System.out.println("ERRO LEXICO - CONSTANTE "+(lex+c)+" INVALIDA! NA LINHA "+pos.getLineNumber() );
						System.exit(0);
					}

					break;
				case 16:
					if( c == '"' ){
						state = final_state;
						token = Token.CONST;
					}else{
						lex += c;
					}
					break;
				case 17:
					if( c == '_' ) {
						lex += c;
					}else if( isLetter(c) || isDigit(c) ) {
						lex += c;
						state = 8;
					}else {
						System.out.println("ERRO LEXICO! - ID "+(lex+c)+" INVALIDO! NA LINHA "+pos.getLineNumber() );
						System.exit(0);
					}
					break;
			}

			pos.nextPos();

		}while( state != final_state );
		
		TabelaSimbolos tabela = TabelaSimbolos.getInstance();
		
		// Adiciona lexema na tabela de simbolos
		tabela.add( lex, token );
		
		return( new ResultadoLexico( tabela.getToken(lex), lex, pos.getLineNumber() ) );
	}
}