package lexico;

public class ResultadoLexico{

	private Token token;
	private String lexema;
	private int line;

	public ResultadoLexico( Token token, String lexema, int line ){
		this.token = token;
		this.lexema = lexema;
		this.line = line;
	}

	public Token getToken(){ return this.token; }
	public String getLexema(){ return this.lexema; }
	public int getLine(){ return this.line; } 
}