public enum ConsolePrinter{
/*
	Red: Error message
	Green: Info message
	Yellow: Prompting user for editing values
	Blue: Processing message
	Purple: Help menu
	Cyan: Prompting user for command
	White: Signaling the start and termination of Editor

*/
	RED 	("\u001B[31m"),	
	GREEN 	("\u001B[32m"),
	YELLOW 	("\u001B[33m"),
	BLUE 	("\u001B[34m"),
	PURPLE 	("\u001B[35m"),
	CYAN 	("\u001B[36m"),
	WHITE 	("\u001B[37m");

	private String prefix;
	private final String SUFFIX = "\u001B[0m";

	private ConsolePrinter(String prefix) {
		this.prefix = prefix;
	}

	public void print(String message) {
		System.out.println(this.prefix + message + SUFFIX);
	}
};