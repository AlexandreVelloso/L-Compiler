package semantico;

import enums.Tipo;
import lexico.RegistroLexico;
import util.FILE;

public class GeradorCodigo {

	private static GeradorCodigo instance;
	private FILE arqAsm = new FILE(FILE.OUTPUT, "C:\\Users\\alexandre\\Desktop\\MASM\\arquivo.asm");
	private static int contadorTemp = 0;
	private int contadorVar = 0x4000;
	private int contadorRotulo = 1;

	// contador auxiliar, somente para debug do codigo assembly
	private int contadorNumVars = 0;

	private GeradorCodigo() {

	}

	public static GeradorCodigo getInstance() {
		if (instance == null) {
			instance = new GeradorCodigo();
		}

		return instance;
	}

	public void inicioASM() {

		arqAsm.println("sseg SEGMENT STACK\t; inicio seg. pilha\r\n" + "	byte 400h DUP(?)\r\n"
				+ "sseg ENDS\t; fim seg. pilha\r\n" + "\r\n" + "dseg SEGMENT PUBLIC\t; inicio seg. dados\r\n"
				+ "	byte 4000h DUP(?)\t; temporarios");
	}

	public void fimVariaveisASM() {

		arqAsm.println("dseg ENDS\t; fim seg. dados\r\n" + "\r\n" + "cseg SEGMENT PUBLIC\t; inicio do seg. codigo\r\n"
				+ "	ASSUME CS:cseg, DS:dseg\r\n\r\n" + "_strt:\t; inicio do programa\n" + "\tmov ax, dseg\r\n"
				+ "\tmov ds, ax\r\n");
	}

	public void fimASM() {
		arqAsm.println("\tmov ah, 4Ch			; termina o programa\r\n" + "\tint 21h	\r\n"
				+ "cseg ENDS				; fim seg. codigo\r\n" + "\r\n" + "END _strt");

		arqAsm.close();
	}

	public int novoTemp(int tamanho) {
		int aux = contadorTemp;
		contadorTemp += tamanho;
		return aux;
	}

	public int novaVariavel(int tamanho) {

		// auxiliar para debug do codigo assembly
		contadorNumVars++;

		int aux = contadorVar;
		contadorVar += tamanho;
		return aux;

	}

	public int novoRotulo() {
		int aux = contadorRotulo;
		contadorRotulo++;
		return aux;
	}

	public void mostrarInt(int adress) {

		int rotulo0 = novoRotulo();
		int rotulo1 = novoRotulo();
		int rotulo2 = novoRotulo();
		
		arqAsm.println("\r\n;Mostrar na tela\r\n");
		arqAsm.println("\tmov di, 4000h\t;end. string temporaria"); // OBS: Eu crio um novo rotulo e uso ele para minha
																	// string temporaria?
		arqAsm.println("\tmov cx,0\t;contador");
		// move numero para regA
		arqAsm.println("\tmov ax, DS:[" + adress + "]\t;move inteiro para regA");
		// compara com 0 para se obter o sinal do numero
		arqAsm.println("\tcmp ax,0\t;compara para saber o sinal do numero");
		// se numero positivo, pula
		arqAsm.println("\tjge R" + rotulo0 + "\t;pula se positivo");
		// se nao pular
		arqAsm.println("\tmov bl, 2Dh\t;senao escreve sinal -");
		arqAsm.println("\tmov ds:[di], bl");
		// incrementa o indice
		arqAsm.println("\tadd di, 1\t;incrementa o indice");
		// faz o modulo do numero
		arqAsm.println("\tneg ax\t;modulo do numero");
		// rotulo
		arqAsm.println("\tR" + rotulo0 + ":");
		contadorRotulo++;
		arqAsm.println("\tmov bx,10\t;divisor");
		// rotulo
		arqAsm.println("\tR" + rotulo1 + ":");
		// incrementa o contador
		arqAsm.println("\tadd cx,1\t;incrementa o contador");
		// zera o DX, estende 32bits p/ div.
		// OBS: na divisao, AX recebe o quociente e DX recebe o resto da divisao
		arqAsm.println("\tmov dx,0\t;estende 32bits p/ div.");
		// divide DXAX por BX
		arqAsm.println("\tidiv bx\t;divide DXAX por BX");
		// empilha o valor do resto
		arqAsm.println("\tpush dx\t;empilha valor do resto");
		// verifica se ax igual 0
		arqAsm.println("\tcmp ax, 0\t;verifica se quoc. = 0");
		// se nao igual pula
		arqAsm.println("\tjne R" + rotulo1 + "\t;se nao acabou o numero, loop");
		contadorRotulo++;
		arqAsm.println("\t;depois de acabar o numero, desemp. os valores");
		arqAsm.println("\tR" + rotulo2 + ":");
		arqAsm.println("\tpop dx\t;desempilha valor");
		arqAsm.println("\tadd dx, 30h\t;transforma em caractere");
		arqAsm.println("\tmov DS:[di],dl\t;escreve caractere");
		arqAsm.println("\tadd di,1\t;incrementa base");
		arqAsm.println("\tadd cx, -1\t;decrementa contador");
		arqAsm.println("\tcmp cx,0\t;verifica se a pilha esta vazia");
		arqAsm.println("\tjne R" + rotulo2 + "\t;se nao pilha vazia, loop");
		arqAsm.println("\t;grava fim de string");
		arqAsm.println("\tmov dl, 024h\t;fim de string");
		arqAsm.println("\tmov ds:[di], dl\t;grava '$'");
		arqAsm.println("\t;exibe string");
		arqAsm.println("\tmov dx, 4000h");// OBS: olhar esse endereco
		arqAsm.println("\tmov ah, 09h");
		arqAsm.println("\tint 21h");

		arqAsm.println("\r\n;Fim mostrar na tela\r\n");
	}

	public void mostrarString(int adress) {
		arqAsm.println("\tmov dx, " + adress + "\t; imprime string na tela");
		arqAsm.println("\tmov ah, 09h");
		arqAsm.println("\tint 21h\r\n");
	}

	public void readString( int endereco, int tamanho ) {
		
		int endTemp = novoTemp( tamanho+3 );
		int rotulo0 = novoRotulo();
		int rotulo1 = novoRotulo();
		
		arqAsm.println(";ler do teclado");
		arqAsm.println("\tmov dx, "+endTemp+"\t;endereco do temporario");
		arqAsm.println("\tmov al, "+tamanho+"\t;tamanho do vetor");
		arqAsm.println("\tmov DS:["+endTemp+"], al");
		arqAsm.println("\tmov ah, 0Ah");
		arqAsm.println("\tint 21h\r\n");
		
		quebrarLinha();
		
		arqAsm.println(";atribuicao da string lida para a variavel");
		
		arqAsm.println("\tmov di, "+(endTemp+2)+"\t;endereco primeiro caractere em temp");
		arqAsm.println("\tmov si, "+endereco+"\t;endereco base do vetor");
		arqAsm.println("R"+rotulo0+":");
		arqAsm.println("\tmov al, DS:[di]");
		arqAsm.println("\tcmp al, 13\t;compara com \\n?");
		arqAsm.println("\tje R"+rotulo1);
		arqAsm.println("\tmov DS:[si], al\t;salva caractere");
		arqAsm.println("\tadd di,1");
		arqAsm.println("\tadd si,1");
		arqAsm.println("\tjmp R"+rotulo0);
		arqAsm.println("R"+rotulo1+":");
		arqAsm.println("\tmov al, 24h\t;final de string");
		arqAsm.println("\tmov DS:[si], al");
		
		arqAsm.println(";fim ler do teclado\r\n");
	}
	
	public void mov(String reg1, String reg2, String... comentario) {

		arqAsm.print("\tmov " + reg1 + ", " + reg2);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}

	}

	public void neg(String reg, String... comentario) {
		arqAsm.print("neg " + reg);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario);
		}
	}

	public void add(String reg1, String reg2, String... comentario) {

		arqAsm.print("\tadd " + reg1 + ", " + reg2);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}

	}

	public void sub(String reg1, String reg2, String... comentario) {

		arqAsm.print("sub " + reg1 + ", " + reg2);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario);
		}

	}

	public void imul(String reg, String... comentario) {
		arqAsm.print("imul " + reg);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario);
		}
	}

	public void idiv(String reg, String... comentario) {
		arqAsm.print("idiv " + reg);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario);
		}
	}

	public void rotulo(int numRotulo) {
		arqAsm.println("R" + numRotulo + ":");
	}

	public void jne(int numRotulo, String... comentario) {

		arqAsm.print("jne R" + numRotulo);

		if (comentario.length == 0) {
			arqAsm.println("\r\n");
		} else {
			arqAsm.println("\t;" + comentario[0] + "\r\n");
		}
	}

	public void jle(int numRotulo, String... comentario) {

		arqAsm.print("jle R" + numRotulo);

		if (comentario.length == 0) {
			arqAsm.println("\r\n");
		} else {
			arqAsm.println("\t;" + comentario[0] + "\r\n");
		}
	}

	public void cmp(String reg1, String reg2, String... comentario) {
		arqAsm.print("\tcmp " + reg1 + ", " + reg2);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}
	}

	public void stringToTemp(String valor, String... comentario) {
		/*
		 * dseg SEGMENT PUBLIC ; inicio seg. dados byte "digite seu nome: $" ; const
		 * string em 16644 dseg ENDS ; fim seg. dados
		 */
		arqAsm.println("\r\ndseg SEGMENT PUBLIC");
		arqAsm.print("\tbyte \"" + valor + "$\"");

		if (comentario.length > 0) {
			arqAsm.println("\t;" + comentario[0]);
		} else {
			arqAsm.println("");
		}

		arqAsm.println("dseg ENDS\t; fim seg. dados\r\n");
	}

	public void quebrarLinha() {
		int endereco = novoTemp(3);

		arqAsm.println(";quebra linha");
		arqAsm.println("\tmov ah, 02h");
		arqAsm.println("\tmov dl, 0Dh");
		arqAsm.println("\tint 21h");
		arqAsm.println("\tmov DL, 0Ah");
		arqAsm.println("\tint 21h");
		arqAsm.println(";fim quebra linha\r\n");
	}

	public void adicionarVariavel(RegistroLexico var, int... valor) {

		// apagar todos os varX do codigo, ele serve somente para debug
		int endereco;

		if (valor.length == 0) {

			if (var.getTamanho() == 0) {

				if (var.getTipo() == Tipo.CARACTERE) {
					endereco = novaVariavel(1);
					arqAsm.println("\tbyte ?\t;var. char em " + endereco);
				} else {
					endereco = novaVariavel(2);
					arqAsm.println("\tsword ?\t;var. int em " + endereco);
				}
			} else {

				if (var.getTipo() == Tipo.CARACTERE) {
					endereco = novaVariavel(var.getTamanho());
					arqAsm.println("\tbyte " + var.getTamanho() + " DUP(?)\t;array char em " + endereco);
				} else {
					endereco = novaVariavel(var.getTamanho() * 2);
					arqAsm.println("\tsword " + var.getTamanho() + " DUP(?)\t;array char em " + endereco);
				}
			}

		} else {

			if (var.getTipo() == Tipo.CARACTERE) {
				endereco = novaVariavel(1);
				arqAsm.println("\tbyte " + valor[0] + "\t;var. char em " + endereco);
			} else {
				endereco = novaVariavel(2);
				arqAsm.println("\tsword " + valor[0] + "\t;var. int em " + endereco);
			}
		}

		var.setEndereco(endereco);
	}
}
