package test;

import otimizacao.Otimizacao;
import otimizacao.Peephole;
import sintatico.Sintatico;
import util.Programa;

public class LC {

    public static void main(String[] args) {

        if( args.length < 2 ){
            System.out.println("ERRO: Você escreveu o comando errado, um exemplo é java LC arquivo.l arquivo.asm");
        }else{
            String arquivoL = args[0];
            String arquivoAsm = args[1];

            try{
                Programa.getInstance().readProgram("./Arquivos/Exemplos/" + arquivoL);
                Sintatico.principal();

                // otimizacao
                Otimizacao peephole = new Peephoke( arquivoAsm );
                peephole.run();
            }catch( Exception e ){
                //e.printStackTrace();
            }
        }
        
    }
}