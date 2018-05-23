package semantico;

import enums.Tipo;
import lexico.RegistroLexico;
import util.FILE;

public class GeradorCodigo {

	private static GeradorCodigo instance;
	private FILE arqAsm = new FILE(FILE.OUTPUT, "C:\\Users\\alexandre\\Desktop\\MASM\\arquivo.asm");
	private static int contadorTemp = 0;
	private int contadorVar = 0x4000;
	private int contadorRotulo = 0;

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

	public void comentario( String comentario ) {
		arqAsm.println(";"+comentario);
	}
	
	public void mostrarInt(int adress) {

		int rotulo0 = novoRotulo();
		int rotulo1 = novoRotulo();
		int rotulo2 = novoRotulo();
		
		int temp = novoTemp( 3 );// tamanho de 1 inteiro mais o caractere $

		arqAsm.println("\r\n;Mostrar na tela\r\n");
		arqAsm.println("\tmov di, "+temp+"\t;end. string temporaria");
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
		arqAsm.println("\tmov dx, "+temp);
		arqAsm.println("\tmov ah, 09h");
		arqAsm.println("\tint 21h");

		arqAsm.println("\r\n;Fim mostrar na tela\r\n");
	}

	public void mostrarString(int adress) {
		arqAsm.println("\tmov dx, " + adress + "\t; imprime string na tela");
		arqAsm.println("\tmov ah, 09h");
		arqAsm.println("\tint 21h\r\n");
	}

	public void mostrarChar(int adress) {
		int endTemp = novoTemp(2);

		mov("di", "DS:[" + adress + "]", "variavel que contem o caractere");
		mov("DS:[" + endTemp + "]", "di", "copia o caractere para o temporario");
		mov("di", "024h", "fim de string");
		mov("DS:[" + (endTemp + 1) + "]", "di", "copia o fim de string");

		mostrarString(endTemp);
	}

	/*
	Olhar essa funcao, por enquanto ela so vai funcionar para leitura
	de uma posicao de um array, pois ela usa o deslocamento para ser
	o endereco base de leitura
	*/
	public void readInt( int endereco ) {

		int endTemp = novoTemp( 254 );
		int rotulo0 = novoRotulo();
		int rotulo1 = novoRotulo();
		int rotulo2 = novoRotulo();
		
		arqAsm.println(";ler int do teclado");
		arqAsm.println("\tmov dx, "+endTemp+"\t;endereco do temporario");
		arqAsm.println("\tmov al, "+(255)+"\t;tamanho do vetor + 1");
		arqAsm.println("\tmov DS:["+endTemp+"], al");
		arqAsm.println("\tmov ah, 0Ah");
		arqAsm.println("\tint 21h\r\n");
		
		quebrarLinha();
		
		arqAsm.println(";transformar string lida em int");
		
		arqAsm.println("\tmov di, "+(endTemp+2)+"\t;posicao do string");
		arqAsm.println("\tmov ax, 0\t;acumulador");
		arqAsm.println("\tmov cx, 10\t;base decimal");
		arqAsm.println("\tmov dx, 1\t;valor sinal +");
		arqAsm.println("\tmov bh, 0");
		arqAsm.println("\tmov bl, DS:[di]\t;caractere");
		arqAsm.println("\tcmp bx, 2Dh\t;verifica sinal");
		arqAsm.println("\tjne R"+rotulo0+"\t;se nao negativo");
		arqAsm.println("\tmov dx, -1\t;valor sinal -");
		arqAsm.println("\tadd di, 1\t;incrementa base");
		arqAsm.println("\tmov bl, DS:[di]\t;proximo caractere");
		arqAsm.println("R"+rotulo0+":");
		arqAsm.println("\tpush dx\t;empilha sinal");
		arqAsm.println("\tmov dx, 0\t;reg. multiplicacao");
		arqAsm.println("R"+rotulo1+":");
		arqAsm.println("\tcmp bx, 0Dh\t;verifica fim string");
		arqAsm.println("\tje R"+rotulo2);
		arqAsm.println("\timul cx\t;mult. 10");
		arqAsm.println("\tadd bx, -48\t;converte caractere");
		arqAsm.println("\tadd ax, bx\t;soma valor caractere");
		arqAsm.println("\tadd di, 1\t;incrementa base");
		arqAsm.println("\tmov bh, 0");
		arqAsm.println("\tmov bl, ds:[di]\t;proximo caractere");
		arqAsm.println("\tjmp R"+rotulo1+"\t;loop");
		arqAsm.println("R"+rotulo2+":");
		arqAsm.println("\tpop cx\t;desempilha sinal");
		arqAsm.println("\timul cx\t;mult. sinal");
		arqAsm.println("\tmov DS:["+endereco+"], ax	;copia valor de volta para variavel");
		
		arqAsm.println(";fim ler do teclado\r\n");
	}
	
	public void readInt( int endereco, int deslocamento ) {
		int endTemp = novoTemp( 254 );
		int rotulo0 = novoRotulo();
		int rotulo1 = novoRotulo();
		int rotulo2 = novoRotulo();
		
		arqAsm.println(";ler int do teclado");
		arqAsm.println("\tmov dx, "+endTemp+"\t;endereco do temporario");
		arqAsm.println("\tmov al, 255\t;tamanho do vetor");
		arqAsm.println("\tmov DS:["+endTemp+"], al");
		arqAsm.println("\tmov ah, 0Ah");
		arqAsm.println("\tint 21h\r\n");
		
		quebrarLinha();
		
		arqAsm.println(";transformar string lida em int");
		
		arqAsm.println("\tmov di, "+(endTemp+2)+"\t;posicao do string");
		arqAsm.println("\tmov ax, 0\t;acumulador");
		arqAsm.println("\tmov cx, 10\t;base decimal");
		arqAsm.println("\tmov dx, 1\t;valor sinal +");
		arqAsm.println("\tmov bh, 0");
		arqAsm.println("\tmov bl, DS:[di]\t;caractere");
		arqAsm.println("\tcmp bx, 2Dh\t;verifica sinal");
		arqAsm.println("\tjne R"+rotulo0+"\t;se nao negativo");
		arqAsm.println("\tmov dx, -1\t;valor sinal -");
		arqAsm.println("\tadd di, 1\t;incrementa base");
		arqAsm.println("\tmov bl, DS:[di]\t;proximo caractere");
		arqAsm.println("R"+rotulo0+":");
		arqAsm.println("\tpush dx\t;empilha sinal");
		arqAsm.println("\tmov dx, 0\t;reg. multiplicacao");
		arqAsm.println("R"+rotulo1+":");
		arqAsm.println("\tcmp bx, 0Dh\t;verifica fim string");
		arqAsm.println("\tje R"+rotulo2);
		arqAsm.println("\timul cx\t;mult. 10");
		arqAsm.println("\tadd bx, -48\t;converte caractere");
		arqAsm.println("\tadd ax, bx\t;soma valor caractere");
		arqAsm.println("\tadd di, 1\t;incrementa base");
		arqAsm.println("\tmov bh, 0");
		arqAsm.println("\tmov bl, ds:[di]\t;proximo caractere");
		arqAsm.println("\tjmp R"+rotulo1+"\t;loop");
		arqAsm.println("R"+rotulo2+":");
		arqAsm.println("\tpop cx\t;desempilha sinal");
		arqAsm.println("\timul cx\t;mult. sinal");
		mov("bx", "DS:["+deslocamento+"]");
		arqAsm.println("\tmov DS:[bx], ax\t;copia valor de volta para variavel");
		
		arqAsm.println(";fim ler do teclado\r\n");
	}
	
	/*
	Olhar essa funcao, por enquando ela so esta sendo chamada quando for ler
	uma posicao de array, quando for ler um char sozinho e' lido usando a
	funcao readString com tamanho 1
	*/
	public void readChar( int endereco ) {
		int endTemp = novoTemp(4);
		
		arqAsm.println(";ler char do teclado");
		mov( "bx", "DS:["+endereco+"]", "endereco do char com deslocamento");
		arqAsm.println("\tmov dx, " + endTemp + "\t;endereco do temporario");
		arqAsm.println("\tmov al, 2\t;tamanho do vetor");
		arqAsm.println("\tmov DS:[" + endTemp + "], al");
		arqAsm.println("\tmov ah, 0Ah");
		arqAsm.println("\tint 21h\r\n");
		
		quebrarLinha();
		
		arqAsm.println(";atribuicao da string lida para a variavel");
		arqAsm.println("\tmov di, " + (endTemp + 2) + "\t;endereco primeiro caractere em temp");
		arqAsm.println("\tmov si, bx\t;endereco base do char");
		arqAsm.println("\tmov al, DS:[di]");
		arqAsm.println("\tmov DS:[si], al\t;salva caractere");
		arqAsm.println(";fim ler do teclado\r\n");
	}
	
	public void readString(int endereco, int tamanho) {

		int endTemp = novoTemp(tamanho + 3);
		int rotulo0 = novoRotulo();
		int rotulo1 = novoRotulo();

		arqAsm.println(";ler do teclado");
		arqAsm.println("\tmov dx, " + endTemp + "\t;endereco do temporario");
		arqAsm.println("\tmov al, " + tamanho + "\t;tamanho do vetor");
		arqAsm.println("\tmov DS:[" + endTemp + "], al");
		arqAsm.println("\tmov ah, 0Ah");
		arqAsm.println("\tint 21h\r\n");

		quebrarLinha();

		arqAsm.println(";atribuicao da string lida para a variavel");

		arqAsm.println("\tmov di, " + (endTemp + 2) + "\t;endereco primeiro caractere em temp");
		arqAsm.println("\tmov si, " + endereco + "\t;endereco base do vetor");
		arqAsm.println("R" + rotulo0 + ":");
		arqAsm.println("\tmov al, DS:[di]");
		arqAsm.println("\tcmp al, 13\t;compara com \\r");
		arqAsm.println("\tje R" + rotulo1);
		arqAsm.println("\tmov DS:[si], al\t;salva caractere");
		arqAsm.println("\tadd di,1");
		arqAsm.println("\tadd si,1");
		arqAsm.println("\tjmp R" + rotulo0);
		arqAsm.println("R" + rotulo1 + ":");
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
		arqAsm.print("\tneg " + reg);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
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

		arqAsm.print("\tsub " + reg1 + ", " + reg2);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}

	}

	public void imul(String reg, String... comentario) {
		arqAsm.print("\timul " + reg);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}
	}

	public void idiv(String reg, String... comentario) {
		arqAsm.print("\tidiv " + reg);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}
	}

	public void rotulo(int numRotulo) {
		arqAsm.println("R" + numRotulo + ":");
	}

	public void je(int numRotulo, String... comentario) {

		arqAsm.print("\tje R" + numRotulo);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}
	}
	
	public void jne(int numRotulo, String... comentario) {

		arqAsm.print("\tjne R" + numRotulo);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}
	}

	public void jmp(int numRotulo, String... comentario) {

		arqAsm.print("\tjmp R" + numRotulo);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}
	}
	
	public void jle(int numRotulo, String... comentario) {

		arqAsm.print("\tjle R" + numRotulo);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}
	}

	public void jl(int numRotulo, String... comentario) {

		arqAsm.print("\tjl R" + numRotulo);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}
	}
	
	public void jge(int numRotulo, String... comentario) {

		arqAsm.print("\tjge R" + numRotulo);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
		}
	}

	public void jg(int numRotulo, String... comentario) {

		arqAsm.print("\tjg R" + numRotulo);

		if (comentario.length == 0) {
			arqAsm.println("");
		} else {
			arqAsm.println("\t;" + comentario[0]);
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

	public void push( String reg1, String... comentario ){
		arqAsm.print("\tpush "+reg1);

		if( comentario.length == 0 ){
			arqAsm.println("");
		}else{
			arqAsm.println("\t;"+ comentario[0] );
		}
	}

	public void pop( String reg1, String... comentario ){
		arqAsm.print("\tpop "+reg1);

		if( comentario.length == 0 ){
			arqAsm.println("");
		}else{
			arqAsm.println( "\t;"+comentario[0] );
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

	public void comando( String comando ) {
		arqAsm.println(comando);
	}
	
	public void adicionarVariavel(RegistroLexico var) {
		int endereco;

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
				arqAsm.println("\tsword " + var.getTamanho() + " DUP(?)\t;array int em " + endereco);
			}
		}
		var.setEndereco( endereco );
	}

	public void adicionarVariavel(RegistroLexico var, int valor) {

		int endereco;

		if (var.getTipo() == Tipo.CARACTERE) {
			endereco = novaVariavel(1);
			arqAsm.println("\tbyte '" + (char) valor + "'\t;var. char em " + endereco);
		} else {
			endereco = novaVariavel(2);
			arqAsm.println("\tsword " + valor + "\t;var. int em " + endereco);
		}
		var.setEndereco(endereco);
	}

	public void adicionarVariavel(RegistroLexico var, String valor) {
		
		int endereco = novaVariavel(1);
		arqAsm.println("\tbyte " + valor + "\t;var. char em " + endereco);
		var.setEndereco(endereco);
	}
}
