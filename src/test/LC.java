package test;

import sintatico.Sintatico;
import util.Programa;

public class LC {

    public static void main(String[] args) {

        try {
            String arquivoL = "t17.l";
            String arquivoASM = "";

            /*
            File folder = new File(".");
            File[] listOfFiles = folder.listFiles();
            
            for( File f : listOfFiles ) {
            	System.out.println( f.getName() );
            }
            */
            
            Programa.getInstance().readProgram("./Arquivos/semantico/Certos/" + arquivoL);
            Sintatico.principal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
