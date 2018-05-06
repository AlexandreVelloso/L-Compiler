package test;

import sintatico.*;
import util.*;

public class LC {

    public static void main(String[] args) {

        try {
            String arquivoL = "Exemplo1.l";
            String arquivoASM = "";

            Programa.getInstance().readProgram("./Arquivos/Exemplos/" + arquivoL);
            Sintatico.principal();
            System.out.println("OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
