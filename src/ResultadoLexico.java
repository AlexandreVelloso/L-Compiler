public class ResultadoLexico{

	private Tokem token;
	private String lexema;

	public ResultadoLexico( Tokem token, String lexema ){
		this.token = token;
		this.lexema = lexema;
	}

	public Tokem getToken(){ return this.token; }
	public String getLexema(){ return this.lexema; }
}