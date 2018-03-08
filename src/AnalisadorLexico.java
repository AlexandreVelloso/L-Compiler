package lexico;

public class AnalisadorLexico{

	public boolean isAritimetic( char c ){
		return(
			c == '+' || c == '-' || c == '/' || c == '*'
		);
	}

	public boolean isLetter( char c ){
		return(
			( c >= 'a' && c <= 'z' ) ||
			( c >= 'A' && c <= 'Z' )
		);
	}
	
	public boolean isDigit( char c ){
		return ( c >= '0' && c <= '9' );
	}

	public boolean isHexLetter( char c ){
		return( 
			(c >= 'a' && c <= 'f') ||
			(c >= 'A' && c <= 'F')
		);
	}

	public boolean isPrintable( char c ){
		// de acordo com a tabela ascii, esses sao os caracteres imprimiveis
		return( c >= 32 && c <= 255 || c == '\n');
	}

	public boolean eof( String programa, FilePosition pos ){
		return( pos.filePos == programa.length() );
	}
	
	public ResultadoLexico getToken( String programa, FilePosition initial_position ){

		final int initial_state = 0;
		final int final_state = 4;

		int state = initial_state;
		char c;
		FilePosition pos = initial_position;
		String lex = "";
		Tokem token = Tokem.EOF;
		do{

			if( eof(programa, pos) ){
	
				if( state != 0 ){
					System.out.println("FIM DE ARQUIVO INESPERADO");
					token = Tokem.ERROR;
				}
				break;
			}
			c = programa.charAt( pos.filePos );
			//System.out.println( c+": "+""+(int)c+"" );

			if( isPrintable(c) == false ){
				System.out.println("CARACTERE '"+c+"' INVALIDO! "+(int)c);
				token = Tokem.ERROR;
				break;
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
						state = final_state;
						token = Tokem.OPERATION;
						lex += c;

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
								break;
							case '\'':
								state = 14;
								lex += c;
								break;
							case '"':
								state = 16;
								break;
							case ';':
								state = final_state;
								lex += c;
								token = Tokem.SEMICOLON;
								break;
							case '(':
								state = final_state;
								lex += c;
								token = Tokem.OPEN_PARENTHESIS;
								break;
							case ')':
								state = final_state;
								lex += c;
								token = Tokem.CLOSE_PARENTHESIS;
								break;
							case '[':
								state = final_state;
								lex += c;
								token = Tokem.CLOSE_PARENTHESIS;
								break;
							case ']':
								state = final_state;
								lex += c;
								token = Tokem.CLOSE_BRACKET;
								break;
							case ',':
								state = final_state;
								lex += c;
								token = Tokem.COMMA;
								break;
							case '=':
								state= final_state;
								lex += c;
								token = Tokem.EQUAL;
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
					}else{
						state = final_state;
						token = Tokem.CONST;
						pos.filePos--; // devolve c
					}

					break;
				case 2:
					if( isDigit(c) ){
						state = 3;
						lex += c;
					}else if( isHexLetter(c) ){
						state = 6;
						lex += c;
					}else{
						state = final_state;
						token = Tokem.CONST;
						pos.filePos--; // devolve c
					}

					break;
				case 3:
					if( isDigit(c) ){
						state = 7;
						lex += c;
					}else{
						if( c == 'h' ){
							lex += c;
							token = Tokem.CONST;
						}else{
							pos.filePos--; // devolve c
						}

						state = final_state;
					}

					break;
				case 5:

					if( isDigit(c) || isHexLetter(c) ){
						state = 6;
						lex += c;
					}else{
						System.out.println("CONSTANTE INVALIDA!");
						token = Tokem.ERROR;
					}

					break;
				case 6:
					if( c == 'h' ){
						state = final_state;
						lex += c;
						token = Tokem.CONST;
					}else{
						System.out.println("CONSTANTE INVALIDA!");
						token = Tokem.ERROR;
						break;
					}

					break;
				case 7:
					if( isDigit(c) ){
						lex += c;
					}else{
						state = final_state;
						token = Tokem.CONST;
						pos.filePos--; // devolve c
					}

					break;
				case 8:
					if( isLetter(c) || isDigit(c) || c == '_' ){
						lex += c;
					}else{
						state = final_state;
						token = Tokem.ID;
						pos.filePos--; // devolve c
					}

					break;
				case 9:
					if( c == '=' ){
						lex += c;
					}else{
						pos.filePos--; // devolve c
					}

					state = final_state;
					token = Tokem.OPERATION;

					break;
				case 10:
					if( c == '>' || c == '=' ){
						token = Tokem.OPERATION;
						lex += c;
					}else if( c == '-' ){
						token = Tokem.ATTR;
						lex += c;
					}else{
						token = Tokem.OPERATION;
						pos.filePos--; // devolve c
					}

					state = final_state;

					break;
				case 11:
					if( c == '*' ){
						state = 12;
					}else{
						state = final_state;
						token = Tokem.OPERATION;
						pos.filePos--; // devolve c
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
						token = Tokem.CONST;
					}

					break;
				case 16:
					if( c == '"' ){
						state = final_state;
						token = Tokem.CONST;
					}else{
						lex += c;
					}
					break;
			}

			pos.filePos++;

		}while( token != Tokem.ERROR && state != final_state );

		return(  new ResultadoLexico( token, lex ) );
	}
}