package edu.caltech.cs2.project02.guessers;

import edu.caltech.cs2.project02.interfaces.IHangmanGuesser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class AIHangmanGuesser implements IHangmanGuesser {

  private static String filePath = "data/scrabble.txt";

  @Override
  public char getGuess(String pattern, Set<Character> guesses) throws FileNotFoundException {
    SortedSet<String> dictionary = getWordSet(pattern);
    SortedSet<String> matchSet = new TreeSet<>();

    for (String word: dictionary) {
      if (fitPattern(pattern, word, guesses)) { //if word matches pattern = return true
        matchSet.add(word);
      }
    }

    Map<Character, Integer> countMap = new TreeMap<>();

    for (String filter_word: matchSet) { //iterate through each word that matches pattern
      for (Character letter: filter_word.toCharArray()) {
        if (!guesses.contains(letter)) { //letter cannot be guessed before
          if (countMap.containsKey(letter)) { //if countMap already has the letter
            countMap.put(letter, countMap.get(letter)+1); //increment count
          } else {
            countMap.put(letter, 1); //set count to 1
          }
        }
      }
    }

    int max = 0;
    for (Character c: countMap.keySet()) {
      if (countMap.get(c) > max) {
        max = countMap.get(c);
      }
    }

    SortedSet<Character> guessSet = new TreeSet<>();

    for (Character c: countMap.keySet()) {
      if (countMap.get(c) == max) {
        guessSet.add(c);
      }
    }
    return guessSet.first();
  }

  private Boolean fitPattern(String pattern, String word, Set<Character> guesses) {

    for (int i = 0; i < pattern.length(); i++) {
      if (word.charAt(i) != pattern.charAt(i) && pattern.charAt(i) != '-') { //letters
        //letters: don't match up, return false
        return false;
      } else if (pattern.charAt(i) == '-' && guesses.contains(word.charAt(i))) { //'-'
        return false;
      }
    }
    return true;
  }//fitPattern method

  private SortedSet<String> getWordSet(String pattern) {
    SortedSet<String> wordSet = new TreeSet<>();

    try {
      File myObj = new File(filePath);
      Scanner fr = new Scanner(myObj);
      while (fr.hasNextLine()) {
        String temp = fr.nextLine();
        if (temp.length() == pattern.length()) {
          wordSet.add(temp);
        }//only add words that are of pattern length
      }
      if (wordSet.isEmpty()) {
        throw new IllegalStateException("Illegal State Exception1");
      } //no words found of wordLength
      fr.close();
    } catch (FileNotFoundException e) {
      System.out.println("Error with file reading");
      e.printStackTrace();
    }

    return wordSet;
  }//getWordSet method

}//AIHangmanGuesser Class
