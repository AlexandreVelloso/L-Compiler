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

		RegistroLexico s = new RegistroLexico();

		while (token == Token.FOR || token == Token.IF || token == Token.READLN || token == Token.WRITE
				|| token == Token.WRITELN || token == Token.ID || token == Token.SEMICOLON) {
			// COMANDO
			COMANDO(s);
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
		} else if (token == Token.CHAR) {
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
				System.out.println(pos.getLineNumber() + ":tamanho do vetor excede o máximo permitido. ");
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

				if ((tipo == Tipo.INTEIRO && tamanho > 2000) || (tipo == Tipo.CARACTERE && tamanho > 4000)) {
					System.out.println(pos.getLineNumber() + ":tamanho do vetor excede o máximo permitido. ");
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
				System.out.println(pos.getLineNumber() + ":tipos incompat�veis");
				throw new Exception();
			}

			codigo.adicionarVariavel(id, result.getLexema());
		}

		casaToken(Token.CONST);
	}

	public static void CONSTANTE() throws Exception {
		Classe classe = Classe.CONSTANTE;
		int tamanho = 0;
		int valor = -1;

		casaToken(Token.FINAL);

		RegistroLexico id = result.clone();
		id.setTamanho(tamanho);
		id.setClasse(classe);

		casaToken(Token.ID);
		casaToken(Token.EQUAL);

		id.setTipo(result.getTipo());

		boolean isNegativo = false;

		if (token == Token.SUM) {
			casaToken(Token.SUM);
		} else if (token == Token.MINUS) {
			casaToken(Token.MINUS);
			isNegativo = true;
		}

		if (id.getTipo() == Tipo.INTEIRO) {
			valor = Integer.parseInt(result.getLexema());
			if (isNegativo) {
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

	public static void COMANDO(RegistroLexico cmd) throws Exception {

		RegistroLexico id;
		RegistroLexico temp = new RegistroLexico();

		if (null != token) {
			switch (token) {
			case FOR:
				casaToken(Token.FOR);
				id = variaveis.getVar(result.getLexema());

				if (id == null) {
					System.out.println("ERRO: Variavel [" + result.getLexema() + "] nao foi declarada.");
					throw new Exception("");
				}

				casaToken(Token.ID);
				casaToken(Token.ATTR);
				EXP(cmd);

				id.setEndereco(cmd.getEndereco());

				casaToken(Token.TO);
				EXP(cmd);

				temp.setEndereco(cmd.getEndereco());

				int step;
				if (token == Token.STEP) {
					casaToken(Token.STEP);

					step = Integer.parseInt(result.getLexema());

					casaToken(Token.CONST);
				} else {
					step = 1;
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
				break;
			case IF:

				int rotulo0 = codigo.novoRotulo();
				int rotulo1 = codigo.novoRotulo();

				casaToken(Token.IF);
				EXP(cmd);

				codigo.comentario("Comeco do if");

				if (cmd.getTipo() != Tipo.RELACIONAL) {
					System.out.println(pos.getLineNumber() + ":tipos incompat�veis.");
					throw new Exception();
				} else {

					codigo.mov("ax", "DS:[" + cmd.getEndereco() + "]", "CMD.end = EXP.end");
					codigo.cmp("ax", "0");
					codigo.je(rotulo0, "pula se falso");
				}

				casaToken(Token.THEN);
				BLOCO();
				codigo.jmp(rotulo1, "pula para o fim do if");
				codigo.rotulo(rotulo0);

				if (token == Token.ELSE) {

					casaToken(Token.ELSE);
					BLOCO();
					codigo.comentario("fim do else");
				}

				codigo.comentario("Fim de if");
				codigo.rotulo(rotulo1);

				break;
			case READLN:
				casaToken(Token.READLN);
				casaToken(Token.OPEN_PARENTHESIS);
				id = variaveis.getVar(result.getLexema());

				if (id == null) {
					System.out.println("ERRO: Variavel [" + result.getLexema() + "] nao foi declarada.");
					throw new Exception("");
				}

				casaToken(Token.ID);

				if (token == Token.OPEN_BRACKET) {
					casaToken(Token.OPEN_BRACKET);

					if (id.getTamanho() == 0) {
						// erro
					}

					int endTemp;
					if (id.getTipo() == Tipo.INTEIRO) {
						endTemp = codigo.novoTemp(2);
					} else {
						endTemp = codigo.novoTemp(1);
					}

					EXP(cmd);

					if ((cmd.getTipo() == Tipo.INTEIRO && cmd.getTamanho() == 0) == false) {
						System.out.println(pos.getLineNumber() + ":tipos incompat�veis.");
						throw new Exception();
					}

					codigo.mov("ax", "DS:[" + cmd.getEndereco() + "]", "carrega o conteudo de E.end para regA");

					if (cmd.getTipo() == Tipo.INTEIRO) {
						codigo.add("ax", "ax");
					}

					codigo.mov("di", "ax");
					codigo.add("di", "" + id.getEndereco(), "soma o deslocamento");
					codigo.mov("DS:[" + endTemp + "]", "di", "salva o valor do deslocamento em temp");

					if (id.getTipo() == Tipo.INTEIRO) {
						codigo.readInt(id.getEndereco(), endTemp);
					} else {
						codigo.readChar(endTemp);
					}

					casaToken(Token.CLOSE_BRACKET);
				} else {
					int tamanho;

					if (id.getTipo() == Tipo.INTEIRO) {

						codigo.readInt(id.getEndereco());
					} else {

						if (id.getTamanho() < 255) {
							tamanho = id.getTamanho();
							// olhar isso
							if (tamanho == 0)
								tamanho = 1;
						} else {
							tamanho = 255;
						}

						codigo.readString(id.getEndereco(), tamanho);
					}
				}

				casaToken(Token.CLOSE_PARENTHESIS);
				casaToken(Token.SEMICOLON);
				break;
			case WRITE:
				casaToken(Token.WRITE);
				casaToken(Token.OPEN_PARENTHESIS);
				EXP(cmd);

				if (cmd.getTipo() == Tipo.INTEIRO) {
					codigo.mostrarInt(cmd.getEndereco());
				} else {

					if (cmd.getToken() == Token.ID && cmd.getTamanho() == 0) {
						codigo.mostrarChar(cmd.getEndereco());
					} else {
						codigo.mostrarString(cmd.getEndereco());
					}
				}

				while (token == Token.COMMA) {
					casaToken(Token.COMMA);
					EXP(cmd);

					if (cmd.getTipo() == Tipo.INTEIRO) {
						codigo.mostrarInt(cmd.getEndereco());
					} else {
						if (cmd.getToken() == Token.ID && cmd.getTamanho() == 0) {
							codigo.mostrarChar(cmd.getEndereco());
						} else {
							codigo.mostrarString(cmd.getEndereco());
						}
					}
				}

				casaToken(Token.CLOSE_PARENTHESIS);
				casaToken(Token.SEMICOLON);
				break;
			case WRITELN:
				casaToken(Token.WRITELN);
				casaToken(Token.OPEN_PARENTHESIS);
				EXP(cmd);
				
				if (cmd.getTipo() == Tipo.INTEIRO) {
					codigo.mostrarInt(cmd.getEndereco());
				} else {
					if (cmd.getToken() == Token.ID && cmd.getTamanho() == 0) {
						codigo.mostrarChar(cmd.getEndereco());
					} else {
						codigo.mostrarString(cmd.getEndereco());
					}
				}

				while (token == Token.COMMA) {
					casaToken(Token.COMMA);
					EXP(cmd);

					if (cmd.getTipo() == Tipo.INTEIRO) {
						codigo.mostrarInt(cmd.getEndereco());
					} else {

						if (cmd.getToken() == Token.ID && cmd.getTamanho() == 0) {
							codigo.mostrarChar(cmd.getEndereco());
						} else {
							codigo.mostrarString(cmd.getEndereco());
						}
					}
				}

				codigo.quebrarLinha();

				casaToken(Token.CLOSE_PARENTHESIS);
				casaToken(Token.SEMICOLON);
				break;
			case ID:

				// pega a variavel da memoria
				id = variaveis.getVar(result.getLexema());

				if (id == null) {
					System.out.println("ERRO: Variavel [" + result.getLexema() + "] nao foi declarada.");
					throw new Exception("");
				}

				casaToken(Token.ID);
				casaToken(Token.ATTR);
				EXP(cmd);

				if (id.getTipo() == cmd.getTipo()) {

					if (id.getClasse() == Classe.CONSTANTE) {
						System.out.println(pos.getLineNumber() + ":classe de identificador incompat�vel ["
								+ id.getLexema() + "].");
						throw new Exception();
					}

					// Copia o valor de CMD.end para regA
					codigo.mov("ax", "DS:[" + cmd.getEndereco() + "]", "Copia o valor de E.end para regA");
					// Copia o valor de regA para ID.end
					codigo.mov("DS:[" + id.getEndereco() + "]", "ax", "Copia o valor de regA para ID.end");

				} else {
					System.out.println("ERRO: tipos incompativeis");
					throw new Exception();
				}

				casaToken(Token.SEMICOLON);
				break;
			case SEMICOLON:
				casaToken(Token.SEMICOLON);
				break;
			default:

				break;
			}
		}

	}

	public static void BLOCO() throws Exception {

		if (token == Token.BEGIN) {
			casaToken(Token.BEGIN);
			
			while (token == Token.FOR || token == Token.IF || token == Token.READLN || token == Token.WRITE
					|| token == Token.WRITELN || token == Token.ID || token == Token.SEMICOLON) {
				
				RegistroLexico bloco = new RegistroLexico();
				COMANDO(bloco);
			}
			
			casaToken(Token.END);
		} else {
			
			RegistroLexico bloco = new RegistroLexico();
			COMANDO(bloco);
		}
	}

	public static void EXP(RegistroLexico exp) throws Exception {

		EXPS(exp);

		RegistroLexico exp1 = new RegistroLexico();
		int novoTemp = -1;
		int rotulo0 = -1;
		int rotulo1 = -1;

		if (null != token) {
			switch (token) {
			case LESS:
				casaToken(Token.LESS);
				EXPS(exp1);

				novoTemp = codigo.novoTemp(2);
				rotulo0 = codigo.novoRotulo();
				rotulo1 = codigo.novoRotulo();

				// verificar tipos antes
				codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "carrega EXP.end para regA");
				codigo.mov("bx", "DS:[" + exp1.getEndereco() + "]", "carrega EXP.end para regB");
				codigo.cmp("ax", "bx");
				codigo.jge(rotulo0, "Jump se falso");
				codigo.mov("ax", "1", "true");
				codigo.jmp(rotulo1, "pula para o final do if");
				codigo.rotulo(rotulo0);
				codigo.mov("ax", "0", "false");
				codigo.rotulo(rotulo1);
				codigo.mov("DS:[" + novoTemp + "]", "ax", "salva valor da exp na memoria");

				exp.setEndereco(novoTemp);
				exp.setTipo(Tipo.RELACIONAL);
				break;
			case GREATHER:
				casaToken(Token.GREATHER);
				EXPS(exp1);

				novoTemp = codigo.novoTemp(2);
				rotulo0 = codigo.novoRotulo();
				rotulo1 = codigo.novoRotulo();

				// verificar tipos antes
				codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "carrega EXP.end para regA");
				codigo.mov("bx", "DS:[" + exp1.getEndereco() + "]", "carrega EXP.end para regB");
				codigo.cmp("ax", "bx");
				codigo.jle(rotulo0, "Jump se falso");
				codigo.mov("ax", "1", "true");
				codigo.jmp(rotulo1, "pula para o final do if");
				codigo.rotulo(rotulo0);
				codigo.mov("ax", "0", "false");
				codigo.rotulo(rotulo1);
				codigo.mov("DS:[" + novoTemp + "]", "ax", "salva valor da exp na memoria");

				exp.setEndereco(novoTemp);
				exp.setTipo(Tipo.RELACIONAL);
				break;
			case LESS_EQUALS:
				casaToken(Token.LESS_EQUALS);
				EXPS(exp1);
				novoTemp = codigo.novoTemp(2);
				rotulo0 = codigo.novoRotulo();
				rotulo1 = codigo.novoRotulo();

				// verificar tipos antes
				codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "carrega EXP.end para regA");
				codigo.mov("bx", "DS:[" + exp1.getEndereco() + "]", "carrega EXP.end para regB");
				codigo.cmp("ax", "bx");
				codigo.jg(rotulo0, "Jump se falso");
				codigo.mov("ax", "1", "true");
				codigo.jmp(rotulo1, "pula para o final do if");
				codigo.rotulo(rotulo0);
				codigo.mov("ax", "0", "false");
				codigo.rotulo(rotulo1);
				codigo.mov("DS:[" + novoTemp + "]", "ax", "salva valor da exp na memoria");

				exp.setEndereco(novoTemp);
				exp.setTipo(Tipo.RELACIONAL);
				break;
			case GREATHER_EQUAL:
				casaToken(Token.GREATHER_EQUAL);
				EXPS(exp1);
				novoTemp = codigo.novoTemp(2);
				rotulo0 = codigo.novoRotulo();
				rotulo1 = codigo.novoRotulo();

				// verificar tipos antes
				codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "carrega EXP.end para regA");
				codigo.mov("bx", "DS:[" + exp1.getEndereco() + "]", "carrega EXP.end para regB");
				codigo.cmp("ax", "bx");
				codigo.jl(rotulo0, "Jump se falso");
				codigo.mov("ax", "1", "true");
				codigo.jmp(rotulo1, "pula para o final do if");
				codigo.rotulo(rotulo0);
				codigo.mov("ax", "0", "false");
				codigo.rotulo(rotulo1);
				codigo.mov("DS:[" + novoTemp + "]", "ax", "salva valor da exp na memoria");

				exp.setEndereco(novoTemp);
				exp.setTipo(Tipo.RELACIONAL);
				break;
			case EQUAL:
				casaToken(Token.EQUAL);
				EXPS(exp1);

				novoTemp = codigo.novoTemp(2);
				rotulo0 = codigo.novoRotulo();
				rotulo1 = codigo.novoRotulo();

				// verificar tipos antes
				codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "carrega EXP.end para regA");
				codigo.mov("bx", "DS:[" + exp1.getEndereco() + "]", "carrega EXP.end para regB");
				codigo.cmp("ax", "bx");
				codigo.jne(rotulo0, "Jump se falso");
				codigo.mov("ax", "1", "true");
				codigo.jmp(rotulo1, "pula para o final do if");
				codigo.rotulo(rotulo0);
				codigo.mov("ax", "0", "false");
				codigo.rotulo(rotulo1);
				codigo.mov("DS:[" + novoTemp + "]", "ax", "salva valor da exp na memoria");

				exp.setEndereco(novoTemp);
				exp.setTipo(Tipo.RELACIONAL);
				break;
			case DIFFERENT:
				casaToken(Token.DIFFERENT);
				EXPS(exp1);

				novoTemp = codigo.novoTemp(2);
				rotulo0 = codigo.novoRotulo();
				rotulo1 = codigo.novoRotulo();

				// verificar tipos antes
				codigo.mov("ax", "DS:[" + exp.getEndereco() + "]", "carrega EXP.end para regA");
				codigo.mov("bx", "DS:[" + exp1.getEndereco() + "]", "carrega EXP.end para regB");
				codigo.cmp("ax", "bx");
				codigo.je(rotulo0, "Jump se falso");
				codigo.mov("ax", "1", "true");
				codigo.jmp(rotulo1, "pula para o final do if");
				codigo.rotulo(rotulo0);
				codigo.mov("ax", "0", "false");
				codigo.rotulo(rotulo1);
				codigo.mov("DS:[" + novoTemp + "]", "ax", "salva valor da exp na memoria");

				exp.setEndereco(novoTemp);
				exp.setTipo(Tipo.RELACIONAL);
				break;
			default:
				return;
			}
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

			if (null != token) {
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
					break;
				}
			}

			RegistroLexico t2 = new RegistroLexico();

			T(t2);

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
			}

			// E.end = NovoTemp
			exps.setEndereco(codigo.novoTemp(2));

			// guarda resultado de regA em E.end
			codigo.mov("DS:[" + exps.getEndereco() + "]", "ax", "E.end recebe o valor de regA");
		}

	}

	public static void T(RegistroLexico t) throws Exception {
		F(t);

		// OBS: olhar se todos os tipos vao passar daqui
		while (token == Token.DIVIDE || token == Token.MULTIPLY || token == Token.AND || token == Token.MOD) {

			final int DIVISAO = 0;
			final int MULTIPLICACAO = 1;
			final int AND = 2;
			final int MOD = 3;
			int operador = -1;

			if (null != token) {
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
					break;
				}
			}

			RegistroLexico f2 = new RegistroLexico();
			F(f2);

			// carrega o conteudo de T.end no regA
			codigo.mov("ax", "DS:[" + t.getEndereco() + "]", "carrega T.end para regA");
			// carrega o conteudo de F2.end no regB
			codigo.mov("bx", "DS:[" + f2.getEndereco() + "]", "carrega F2.end para regA");

			switch (operador) {
			case DIVISAO:
				codigo.idiv("bx", "divide ax por bx");
				break;
			case MULTIPLICACAO:
				codigo.imul("bx", "multiplica ax por bx");
				break;
			case AND:
				codigo.imul("bx", "O operador and sera' a multiplicacao de ax com bx");
				break;
			case MOD:
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

		if (null == token) {
			// OLHAR
			error();
		} else
			switch (token) {
			case OPEN_PARENTHESIS:
				casaToken(Token.OPEN_PARENTHESIS);
				EXP(f);
				casaToken(Token.CLOSE_PARENTHESIS);
				break;
			case NOT:
				casaToken(Token.NOT);
				F(f);
				codigo.mov("ax", "DS:["+f.getEndereco()+"]", "Copia valor de F.end");
				codigo.neg("ax", "nego ax");
				int temp = codigo.novoTemp(2);
				codigo.mov("DS:["+temp+"]", "ax", "copia o valor de ax negado para o novoTemp");
				f.setEndereco( temp );
				break;
			case CONST:
				f.setTipo(result.getTipo());
				f.setClasse(result.getClasse());
				f.setTamanho(0);// eu acho que aqui sempre sera 0
				f.setToken(Token.CONST);
				if (f.getTipo() == Tipo.INTEIRO) {

					int valor;

					// F.end = NovoTemp
					f.setEndereco(codigo.novoTemp(2));
					valor = Integer.parseInt(result.getLexema());

					// mov regA, imed
					codigo.mov("ax", "" + valor, "Valor a ser copiado para o temporario");
					// mov F.end, regA
					codigo.mov("DS:[" + f.getEndereco() + "]", "ax", "copia constante para temporario");
				} else {

					result.setTamanho(result.getLexema().length() - 2);

					// F.end = NovoTemp
					f.setEndereco(codigo.novaVariavel(result.getTamanho() + 1));

					String valor = result.getLexema().substring(1, result.getTamanho() + 1);

					// copia String para o seu temporario
					codigo.stringToTemp(valor, "const string em " + f.getEndereco() + "");
				}
				casaToken(Token.CONST);
				break;
			case ID:
				RegistroLexico id = variaveis.getVar(result.getLexema());
				if (id == null) {
					System.out.println("ERRO: Variavel [" + result.getLexema() + "] nao foi declarada.");
					throw new Exception("");
				}
				f.setClasse(result.getClasse());
				// F.end = id.end
				f.setEndereco(id.getEndereco());
				// F.tipo = id.tipo
				f.setTipo(id.getTipo());
				// F.tam = id.tam
				f.setTamanho(id.getTamanho());
				f.setToken(Token.ID);
				casaToken(Token.ID);
				
				boolean indice = false;
				RegistroLexico f1 = new RegistroLexico();

				if (token == Token.OPEN_BRACKET) {
					indice = true;
					
					casaToken(Token.OPEN_BRACKET);
					EXP(f1);
					
					if( f1.getTipo() == Tipo.INTEIRO ) {
						f.setTamanho(0);
					}else if( f1.getTipo() == Tipo.CARACTERE ) {
						f.setTamanho(0);
					}else {	
						System.out.println("ERRO");
						throw new Exception();
					}
					
					casaToken(Token.CLOSE_BRACKET);
				}
				
				if( indice ) {
					
					if( id.getTipo() == Tipo.INTEIRO ) {
						codigo.mov("di", "DS:["+f1.getEndereco()+"]");
						codigo.add("di", "di");
						codigo.add("di", ""+id.getEndereco());
						codigo.mov("ax", "DS:[di]");
						f.setEndereco( codigo.novoTemp(2) );
						codigo.mov("DS:["+f.getEndereco()+"]", "ax");
					}else if( id.getTipo() == Tipo.CARACTERE ) {
						codigo.mov("di", "DS:["+f1.getEndereco()+"]");
						codigo.add("di", ""+id.getEndereco());
						codigo.mov("ax", "DS:[di]");
						f.setEndereco( codigo.novoTemp(1) );
						codigo.mov("DS:["+f.getEndereco()+"]", "ax");
					}else {
						System.out.println("ERRO");
						throw new Exception();
					}
					
				}

				break;
			default:
				// OLHAR
				error();
				break;
			}
	}

}
