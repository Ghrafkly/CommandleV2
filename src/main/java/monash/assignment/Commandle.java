package monash.assignment;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * The Commandle class handles the logic for the game session
 * A session is defined as the time between the start of
 * the program and the end of the program
 *
 * <p>Variables defined in this class:</p>
 * <ol>
 *     <li>targetWord - the word that the user is trying to guess</li>
 *     <li>wordGenerator - the {@link WordGenerator} used to generate the target word </li>
 *     <li>game - the {@link Game} that is currently being played </li>
 *     <li>wordList - the list of words that are used for generating targetWords and to validate user guesses</li>
 *     <li>sessionTargets - the list of words that have been used as a target word in the current session</li>
 * </ol>
 *
 */
@Data
public class Commandle {
	private static final Logger log = LogManager.getLogger(Commandle.class);

	private static final int MAX_TRIES = 6;

	private String targetWord;
	private WordGenerator wordGenerator;
	private Game game;
	private List<String> wordList = new ArrayList<>();
	private Set<String> sessionTargets = new HashSet<>();

	/**
	 * Method that starts the game
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
	 * Loads the dictionary file into wordList
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
	 * Starts the game.
	 * <p>If the game ends, the user will be prompted to play again
	 * If the user chooses to play again, a new target word will be generated
	 * and the game will start again</p>
	 * <p>If the user chooses not to play again, or if the
	 * user enters an invalid input, the program will exit </p>
	 *
	 */
	private void startGame() {
		game = new Game(targetWord, wordList, MAX_TRIES);
		boolean gameEnd = game.start();

		if (gameEnd) {
			Scanner scanner = new Scanner(System.in);
			System.out.print("Play again? (Y/N): ");
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
