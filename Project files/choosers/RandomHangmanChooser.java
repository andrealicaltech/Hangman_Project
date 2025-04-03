package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class RandomHangmanChooser implements IHangmanChooser {

  private final String rand_word;
  private int maxGuesses;
  private SortedSet<Character> all_guesses = new TreeSet<>();
  private static final Random RANDOM = new Random();

  public RandomHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {
      this.maxGuesses = maxGuesses;

      if (wordLength < 1|| maxGuesses < 1) {
        throw new IllegalArgumentException("Illegal Argument Exception!") {};
      }
      //throw exception if no words of wordLength

      SortedSet<String> wordSet = new TreeSet<>();

      try {
        File myObj = new File("data/scrabble.txt");
        Scanner fr = new Scanner(myObj);
        while (fr.hasNextLine()) {
          String temp = fr.nextLine();
          if (temp.length() == wordLength) {
            wordSet.add(temp);
          }//only add words that are of wordLength
        }
        if (wordSet.isEmpty()) {
          throw new IllegalStateException("Illegal State Exception1");
        } //no words found of wordLength
        fr.close();
      } catch (FileNotFoundException e) {
        System.out.println("Error with file reading");
        e.printStackTrace();
      }//try-catch to read file, add to wordSet, and check exceptions

      String[] wordArray = wordSet.toArray(new String[0]);
      rand_word = wordArray[RANDOM.nextInt(wordArray.length)]; //final rand_word
  }    

  @Override
  public int makeGuess(char letter) {

    if (letter > 122 || letter < 97 || all_guesses.contains(letter)) {
      throw new IllegalArgumentException();
    }//check lowercase, previous guesses

    all_guesses.add(letter); //if exception is not thrown, that means hasn't guessed letter yet

    if (this.maxGuesses < 1) {
      throw new IllegalStateException("Illegal Argument Exception!");
    }//check guesses

    int count = 0;
    for (char c: rand_word.toCharArray()) {
      if (c == letter) {
        count++;
      }
    }

    if (count == 0) {
      maxGuesses--; //decrement number of guesses
    }

    return count; //return occurrences of letter in rand_word
  }

  @Override
  public boolean isGameOver() {
    int counter = 0;
    for (Character c: rand_word.toCharArray()) {
      for (Character g: all_guesses) {
        if (c.equals(g)) {
          counter++; //increment if guessed correct letter in word
        }
      }
    }

    if (counter == rand_word.length()) {
      return true; //if guessed all letters in rand_word, return true
    }

    if (maxGuesses == 0) {
      return true;
    }
    return false;
  }

  @Override
  public String getPattern() {
    String template = "";

    for (int i = 0; i < rand_word.length(); i++) {

        if (all_guesses.contains(rand_word.charAt(i))) {
          template += rand_word.charAt(i);
        } else {
          template += "-";
        }
    }
    return template;
  }

  @Override
  public SortedSet<Character> getGuesses() {
    return all_guesses;
  }

  @Override
  public int getGuessesRemaining() {
    return maxGuesses;
  }

  @Override
  public String getWord() {
    maxGuesses = 0;
    return rand_word;
  }
}