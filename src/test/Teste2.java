package test;

import sintatico.*;
import util.*;

public class Teste2 {
	public static void main(String[] args) {

		int i = 17;
		
		try {
			Programa.getInstance().readProgram("./Arquivos/semantico/t" + i + ".l");
			Sintatico.principal();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
