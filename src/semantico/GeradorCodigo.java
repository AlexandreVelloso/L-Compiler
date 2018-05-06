package semantico;

import enums.Tipo;
import lexico.RegistroLexico;
import util.FILE;

public class GeradorCodigo {

    private static GeradorCodigo instance;
    private FILE arqAsm = new FILE(FILE.OUTPUT, "arquivo.asm");
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

        arqAsm.println(
                "stack segment\r\n"
                + "\tdw   4000h  dup(0)\r\n"
                + "ends\r\n"
                + "\r\n"
                + "data segment\r\n"
                + "\tdb 4000h DUP(?)   ;temporarios\r\n"
        //+ "\r\n    db 258 DUP(?)    ;string temporaria" // OBS: Olhar isso aqui
        );
    }

    public void fimVariaveisASM() {

        arqAsm.println(
                "ends\r\n"
                + "\r\n"
                + "code segment\r\n"
                + "start:\r\n"
                + "; set segment registers:\r\n"
                + "\tmov ax, data\r\n"
                + "\tmov ds, ax\r\n"
                + "\tmov es, ax\r\n"
                + "\r\n"
                + "\t; add your code here"
        );
    }

    public void fimASM() {
        arqAsm.println(
                "; wait for any key....    \r\n"
                + "\tmov ah, 1\r\n"
                + "\tint 21h\r\n"
                + "\r\n"
                + "\tmov ax, 4c00h ; exit to operating system.\r\n"
                + "\tint 21h    \r\n"
                + "ends\r\n"
                + "\r\n"
                + "end start ; set entry point and stop the assembler."
        );

        arqAsm.close();
    }

    public static int novoTemp(int tamanho) {
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

    public void mostrarInt(int adress) {

        arqAsm.println("\r\n;Mostrar na tela\r\n");
        arqAsm.println("mov di, 4000h\t;end. string temporaria"); // OBS: Eu crio um novo rotulo e uso ele para minha string temporaria?
        arqAsm.println("mov cx,0\t;contador");
        // move numero para regA
        arqAsm.println("mov ax, DS:[" + adress + "]\t;move inteiro para regA");
        // compara com 0 para se obter o sinal do numero
        arqAsm.println("cmp ax,0\t;compara para saber o sinal do numero");
        // se numero positivo, pula
        arqAsm.println("jge R" + contadorRotulo + "\t;pula se positivo");
        // se nao pular
        arqAsm.println("mov bl, 2Dh\t;senao escreve sinal -");
        arqAsm.println("mov ds:[di], bl");
        // incrementa o indice
        arqAsm.println("add di, 1\t;incrementa o indice");
        // faz o modulo do numero
        arqAsm.println("neg ax\t;modulo do numero");
        // rotulo
        arqAsm.println("R" + contadorRotulo + ":");
        contadorRotulo++;
        arqAsm.println("mov bx,10\t;divisor");
        // rotulo
        arqAsm.println("R" + contadorRotulo + ":");
        // incrementa o contador
        arqAsm.println("add cx,1\t;incrementa o contador");
        // zera o DX, estende 32bits p/ div.
        // OBS: na divisao, AX recebe o quociente e DX recebe o resto da divisao	
        arqAsm.println("mov dx,0\t;estende 32bits p/ div.");
        // divide DXAX por BX
        arqAsm.println("idiv bx\t;divide DXAX por BX");
        // empilha o valor do resto
        arqAsm.println("push dx\t;empilha valor do resto");
        // verifica se ax igual 0
        arqAsm.println("cmp ax, 0\t;verifica se quoc. = 0");
        // se nao igual pula
        arqAsm.println("jne R" + contadorRotulo + "\t;se nao acabou o numero, loop");
        contadorRotulo++;
        arqAsm.println(";depois de acabar o numero, desemp. os valores");
        arqAsm.println("R" + contadorRotulo + ":");
        arqAsm.println("pop dx\t;desempilha valor");
        arqAsm.println("add dx, 30h\t;transforma em caractere");
        arqAsm.println("mov DS:[di],dl\t;escreve caractere");
        arqAsm.println("add di,1\t;incrementa base");
        arqAsm.println("add cx, -1\t;decrementa contador");
        arqAsm.println("cmp cx,0\t;verifica se a pilha esta vazia");
        arqAsm.println("jne R" + contadorRotulo + "\t;se nao pilha vazia, loop");
        arqAsm.println(";grava fim de string");
        arqAsm.println("mov dl, 024h\t;fim de string");
        arqAsm.println("mov ds:[di], dl\t;grava '$'");
        arqAsm.println(";exibe string");
        arqAsm.println("mov dx, 4000h");// OBS: olhar esse endereco
        arqAsm.println("mov ah, 09h");
        arqAsm.println("int 21h");

        arqAsm.println("\r\n;Fim mostrar na tela\r\n");
    }

    public void mostrarString( int adress ){
        arqAsm.println("\r\n\t;codigo para mostrar string\r\n");
    }
    
    public void mov( String reg1, String reg2, String... comentario ){
        
        arqAsm.print("\tmov "+reg1+", "+reg2);
        
        if( comentario.length == 0 ){
            arqAsm.println("");
        }else{
            arqAsm.println("\t;"+comentario[0]);
        }
        
    }
    
    public void neg( String reg, String... comentario ){
        arqAsm.print("neg "+reg);
        
        if( comentario.length == 0 ){
            arqAsm.println("");
        }else{
            arqAsm.println("\t;"+comentario);
        }
    }
    
    public void add( String reg1, String reg2, String... comentario ){
        
        arqAsm.print("add "+reg1+", "+reg2);
        
        if( comentario.length == 0 ){
            arqAsm.println("");
        }else{
            arqAsm.println("\t;"+comentario);
        }
        
    }
    
    public void sub( String reg1, String reg2, String... comentario ){
        
        arqAsm.print("sub "+reg1+", "+reg2);
        
        if( comentario.length == 0 ){
            arqAsm.println("");
        }else{
            arqAsm.println("\t;"+comentario);
        }
        
    }
    
    public void imul( String reg, String... comentario ){
        arqAsm.print("imul "+reg);
        
        if( comentario.length == 0 ){
            arqAsm.println("");
        }else{
            arqAsm.println("\t;"+comentario);
        }
    }
    
    public void idiv( String reg, String... comentario ){
        arqAsm.print("idiv "+reg);
        
        if( comentario.length == 0 ){
            arqAsm.println("");
        }else{
            arqAsm.println("\t;"+comentario);
        }
    }

    public void quebrarLinha(){
        arqAsm.println(";codigo para quebra de linha");
    }
    
    public void adicionarVariavel(RegistroLexico var, int... valor) {

        // apagar todos os varX do codigo, ele serve somente para debug
        int endereco;

        if (valor.length == 0) {

            if (var.getTamanho() == 0) {

                if (var.getTipo() == Tipo.CARACTERE) {
                    endereco = novaVariavel(1);
                    arqAsm.println("\tvar" + contadorNumVars + " db ?\t;var. char em " + Integer.toHexString(endereco) + "h");
                } else {
                    endereco = novaVariavel(2);
                    arqAsm.println("\tvar" + contadorNumVars + " dw ?\t;var. int em " + Integer.toHexString(endereco) + "h");
                }
            } else {

                if (var.getTipo() == Tipo.CARACTERE) {
                    endereco = novaVariavel(var.getTamanho());
                    arqAsm.println("\tvar" + contadorNumVars + " db " + var.getTamanho() + " DUP(?)\t;array char em " + Integer.toHexString(endereco) + "h");
                } else {
                    endereco = novaVariavel(var.getTamanho() * 2);
                    arqAsm.println("\tvar" + contadorNumVars + " dw " + var.getTamanho() + " DUP(?)\t;array char em " + Integer.toHexString(endereco) + "h");
                }
            }

        } else {

            if (var.getTipo() == Tipo.CARACTERE) {
                endereco = novaVariavel(1);
                arqAsm.println("\tvar" + contadorNumVars + " db " + valor[0] + "\t;var. char em " + Integer.toHexString(endereco) + "h");
            } else {
                endereco = novaVariavel(2);
                arqAsm.println("\tvar" + contadorNumVars + " dw " + valor[0] + "\t;var. int em " + Integer.toHexString(endereco) + "h");
            }
        }

        var.setEndereco(endereco);
    }
}
