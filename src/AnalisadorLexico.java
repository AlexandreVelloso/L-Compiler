public class AnalisadorLexico{

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

	public boolean isSingleChar( char c ){
		return ( c == '(' || c == ')' || c == ',' || c == '=' );
	}

	public boolean isPrintable( char c ){
		// de acordo com a tabela ascii, esses sao os caracteres imprimiveis
		return( c >= 32 && c <= 126);
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
		Tokem token = Tokem.FAZER;
		do{

			if( eof(programa, pos) ){
	
				if( state != 0 ){
					System.out.println("FIM DE ARQUIVO INESPERADO");
					token = Tokem.ERROR;
				}
				break;
			}
			c = programa.charAt( pos.filePos );
			//System.out.println( c+" "+state );

			if( isPrintable(c) == false ){
				System.out.println("CARACTERE INVALIDO!");
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

					}else if( isAritimetic(c) ){ // OLHAR AS 4 OPERACOES BASICAS, / TAMBEM E COMENTARIO
						state = final_state;
						token = Tokem.ARITIMETIC_OP;
						lex += c;

					}else if( isSingleChar(c) ){
						state = final_state;
						token = Tokem.FAZER;
						lex += c;

					}else if( isLetter(c) ){
						state = 8;
						lex += c;

					}else if( c == '>' ){
						state = 9;
						lex += c;

					}else if( c == '<' ){
						state = 10;
						lex += c;
					}else if( c == '/' ){
						state = 11;

					}else if( c == '\''){
						state = 14;
						lex += c;

					}else if( c == '"' ){
						state = 16;
						lex += c;

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
						token = Tokem.INT;
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
						token = Tokem.INT;
						pos.filePos--; // devolve c
					}

					break;
				case 3: // ELE PODE RECEBER 0A90345345374, olhar transicao de 5 para 3
					if( isDigit(c) ){
						state = 7;
						lex += c;
					}else{
						if( c == 'h' ){
							lex += c;
							token = Tokem.CHAR;
						}else{
							pos.filePos--; // devolve c
						}

						state = final_state;
					}

					break;
				case 5:

					// OLHAR ESSE IF
					if( isDigit(c) ){
						state = 3;
						lex += c;
					}else if( isHexLetter(c) ){
						state = 6;
						lex += c;
					}

					break;
				case 6:
					if( c == 'h' ){
						state = final_state;
						lex += c;
						token = Tokem.CHAR;
					}

					break;
				case 7:
					if( isDigit(c) ){
						lex += c;
					}else{
						state = final_state;
						token = Tokem.INT;
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
					token = Tokem.BOOL_OP;

					break;
				case 10:
					if( c == '>' || c == '=' ){
						token = Tokem.BOOL_OP;
						lex += c;
					}else if( c == '-' ){
						token = Tokem.ATTR;
						lex += c;
					}else{
						token = Tokem.BOOL_OP;
						pos.filePos--; // devolve c
					}

					state = final_state;

					break;
				case 11:
					if( c == '*' ){
						state = 12;
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
						token = Tokem.CHAR;
					}

					break;
				case 16:
					if( c == '"' ){
						state = final_state;
						token = Tokem.STRING;
					}else{
						lex += c;
					}
					break;
			}

			pos.filePos++;

		}while( state != final_state );

		return(  new ResultadoLexico( token, lex ) );
	}
}