package monash.assignment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandleTest {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

	private Commandle commandle;

	private String targetWord;

	@Mock
	private Game game;

	@Mock
	private WordGenerator wordGenerator;

	@BeforeEach
	public void setup() {
		System.setOut(new PrintStream(outContent));

		targetWord = "apple";

		commandle = new Commandle();
		commandle.setWordGenerator(wordGenerator);
		commandle.setGame(game);
		commandle.setTestFlag(true);
	}

	@Test
	public void testLoadDictionaryWithValidPath() throws IOException {
		// given
		String path = "src/main/resources/dictionary.txt";

		// when
		commandle.loadDictionary(path);

		// then
		assertEquals(12947, commandle.getWordList().size());
	}

	@Test
	public void testLoadDictionaryWithInvalidPath() {
		// given
		String path = "dictionary.text";

		// then
		assertThrows(IOException.class, () -> commandle.loadDictionary(path));
	}

	@Test
	public void testRunWithNoArgs() throws IOException {
		// given
		String path = "src/main/resources/dictionary.txt";
		String[] args = {};

		// when
		when(wordGenerator.generateTargetWord()).thenReturn(targetWord);
		when(game.start()).thenReturn(false);

		commandle.loadDictionary(path);
		commandle.run(args);

		// then
		assertEquals(targetWord, commandle.getTargetWord());
	}

	@Test
	public void testRunWithValidArgs() throws IOException {
		// given
		String path = "src/main/resources/dictionary.txt";
		String[] args = {targetWord};

		// when
		when(wordGenerator.generateTargetWord(targetWord)).thenReturn(targetWord);
		when(game.start()).thenReturn(false);

		commandle.loadDictionary(path);
		commandle.run(args);

		// then
		assertEquals(targetWord, commandle.getTargetWord());
	}

	@Test
	public void testRunWithInvalidArgs() throws IOException {
		// given
		String path = "src/main/resources/dictionary.txt";
		String[] args = {"banana"};

		// when
		commandle.loadDictionary(path);
		commandle.run(args);

		// then
		assertNull(commandle.getTargetWord());
	}

	@Test
	public void testRunWithEmptyDictionary() {
		// given
		String[] args = {targetWord};

		commandle.setWordList(new ArrayList<>());

		// when
		commandle.run(args);

		// then
		assertNull(commandle.getTargetWord());
	}

	@Test
	public void testCongratsMessage() throws Exception {
		// given
		String message = tapSystemOut(() -> commandle.messages("win"));

		// then
		assertEquals("Congratulations! You have guessed the target word!\r\n", message);
	}

	@Test
	public void testLoseMessage() throws Exception {
		// given
		String message = tapSystemOut(() -> commandle.messages("lose"));

		// then
		assertEquals("You have run out of tries. The target word was [null]\r\n", message);
	}

	@Test
	public void testPlayAgainMessage() throws Exception {
		// given
		String message = tapSystemOut(() -> commandle.messages("again"));

		// then
		assertEquals("Play again? (Y/N): ", message);
	}

	@Test
	public void testEndMessage() throws Exception {
		// given
		String message = tapSystemOut(() -> commandle.messages("end"));

		// then
		assertEquals("Thank you for playing Commandle!\r\n", message);
	}

	@Test
	public void testPlayAgainWithNo() throws Exception {
		// given
		String again = "Play again? (Y/N): ";
		String end = "Thank you for playing Commandle!\r\n";

		// when
		withTextFromSystemIn("n")
				.execute(() -> commandle.playAgain());

		// then
		assertEquals(again + end, outContent.toString());
	}

	@Test
	public void testPlayAgainWithYes_whenGameWon() throws Exception {
		// given
		String again = "Play again? (Y/N): ";
		String win = "Congratulations! You have guessed the target word!\r\n";

		commandle.setWordList(List.of("apple"));

		// when
		when(wordGenerator.generateTargetWord()).thenReturn("apple");
		when(game.start()).thenReturn(true);

		withTextFromSystemIn("y")
				.execute(() -> commandle.playAgain());

		// then
		assertEquals(again + win, outContent.toString());
	}

	@Test
	public void testPlayAgainWithYes_whenGameLost() throws Exception {
		// given
		String again = "Play again? (Y/N): ";
		String lose = "You have run out of tries. The target word was [apple]\r\n";

		commandle.setWordList(List.of("apple"));

		// when
		when(wordGenerator.generateTargetWord()).thenReturn("apple");
		when(game.start()).thenReturn(false);


		withTextFromSystemIn("y")
				.execute(() -> commandle.playAgain());

		// then
		assertEquals(again + lose, outContent.toString());
		assertNotNull(commandle.getGame());
	}

	@Test
	public void testUniqueTargetWordsForSession() {
		// given
		List<String> sessionTargets = new ArrayList<>();
		List<String> wordList = List.of("apple", "woman", "ultra", "pears", "grape");
		int size = wordList.size();

		commandle.setWordList(wordList);

		// when
		when(wordGenerator.generateTargetWord()).thenReturn("apple", "woman", "ultra", "pears", "grape");

		for (int i = 0; i < size; i++) {
			commandle.setTargetWord();
			sessionTargets.add(commandle.getTargetWord());
		}

		// then
		assertEquals(size, sessionTargets.stream().distinct().count());
	}
}