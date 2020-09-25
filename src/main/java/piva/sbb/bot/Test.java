package piva.sbb.bot;

import java.io.*;

public class Test {
    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File("emojis.txt")));

            String[] lines = in.lines().toArray(String[]::new);
            in.close();

            for (String line : lines) {
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
            }

            System.out.println("\uD83D\uDE00");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}