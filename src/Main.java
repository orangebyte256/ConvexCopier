import java.time.Duration;
import java.time.Instant;

public class Main {
    private static ImageEditor imageEditor = null;

    private static void setupImageEditor(String path) {
        imageEditor = new ImageEditor(ImageUtils.importImage(path));
    }

    public static void main(String[] args) {
        System.out.println("Start program");
        setupImageEditor("beer");

        Instant startTime = Instant.now();
//        imageEditor.fillPolygon(new Convex(new Point(0,0), new Point(9000,0),
//                new Point(9000,9000), new Point(0,9000)), ImageUtils.importImage("red"));
//        imageEditor.fillPolygon(new Convex(new Point(12,2), new Point(21,12),
//                new Point(12,21), new Point(5,12)), ImageUtils.importImage("red"));
        imageEditor.fillPolygon(Convex.importConvex("convex.ser"), ImageUtils.importImage("penguins"));
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");

        ImageUtils.exportImage("result", imageEditor.getImage());
        System.out.println("End");
    }
}