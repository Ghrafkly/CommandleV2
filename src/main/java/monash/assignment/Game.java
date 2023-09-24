package monash.assignment;

import lombok.Data;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * The Game class handles the logic for a single game of Commandle
 * A game is defined as the time between the start of the user guessing
 * until the user guesses the target word or runs out of tries
 */
@Data
public class Game {
	@NonNull
	private String targetWord;
	@NonNull
	private List<String> wordList;
	@NonNull
	private int tries;

	private Set<String> guesses = new HashSet<>();

	/**
	 * Starts the game
	 *
	 * @return true if the game was completed successfully or user chose to exit
	 */
	public boolean startGame() {
		System.out.println("You have 6 tries to guess the target word.");
		int round = 1;
		while (round <= tries) {
			System.out.print("Please enter your guess: ");
			String guess = getGuess();
			if (checkGuess(guess, round)) {
				break;
			} else if (round == tries) {
				System.out.printf("You have run out of tries. The target word was [%s]%n", targetWord);
			}

			round++;
		}

		return true;
	}


	/**
	 * Checks if the user's guess is valid, and provides feedback if it is not
	 *
	 * @return A valid guess from the user
	 */
	public String getGuess() {
		Scanner scanner = new Scanner(System.in);
		String guess = scanner.nextLine().trim().toLowerCase();

		if (guess.length() != 5) {
			System.err.print("Please enter a word of 5 letters: ");
		} else if (guesses.contains(guess)) {
			System.err.printf("You have already guessed [%s]. Please try again: ", guess);
		} else if (!wordList.contains(guess)) {
			System.err.printf("[%s] is not in the dictionary or is invalid. Please try again: ", guess);
		} else {
			guesses.add(guess);
			return guess;
		}

		return getGuess();
	}

	/**
	 * Checks if the user's guess is correct, and provides feedback if it is not
	 *
	 * @param guess The user's guess
	 * @param round The current round number
	 * @return true if the user's guess is correct
	 */
	public boolean checkGuess(String guess, int round) {
		StringBuilder result = new StringBuilder();

		Map<Character, Integer> targetLetterCount = new HashMap<>();
		for (char c : targetWord.toCharArray()) {
			targetLetterCount.put(c, targetLetterCount.getOrDefault(c, 0) + 1);
		}

		for (int i = 0; i < guess.length(); i++) {
			char c = guess.charAt(i);
			if (c == targetWord.charAt(i)) {
				result.append(c);
			} else if (targetLetterCount.containsKey(c) && targetLetterCount.get(c) > 0) {
				result.append("?");
			} else {
				result.append("#");
			}

			targetLetterCount.put(c, targetLetterCount.getOrDefault(c, 0) - 1);
		}

		System.out.printf("%d: %s  %d: %s\n", round, guess, round, result);

		if (guess.equals(this.targetWord)) {
			System.out.println("Congratulations! You have guessed the target word!");
			return true;
		}

		return false;
	}
}
