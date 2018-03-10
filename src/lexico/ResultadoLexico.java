package lexico;

public class ResultadoLexico{

	private Token token;
	private String lexema;

	public ResultadoLexico( Token token, String lexema ){
		this.token = token;
		this.lexema = lexema;
	}

	public Token getToken(){ return this.token; }
	public String getLexema(){ return this.lexema; }
}