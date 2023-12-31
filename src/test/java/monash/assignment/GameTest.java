package monash.assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameTest {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	private Game game;

	private String targetWord;

	private List<String> dictionary;

	@BeforeEach
	public void setup() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));

		dictionary = List.of("apple", "pears", "whirs", "easel", "upper", "abers", "bezel");
		targetWord = "apple";

		game = new Game(targetWord, dictionary, 6);
	}

	@Test
	public void testStartGame() throws Exception {
		// given
		game.setTries(1);
		game.setRound(2);

		String message = tapSystemOut(() -> game.messages("start"));

		// when
		game.start();

		// then
		assertEquals(message, outContent.toString());
	}

	@Test
	public void testGuessValidity_whenValidGuess() {
		// given
		boolean result = game.guessValidity("apple");

		// then
		assertTrue(result);
		assertEquals("", outContent.toString());
	}

	@Test
	public void testGuessValidity_whenInvalidLength() {
		// given
		boolean result = game.guessValidity("banana");

		// then
		assertFalse(result);
		assertEquals("Please enter a word of 5 letters: ", errContent.toString());
	}

	@Test
	public void testGuessValidity_whenDuplicateGuess() {
		// given
		game.getGuesses().add("apple");
		boolean result = game.guessValidity("apple");

		// then
		assertFalse(result);
		assertEquals("You have already guessed [apple]. Please try again: ", errContent.toString());
	}

	@Test
	public void testGuessValidity_whenDuplicateGuessInvalidLength() {
		// given
		game.getGuesses().add("banana");
		boolean result = game.guessValidity("banana");

		// then
		assertFalse(result);
		assertEquals("Please enter a word of 5 letters: ", errContent.toString());
	}

	@Test
	public void testGuessValidity_whenDuplicateGuessInvalidWord() {
		// given
		game.getGuesses().add("lapel");
		boolean result = game.guessValidity("lapel");

		// then
		assertFalse(result);
		assertEquals("You have already guessed [lapel]. Please try again: ", errContent.toString());
	}

	@Test
	public void testGuessValidity_whenInvalidGuess() {
		// given
		boolean result = game.guessValidity("lapel");

		// then
		assertFalse(result);
		assertEquals("[lapel] is not in the dictionary or is invalid. Please try again: ", errContent.toString());
	}

	@Test
	public void testGuessValidity_whenNonAlphaGuessInvalidLength() {
		// given
		boolean result = game.guessValidity("1234");

		// then
		assertFalse(result);
		assertEquals("Please enter a word of 5 letters: ", errContent.toString());
	}

	@Test
	public void testGuessValidity_whenNonAlphaGuessValidLength() {
		// given
		boolean result = game.guessValidity("12345");

		// then
		assertFalse(result);
		assertEquals("[12345] is not in the dictionary or is invalid. Please try again: ", errContent.toString());
	}

	@Test
	public void testCheckGuess_whenCorrectGuess() {
		// given
		String result = game.checkGuess("apple");

		// then
		assertEquals(targetWord, result);
	}

	@Test
	public void testCheckGuess_whenIncorrectGuess() {
		// given
		String result = game.checkGuess("whirs");

		// then
		assertEquals("#####", result);
	}

	@Test
	public void testCheckGuess_whenPartiallyCorrectGuess() {
		// given
		String result = game.checkGuess("pelpa");

		// then
		assertEquals("?????", result);
	}

	@Test
	public void testCheckGuess_whenMixedGuess() {
		// given
		String result = game.checkGuess("grape");

		// then
		assertEquals("##??e", result);
	}

	@Test
	public void testCheckGuess_whenCaseInsensitiveGuess() {
		// given
		String result = game.checkGuess("ApPlE");

		// then
		assertEquals(targetWord, result);
	}

	@Test
	public void testStart_canWin() throws Exception {
		// given
		String message = """
				You have 6 tries to guess the target word.
				Please enter your guess: 1: apple  1: apple
				""";

		// when
		withTextFromSystemIn("apple").execute(() -> {
			boolean result = game.start();
			assertTrue(result);
		});

		// then
		assertEquals(message, outContent.toString());
	}

	@Test
	public void testStart_canLose() throws Exception {
		// given
		List<Boolean> results = new ArrayList<>();

		game.setDictionary(dictionary);

		String message = """
				You have 6 tries to guess the target word.
				Please enter your guess: 1: pears  1: ???##
				Please enter your guess: 2: whirs  2: #####
				Please enter your guess: 3: easel  3: ??##?
				Please enter your guess: 4: upper  4: #pp?#
				Please enter your guess: 5: abers  5: a#?##
				Please enter your guess: 6: bezel  6: #?##?
				""";

		// when
		withTextFromSystemIn("pears", "whirs", "easel", "upper", "abers", "bezel")
				.execute(() -> results.add(game.start()));

		// then
		results.forEach(Assertions::assertFalse);
		assertEquals(message, outContent.toString());
	}

	@Test
	public void testInvalidGuess_DoesNotIncreaseTurnCounter() throws Exception {
		// given
		List<Boolean> results = new ArrayList<>();

		game.setTries(2);
		game.setDictionary(dictionary);

		String message = """
				You have 2 tries to guess the target word.
				Please enter your guess: 1: pears  1: ???##
				Please enter your guess: 2: whirs  2: #####
				""";

		String errMessage = "Please enter a word of 5 letters: ";

		// when
		withTextFromSystemIn("pears", "banana", "whirs")
				.execute(() -> results.add(game.start()));

		// then
		results.forEach(Assertions::assertFalse);
		assertEquals(3, game.getRound());
		assertEquals(Set.of("whirs", "pears"), game.getGuesses());
		assertEquals(message, outContent.toString());
		assertEquals(errMessage, errContent.toString());
	}
}