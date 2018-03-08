package test;

import lexico.*;
import util.MyFile;

public class TesteLexico{
	public static void main( String [] args ){

		if( args.length < 1 ){
			System.out.println("\nNumero de argumentos invalido.\nUse java LC programa.l");
			System.exit(0);
		}

		AnalisadorLexico lex = new AnalisadorLexico();
		
		MyFile arquivo = new MyFile( args[0] );
		FilePosition p = new FilePosition(0);

		String programa = arquivo.readAll();

		do{

			ResultadoLexico result = lex.getToken(programa, p );

			if( result.getToken() == Tokem.ERROR ){
				break;
			}

			System.out.println( result.getLexema()+" : "+result.getToken() );
		}while( p.filePos < programa.length() );
	}
}