package monash.assignment;

import lombok.Data;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * The WordGenerator class handles the logic for generating a target word for a game of Commandle
 * A target word is defined as the word that the user is trying to guess
 *
 * <p>Variables defined in this class:</p>
 * <ol>
 *     <li>wordList - the list of words that can be used as the target word</li>
 *     <li>sessionTargets - the list of words that have been used as a target word in the current session</li>
 *     <li>targetWord - the word that the user is trying to guess</li>
 * </ol>
 */
@Data
public class WordGenerator {
	private static final Logger log = LogManager.getLogger(WordGenerator.class);

	@NonNull
	private List<String> wordList;
	@NonNull
	private Set<String> sessionTargets;

	private String targetWord;

	/**
	 * @param word The word to be used as the target word
	 * @return The target word to be used for the game.
	 * If the word has already been used in the session, a new target word will be generated
	 * with {@link #generateTargetWord()}
	 */
	public String generateTargetWord(String word) {
		if (sessionTargets.contains(word)) {
			log.warn(String.format("Word [%s] has already been used in this session. Another target word will be generated", word));
			word = generateTargetWord();
		} else {
			sessionTargets.add(word);
		}

		return word;
	}

	/**
	 * <p>Variables defined in this method:</p>
	 * <ol>
	 *     <li>availableWords - the list of words that have not been used as the target word in the current session</li>
	 * </ol>
	 *
	 * @return A random word from the dictionary that has not been used in the session
	 */
	public String generateTargetWord() {
		List<String> availableWords = new ArrayList<>(wordList);
		sessionTargets.forEach(availableWords::remove);
		return availableWords.get(new Random().nextInt(availableWords.size()));
	}
}
