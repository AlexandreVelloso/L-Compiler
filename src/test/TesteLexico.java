package test;

import lexico.*;
import util.MyFile;

public class TesteLexico{
	public static void main( String [] args ){
		
		TabelaSimbolos tabelaSimbolos = null;

		/*
		if( args.length < 1 ){
			System.out.println("\nNumero de argumentos invalido.\nUse java LC programa.l");
			System.exit(0);
		}
		*/
		String nomeArquivo = "C:\\Users\\Alexandre Velloso\\eclipse-workspace\\LC-Compiler\\src\\Programas\\"+
							 "ErroLinha.l"; 

		AnalisadorLexico lex = new AnalisadorLexico();
		
		MyFile arquivo = new MyFile( nomeArquivo );
		FilePosition p;

		int numCasos = Integer.parseInt( arquivo.readLine() );
		
		for( int i = 1; i <= numCasos; i++ ) {
			String programa = arquivo.readLine();
			p = new FilePosition(0);
			ResultadoLexico result;
			
			System.out.print( programa+" " );
			
			do {
				result = lex.getToken( programa, p );
				tabelaSimbolos.getInstance().add( result.getLexema(), result.getToken() );
				
			}while( result.getToken() != Token.ERROR && p.filePos < programa.length() );
			
			tabelaSimbolos.getInstance().add( result.getLexema(), result.getToken() );
			
			if( result.getToken() != Token.ERROR )
				System.out.println("OK");
		}
		
		//System.out.println("\n\n\n");
		//tabelaSimbolos.getInstance().print();
	}
}