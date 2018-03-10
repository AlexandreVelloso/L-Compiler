package test;

import lexico.Lexico;
import lexico.ResultadoLexico;
import lexico.TabelaSimbolos;
import lexico.Token;
import util.FilePosition;
import util.MyFile;
import util.Programa;

public class TesteLexico {
	
	public static void main(String [] args ) {
		
		Programa.getInstance().readProgram( "C:\\Users\\Alexandre Velloso\\eclipse-workspace\\LC-Compiler\\src\\Programas\\Exemplo1.l" );
		
		ResultadoLexico result;
		do {
			result = Lexico.getToken( );
		}while( result.getToken() != Token.ERROR && Programa.getInstance().getPosition().filePos < Programa.getInstance().getProgram().length() );
		
		if( result.getToken() != Token.ERROR ) {
			System.out.println("OK");
			TabelaSimbolos.getInstance().print();
		}
	}
	
}
