package util;

public class FilePosition{
	private static int filePos;
	private static int lineNumber;
	
	private static FilePosition instance;
	
	private FilePosition() {
		filePos = 0;
		lineNumber = 1;
	}
	
	public static FilePosition getInstance() {
		if( instance == null ) {
			instance = new FilePosition();
		}
		
		return instance;
	}

	public int getFilePos() { return filePos;}
	public int getLineNumber() { return lineNumber; }
	public void reset(){ filePos = 0; }
   
	public void sumLine() { lineNumber++; }
	public void devolveChar( char c ) {
		if( c == '\n' ) {
			lineNumber--;
		}
		filePos--;
	}
	public void nextPos() { filePos++; }
}