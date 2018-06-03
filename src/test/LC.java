package test;

import otimizacao.Otimizacao;
import otimizacao.Peephole;
import sintatico.Sintatico;
import util.Programa;

public class LC {

    public static void main(String[] args) {

        try {
            String arquivoL = "exemplo2.l";
            String arquivoASM = "C:\\Users\\alexandre\\Desktop\\MASM\\arquivo.asm";
            
            Programa.getInstance().readProgram("./Arquivos/Exemplos/" + arquivoL);
            Sintatico.principal();
            
            // otimizacao
            Otimizacao peephole = new Peephole( arquivoASM );
            peephole.run();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
