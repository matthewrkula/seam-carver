package com.mattkula.seamcarver;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            long time = System.currentTimeMillis();

//            new Carver("/home/matt/Pictures/core05-4.png").carve();
//            new Carver("/home/matt/Pictures/castle.png").carve();
            new Carver("/home/matt/Pictures/lake.png").carve(0, 1);

            System.out.println(String.format("%dms", System.currentTimeMillis() - time));

        } catch (IOException e) {
            System.err.println("Image could not be found.");
        }
        System.out.println("Done.");
    }
}
