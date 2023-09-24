package monash.assignment;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * The Commandle class handles the logic for the game session
 * A session is defined as the time between the start of
 * the program and the end of the program
 */
@Data
public class Commandle {
	private static final Logger log = LogManager.getLogger(Commandle.class);

	private static final int MAX_TRIES = 6;

	private String targetWord;
	private WordGenerator wordGenerator;
	private List<String> wordList = new ArrayList<>();
	private Set<String> sessionTargets = new HashSet<>();

	/**
	 * Main method that starts the game
	 *
	 * @param args Argument that allows the user to specify a target word for the first game of the session
	 */
	public void run(String[] args) {
		loadDictionary();
		wordGenerator = new WordGenerator(getWordList(), sessionTargets);

		if (args.length > 0) {
			String word = args[0].trim().toLowerCase();
			if (getWordList().contains(word)) {
				targetWord = wordGenerator.generateTargetWord(word);
			} else {
				log.error(String.format("Word [%s] is not in the game dictionary. Game will now exit", word));
			}
		} else {
			log.info("No word provided. Generating random target word");
			targetWord = wordGenerator.generateTargetWord();
		}

		if (targetWord != null) {
			log.info(String.format("Target word is [%s]", targetWord));
			sessionTargets.add(targetWord);
			startGame();
		}

		log.info("Exiting Commandle");
	}

	/**
	 * Loads the dictionary file into memory
	 */
	private void loadDictionary() {
		try (FileReader fr = new FileReader("src/main/resources/dictionary.txt")) {
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();

			while (line != null) {
				String word = line.trim().toLowerCase();
				wordList.add(word);
				line = br.readLine();
			}

			log.info("Dictionary loaded: Number of words: " + wordList.size());
		} catch (Exception e) {
			log.error("Error loading dictionary file", e);
		}
	}

	/**
	 * Starts the game
	 */
	private void startGame() {
		Game game = new Game(targetWord, wordList, MAX_TRIES);
		if (game.startGame()) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Play again? (Y/N)");
			String answer = scanner.nextLine().trim().toLowerCase();

			if (answer.equals("y")) {
				targetWord = wordGenerator.generateTargetWord();
				startGame();
			} else {
				System.out.println("Thank you for playing Commandle!");
			}
		}
	}
}
