package com.mattkula.seamcarver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by matt on 12/2/14.
 */
public class Carver {

    BufferedImage mImage;
    BufferedImage mGreyscaleImage;
    BufferedImage mEdgeImage;
    int height, width;

    public Carver(String name) throws IOException {
        mImage = ImageIO.read(new File(name));
        height = mImage.getHeight();
        width = mImage.getWidth();
        generateGreyscaleImage();
        detectEdges();
    }

    private void generateGreyscaleImage() {
        mGreyscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = mGreyscaleImage.getGraphics();
        g.drawImage(mImage, 0, 0, null);
        g.dispose();
    }

    private void detectEdges() {
        mEdgeImage = EdgeDetector.getEdges(mGreyscaleImage, EdgeDetector.Type.SOBEL);
        Utils.saveImage("edges.png", mEdgeImage);
    }

    public void carve() {
        verticalCarve();
    }

    private void verticalCarve() {
        PixelPosition[][] pathMap = new PixelPosition[height][width];

        for (int x = 0; x < width; x++) {
            pathMap[0][x] = new PixelPosition(getCost(x, 0), null, x, 0);
        }

        int lowestCost = Integer.MAX_VALUE;
        PixelPosition lowestPixel = null;
        int cost;

        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width; x++) {

                for (int i = -1; i < 2; i++ ) {
                    cost = getCost(x + i, y - 1);
                    if (cost < lowestCost) {
                        lowestCost = cost;
                        lowestPixel = pathMap[y - 1][x + i];
                    }
                }

                pathMap[y][x] = new PixelPosition(getCost(x, y) + lowestPixel.cost, lowestPixel, x, y);

                lowestCost = Integer.MAX_VALUE;
                lowestPixel = null;
            }
        }

        PixelPosition pixel = getStartingPixelPosition(pathMap[height-1]);

        while (pixel != null) {
            mImage.setRGB(pixel.x, pixel.y, 0xFFFF0000);
            pixel.deleted = true;
            pixel = pixel.previousPosition;
        }

        BufferedImage newImage = new BufferedImage(width - 1, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            boolean found = false;
            for (int x = 0; x < width - 1; x++) {
                if (pathMap[y][x].deleted) {
                    found = true;
                }
                newImage.setRGB(x, y, mImage.getRGB(x + (found ? 1 : 0), y));
            }
        }
        Utils.saveImage("lastline.png", mImage);
        Utils.saveImage("done.png", newImage);
    }

    private PixelPosition getStartingPixelPosition(PixelPosition[] array) {
        PixelPosition smallestNumber = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i].cost < smallestNumber.cost) {
                smallestNumber = array[i];
            }
        }
        return smallestNumber;
    }

    private int getCost(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return Integer.MAX_VALUE;
        }
        return (int)Math.sqrt(mEdgeImage.getRGB(x, y) & 0xFF);
    }

    private class PixelPosition {
        public PixelPosition(int cost, PixelPosition previousPosition, int x, int y) {
            this.previousPosition = previousPosition;
            this.cost = cost;
            this.x = x;
            this.y = y;
        }
        int cost = -1;
        int x, y;
        PixelPosition previousPosition = null;
        boolean deleted = false;
    }
}
