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

    public String readAll( ){
        String allFile = "";
        String line;

        while( sc.hasNext() ){
            allFile += sc.nextLine()+'\n';
        }
        
        return allFile;
    }

}