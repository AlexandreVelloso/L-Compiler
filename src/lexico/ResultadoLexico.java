package lexico;

public class ResultadoLexico{

	private byte token;
	private String lexema;

	public ResultadoLexico( byte token, String lexema ){
		this.token = token;
		this.lexema = lexema;
	}

	public byte getToken(){ return this.token; }
	public String getLexema(){ return this.lexema; }
}