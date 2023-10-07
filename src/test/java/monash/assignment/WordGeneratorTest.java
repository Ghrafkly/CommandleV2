package monash.assignment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WordGeneratorTest {
	private WordGenerator wordGenerator;

	private List<String> dictionary;

	private Set<String> sessionTargets;

	@BeforeEach
	public void setup() {
		dictionary = List.of("apple", "whirs", "ultra", "pears", "grape");
		sessionTargets = new HashSet<>();

		wordGenerator = new WordGenerator(dictionary, sessionTargets);
	}

	@Test
	public void testGenerateTargetWord_whenValidWord() {
		// given
		String word = "apple";

		// when
		String result = wordGenerator.generateTargetWord(word);

		// then
		assertEquals(word, result);
	}

	@Test
	public void testGenerateTargetWord_whenAlreadyUsedWord() {
		// given
		String word = "apple";
		sessionTargets.add(word);

		// when
		String result = wordGenerator.generateTargetWord(word);

		// then
		assertNotEquals(word, result);
	}

	@Test
	public void testGenerateTargetWord_whenNoWord() {
		// given
		String word = "";

		// when
		String result = wordGenerator.generateTargetWord(word);

		// then
		assertNotEquals(word, result);
	}

	@Test
	public void testGenerateTargetWord_whenInvalidWord() {
		// given
		String word = "banana";

		// when
		String result = wordGenerator.generateTargetWord(word);

		// then
		assertNotEquals(word, result);
	}

	@Test
	public void testGenerateTargetWord_whenNoWordPassed() {
		// when
		String result = wordGenerator.generateTargetWord();

		// then
		assertNotNull(result);
	}
}