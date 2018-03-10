package util;

public class FilePosition{
	private static int filePos;
	private static int line;
	
	private static FilePosition instance;
	
	private FilePosition() {
		filePos = 0;
		line = 1;
	}
	
	public static FilePosition getInstance() {
		if( instance == null ) {
			instance = new FilePosition();
		}
		
		return instance;
	}

	public int getFilePos() { return filePos;}
	public int getLine() { return line; }
	
	public void sumLine() { line++; }
	public void devolveChar() { filePos--; }
	public void nextPos() { filePos++; }
}