package otimizacao;

import util.FILE;
import util.MyFile;

public abstract class Otimizacao {
	protected String arquivo;
	protected FILE arqAsm;

	public Otimizacao(String nomeArquivo) {
		
		MyFile file = new MyFile(nomeArquivo);
		arquivo = file.readAll();
		
		arqAsm = new FILE(FILE.OUTPUT, nomeArquivo );
	}

	public void run() {
	}

}
