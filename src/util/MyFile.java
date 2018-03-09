package util;

import java.util.Scanner;
import java.io.File;

public class MyFile{

    Scanner sc = null;

    public MyFile( String path ){

        try{
            sc = new Scanner( new File(path) );
        }catch( Exception e ){
            e.printStackTrace();
        }
    }

    public String readLine( ) {
    	if( sc.hasNext() ) {
    		return sc.nextLine();
    	}else {
    		return null;
    	}
    }
    
    public String readAll( ){
        String allFile = "";

        while( sc.hasNext() ){
            allFile += sc.nextLine()+'\n';
        }
        
        return allFile;
    }

}