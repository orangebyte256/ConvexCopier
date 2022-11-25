import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class PointCreator extends JLabel {
    ArrayList<Point> points = new ArrayList<>();
    Boolean isFinished = false;
    final JFrame frame;

    void updateFrame() {
        SwingUtilities.updateComponentTreeUI(PointCreator.this);
    }

    class MouseListenerImpl implements MouseListener {

        @Override
        public void mousePressed(MouseEvent e) {
            points.add(new Point(e.getX(), e.getY()));
            updateFrame();
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }

    class KeyListenerImpl implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    if (points.size() < 3) {
                        JOptionPane.showMessageDialog(PointCreator.this, "Please, choose more than two points");
                    } else {
                        isFinished = true;
                        updateFrame();
                        JOptionPane.showMessageDialog(PointCreator.this, "Operation done successfully");
                        (new Convex(points)).export("convex.ser");
                        System.exit(0);
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    points.clear();
                    updateFrame();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        for (Point point : points) {
            g.fillOval(point.x - 5, point.y - 5, 10, 10);
        }
        for (int i = 1; i < points.size(); i++) {
            g.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y);
        }
        if (isFinished) {
            Point last = points.get(points.size() - 1);
            Point first = points.get(0);
            g.drawLine(last.x, last.y, first.x, first.y);
        }
    }

    PointCreator(String path) {
        super(new ImageIcon(ImageUtils.importImage(path)));
        addMouseListener(new MouseListenerImpl());

        frame = new JFrame();
        frame.setContentPane(this);
        frame.pack();
        frame.setVisible(true);
        frame.addKeyListener(new KeyListenerImpl());
    }

    public static void main(String[] args) {
        new PointCreator("penguins");
    }


}
