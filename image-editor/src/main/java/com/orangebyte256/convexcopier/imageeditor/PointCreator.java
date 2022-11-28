package com.orangebyte256.convexcopier.imageeditor;

import com.orangebyte256.convexcopier.common.Convex;
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
                        if (!Convex.isPointsFits(points)) {
                            JOptionPane.showMessageDialog(PointCreator.this, "Points cannot form correct polygon");
                            break;
                        }
                        isFinished = true;
                        updateFrame();
                        JOptionPane.showMessageDialog(PointCreator.this, "Operation done successfully");
                        (new Convex(points)).export(output);
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
