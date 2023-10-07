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
	public void testLoadDictionary_whenValidPath() throws IOException {
		// given
		String path = "src/main/resources/dictionary.txt";

		// when
		commandle.loadDictionary(path);

		// then
		assertEquals(12947, commandle.getDictionary().size());
	}

	@Test
	public void testLoadDictionary_whenInvalidPath() {
		// given
		String path = "dictionary.text";

		// then
		assertThrows(IOException.class, () -> commandle.loadDictionary(path));
	}

	@Test
	public void testRun_whenNoArgs() throws IOException {
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
	public void testRun_whenValidArgs() throws IOException {
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
	public void testRun_whenInvalidArgs() throws IOException {
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
	public void testRun_whenEmptyDictionary() {
		// given
		String[] args = {targetWord};

		commandle.setDictionary(new ArrayList<>());

		// when
		commandle.run(args);

		// then
		assertNull(commandle.getTargetWord());
	}

	@Test
	public void testMessage_Congrats() throws Exception {
		// given
		String message = tapSystemOut(() -> commandle.messages("win"));

		// then
		assertEquals("Congratulations! You have guessed the target word!\r\n", message);
	}

	@Test
	public void testMessage_Lose() throws Exception {
		// given
		String message = tapSystemOut(() -> commandle.messages("lose"));

		// then
		assertEquals("You have run out of tries. The target word was [null]\r\n", message);
	}

	@Test
	public void testMessage_PlayAgain() throws Exception {
		// given
		String message = tapSystemOut(() -> commandle.messages("again"));

		// then
		assertEquals("Play again? (Y/N): ", message);
	}

	@Test
	public void testMessage_End() throws Exception {
		// given
		String message = tapSystemOut(() -> commandle.messages("end"));

		// then
		assertEquals("Thank you for playing Commandle!\r\n", message);
	}

	@Test
	public void testPlayAgain_whenNo() throws Exception {
		// given
		String end = "Thank you for playing Commandle!\r\n";

		// when
		withTextFromSystemIn("n")
				.execute(() -> commandle.playAgain());

		// then
		assertTrue(outContent.toString().contains(end));
	}

	@Test
	public void testPlayAgain_whenYes() throws Exception {
		// given
		commandle.setDictionary(List.of("apple"));

		// when
		when(wordGenerator.generateTargetWord()).thenReturn("apple");
		when(game.start()).thenReturn(true);

		withTextFromSystemIn("y")
				.execute(() -> commandle.playAgain());

		// then
		assertNotNull(commandle.getTargetWord());
	}

	@Test
	public void testUniqueTargetWordsForSession() {
		// given
		List<String> sessionTargets = new ArrayList<>();
		List<String> dictionary = List.of("apple", "woman", "ultra");
		int size = dictionary.size();

		commandle.setDictionary(dictionary);

		// when
		when(wordGenerator.generateTargetWord()).thenReturn("apple", "woman", "ultra");

		for (int i = 0; i < size; i++) {
			commandle.setTargetWord();
			sessionTargets.add(commandle.getTargetWord());
		}

		// then
		assertEquals(size, sessionTargets.stream().distinct().count());
	}
}