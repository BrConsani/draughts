package br.unesp.draughts.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

public class AllowedPosition extends JComponent {
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Shape circleShape = new Ellipse2D.Double(22, 22, 20, 20);
        g2d.setColor(new Color(20, 85, 30, 128));
        g2d.fill(circleShape);

        g2d.draw(circleShape);
    }
}
