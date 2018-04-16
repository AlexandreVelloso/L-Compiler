package util;

public class Programa {
	
	private static Programa instance = null;
	private static FilePosition position = null;
	private static String program = null;
	
	private Programa() {
		position = FilePosition.getInstance();
	}
	
	public void readProgram( String file ) {
		MyFile arq = new MyFile(file);
      
      position.reset();
      program = arq.readAll();
		
		arq.close();
	}
	
	public static Programa getInstance() {
		if( instance == null ) {
			instance = new Programa();
		}
		
		return instance;
	}

	public String getProgram() {
		return program;
	}
	
	public FilePosition getPosition() {
		return position;
	}
}
