import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    private static ImageEditor imageEditor = null;

    private static void exportImage(String path) {
        File outputfile = new File(path + ".jpg");
        try {
            ImageIO.write(imageEditor.getImage(), "jpg", outputfile);
        } catch (IOException e) {
            // TODO change it
            System.err.println("Some error happened while exporting");
            throw new RuntimeException(e);
        }
    }

    private static BufferedImage importImage(String path) {
        File img = new File(path + ".jpg");
        try {
            return ImageIO.read(img);
        } catch (IOException e) {
            // TODO change it
            System.err.println("Some error happened while exporting");
            throw new RuntimeException(e);
        }
    }

    private static void setupImageEditor(String path) {
        imageEditor = new ImageEditor(importImage(path));
    }

    public static void main(String[] args) {
        System.out.println("Start program");
        setupImageEditor("beer");
        Point[] points = new Point[6];
        // TODO add checks to size
        points[0] = new Point(250, 30);
        points[1] = new Point(100, 200);
        points[2] = new Point(100, 340);
        points[3] = new Point(250, 200);
        points[4] = new Point(400, 340);
        points[5] = new Point(400, 200);
/*        points[0] = new Point(196,360 - 114);
        points[1] = new Point(186,360 - 182);
        points[2] = new Point(200,360 - 250);
        points[3] = new Point(255,360 - 255);
        points[4] = new Point(277,360 - 204);
        points[5] = new Point(310,360 - 156);
        points[6] = new Point(338,360 - 112);
        points[7] = new Point(275,360 - 74);
        points[8] = new Point(215,360 - 95);*/
        imageEditor.fillPolygon(points, importImage("penguins"));
        exportImage("result");
        System.out.println("End");
    }
}