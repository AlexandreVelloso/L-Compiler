package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * 
 * @author Alexandre
 *
 * Classe para tratar arquivos texto
 *
 */

public class FILE{
   
   //guarda o nome do arquivo para apagar
   public String nome;
	
   //variavel para escrita
   private BufferedWriter bw;
   
   //vatiavel para leitura
   private BufferedReader br;
   
   //variaveis para o usuario escolher qual
   //operacao ele vai fazer
   public static int OUTPUT = 1;
   public static int INPUT = 2;
   public static int APPEND = 3;
   
   /**
    * Construtor que abre o arquivo para leitura ou escrita
    * 
    * @param opc
    * Dado para dizer se o arquivo e de leitura ou escrita
    * 
    * @param nomeArquivo
    * Nome do arquivo a ser aberto
    * 
    */
   public FILE (int opc, String nomeArquivo){
      //abrir o arquivo para escrita, apaga tudo que esta dentro
      if( opc == OUTPUT ){
         try{
            bw = new BufferedWriter( new FileWriter( nomeArquivo ) );
         }
         catch(Exception e){}
      }
      //abrir o arquivo para leitura
      else if( opc == INPUT ){
         try{
            br = new BufferedReader(new FileReader(nomeArquivo));
         }
         catch(Exception e){}
      }
      //abrir o arquivo para escriva comecando do final do arquivo
      else if( opc == APPEND ){
         try{
            bw = new BufferedWriter( new FileWriter( nomeArquivo,true ) );            
         }catch(Exception e){}
      }
      
   this.nome = nomeArquivo;
   }//fim metodo construtor
   
   /**
    * Escreve no arquivo sem pular linha no final
    * 
    * @param linha
    * Linha a ser escrita
    * 
    */
   public void print( String linha ){
      try{ 
         bw.write(linha);
      }catch(Exception e){}
   }
   
   /**
    * Escreve e no final da um \n para pular para a proxima linha
    * 
    * @param linha
    * Linha a ser escrita
    * 
    */
   public void println (String linha){
      try{ 
         bw.write(linha);
         bw.newLine();
      }catch(Exception e){}
   }
   
   /**
    * Le a linha toda no arquivo, e no final posiciona o
    * ponteiro no comeco da proxima linha
    * 
    * @return
    * Linha lida
    * 
    */
   public String readln(){
		String linha = null;
		
      try{
			linha = br.readLine();
		}catch(Exception e){}
		
		return linha;
	}
   
   /**
    * Fecha o arquivo texto
    */
   public void close (){
      try{ 
         if( bw != null ){
            bw.close();
         }else if( br != null){
            br.close();
         }
      }catch(Exception e){System.out.println(e);}
   }
   
   /**
    * Apaga um arquivo texto
    * 
    * @return
    * Boolean para avisar se conseguiu ou nao apagar o arquivo
    * 
    */
   public boolean delete (){
 	  File apagar = new File (this.nome);
 	  return apagar.delete();
   }
   
   //****** metodos adicionais *****
   //os metodos aqui sao static
   
   /**
    * Metodo para testar se um arquivo existe
    * 
    * @param nomeArquivo
    * Nome do arquivo a ser textado
    * 
    * @return
    * Boolean para falar se o arquivo existe ou nao
    * 
    */
   public static boolean exists(String nomeArquivo) {
		boolean exist;
		
		File teste = new File (nomeArquivo);
		exist = teste.exists();
		
		return exist;
	}
   
   /**
    * Metodo para contar linhas de um arquivo
    * 
    * @param nomeArq
    * Nome do arquivo
    * 
    * @return
    * Numero de linhas
    * 
    */
   public static int contarLinhas (String nomeArq)
   {
      FILE arqCont = new FILE ( FILE.INPUT, nomeArq );
      
      int contar = 0;
      String linha;
      
      do{
         linha = arqCont.readln();
         
         if( linha != null)
            contar++;
            
      }while( linha != null );
      
      arqCont.close();
      
      return contar;
   }//fim metodo contarLinhas
   
   /**
    * Metodo para transformar String em int, se a String nao conter
    * so inteiros, vai ser pego o caracter ascii daquela letra
    * 
    * @param str
    * String a ser substituida
    * 
    * @return
    * 
    * inteiro equivalente
    */
   public static int toInt (String str){
	   int retorno = 0;
	   
	   for( int i = str.length() -1; i >= 0; i--){
		   retorno += (str.charAt(i) - 48 );
		   retorno *= 10;
	   }
	   
	   return retorno;
   }
   //**** fim metodos adicionais ***
   
}//fim classe FILE