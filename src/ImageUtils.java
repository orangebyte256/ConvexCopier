import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {
    public static void exportImage(String path, BufferedImage image) {
        File outputfile = new File(path + ".jpg");
        try {
            ImageIO.write(image, "jpg", outputfile);
        } catch (IOException e) {
            // TODO change it
            System.err.println("Some error happened while exporting");
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage importImage(String path) {
        File img = new File(path + ".jpg");
        try {
            return ImageIO.read(img);
        } catch (IOException e) {
            // TODO change it
            System.err.println("Some error happened while exporting");
            throw new RuntimeException(e);
        }
    }
}
