package com.mattkula.seamcarver;

import java.awt.image.BufferedImage;

/**
 * Created by matt on 12/2/14.
 */
public class EdgeDetector {

    enum Type {
        SOBEL;
    }

//    static int[][] sobelVert = {{-1, -2, -1},
//                                {0, 0, 0},
//                                {1, 2, 1}};
//    static int[][] sobelHorz = {{-1, 0, 1},
//                                {-2, 0, 2},
//                                {-1, 0, 1}};
    static int[][] sobelVert = {{-3, -10, -3},
            {0, 0, 0},
            {3, 10, 3}};
    static int[][] sobelHorz = {{-3, 0, 3},
            {-10, 0, 10},
            {-3, 0, 3}};
    static int[][] robertsVert = {{+1, 0}, {0, -1}};
    static int[][] robertsHorz = {{0, +1}, {-1, 0}};

    public static BufferedImage getEdges(BufferedImage image, EdgeDetector.Type type) {
        BufferedImage edgeImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        int [][] vertFilter = sobelVert;
        int [][] horzFilter = sobelHorz;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                edgeImage.setRGB(x, y, 0xFFFFFFFF);
            }
        }

        int offset = vertFilter.length / 2;

        for (int y = offset; y < image.getHeight() - offset; y++) {
            for (int x = offset; x < image.getWidth() - offset; x++) {

                int vert = 0;
                int horz = 0;
                for (int i = -offset; i < vertFilter.length - offset; i++) {
                    for (int j = -offset; j < vertFilter.length - offset; j++) {
                        vert += vertFilter[i+offset][j+offset] * (image.getRGB(x + j, y + i) & 0xFF);
                        horz += horzFilter[i+offset][j+offset] * (image.getRGB(x + j, y + i) & 0xFF);
                    }
                }

                int val = (int)Math.sqrt((horz * horz) + (vert * vert));

                val = Math.max(0, Math.min(255, val));

                edgeImage.setRGB(x, y, val * val);
            }
        }

        return edgeImage;
    }


}
