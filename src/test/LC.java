package test;

import sintatico.Sintatico;
import util.Programa;

public class LC {

    public static void main(String[] args) {

        try {
            String arquivoL = "Exemplo1.l";
            String arquivoASM = "";

            /*
            File folder = new File(".");
            File[] listOfFiles = folder.listFiles();
            
            for( File f : listOfFiles ) {
            	System.out.println( f.getName() );
            }
            */
            
            Programa.getInstance().readProgram("./Arquivos/Exemplos/" + arquivoL);
            Sintatico.principal();
            System.out.println("OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
