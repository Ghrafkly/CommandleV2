package monash.assignment;


import java.io.IOException;

/**
 * Commandle is a Wordle clone that runs in the command line.
 */
public class Application {
	public static void main(String[] args) throws IOException {
		Commandle commandle = new Commandle();
		commandle.loadDictionary("src/main/resources/dictionary.txt");
		commandle.run(args);
	}
}
