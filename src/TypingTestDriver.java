import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

/**
 * @author Husam Saleem
 */
public class TypingTestDriver extends JFrame {
	private static final long serialVersionUID = 1L;

	ArrayList<String> wordList = new ArrayList<String>();
	String userInput = "";
	String wordListAsString = "";

	final int SECONDS = 60;
	int secondsElapsed = 0;

	final int MAX_WORDS = 100;

	boolean timerFinished = false;

	public static JFrame frame;
	public static JTextField inputField = new JTextField();
	public static JTextPane allWordsText = new JTextPane();
	public static JTextArea clock = new JTextArea();
	public static JTextArea accuracy = new JTextArea();
	public static JTextArea wordsPerMinute = new JTextArea();
	public static JTextArea correctWords = new JTextArea();
	public static JButton startBtn = new JButton("Start");
	public static JButton easyBtn = new JButton("Easy");
	public static JButton challengeBtn = new JButton("Challenging");
	public static JButton insaneBtn = new JButton("Insane");
	public static JButton stopBtn = new JButton("Stop");

	/**
	 * <h1>Creates and shows the main GUI</h1>
	 * 
	 * @param s This is the reference to this main driver class
	 */
	public static void createAndShowGui(TypingTestDriver app) {
		frame = new JFrame();// creating instance of JFrame
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// User Input Field
		inputField.setBounds(75, 380, 350, 40);// x axis, y axis, width, height
		inputField.setEditable(false);

		// Will listen to the input field when user inputs something new
		inputField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				app.getInput();
				allWordsText.getHighlighter().removeAllHighlights();
				app.updateStatisticsAndPosition();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				app.getInput();
				allWordsText.getHighlighter().removeAllHighlights();
				app.updateStatisticsAndPosition();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				app.getInput();
				allWordsText.getHighlighter().removeAllHighlights();
				app.updateStatisticsAndPosition();
			}
		});

		startBtn.setBounds(545, 200, 80, 30);
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.start();
				startBtn.setVisible(false);
				stopBtn.setVisible(true);

				easyBtn.setVisible(false);
				challengeBtn.setVisible(false);
				insaneBtn.setVisible(false);

				inputField.setEditable(true);
				inputField.requestFocus();
			}
		});

		easyBtn.setBounds(535, 25, 100, 30);
		challengeBtn.setBounds(535, 75, 100, 30);
		insaneBtn.setBounds(535, 125, 100, 30);

		stopBtn.setBounds(545, 250, 80, 30);
		stopBtn.setVisible(false);
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.timerFinished = true;
			}

		});

		easyBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				app.showWordsOnScreen("easywords.txt");
				inputField.setEditable(false);
			}

		});

		challengeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				app.showWordsOnScreen("challengewords.txt");
				inputField.setEditable(false);
			}

		});

		insaneBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				app.showWordsOnScreen("insane");
				inputField.setEditable(false);
			}

		});

		allWordsText.setBounds(5, 15, 500, 360);
		Font trb = new Font("TimesRoman", Font.PLAIN, 16);
		allWordsText.setFont(trb);
		allWordsText.setEditable(false);

		clock.setBounds(28, 425, 150, 15);
		clock.setBackground(null);
		clock.setEditable(false);

		accuracy.setBounds(28, 450, 150, 15);
		accuracy.setBackground(null);
		accuracy.setEditable(false);

		wordsPerMinute.setBounds(28, 475, 150, 15);
		wordsPerMinute.setBackground(null);
		wordsPerMinute.setEditable(false);

		correctWords.setBounds(28, 500, 150, 15);
		correctWords.setBackground(null);
		correctWords.setEditable(false);

		frame.add(inputField);// adding button in JFrame
		frame.add(allWordsText);
		frame.add(clock);
		frame.add(accuracy);
		frame.add(wordsPerMinute);
		frame.add(correctWords);
		frame.add(startBtn);
		frame.add(easyBtn);
		frame.add(challengeBtn);
		frame.add(insaneBtn);
		frame.add(stopBtn);

		frame.setSize(675, 600);// 800 width and 500 height
		frame.setLayout(null);// using no layout managers
		frame.setVisible(true);// making the frame visible
	}

	public static void main(String[] args) throws IOException {
		TypingTestDriver app = new TypingTestDriver();
		app.generateInsanelyDifficultWordList();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				createAndShowGui(app);
			}
		};
		EventQueue.invokeLater(r);
	}

	private void showWordsOnScreen(String txtFile) {
		reset();

		if (txtFile.equals("insane")) {
			generateInsanelyDifficultWordList();
		} else {
			readFromFile(txtFile);
			shuffleWords();
		}
		wordListAsString = getWordsAsString();
		allWordsText.setText(wordListAsString);
	}

	private void generateInsanelyDifficultWordList() {
		wordList.clear();

		Random randLength = new Random();
		Random randIndex = new Random();
		Random randRoll = new Random();

		String[] alphabet = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
				"s", "t", "u", "v", "w", "x", "y", "z" };
		String[] specialCharacters = { ",", ";", "!", "@", "$", "&", "%", "#", "*", "(", ")" };

		final int MAX_WORD_LENGTH = 12;
		final int MIN_WORD_LENGTH = 5;
		final int SPECIAL_CHARACTER_CHANCE = 15;
		final int UPPERCASE_CHANCE = 25;

		for (int i = 0; i < MAX_WORDS; i++) {
			int length = randLength.nextInt(MAX_WORD_LENGTH - MIN_WORD_LENGTH) + MIN_WORD_LENGTH;
			String word = "";

			for (int j = 0; j < length; j++) {
				int specialCharacterChance = randRoll.nextInt(101);
				int index = 0;

				if (SPECIAL_CHARACTER_CHANCE >= specialCharacterChance) {
					index = randIndex.nextInt(specialCharacters.length);
					word += specialCharacters[index];
				} else {
					index = randIndex.nextInt(alphabet.length);

					int upperCaseChance = randRoll.nextInt(101);

					if (UPPERCASE_CHANCE >= upperCaseChance) {
						word += alphabet[index].toUpperCase();
					} else {
						word += alphabet[index].toLowerCase();
					}
				}
			}

			wordList.add(word);
		}
	}

	private void start() {
		startTimer();
	}

	public String getWordsAsString() {
		String result = "";

		int i = 0;
		for (String s : wordList) {
			result += s + " ";

			i++;
			if (i >= MAX_WORDS)
				break;
		}

		result = result.trim();
		return result;
	}

	private void readFromFile(String txtFile) {
		File file;
		Scanner fileReader = null;
		wordList.clear();
		try {
			file = new File("./TextFiles/" + txtFile);
			fileReader = new Scanner(file);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (fileReader.hasNext()) {
			String word = fileReader.next();
			wordList.add(word);
		}
	}

	private void shuffleWords() {
		Random rand = new Random();

		String temp = null;

		for (int i = 0; i < MAX_WORDS; i++) {
			int randIndex = rand.nextInt(wordList.size());

			temp = wordList.get(i);
			wordList.set(i, wordList.get(randIndex));
			wordList.set(randIndex, temp);
		}
	}

	private void getInput() {
		userInput = inputField.getText();
	}

	private void updateClock() {
		clock.setText("Time Remaining: " + (SECONDS - secondsElapsed));
	}

	private void startTimer() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				timerFinished = true;
			}
		};

		TimerTask increaseTime = new TimerTask() {
			public void run() {
				if (!timerFinished) {
					secondsElapsed++;
				} else {
					timer.cancel();
					inputField.setEditable(false);

					stopBtn.setVisible(false);
					startBtn.setVisible(true);
					easyBtn.setVisible(true);
					challengeBtn.setVisible(true);
					insaneBtn.setVisible(true);
				}

				if (userInput.length() >= wordListAsString.length()) {
					timer.cancel();

					stopBtn.setVisible(false);
					startBtn.setVisible(true);
					easyBtn.setVisible(true);
					challengeBtn.setVisible(true);
					insaneBtn.setVisible(true);

					inputField.setEditable(false);
				}

				updateClock();
			}
		};

		timer.schedule(increaseTime, 0, 1000);
		timer.schedule(task, SECONDS * 1000);
	}

	private void reset() {
		userInput = "";
		wordListAsString = "";
		secondsElapsed = 0;
		timerFinished = false;
		inputField.setEditable(true);
		inputField.setText("");
		accuracy.setText("");
		wordsPerMinute.setText("");
	}

	private void updateStatisticsAndPosition() {
		int wrongIndexCharCount = 0;

		for (int i = 0; i < userInput.length(); i++) {
			if (userInput.charAt(i) != wordListAsString.charAt(i)) {
				highlightCurrentPosition("red", i);
				wrongIndexCharCount++;
			} else {
				highlightCurrentPosition("green", i);
			}
		}

		// Calculate the words per minute
		float grossWPM = (userInput.length() / 5) / (secondsElapsed / 60f);
		float errorRate = wrongIndexCharCount / (secondsElapsed / 60f);
		float netWPM = grossWPM - errorRate;

		if (netWPM < 0)
			netWPM = 0;
		wordsPerMinute.setText("Estimated WPM: " + Math.round(netWPM));

		// Calculate the accuracy
		float accuracyCalc = ((userInput.length() - wrongIndexCharCount) / (float) userInput.length()) * 100f;
		accuracy.setText("Accuracy: " + Math.round(accuracyCalc) + "%");
	}

	private void highlightCurrentPosition(String colorChoice, int startIndex) {
		Color color = null;
		if (colorChoice.equals("red")) {
			color = Color.red;
		} else {
			color = Color.green;
		}

		Highlighter highlighter = allWordsText.getHighlighter();
		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(color);
		try {
			highlighter.addHighlight(startIndex, startIndex + 1, painter);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}
}