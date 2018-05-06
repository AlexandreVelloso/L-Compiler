package lexico;

import enums.Classe;
import enums.Tipo;
import enums.Token;

public class RegistroLexico {

    private Token token;
    private String lexema;
    private int line;
    private int tamanho;
    private Classe classe;
    private Tipo tipo;
    private int endereco;

    public RegistroLexico(Token token, String lexema, int line) {
        this.token = token;
        this.lexema = lexema;
        this.line = line;
        this.tamanho = 0;
        this.classe = null;
        this.tipo = null;
        this.endereco = -1;
    }

    public RegistroLexico(Token token, String lexema, int line, Tipo tipo) {
        this.token = token;
        this.lexema = lexema;
        this.line = line;
        this.tamanho = 0;
        this.classe = null;
        this.tipo = tipo;
        this.endereco = -1;
    }
    
    private RegistroLexico(Token token, String lexema, int line, int tamanho, Classe classe, Tipo tipo, int endereco) {
        this.token = token;
        this.lexema = lexema;
        this.line = line;
        this.tamanho = tamanho;
        this.classe = classe;
        this.tipo = tipo;
        this.endereco = endereco;
    }

    public RegistroLexico clone() {
        return new RegistroLexico(token, lexema, line, tamanho, classe, tipo, endereco);
    }
    
    public String toString(){
        return
                "Token: "+token+"\n"+
                "Lexema: "+lexema+"\n"+
                "Line: "+line+"\n"+
                "Tamanho: "+tamanho+"\n"+
                "Classe: "+classe+"\n"+
                "Tipo: "+tipo+"\n";
    }

    public Token getToken() {
        return this.token;
    }

    public String getLexema() {
        return this.lexema;
    }

    public int getLine() {
        return this.line;
    }

    public int getTamanho() {
        return this.tamanho;
    }

    public Classe getClasse() {
        return this.classe;
    }

    public Tipo getTipo() {
        return this.tipo;
    }
    
    public int getEndereco(){
        return this.endereco;
    }

    public void setTamanho( int tamanho ) {
        this.tamanho = tamanho;
    }

    public void setClasse( Classe classe ) {
        this.classe = classe;
    }

    public void setTipo( Tipo tipo ) {
        this.tipo = tipo;
    }
    
    public void setEndereco( int endereco ){
        this.endereco = endereco;
    }
}
