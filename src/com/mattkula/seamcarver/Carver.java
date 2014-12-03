package com.mattkula.seamcarver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

/**
 * Created by matt on 12/2/14.
 */
public class Carver {

    public static boolean DEBUG = false;

    String mName;
    BufferedImage mImage;
    BufferedImage mEdgeImage;
    int height, width;

    public Carver(String name) throws IOException {
        mName = name;
        setImage(ImageIO.read(new File(name)));
    }

    private void setImage(BufferedImage image) {
        mImage = image;
        height = mImage.getHeight();
        width = mImage.getWidth();
        detectEdges();
    }

    private void detectEdges() {
        mEdgeImage = EdgeDetector.getEdges(mImage, EdgeDetector.Type.SOBEL);
        if (DEBUG) Utils.saveImage("edges.png", mEdgeImage);
    }

    public void carve(int rows, int columns) {
        for (int i=0; i < columns; i++) {
            verticalCarve();
        }
        Utils.saveImage("done.png", mImage);
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
            if (DEBUG) mImage.setRGB(pixel.x, pixel.y, 0xFFFF0000);
            pixel.deleted = true;
            pixel = pixel.previousPosition;
        }

        BufferedImage newImage = new BufferedImage(width - 1, height, BufferedImage.TYPE_INT_ARGB);
        int foundOffset = 0;
        for (int y = 0; y < height; y++) {
            foundOffset = 0;
            for (int x = 0; x < width - 1; x++) {
                if (pathMap[y][x].deleted) {
                    foundOffset = 1;
                }
                newImage.setRGB(x, y, mImage.getRGB(x + foundOffset, y));
            }
        }
        if (DEBUG) Utils.saveImage("lastline.png", mImage);

        setImage(newImage);
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

        int cost, x, y;
        boolean deleted = false;

        PixelPosition previousPosition = null;

        public PixelPosition(int cost, PixelPosition previousPosition, int x, int y) {
            this.previousPosition = previousPosition;
            this.cost = cost;
            this.x = x;
            this.y = y;
        }
    }
}
