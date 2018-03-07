public class TesteLexico{
	public static void main( String [] args ){
		AnalisadorLexico lex = new AnalisadorLexico();

		FilePosition p = new FilePosition(0);
		//String arquivo = "um_id_qualquer = 5;";
		//String arquivo = "0; 005h; 005 089483 123456;";
		//String arquivo = "089483";
		String arquivo = " 5 + 4-0+058473 = 4; /* é aqui e um comentario, eu posso colocar +--=af--a--s=*/ 6 + 5 = 10 /**/";

		do{

			ResultadoLexico result = lex.getToken(arquivo, p );

			if( result.getToken() == Tokem.ERROR ){
				break;
			}

			System.out.println( result.getLexema()+" = "+result.getToken() );
		}while( p.filePos < arquivo.length() );
	}
}