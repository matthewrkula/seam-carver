package com.mattkula.seamcarver;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            long time = System.currentTimeMillis();

//            new Carver("/home/matt/Pictures/core05-4.png").carve();
//            new Carver("/home/matt/Pictures/castle.png").carve();
//            for (int i = 0; i < 40; i++) {
                new Carver("/home/matt/Pictures/castle.png").carve();
//            }

            System.out.println(String.format("%dms", System.currentTimeMillis() - time));

        } catch (IOException e) {
            System.err.println("Image could not be found.");
        }
        System.out.println("Done.");
    }
}
