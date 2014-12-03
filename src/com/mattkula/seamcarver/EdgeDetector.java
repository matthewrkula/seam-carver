package com.mattkula.seamcarver;

import java.awt.image.BufferedImage;

/**
 * Created by matt on 12/2/14.
 */
public class EdgeDetector {

    enum Type {
        SOBEL;
    }

    static int[][] sobelVert = {{-3, -10, -3},
                                {0, 0, 0},
                                {3, 10, 3}};
    static int[][] sobelHorz = {{-3, 0, 3},
                                {-10, 0, 10},
                                {-3, 0, 3}};
    static int[][] robertsVert = {{+1, 0}, {0, -1}};
    static int[][] robertsHorz = {{0, +1}, {-1, 0}};

    public static BufferedImage getEdges(BufferedImage image, EdgeDetector.Type type) {
        long time = System.currentTimeMillis();
        BufferedImage edgeImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        int [][] vertFilter = sobelVert;
        int [][] horzFilter = sobelHorz;

        int height = image.getHeight();
        int width = image.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || y == 0 || x >= width - 1 || y >= height - 1) {
                    continue;
                }
                edgeImage.setRGB(x, y, 0xFFFFFFFF);
            }
        }

        int offset = vertFilter.length / 2;
        int v;

        for (int y = offset; y < image.getHeight() - offset; y++) {
            for (int x = offset; x < image.getWidth() - offset; x++) {

                int vert = 0;
                int horz = 0;
                for (int i = -offset; i < vertFilter.length - offset; i++) {
                    for (int j = -offset; j < vertFilter.length - offset; j++) {
                        v = image.getRGB(x + j, y + i) & 0xFF;
                        vert += vertFilter[i+offset][j+offset] * v;
                        horz += horzFilter[i+offset][j+offset] * v;
                    }
                }

                int val = (int)Math.sqrt((horz * horz) + (vert * vert));

                val = Math.max(0, Math.min(255, val));

                edgeImage.setRGB(x, y, val * val);
            }
        }

//        if (Carver.DEBUG) System.out.println(System.currentTimeMillis() - time);

        return edgeImage;
    }
}
