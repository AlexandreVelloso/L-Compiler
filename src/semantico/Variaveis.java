package semantico;

import java.util.HashMap;
import lexico.RegistroLexico;
import util.FilePosition;

public class Variaveis {
    private HashMap<String, RegistroLexico> variaveis;
    private static Variaveis instance;
    
    private Variaveis(){
        variaveis = new HashMap<>();
    }
    
    public static Variaveis getInstance(){
        if( instance == null ){
            instance = new Variaveis();
        }
        
        return instance;
    }
    
    public void addVariavel( RegistroLexico var ) throws Exception{
        
        if( variaveis.containsKey(var.getLexema()) ){
            System.out.println( FilePosition.getInstance().getLineNumber()+":identificador ja declarado ["+var.getLexema()+"].");
            throw new Exception();
        }else{
            variaveis.put( var.getLexema(), var);
        }
    }
    
    public RegistroLexico getVar( String lexema ){
        return variaveis.get( lexema );
    }
}
