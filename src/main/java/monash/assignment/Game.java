package monash.assignment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

/**
 * The Game class handles the logic for a single game of Commandle
 * A game is defined as the time between the start of the user guessing
 * until the user guesses the target word or runs out of tries
 *
 * <p>Variables defined in this class:</p>
 * <ol>
 *     <li>targetWord - the word that the user is trying to guess</li>
 *     <li>wordList - the list of words considered a valid guess</li>
 *     <li>tries - the number of tries the user has to guess the target word</li>
 *     <li>guesses - the guesses the user has made for the game</li>
 *</ol>
 */
@Data
public class Game {
	@NonNull
	@Getter(AccessLevel.NONE)
	private String targetWord;
	@NonNull
	@Getter(AccessLevel.NONE)
	private List<String> dictionary;
	@NonNull
	@Getter(AccessLevel.NONE)
	private int tries;

	private int round = 1;

	private Set<String> guesses = new HashSet<>();

	/**
	 * Starts the game.
	 * <p>Variables defined in this method:</p>
	 * <ol>
	 *     <li>round - the current round of the game</li>
	 *     <li>scanner - the scanner used to read user input</li>
	 *     <li>guess - the user's guess</li>
	 *     <li>result - the result of the user's guess from {@link #checkGuess(String)}</li>
	 * </ol>
	 *
	 * Upon completion of the game, the guesses set will be cleared. This is to ensure that
	 * guesses from previous games are not carried over to the next game.
	 *
	 * @return true if the game was completed, either by the user guessing the target word or running out of tries
	 */
	public boolean start() {
		messages("start");
		Scanner scanner = new Scanner(System.in);

		while (round <= tries) {
			messages("input");
			String guess = scanner.nextLine().trim().toLowerCase();

			while (!guessValidity(guess)) {
				guess = scanner.nextLine().trim().toLowerCase();
			}

			String result = checkGuess(guess);

			messages("round", String.valueOf(round), guess, result);

			if (result.equals(targetWord)) {
				return true;
			}

			round++;
		}

		return false;
	}

	/**
	 * Checks if the user's guess is valid, and provides feedback if it is not.
	 * A guess is valid if:
	 * <ol>
	 *     <li>It is 5 letters long</li>
	 *     <li>It has not been guessed before</li>
	 *     <li>It is in the game dictionary</li>
	 * </ol>
	 * If the guess is not valid, the user will be prompted to enter another guess
	 * until a valid guess is entered
	 *
	 * @param guess The user's guess
	 * @return true if the user's guess is valid
	 */
	public boolean guessValidity(String guess) {
		if (guess.length() != 5) {
			messages("invalidLength");
		} else if (guesses.contains(guess)) {
			messages("alreadyGuessed", guess);
		} else if (!dictionary.contains(guess)) {
			messages("invalidGuess", guess);
		} else {
			guesses.add(guess);
			return true;
		}

		return false;
	}

	/**
	 * Checks if the user's guess is correct, and provides feedback if it is not
	 * <ol>
	 *     <li># indicates that the letter is not in the target word</li>
	 *     <li>? indicates that the letter is in the target word, but not in the correct position</li>
	 *     <li>The letter itself indicates that the letter is in the correct position</li>
	 *     <li>If the user's guess is correct, the game will end</li>
	 * </ol>
	 *
	 * Variables defined in this method:
	 * <ol>
	 *     <li>result - the result of the user's guess</li>
	 *     <li>targetLetterCount - a map of the letters in the target word and how often the appear. Deals with duplicate letters</li>
	 * </ol>
	 *
	 * @param guess The user's guess
	 * @return A string containing the feedback for the user's guess
	 */
	public String checkGuess(String guess) {
		StringBuilder result = new StringBuilder();

		guess = guess.toLowerCase();

		Map<Character, Integer> targetLetterCount = new HashMap<>();
		for (char c : targetWord.toCharArray()) {
			targetLetterCount.put(c, targetLetterCount.getOrDefault(c, 0) + 1);
		}

		Map<Character, Integer> guessLetterCount = new HashMap<>();
		for (char c : guess.toCharArray()) {
			guessLetterCount.put(c, guessLetterCount.getOrDefault(c, 0) + 1);
		}

		for (int i = 0; i < guess.length(); i++) {
			char c = guess.charAt(i);
			if (c == targetWord.charAt(i)) {
				result.append(c);
				targetLetterCount.computeIfPresent(c, (k, v) -> v - 1);
			} else if (targetLetterCount.containsKey(c) && targetLetterCount.get(c) > 0) {
				if (guessLetterCount.get(c) > targetLetterCount.get(c)) {
					result.append("#");
				} else {
					result.append("?");
					targetLetterCount.computeIfPresent(c, (k, v) -> v - 1);
				}
			} else {
				result.append("#");
			}

			guessLetterCount.computeIfPresent(c, (k, v) -> v - 1);

		}

		return result.toString();
	}

	protected void messages(String id, String... args) {
		switch (id) {
			case "start" -> System.out.printf("You have %d tries to guess the target word.\n", tries);
			case "input" -> System.out.print("Please enter your guess: ");
			case "round" -> System.out.printf("%s: %s  %s: %s\n", args[0], args[1], args[0], args[2]);
			case "invalidLength" -> System.err.print("Please enter a word of 5 letters: ");
			case "invalidGuess" -> System.err.printf("[%s] is not in the dictionary or is invalid. Please try again: ", args[0]);
			case "alreadyGuessed" -> System.err.printf("You have already guessed [%s]. Please try again: ", args[0]);
		}
	}
}
