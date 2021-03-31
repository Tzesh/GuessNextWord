/**
package edu.anadolu.game;

import edu.anadolu.Main;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class GuessNextWord {
    private Scanner scanner = new Scanner(System.in);
    private boolean isGameStarted;
    private final int delay = 1000;
    private final int period = 1000;
    private int interval;
    private Timer timer;
    private HashSet<String> vocabulary;
    private String lastWord;
    private boolean player = false;

    public void startGame() throws IOException {
        isGameStarted = true;
        vocabulary = new HashSet<>();
        lastWord = null;
        timer = new Timer();
        interval = 30;
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                setInterval();
            }
        }, delay, period);
        System.out.println("Game has begun!");
        while (isGameStarted) {
            guess(getWord());
        }
    }

    public String getWord() throws IOException {
        if (player) {
            System.out.println("Your turn: " + lastWord);
            String word = scanner.nextLine();
            return word;
        }
        else return Main.getword(lastWord);
    }


    public boolean guess(String word) {
        if (word.isBlank() || word.isEmpty()) {
            System.out.println("Wrong usage! Please make sure that your word is at least consists 3 letters");
            return false;
        }
        if (vocabulary.contains(word)) {
            System.out.println("This word has been used already");
            return false;
        }
        if (lastWord != null && !word.substring(0, 2).equals(lastWord.substring(lastWord.length() - 2))) {
            System.out.println("New word is not starting with the last 2 letters of the last one");
            return false;
        }
        vocabulary.add(word);
        lastWord = word;
        changeTurns();
        return true;
    }

    private int setInterval() {
        if (interval == 15) System.out.println("15 seconds left");
        if (interval < 10) System.out.println(String.format("%s second(s) left!", interval));
        if (interval == 1) {
            System.out.println("Timed out!");
            System.out.println("Player " + (player ? 1 : 2) + " won!");
            isGameStarted = false;
            timer.cancel();
        }
        return --interval;
    }

    public boolean isGameOver() {
        return isGameStarted;
    }

    public boolean getPlayer() {
        return player;
    }

    private void changeTurns() {
        player = !player;
        interval = 30;
    }
}
 */
