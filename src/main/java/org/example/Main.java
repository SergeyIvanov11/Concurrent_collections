package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    static ArrayBlockingQueue<String> queueForA = new ArrayBlockingQueue<>(100);
    static ArrayBlockingQueue<String> queueForB = new ArrayBlockingQueue<>(100);
    static ArrayBlockingQueue<String> queueForC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        Thread TextGeneratorThread = new Thread(new TextGenerator());
        Thread TextCheckerForMaxAThread = new Thread(new TextCheckerForMaxA());
        Thread TextCheckerForMaxBThread = new Thread(new TextCheckerForMaxB());
        Thread TextCheckerForMaxCThread = new Thread(new TextCheckerForMaxC());

        TextGeneratorThread.start();
        TextCheckerForMaxAThread.start();
        TextCheckerForMaxBThread.start();
        TextCheckerForMaxCThread.start();

        TextGeneratorThread.join();
        TextCheckerForMaxAThread.join();
        TextCheckerForMaxBThread.join();
        TextCheckerForMaxCThread.join();
    }

    public static class TextGenerator implements Runnable {
        @Override
        public void run() {
            Random random = new Random();
            for (int i = 0; i < 10_000; i++) {
                String newText = generateText("abc", 100_000);
                try {
                    queueForA.put(newText);
                    queueForB.put(newText);
                    queueForC.put(newText);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static class TextCheckerForMaxA implements Runnable {
        @Override
        public void run() {
            int maxA = checkForMaxOfSymbol(queueForA, 'a');
            System.out.println("Максимальное количество найденных символов A во всех текстах " + maxA);
        }
    }

    public static class TextCheckerForMaxB implements Runnable {
        @Override
        public void run() {
            int maxB = checkForMaxOfSymbol(queueForB, 'b');
            System.out.println("Максимальное количество найденных символов B во всех текстах " + maxB);
        }
    }

    public static class TextCheckerForMaxC implements Runnable {
        @Override
        public void run() {
            int maxC = checkForMaxOfSymbol(queueForC, 'c');
            System.out.println("Максимальное количество найденных символов C во всех текстах " + maxC);
        }
    }

    public static int checkForMaxOfSymbol(ArrayBlockingQueue<String> queue, char c) {
        List<Integer> list = new ArrayList<>();
        try {
            while (list.size() < 10_000) {
                String text = queue.take();
                int count = 0;
                for (int i = 0; i < text.length(); i++) {
                    if (text.charAt(i) == c) {
                        count++;
                    }
                }
                list.add(count);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.max(list);
    }
}