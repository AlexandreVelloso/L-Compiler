package test;

import sintatico.*;
import util.*;

public class Teste1{
   public static void main( String [] args ){
   
      try{
         Programa.getInstance().readProgram("./Arquivos/Exemplos/Exemplo1.l");
         Sintatico.principal();
      }catch( Exception e ){
         //e.printStackTrace();
      }
   }
}
