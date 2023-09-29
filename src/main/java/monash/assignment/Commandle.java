package monash.assignment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	private List<String> wordList = new ArrayList<>();
	private Set<String> sessionTargets = new HashSet<>();
	private Game game;
	@Getter(AccessLevel.NONE)
	private WordGenerator wordGenerator;
	@Getter(AccessLevel.NONE)
	private boolean testFlag;

	/**
	 * Method that starts the game
	 *
	 * @param args Argument that allows the user to specify a target word for the first game of the session
	 */
	protected void run(String[] args) {
		if (wordGenerator == null) {
			wordGenerator = new WordGenerator(getWordList(), sessionTargets);
		}

		if (args.length > 0) {
			setTargetWord(args);
		} else {
			setTargetWord();
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
	protected void loadDictionary(String filePath) throws IOException {
		FileReader fr = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();

		while (line != null) {
			String word = line.trim().toLowerCase();
			wordList.add(word);
			line = br.readLine();
		}
	}

	protected void setTargetWord(String[] args) {
		String word = args[0].trim().toLowerCase();
		if (getWordList().contains(word)) {
			targetWord = wordGenerator.generateTargetWord(word);
		} else {
			log.error(String.format("Word [%s] is not in the game dictionary. Game will now exit", word));
		}
	}

	protected void setTargetWord() {
		log.info("No word provided. Generating random target word");
		targetWord = wordGenerator.generateTargetWord();
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
	protected void startGame() {
		if (game == null) {
			game = new Game(targetWord, wordList, MAX_TRIES);
		}

		messages(game.start() ? "win" : "lose");

		if (!testFlag) {
			playAgain();
		}
	}

	/**
	 * Method that prompts the user to play again
	 */
	protected void playAgain() {
		Scanner scanner = new Scanner(System.in);
		messages("again");
		String answer = scanner.nextLine().trim().toLowerCase();

		if (answer.equals("y")) {
			game = testFlag ? game : null;
			targetWord = wordGenerator.generateTargetWord();
			run(new String[]{});
		} else {
			messages("end");
		}
	}

	protected void messages(String id) {
		switch (id) {
			case "win" -> System.out.println("Congratulations! You have guessed the target word!");
			case "lose" -> System.out.printf("You have run out of tries. The target word was [%s]%n", targetWord);
			case "again" -> System.out.print("Play again? (Y/N): ");
			case "end" -> System.out.println("Thank you for playing Commandle!");
		}
	}
}
