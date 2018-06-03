package otimizacao;

public class Peephole extends Otimizacao{

	public Peephole( String nomeArquivo ) {
		super( nomeArquivo );
	}
	
	@Override
	public void run() {
		
		String[] linhas = this.arquivo.split("\n");
		
		for( int i = 0; i < linhas.length; i++ ) {
			
			if( linhas[i].contains("mov") && linhas[i+1].contains("mov") ) {
				
				String[] mov1 = linhas[i].replace(",","").split(" ");
				String[] mov2 = linhas[i+1].replace(",","").split(" ");
				
				if( mov1[1].contains("DS") && mov2[2].contains("DS") && mov1[1].equals( mov2[2] ) ) {
				
					/*
					 * Esse if trata redundancia do tipo:
					 * 
					 * mov DS:[x], ax
					 * mov bx, DS:[x]
					 * 
					 * E transforma em:
					 * 
					 * mov bx, ax
					*/
					
					arqAsm.println("\tmov "+mov2[1]+", "+mov1[2]);
					i++;
					
				}else {
					arqAsm.println( linhas[i] );
				}
			}else {
				arqAsm.println( linhas[i] );
			}
			
		}
		
		arqAsm.close();
	}
}
