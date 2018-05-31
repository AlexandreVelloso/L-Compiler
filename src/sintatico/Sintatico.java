package sintatico;

import enums.Classe;
import enums.Tipo;
import enums.Token;
import lexico.Lexico;
import lexico.RegistroLexico;
import semantico.GeradorCodigo;
import semantico.Variaveis;
import util.FilePosition;

public class Sintatico {

	private static Token token;
	private static RegistroLexico result;
	private static final FilePosition pos = FilePosition.getInstance();
	private static final Variaveis variaveis = Variaveis.getInstance();
	private static final GeradorCodigo codigo = GeradorCodigo.getInstance();

	public static Token readToken() throws Exception {
		result = Lexico.getToken();

		// System.out.println("lex: " + result.getLexema() + " tok: " +
		// result.getToken() + " linha: " + result.getLine());
		return result.getToken();
	}

	public static void casaToken(Token tokenEsperado) throws Exception {

		if (token == tokenEsperado) {
			//System.out.println("CASOU TOKEN " + token);
		} else {
			error();
		}

		token = readToken();
	}

	public static void error() throws Exception {

		if (result.getToken() == null) {
			System.out.println(pos.getLineNumber() + ":fim de arquivo nao esperado.");
		} else {
			System.out.println(pos.getLineNumber() + ":token nao esperado[" + result.getLexema() + "].");
		}

		throw new Exception();
	}

	public static void principal() throws Exception {
		token = readToken();
		S();
	}

	public static void S() throws Exception {

		codigo.inicioASM();

		// VARIAVEL ou CONSTANTE
		while (token == Token.INT || token == Token.CHAR || token == Token.FINAL) {

			if (token == Token.INT || token == Token.CHAR) {
				VARIAVEL();

			} else if (token == Token.FINAL) {
				CONSTANTE();
			}
		}

		codigo.fimVariaveisASM();

		//RegistroLexico s = new RegistroLexico();

		while (token == Token.FOR || token == Token.IF || token == Token.READLN || token == Token.WRITE
				|| token == Token.WRITELN || token == Token.ID || token == Token.SEMICOLON) {
			// COMANDO
			COMANDO();
		}
		
		if( token != null ) {
			error();
		}

		codigo.fimASM();
	}

	public static void VARIAVEL() throws Exception {

		Tipo tipo = null;
		int tamanho = 0;
		Classe classe = Classe.VARIAVEL;

		boolean varInicializada = false;

		if (token == Token.INT) {
			casaToken(Token.INT);

			tipo = Tipo.INTEIRO;
		} else {
			casaToken(Token.CHAR);

			tipo = Tipo.CARACTERE;
		}

		RegistroLexico id = result.clone();
		id.setTipo(tipo);
		id.setTamanho(tamanho);
		id.setClasse(classe);

		casaToken(Token.ID);
		variaveis.addVariavel(id);

		if (token == Token.OPEN_BRACKET) {
			casaToken(Token.OPEN_BRACKET);

			tamanho = Integer.parseInt(result.getLexema());

			if (tamanho == 0 || (tipo == Tipo.INTEIRO && tamanho > 2000)
					|| (tipo == Tipo.CARACTERE && tamanho > 4000)) {
				System.out.println(pos.getLineNumber() + ":tamanho do vetor excede o maximo permitido. ");
				throw new Exception();
			} else {
				id.setTamanho(tamanho);
			}

			casaToken(Token.CONST);
			casaToken(Token.CLOSE_BRACKET);
		} else if (token == Token.ATTR) {
			ATRIBUICAO(id);
			varInicializada = true;
		}

		// se a variavel nao foi inicializada
		// gera o codigo dela.
		// mas se ela foi inicializada o procedimento
		// ATRIBUICAO ja escreveu ela no arquivo
		if (!varInicializada) {
			codigo.adicionarVariavel(id);
		}

		while (token == Token.COMMA) {
			casaToken(Token.COMMA);

			tamanho = 0;
			varInicializada = false;

			id = result.clone();
			id.setTipo(tipo);
			id.setTamanho(tamanho);
			id.setClasse(classe);

			casaToken(Token.ID);
			variaveis.addVariavel(id);

			if (token == Token.OPEN_BRACKET) {
				casaToken(Token.OPEN_BRACKET);

				tamanho = Integer.parseInt(result.getLexema());

				if (tamanho == 0 || (tipo == Tipo.INTEIRO && tamanho > 2000)
						|| (tipo == Tipo.CARACTERE && tamanho > 4000)) {
					System.out.println(pos.getLineNumber() + ":tamanho do vetor excede o maximo permitido. ");
					throw new Exception();
				} else {
					id.setTamanho(tamanho);
				}

				casaToken(Token.CONST);
				casaToken(Token.CLOSE_BRACKET);
			} else if (token == Token.ATTR) {
				ATRIBUICAO(id);
				varInicializada = true;
			}

			// se a variavel nao foi inicializada
			// gera o codigo dela.
			// mas se ela foi inicializada o procedimento
			// ATRIBUICAO ja escreveu ela no arquivo
			if (!varInicializada) {
				codigo.adicionarVariavel(id);
			}
		}

		casaToken(Token.SEMICOLON);
	}

	public static void ATRIBUICAO(RegistroLexico id) throws Exception {
		
		casaToken(Token.ATTR);

		boolean trocarSinal = false;

		if (token == Token.SUM) {
			casaToken(Token.SUM);
		} else if (token == Token.MINUS) {
			casaToken(Token.MINUS);

			trocarSinal = true;
		}

		if (result.getTipo() == Tipo.INTEIRO) {

			int valor = Integer.parseInt(result.getLexema());

			if (trocarSinal) {
				valor *= -1;
			}

			codigo.adicionarVariavel(id, valor);

		} else {

			if (trocarSinal) {
				System.out.println(pos.getLineNumber() + ":tipos incompativeis");
				throw new Exception();
			}

			codigo.adicionarVariavel(id, result.getLexema());
		}

		casaToken(Token.CONST);
	}

	public static void CONSTANTE() throws Exception {
		Classe classe = Classe.CONSTANTE;
		int tamanho = 0;
		int valor;

		casaToken(Token.FINAL);

		RegistroLexico id = result.clone();
		id.setTamanho(tamanho);
		id.setClasse(classe);
		id.setTipo(result.getTipo());

		casaToken(Token.ID);
		casaToken(Token.EQUAL);

		boolean trocarSinal = false;

		if (token == Token.SUM) {
			casaToken(Token.SUM);
		} else if (token == Token.MINUS) {
			casaToken(Token.MINUS);
			trocarSinal = true;
		}

		if (id.getTipo() == Tipo.INTEIRO) {
			valor = Integer.parseInt(result.getLexema());
			if (trocarSinal) {
				valor *= -1;
			}
		} else {
			valor = result.getLexema().charAt(0);
		}

		variaveis.addVariavel(id);
		codigo.adicionarVariavel(id, valor);

		casaToken(Token.CONST);
		casaToken(Token.SEMICOLON);
	}

	public static void COMANDO() throws Exception {
		
		if( token == Token.FOR ) {
			
			casaToken(Token.FOR);
			RegistroLexico id = variaveis.getVar(result.getLexema());
			
			if (id == null) {
				
				System.out.println("ERRO: Variavel [" + result.getLexema() + "] nao foi declarada.");
				throw new Exception();
				
			}else if( id.getTipo() != Tipo.INTEIRO ) {
				
				System.out.println(pos.getLineNumber() + ":tipos incompativeis");
				throw new Exception();
				
			}else {
				
				casaToken(Token.ID);
				casaToken(Token.ATTR);
				
				RegistroLexico exp = new RegistroLexico();
				EXP( exp );
				
				if( exp.getTipo() != Tipo.INTEIRO ) {
					System.out.println(pos.getLineNumber() + ":tipos incompativeis");
					throw new Exception();
				}
				
				id.setEndereco(exp.getEndereco());

				casaToken(Token.TO);
				
				RegistroLexico exp1 = new RegistroLexico();
				EXP(exp1);
				
				RegistroLexico temp = new RegistroLexico();
				temp.setEndereco( exp1.getEndereco() );
				
				if( exp1.getTipo() != Tipo.INTEIRO ) {
					System.out.println(pos.getLineNumber() + ":tipos incompativeis");
					throw new Exception();
				}
				
				int step = 1;
				if( token == Token.STEP ) {
					casaToken( Token.STEP );
					
					step = Integer.parseInt( result.getLexema() );
					
					casaToken( Token.CONST );
				}
				
				int numRotulo = codigo.novoRotulo();
				codigo.rotulo(numRotulo);
				
				casaToken(Token.DO);
				BLOCO();
				
				codigo.mov("cx", "DS:[" + id.getEndereco() + "]");
				codigo.mov("dx", "DS:[" + temp.getEndereco() + "]");
				codigo.add("cx", "" + step, "incremento do for");
				codigo.mov("DS:[" + id.getEndereco() + "]", "cx", "retorna o valor de cx para memoria");
				codigo.cmp("cx", "dx");
				codigo.jle(numRotulo, "fim for");
			}
			
		}else if( token == Token.IF ) {
			
			int rotulo0 = codigo.novoRotulo();
			int rotulo1 = codigo.novoRotulo();
			
			casaToken( Token.IF );
			RegistroLexico exp = new RegistroLexico();
			EXP(exp);
			
			codigo.comentario("Comeco do if");
			
			if( exp.getTipo() != Tipo.RELACIONAL ) {
				System.out.println(pos.getLineNumber() + ":tipos incompativeis");
				throw new Exception();
			}
			
			codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "CMD.end = EXP.end");
			codigo.cmp("ax", "0");
			codigo.je(rotulo0, "pula se falso");
			
			casaToken( Token.THEN );
			BLOCO();
			
			codigo.jmp(rotulo1, "pula para o fim do if");
			codigo.rotulo(rotulo0);
			
			if( token == Token.ELSE ) {
				casaToken( Token.ELSE );
				BLOCO();
				codigo.comentario("fim do else");
			}
			
			codigo.comentario("Fim de if");
			codigo.rotulo(rotulo1);
			
		}else if( token == Token.READLN ) {
			
			casaToken( Token.READLN );
			casaToken( Token.OPEN_PARENTHESIS );
			
			RegistroLexico id = variaveis.getVar(result.getLexema());
			
			if( id == null ) {
				System.out.println("ERRO: Variavel [" + result.getLexema() + "] nao foi declarada.");
				throw new Exception();
			}
			
			casaToken( Token.ID );
			
			if( token == Token.OPEN_BRACKET ) {
				
				if( id.getTamanho() == 0 ) {
					System.out.println( pos.getLineNumber()+":tamanho do vetor excede o maximo permitido.");
					throw new Exception();
				}
				
				casaToken( Token.OPEN_BRACKET );
				
				int endTemp;
				if( id.getTipo() == Tipo.INTEIRO ) {
					endTemp = codigo.novoTemp(2);
				}else {
					endTemp = codigo.novoTemp(1);
				}
				
				RegistroLexico exp = new RegistroLexico();
				EXP( exp );
				
				if( exp.getTipo() != Tipo.INTEIRO || exp.getTamanho() != 0 ) {
					System.out.println(pos.getLineNumber() + ":tipos incompativeis");
					throw new Exception();
				}
				
				codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "carrega o conteudo de E.end para regA");
				
				if( exp.getTipo() == Tipo.INTEIRO ) {
					codigo.add("ax", "ax");
				}
				
				codigo.mov("di", "ax");
				codigo.add("di", "" + id.getEndereco(), "soma o deslocamento");
				codigo.mov("DS:[" + endTemp + "]", "di", "salva o valor do deslocamento em temp");
				
				if( id.getTipo() == Tipo.INTEIRO ) {
					codigo.readInt(id.getEndereco(), endTemp );
				}else {
					codigo.readChar( endTemp );
				}
				
				casaToken( Token.CLOSE_BRACKET );
				
			}else // nao abriu []
			{
				
				if( id.getTipo() == Tipo.INTEIRO ) {
					codigo.readInt( id.getEndereco() );
				}else{
			
					int tamanho;
					if( id.getTamanho() < 255 ) {
						tamanho = id.getTamanho();
						
						// olhar isso
						if( tamanho == 0 ) {
							tamanho = 1;
						}
					}else {
						tamanho = 255;
					}
					
					codigo.readString(id.getEndereco(), tamanho);
				}
			}
			
			casaToken( Token.CLOSE_PARENTHESIS );
			casaToken( Token.SEMICOLON );
			
		}else if( token == Token.WRITE ) {
			
			casaToken( Token.WRITE );
			casaToken( Token.OPEN_PARENTHESIS );
			
			RegistroLexico exp = new RegistroLexico();
			EXP(exp);
			
			if( exp.getTipo() == Tipo.INTEIRO ) {
				
				if( exp.getTamanho() != 0 ) {
					System.out.println(pos.getLineNumber() + ":tipos incompativeis");
					throw new Exception();
				}
				
				codigo.mostrarInt( exp.getEndereco() );
			}else if( exp.getTipo() == Tipo.CARACTERE ) {
				
				if( exp.getToken() == Token.ID && exp.getTamanho() == 0 ) {
					codigo.mostrarChar( exp.getEndereco() );
				}else {
					codigo.mostrarString( exp.getEndereco() );
				}
				
			}else {
				// OBS: Olhar isso aqui, se posso mostrar tipo relacional
				System.out.println("Devo mostrar tipo relacional?");
			}
			
			while( token == Token.COMMA ) {
				casaToken( Token.COMMA );
				exp = new RegistroLexico();
				EXP(exp);
				
				if( exp.getTipo() == Tipo.INTEIRO ) {
					
					if( exp.getTamanho() != 0 ) {
						System.out.println(pos.getLineNumber() + ":tipos incompativeis");
						throw new Exception();
					}
					
					codigo.mostrarInt( exp.getEndereco() );
				}else if( exp.getTipo() == Tipo.CARACTERE ) {
					
					if( exp.getToken() == Token.ID && exp.getTamanho() == 0 ) {
						codigo.mostrarChar( exp.getEndereco() );
					}else {
						codigo.mostrarString( exp.getEndereco() );
					}
					
				}else {
					// OBS: Olhar isso aqui, se posso mostrar tipo relacional
					System.out.println("Devo mostrar tipo relacional?");
				}
			}
			
			casaToken( Token.CLOSE_PARENTHESIS );
			casaToken( Token.SEMICOLON );
			
		}else if( token == Token.WRITELN ) {
			
			casaToken( Token.WRITELN );
			casaToken( Token.OPEN_PARENTHESIS );
			
			RegistroLexico exp = new RegistroLexico();
			EXP(exp);
			
			if( exp.getTipo() == Tipo.INTEIRO ) {
				
				if( exp.getTamanho() != 0 ) {
					System.out.println(pos.getLineNumber() + ":tipos incompativeis");
					throw new Exception();
				}
				
				codigo.mostrarInt( exp.getEndereco() );
			}else if( exp.getTipo() == Tipo.CARACTERE ) {
				
				if( exp.getToken() == Token.ID && exp.getTamanho() == 0 ) {
					codigo.mostrarChar( exp.getEndereco() );
				}else {
					codigo.mostrarString( exp.getEndereco() );
				}
				
			}else {
				// OBS: Olhar isso aqui, se posso mostrar tipo relacional
				System.out.println("Devo mostrar tipo relacional?");
			}
			
			while( token == Token.COMMA ) {
				casaToken( Token.COMMA );
				exp = new RegistroLexico();
				EXP(exp);
				
				if( exp.getTipo() == Tipo.INTEIRO ) {
					
					if( exp.getTamanho() != 0 ) {
						System.out.println(pos.getLineNumber() + ":tipos incompativeis");
						throw new Exception();
					}
					
					codigo.mostrarInt( exp.getEndereco() );
				}else if( exp.getTipo() == Tipo.CARACTERE ) {
					
					if( exp.getToken() == Token.ID && exp.getTamanho() == 0 ) {
						codigo.mostrarChar( exp.getEndereco() );
					}else {
						codigo.mostrarString( exp.getEndereco() );
					}
					
				}else {
					// OBS: Olhar isso aqui, se posso mostrar tipo relacional
					System.out.println("Devo mostrar tipo relacional?");
				}
			}
			
			codigo.quebrarLinha();
			
			casaToken( Token.CLOSE_PARENTHESIS );
			casaToken( Token.SEMICOLON );
			
		}else if( token == Token.ID ) {
			
			// pega a variavel da memoria
			RegistroLexico id = variaveis.getVar(result.getLexema());

			if (id == null) {
				System.out.println(pos.getLineNumber()+":identificador nao declarado [" + result.getLexema() + "].");
				throw new Exception("");
			}

			casaToken(Token.ID);
			casaToken(Token.ATTR);

			RegistroLexico exp = new RegistroLexico();
			EXP(exp);

			if (id.getTipo() == exp.getTipo()) {
				
				if (id.getClasse() == Classe.CONSTANTE) {
					System.out.println(pos.getLineNumber() + ":classe de identificador incompativel ["
							+ id.getLexema() + "].");
					throw new Exception();
				}
				
				if( id.getTipo() == Tipo.RELACIONAL ) {
					
					System.out.println( pos.getLineNumber()+":tipos incompativeis");
					throw new Exception();
					
				}else if( exp.getTipo() == Tipo.INTEIRO ) {
					
					if( exp.getTamanho() != 0 || id.getTamanho() != 0 ) {
						System.out.println( pos.getLineNumber()+":tipos incompativeis");
						throw new Exception();
					}
					
					// Copia o valor de EXP.end para regA
					codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "Copia o valor de E.end para regA");
					// Copia o valor de regA para ID.end
					codigo.mov("DS:[" + id.getEndereco() + "]", "ax", "Copia o valor de regA para ID.end");
					
				} else if( exp.getTipo() == Tipo.CARACTERE ) {
					
					if( exp.getToken() == Token.CONST ) { // olhar
						
						if( id.getTamanho() == 0 ) // caractere
						{
							
							if( exp.getTamanho() != 1 ) {
								System.out.println( pos.getLineNumber()+":tipos incompativeis");
								throw new Exception();
							}
							
							codigo.mov("ax", "'"+exp.getLexema()+"'", "copia char para regA");
							codigo.mov("DS:["+id.getEndereco()+"]", "ax", "copia char para memoria");
							
						}else // vetor de caractere
						{
						
							if( (exp.getTamanho() ) > id.getTamanho() ) {
								System.out.println( pos.getLineNumber()+":tipos incompativeis");
								throw new Exception();								
							}
							
							codigo.copiarString( id.getEndereco(), exp.getEndereco() );
							
						}
						
					}else if( exp.getToken() == Token.ID ) {
						
						if( id.getTamanho() < exp.getTamanho() ) {
							System.out.println( pos.getLineNumber()+":tipos incompativeis");
							throw new Exception();
						}
						
						if( id.getTamanho() == 0 && exp.getTamanho() == 0 ) {
							
							// Copia o valor de EXP.end para regA
							codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "Copia o valor de E.end para regA");
							// Copia o valor de regA para ID.end
							codigo.mov("DS:[" + id.getEndereco() + "]", "ax", "Copia o valor de regA para ID.end");
							
						}else {
							
							codigo.copiarString( id.getEndereco(), exp.getEndereco() );
							
						}
						
					}
					
				}

			} else {
				System.out.println( pos.getLineNumber()+":tipos incompativeis");
				throw new Exception();
			}

			casaToken(Token.SEMICOLON);

		}else if( token == Token.SEMICOLON ) {
			
			casaToken(Token.SEMICOLON);

		}else {
			error();
		}

	}

	public static void BLOCO() throws Exception {

		if (token == Token.BEGIN) {
			casaToken(Token.BEGIN);
			
			while (token == Token.FOR || token == Token.IF || token == Token.READLN || token == Token.WRITE
					|| token == Token.WRITELN || token == Token.ID || token == Token.SEMICOLON) {
				
				//RegistroLexico bloco = new RegistroLexico();
				COMANDO();
			}
			
			casaToken(Token.END);
		} else {
			
			//RegistroLexico bloco = new RegistroLexico();
			COMANDO();
		}
	}

	public static void EXP(RegistroLexico exp) throws Exception {

		EXPS(exp);

		if(
			token == Token.LESS || token == Token.GREATHER || token == Token.LESS_EQUALS ||
			token == Token.GREATHER_EQUAL || token == Token.EQUAL || token == Token.DIFFERENT
		){
			
			final int OP_LESS = 0;
			final int OP_GREATHER = 1;
			final int OP_LESS_EQUALS = 2;
			final int OP_GREATHER_EQUAL = 3;
			final int OP_EQUAL = 4;
			final int OP_DIFFERENT = 5;
			int operacao = -1;
			
			switch( token ){
				case LESS:
					casaToken( Token.LESS );
					operacao = OP_LESS;
					break;
				case GREATHER:
					casaToken( Token.GREATHER );
					operacao = OP_GREATHER;
					break;
				case LESS_EQUALS:
					casaToken( Token.LESS_EQUALS );
					operacao = OP_LESS_EQUALS;
					break;
				case GREATHER_EQUAL:
					casaToken( Token.GREATHER_EQUAL );
					operacao = OP_GREATHER_EQUAL;
					break;
				case EQUAL:
					casaToken( Token.EQUAL );
					operacao = OP_EQUAL;
					break;
				case DIFFERENT:
					casaToken( Token.DIFFERENT );
					operacao = OP_DIFFERENT;
					break;
				default:
					break;
			}
			
			RegistroLexico exp1 = new RegistroLexico();
			EXPS(exp1);
			
			if( exp.getTipo() != exp1.getTipo() ){
				System.out.println( pos.getLineNumber()+":tipos incompativeis");
				throw new Exception();
			}else if( exp.getTipo() == Tipo.RELACIONAL ) {
				System.out.println( pos.getLineNumber()+":tipos incompativeis");
				throw new Exception();
			}else if( exp.getTipo() == Tipo.INTEIRO ) {
				
				if( exp.getTamanho() != 0 && exp1.getTamanho() != 0 ) {
					System.out.println( pos.getLineNumber()+":tipos incompativeis");
					throw new Exception();
				}
				
				codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "carrega EXP.end para regA");
				codigo.mov("bx", "DS:[" + exp1.getEndereco() + "]", "carrega EXP.end para regB");
				codigo.cmp("ax", "bx");
				
				int novoTemp = codigo.novoTemp(2);
				int rotulo0 = codigo.novoRotulo();
				int rotulo1 = codigo.novoRotulo();

				switch( operacao ){
					case OP_LESS:
						codigo.jge(rotulo0, "Jump se falso");
						break;
					case OP_GREATHER:
						codigo.jle(rotulo0, "Jump se falso");
						break;
					case OP_LESS_EQUALS:
						codigo.jg(rotulo0, "Jump se falso");
						break;
					case OP_GREATHER_EQUAL:
						codigo.jl(rotulo0, "Jump se falso");
						break;
					case OP_EQUAL:
						codigo.jne(rotulo0, "Jump se falso");
						break;
					case OP_DIFFERENT:
						codigo.je(rotulo0, "Jump se falso");
						break;
					default:
						System.out.println("Opera��o n�o esperada");
						throw new Exception();
				}

				codigo.mov("ax", "1", "true");
				codigo.jmp(rotulo1, "pula para o final do if");
				codigo.rotulo(rotulo0);
				codigo.mov("ax", "0", "false");
				codigo.rotulo(rotulo1);
				codigo.mov("DS:[" + novoTemp + "]", "ax", "salva valor da exp na memoria");
				exp.setEndereco( novoTemp );
				
			}else if( exp.getTipo() == Tipo.CARACTERE ) {
				
				if( exp.getToken() == Token.ID && exp1.getToken() == Token.ID ) {
					
					if( exp.getTamanho() == 0 && exp1.getTamanho() == 0 )// caractere
					{
					
						codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "carrega EXP.end para regA");
						codigo.mov("ah", "0");
						codigo.mov("bx", "DS:[" + exp1.getEndereco() + "]", "carrega EXP.end para regB");
						codigo.mov("bh", "0");
						codigo.cmp("ax", "bx");
						
						int novoTemp = codigo.novoTemp(2);
						int rotulo0 = codigo.novoRotulo();
						int rotulo1 = codigo.novoRotulo();

						switch( operacao ){
							case OP_LESS:
								codigo.jge(rotulo0, "Jump se falso");
								break;
							case OP_GREATHER:
								codigo.jle(rotulo0, "Jump se falso");
								break;
							case OP_LESS_EQUALS:
								codigo.jg(rotulo0, "Jump se falso");
								break;
							case OP_GREATHER_EQUAL:
								codigo.jl(rotulo0, "Jump se falso");
								break;
							case OP_EQUAL:
								codigo.jne(rotulo0, "Jump se falso");
								break;
							case OP_DIFFERENT:
								codigo.je(rotulo0, "Jump se falso");
								break;
							default:
								System.out.println("Opera��o n�o esperada");
								throw new Exception();
						}

						codigo.mov("ax", "1", "true");
						codigo.jmp(rotulo1, "pula para o final do if");
						codigo.rotulo(rotulo0);
						codigo.mov("ax", "0", "false");
						codigo.rotulo(rotulo1);
						codigo.mov("DS:[" + novoTemp + "]", "ax", "salva valor da exp na memoria");
						exp.setEndereco( novoTemp );
						
					}else // vetor de char
					{
					
						if( operacao != OP_EQUAL ) {
							System.out.println( pos.getLineNumber()+":tipos incompativeis");
							throw new Exception();
						}
						
						int novoTemp = codigo.novoTemp(2);
						
						int rotulo0 = codigo.novoRotulo();
						int rotulo1 = codigo.novoRotulo();
						int rotulo2 = codigo.novoRotulo();
						
						codigo.mov("di",""+exp.getEndereco(), "endereco da primeira string");
						codigo.mov("si",""+exp1.getEndereco(), "endereco da segunda string");
						codigo.mov("ax", "1", "strings iguais");
						codigo.rotulo( rotulo0 );
						codigo.mov("bx", "DS:[di]", "carrega caractere para regB");
						codigo.mov("bh","0");
						codigo.mov("cx", "DS:[si]", "carrega caractere para regC");
						codigo.mov("ch","0");
						codigo.cmp("bx","cx");
						codigo.jne(rotulo1, "se char for diferente sao diferentes");
						codigo.cmp("bx","24h", "compara com fim de string");
						codigo.je(rotulo2, "pula para fim do for");
						codigo.cmp("cx", "24h", "compara com fim de string");
						codigo.je(rotulo2,"pula para fim do for");
						codigo.add("di", "1");
						codigo.add("si", "1");
						codigo.jmp(rotulo0);
						codigo.rotulo(rotulo1);
						codigo.mov("ax", "0", "strings diferentes");
						codigo.rotulo(rotulo2);
						
						codigo.mov("DS:[" + novoTemp + "]", "ax", "salva valor da exp na memoria");
						exp.setEndereco( novoTemp );
						
					}

				}else // ALGUEM E' constante
				{
				
					// comparar id( char ) com constante de tamanho 1
					if( ( exp.getTamanho() == 0 && exp1.getTamanho() == 1 ) ||
						( exp.getTamanho() == 1 && exp1.getTamanho() == 0 )) 
					{
					
						codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "carrega EXP.end para regA");
						codigo.mov("ah", "0");
						codigo.mov("bx", "DS:[" + exp1.getEndereco() + "]", "carrega EXP.end para regB");
						codigo.mov("bh", "0");
						codigo.cmp("ax", "bx");
						
						int novoTemp = codigo.novoTemp(2);
						int rotulo0 = codigo.novoRotulo();
						int rotulo1 = codigo.novoRotulo();

						switch( operacao ){
							case OP_LESS:
								codigo.jge(rotulo0, "Jump se falso");
								break;
							case OP_GREATHER:
								codigo.jle(rotulo0, "Jump se falso");
								break;
							case OP_LESS_EQUALS:
								codigo.jg(rotulo0, "Jump se falso");
								break;
							case OP_GREATHER_EQUAL:
								codigo.jl(rotulo0, "Jump se falso");
								break;
							case OP_EQUAL:
								codigo.jne(rotulo0, "Jump se falso");
								break;
							case OP_DIFFERENT:
								codigo.je(rotulo0, "Jump se falso");
								break;
							default:
								System.out.println("Operacao nao esperada");
								throw new Exception();
						}

						codigo.mov("ax", "1", "true");
						codigo.jmp(rotulo1, "pula para o final do if");
						codigo.rotulo(rotulo0);
						codigo.mov("ax", "0", "false");
						codigo.rotulo(rotulo1);
						codigo.mov("DS:[" + novoTemp + "]", "ax", "salva valor da exp na memoria");
						exp.setEndereco( novoTemp );
						
					}else // strings ou constantes maiores que 1
					{
						
						if( operacao != OP_EQUAL ) {
							System.out.println( pos.getLineNumber()+":tipos incompativeis");
							throw new Exception();
						}
						
						int novoTemp = codigo.novoTemp(2);
						
						int rotulo0 = codigo.novoRotulo();
						int rotulo1 = codigo.novoRotulo();
						int rotulo2 = codigo.novoRotulo();
						
						codigo.mov("di",""+exp.getEndereco(), "endereco da primeira string");
						codigo.mov("si",""+exp1.getEndereco(), "endereco da segunda string");
						codigo.mov("ax", "1", "strings iguais");
						codigo.rotulo( rotulo0 );
						codigo.mov("bx", "DS:[di]", "carrega caractere para regB");
						codigo.mov("bh","0");
						codigo.mov("cx", "DS:[si]", "carrega caractere para regC");
						codigo.mov("ch","0");
						codigo.cmp("bx","cx");
						codigo.jne(rotulo1, "se char for diferente sao diferentes");
						codigo.cmp("bx","24h", "compara com fim de string");
						codigo.je(rotulo2, "pula para fim do for");
						codigo.cmp("cx","24h", "compara com fim de string");
						codigo.je(rotulo2, "pula para fim do for");
						codigo.add("di", "1");
						codigo.add("si", "1");
						codigo.jmp(rotulo0);
						codigo.rotulo(rotulo1);
						codigo.mov("ax", "0", "strings diferentes");
						codigo.rotulo(rotulo2);
						
						codigo.mov("DS:[" + novoTemp + "]", "ax", "salva valor da exp na memoria");
						exp.setEndereco( novoTemp );

					}

				}
				
			}
			
			exp.setTipo( Tipo.RELACIONAL );
		}

	}

	public static void EXPS(RegistroLexico exps) throws Exception {

		boolean negarExpressao = false;

		if (token == Token.SUM) {
			casaToken(Token.SUM);
		} else if (token == Token.MINUS) {
			casaToken(Token.MINUS);
			negarExpressao = true;
		}

		T(exps);

		// negar o valor de T1 se necessario
		if (negarExpressao) {

			if (exps.getTipo() == Tipo.INTEIRO && exps.getTamanho() == 0) {

				// copia E.end para regA
				codigo.mov("ax", "DS:[" + exps.getEndereco() + "]", "copia o valor de E.end para regA");
				// nega ax
				codigo.neg("ax", "nega o valor de regA");

				exps.setEndereco(codigo.novoTemp(2));

				// copia para um novo endereco o novo valor de E.end
				codigo.mov("DS:[" + exps.getEndereco() + "]", "ax", "copia para o temporario o valor negado de E");

			} else {
				System.out.println(pos.getLineNumber() + ":tipos incompativeis.");
				throw new Exception();
			}
		}

		// OBS: olhar a partir daqui se todos os tipos podem entrar
		while (token == Token.SUM || token == Token.MINUS || token == Token.OR) {

			final int SOMA = 0;
			final int SUBTRACAO = 1;
			final int OR = 2;
			int operador = -1;

			switch (token) {
				case SUM:
					casaToken(Token.SUM);
					operador = SOMA;
					break;
				case MINUS:
					casaToken(Token.MINUS);
					operador = SUBTRACAO;
					break;
				case OR:
					casaToken(Token.OR);
					operador = OR;
					break;
				default:
					error();
					break;
			}

			RegistroLexico t2 = new RegistroLexico();
			T(t2);

			if( exps.getTipo() != t2.getTipo() ){
				
				System.out.println(pos.getLineNumber() + ":tipos incompativeis.");
				throw new Exception();

			}else if( operador == OR && ( exps.getTipo() != Tipo.RELACIONAL || t2.getTipo() != Tipo.RELACIONAL ) ){
				
				System.out.println(pos.getLineNumber() + ":tipos incompativeis.");
				throw new Exception();

			}else if( exps.getTipo() == Tipo.RELACIONAL || t2.getTipo() == Tipo.RELACIONAL ){

				System.out.println(pos.getLineNumber() + ":tipos incompativeis.");
				throw new Exception();

			}

			// carrega o conteudo de E.end para regA
			codigo.mov("ax", "DS:[" + exps.getEndereco() + "]", "carrega E.end para regA");
			// carrega o conteudo de t2.end para regB
			codigo.mov("bx", "DS:[" + t2.getEndereco() + "]", "carrega T2.end para regB");

			switch (operador) {
			case SOMA:
				codigo.add("ax", "bx", "soma regA com regB");
				break;
			case SUBTRACAO:
				codigo.sub("ax", "bx", "subtrai regA de regB");
				break;
			case OR:
				int rotulo0 = codigo.novoRotulo();
				int rotulo1 = codigo.novoRotulo();
				int rotulo2 = codigo.novoRotulo();
				codigo.cmp("ax", "1");
				codigo.je( rotulo0 );
				codigo.cmp("bx","1");
				codigo.je( rotulo0);
				codigo.jmp(rotulo1);
				codigo.rotulo( rotulo0 );
				codigo.mov("ax", "1", "resultado verdadeiro");
				codigo.jmp(rotulo2, "pula para fim do if");
				codigo.rotulo(rotulo1);
				codigo.mov("ax","0","resultado falso");
				codigo.rotulo(rotulo2);
				break;
			default:
				System.out.println("Operador invalido");
				throw new Exception();
			}

			// E.end = NovoTemp
			exps.setEndereco(codigo.novoTemp(2));

			// guarda resultado de regA em E.end
			codigo.mov("DS:[" + exps.getEndereco() + "]", "ax", "E.end recebe o valor de regA");
		}

	}

	public static void T(RegistroLexico t) throws Exception {
		F(t);

		while (token == Token.DIVIDE || token == Token.MULTIPLY || token == Token.AND || token == Token.MOD) {

			final int DIVISAO = 0;
			final int MULTIPLICACAO = 1;
			final int AND = 2;
			final int MOD = 3;
			int operador = -1;

			switch (token) {
				case DIVIDE:
					casaToken(Token.DIVIDE);
					operador = DIVISAO;
					break;
				case MULTIPLY:
					casaToken(Token.MULTIPLY);
					operador = MULTIPLICACAO;
					break;
				case AND:
					casaToken(Token.AND);
					operador = AND;
					break;
				case MOD:
					casaToken(Token.MOD);
					operador = MOD;
					break;
				default:
					System.out.println("Operador invalido");
					break;
			}

			RegistroLexico f2 = new RegistroLexico();
			F(f2);

			if( t.getTipo() != f2.getTipo() ){
				
				System.out.println(pos.getLineNumber() + ":tipos incompativeis.");
				throw new Exception();

			}else if( operador == AND && ( t.getTipo() != Tipo.RELACIONAL || f2.getTipo() != Tipo.RELACIONAL ) ){
				
				System.out.println(pos.getLineNumber() + ":tipos incompativeis.");
				throw new Exception();

			}else if( t.getTipo() == Tipo.RELACIONAL || f2.getTipo() == Tipo.RELACIONAL ){

				System.out.println(pos.getLineNumber() + ":tipos incompativeis.");
				throw new Exception();

			}

			// carrega o conteudo de T.end no regA
			codigo.mov("ax", "DS:[" + t.getEndereco() + "]", "carrega T.end para regA");
			// carrega o conteudo de F2.end no regB
			codigo.mov("bx", "DS:[" + f2.getEndereco() + "]", "carrega F2.end para regA");

			switch (operador) {
			case DIVISAO:
				codigo.mov("dx", "0", "estende 32bits p/ div.");
				codigo.idiv("bx", "divide ax por bx");
				break;
			case MULTIPLICACAO:
				codigo.imul("bx", "multiplica ax por bx");
				break;
			case AND:
				codigo.imul("bx", "O operador and sera' a multiplicacao de ax com bx");
				break;
			case MOD:
				codigo.mov("dx", "0", "estende 32bits p/ div.");
				codigo.idiv("bx", "divide ax por bx para fazer o mod");
				codigo.mov("ax", "dx", "Copia o resto da divisao para ax");
				break;
			}

			// OBS: olhar esse tamanho
			// T.end = NovoTemps
			t.setEndereco(codigo.novoTemp(2));
			// Guarda resultado de regA em T.end
			codigo.mov("DS:[" + t.getEndereco() + "]", "ax", "Guarda resultado de regA em T.end");
		}
	}

	public static void F(RegistroLexico f) throws Exception {

		if( token == Token.OPEN_PARENTHESIS ){

			casaToken(Token.OPEN_PARENTHESIS);
			EXP(f);
			casaToken(Token.CLOSE_PARENTHESIS);

		}else if( token == Token.NOT ){

			casaToken(Token.NOT);
			EXP(f);

			if( f.getTipo() != Tipo.INTEIRO && f.getTamanho() != 0 ){
				System.out.println(pos.getLineNumber() + ":tipos incompativeis.");
				throw new Exception();
			}

			codigo.mov("ax", "DS:["+f.getEndereco()+"]", "Copia valor de F.end");
			codigo.neg("ax", "nego ax");
			int temp = codigo.novoTemp(2);
			codigo.mov("DS:["+temp+"]", "ax", "copia o valor de ax negado para o novoTemp");
			f.setEndereco( temp );

		}else if( token == Token.CONST ){

			f.setTipo(result.getTipo());
			f.setToken(Token.CONST);

			if (f.getTipo() == Tipo.INTEIRO) {

				int valor;

				// F.end = NovoTemp
				f.setEndereco(codigo.novoTemp(2));
				f.setTamanho(0);
				valor = Integer.parseInt(result.getLexema());

				// mov regA, imed
				codigo.mov("ax", "" + valor, "Valor a ser copiado para o temporario");
				// mov F.end, regA
				codigo.mov("DS:[" + f.getEndereco() + "]", "ax", "copia constante para temporario");
			} else {
				
				int tamanho = result.getTamanho() -2 ;

				String valor = result.getLexema().substring(1, tamanho+1 );
				f.setTamanho( tamanho );
				f.setLexema( valor );
				
				// se nao for string
				if( valor.charAt(tamanho-1) != '$' ) {
					tamanho += 1;
					valor += '$';
				}
				
				f.setEndereco(codigo.novaVariavel( tamanho ));
				// copia String para o seu temporario
				codigo.stringToTemp(valor, "const string em " + f.getEndereco() + "");
			}
			
			casaToken(Token.CONST);

		}else if( token == Token.ID ){

			RegistroLexico id = variaveis.getVar(result.getLexema());
			if (id == null) {
				System.out.println("ERRO: Variavel [" + result.getLexema() + "] nao foi declarada.");
				throw new Exception("");
			}
			f.setClasse(id.getClasse());
			// F.tipo = id.tipo
			f.setTipo(id.getTipo());
			// F.end = id.end
			f.setEndereco(id.getEndereco());
			// F.tam = id.tam
			f.setTamanho(id.getTamanho());
			// OBS: olhar esse token aqui
			f.setToken(Token.ID);

			casaToken(Token.ID);

			if (token == Token.OPEN_BRACKET) {
				
				casaToken(Token.OPEN_BRACKET);

				RegistroLexico exp = new RegistroLexico();
				EXP( exp );

				if( exp.getTipo() != Tipo.INTEIRO && exp.getTamanho() != 0 ){
					System.out.println(pos.getLineNumber() + ":tipos incompativeis.");
					throw new Exception();
				}
				
				f.setTamanho(0);

				if( id.getTipo() == Tipo.INTEIRO ) {
					codigo.mov("di", "DS:["+exp.getEndereco()+"]");
					codigo.add("di", "di");
					codigo.add("di", ""+id.getEndereco());
					codigo.mov("ax", "DS:[di]");
					f.setEndereco( codigo.novoTemp(2) );
					codigo.mov("DS:["+f.getEndereco()+"]", "ax");
				}else if( id.getTipo() == Tipo.CARACTERE ) {
					codigo.mov("di", "DS:["+exp.getEndereco()+"]");
					codigo.add("di", ""+id.getEndereco());
					codigo.mov("ax", "DS:[di]");
					f.setEndereco( codigo.novoTemp(1) );
					codigo.mov("DS:["+f.getEndereco()+"]", "ax");
				}else {
					System.out.println("ERRO");
					throw new Exception();
				}
				
				casaToken(Token.CLOSE_BRACKET);
			}
			
		}else{
			error();
		}
		
	}

}
