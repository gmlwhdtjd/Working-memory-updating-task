package WMUT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;

public class WordSet {
	private class Word {
		protected String text;
		protected int referenceNumber = -1; // 0, 1, 2

		public Word(String w) {
			text = w;
		}
	}

	private Random random = new Random(System.currentTimeMillis());

	private Vector<Word> words = new Vector<Word>();

	public boolean loadingFromFile(File file) {
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

			String line = null;
			while ((line = reader.readLine()) != null) {

				String word = line.split(",")[0];

				if (word.startsWith("\uFEFF")) {
					words.add(new Word(word.substring(1)));
				} else {
					words.add(new Word(word));
				}
			}

			reader.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	public String getWordFor(int cardNumber) {
		while (true) {
			Word getWord = words.get(random.nextInt(words.size()));

			if (getWord.referenceNumber == -1 || getWord.referenceNumber == cardNumber) {
				getWord.referenceNumber = cardNumber;
				return getWord.text;
			}
		}
	}
	
	public void cardNumberReset() {
		for (int i = 0; i < words.size(); i++) {
			words.get(i).referenceNumber = -1;
		}
	}
}
