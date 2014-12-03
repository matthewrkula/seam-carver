package com.mattkula.seamcarver;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by matt on 12/2/14.
 */
public class Utils {

    public static void saveImage(String name, RenderedImage image) {
        try {
            ImageIO.write(image, "png", new File("/home/matt/Pictures/" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
