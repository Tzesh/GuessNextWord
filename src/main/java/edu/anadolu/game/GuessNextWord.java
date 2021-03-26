package edu.anadolu.game;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class GuessNextWord {
    private final int delay = 1000;
    private final int period = 1000;
    private int interval;
    private Timer timer;
    private HashSet<String> vocabulary = new HashSet<>();
    private String lastWord = null;
    private boolean player = false;

    public void startGame() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Input seconds => : ");
        timer = new Timer();
        interval = 30;
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                setInterval();
            }
        }, delay, period);
        System.out.println("Game over!");
    }


    public boolean guess(String word) {
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
            timer.cancel();
        }
        return --interval;
    }

    private void changeTurns() {
        player = !player;
        System.out.println("Player " + (player ? 1 : 2) + "'s turn!");
        interval = 30;
    }

    public static void main(String[] args) {
        GuessNextWord guessNextWord = new GuessNextWord();
        guessNextWord.startGame();
    }
}
