package test;

import util.Programa;
import sintatico.Sintatico;

public class Test1{
    public static void main(String [] args){


        //Programa.getInstance().readProgram("../Testes/1/t1.l");
        //System.out.println( Programa.getInstance().getProgram() );

        for( int i = 1; i <= 9; i++ ){
            
            try{
                //System.out.println( "../Testes/1/t"+i+".l" );
                Programa.getInstance().readProgram( "../Testes/1/t"+i+".l" );
                Sintatico.principal();
                System.out.println("OK");
            }catch( Exception e ){
                //e.printStackTrace();
            }
            
        }

    }
}
