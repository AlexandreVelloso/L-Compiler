package test;

import sintatico.*;
import util.*;

public class LC{
	public static void main( String [] args ){
		
		try{
			String arquivoL = args[0];
			String arquivoASM = args[1];
			
			Programa.getInstance().readProgram("../Exemplos/"+arquivoL);
			Sintatico.principal();
		}catch( Exception e ){

		}
	}
}
