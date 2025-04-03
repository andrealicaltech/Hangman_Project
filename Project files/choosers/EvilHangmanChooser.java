package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class EvilHangmanChooser implements IHangmanChooser {

  private SortedSet<Character> all_guesses;
  private int guesses;
  private String currPattern = "";
  private SortedSet<String> wordSet;


  public EvilHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {

    if (wordLength < 1|| maxGuesses < 1) {
      throw new IllegalArgumentException("Illegal Argument Exception!") {};
    }
    //throw exception if no words of wordLength

    wordSet = new TreeSet<>();

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
    }

    this.guesses = maxGuesses;
    this.wordSet = new TreeSet<>(wordSet);
    this.all_guesses = new TreeSet<>();

    for (int i = 0; i < wordLength; i++) {
      this.currPattern += '-';
    }

  }

  @Override
  public int makeGuess(char letter) {
    int count = 0; //count how many words are in largest group

    if (letter > 122 || letter < 97 || this.all_guesses.contains(letter)) {
      throw new IllegalArgumentException();
    }//check lowercase, previous guesses

    this.all_guesses.add(letter); //if exception is not thrown, that means hasn't guessed letter yet

    if (this.guesses < 1) {
      throw new IllegalStateException("Illegal Argument Exception!");
    }//check guesses

    Map<String, TreeSet<String>> patternMap = new TreeMap<>(); //pattern --> all words of that pattern

    //create patterns
    for (String word: wordSet) {
      String template = "";
      for (char c: word.toCharArray()) {
        if (all_guesses.contains(c)) {
          template += c;
        } else {
          template += '-';
        }
      }
      if (patternMap.containsKey(template)) {
        patternMap.get(template).add(word);
      } else {
        TreeSet<String> newPattern = new TreeSet<>();
        newPattern.add(word);
        patternMap.put(template, newPattern);
      }
    }

    int max = 0;

    for (String pattern: patternMap.keySet()) {
      if (max < patternMap.get(pattern).size()) {
        max = patternMap.get(pattern).size();
        currPattern = pattern;
      }
    } //choose largest set

    wordSet = patternMap.get(currPattern);

    for (char c: currPattern.toCharArray()) {
      if (c == letter) {
        count++;
      }
    }

    if (count == 0) {
      this.guesses--; //decrement number of guesses if there are no letters of word guessed
    }

    return count;
  }

  @Override
  public boolean isGameOver() {
    int counter = 0;
    for (Character c: this.currPattern.toCharArray()) {
      for (Character g: this.all_guesses) {
        if (c.equals(g)) {
          counter++; //increment if guessed correct letter in word
        }
      }
    }

    if (counter == this.currPattern.length()) {
      return true; //if guessed all letters in rand_word, return true
    }

    if (this.guesses == 0) {
      return true;
    }
    return false;
  }

  @Override
  public String getPattern() {
    return this.currPattern;
  }

  @Override
  public SortedSet<Character> getGuesses() {
    return this.all_guesses;
  }

  @Override
  public int getGuessesRemaining() {
    return this.guesses;
  }

  @Override
  public String getWord() {
    this.guesses = 0;
    return this.wordSet.first();
  }
}