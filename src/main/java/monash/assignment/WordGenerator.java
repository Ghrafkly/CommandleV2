package monash.assignment;

import lombok.Data;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

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
	 * @return A random word from the dictionary that has not been used in the session
	 */
	public String generateTargetWord() {
		List<String> availableWords = new ArrayList<>(wordList);
		sessionTargets.forEach(availableWords::remove);
		return availableWords.get(new Random().nextInt(availableWords.size()));
	}
}
