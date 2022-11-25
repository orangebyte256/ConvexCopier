package com.orangebyte256.convexcopier.common;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {
    public static void exportImage(String path, BufferedImage image) {
        File outputfile = new File(path + ".png");
        try {
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            // TODO change it
            System.err.println("Some error happened while exporting");
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage importImage(String path) {
        File img = new File(path + ".png");
        try {
            return ImageIO.read(img);
        } catch (IOException e) {
            // TODO change it
            System.err.println("Some error happened while exporting");
            throw new RuntimeException(e);
        }
    }

    public static int compareImage(BufferedImage first, BufferedImage second) {
        if (first.getWidth() != second.getWidth() || first.getHeight() != second.getHeight()) {
            return -1;
        }
        int result = 0;
        for (int y = 0; y < first.getHeight(); y++) {
            for (int x = 0; x < first.getWidth(); x++) {
                if (first.getRGB(x, y) != second.getRGB(x, y)) {
                    result++;
                }
            }
        }
        return result;
    }
}
