package com.orangebyte256.convexcopier.imageeditor;

import com.orangebyte256.convexcopier.common.Polygon;
import com.orangebyte256.convexcopier.common.Line;
import com.orangebyte256.convexcopier.common.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PointCreator extends JLabel {
    ArrayList<Point> points = new ArrayList<>();
    Boolean isFinished = false;
    final JFrame frame;
    final String output;

    private void updateFrame() {
        SwingUtilities.updateComponentTreeUI(PointCreator.this);
    }

    class MouseListenerImpl implements MouseListener {
        @Override
        public void mousePressed(MouseEvent e) {
            Point curPoint = new Point(e.getX(), e.getY());
            if (points.size() > 1) {
                if (points.get(points.size() - 1).equals(curPoint)) {
                    return;
                }
                Line newLine = new Line(points.get(points.size() - 1), curPoint);
                for (int i = 1; i < points.size() - 1; i++) {
                    if ((new Line(points.get(i - 1), points.get(i))).findCrossPoint(newLine).isPresent()) {
                        return;
                    }
                }
            }
            points.add(curPoint);
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
                        if (!Polygon.isPointsFits(points)) {
                            JOptionPane.showMessageDialog(PointCreator.this, "Points cannot form correct polygon");
                            points.clear();
                            updateFrame();
                            break;
                        }
                        isFinished = true;
                        updateFrame();
                        JOptionPane.showMessageDialog(PointCreator.this, "Operation done successfully");
                        (new Polygon(points)).export(output);
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

    private void drawBoldLine(Graphics g, Point first, Point second) {
        g.setColor(Color.WHITE);
        g.drawLine(first.x, first.y + 1, second.x, second.y + 1);
        g.drawLine(first.x, first.y - 1, second.x, second.y - 1);
        g.drawLine(first.x + 1, first.y, second.x + 1, second.y);
        g.drawLine(first.x - 1, first.y, second.x - 1, second.y);
        g.setColor(Color.BLACK);
        g.drawLine(first.x, first.y, second.x, second.y);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        for (int i = 1; i < points.size(); i++) {
            drawBoldLine(g, points.get(i - 1), points.get(i));
        }
        if (isFinished) {
            Point last = points.get(points.size() - 1);
            Point first = points.get(0);
            drawBoldLine(g, last, first);
        }

        for (Point point : points) {
            g.fillOval(point.x - 5, point.y - 5, 10, 10);
        }

    }

    PointCreator(BufferedImage image, String output) {
        super(new ImageIcon(image));
        addMouseListener(new MouseListenerImpl());
        this.output = output;

        frame = new JFrame();
        frame.setContentPane(this);
        frame.pack();
        frame.setVisible(true);
        frame.addKeyListener(new KeyListenerImpl());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
