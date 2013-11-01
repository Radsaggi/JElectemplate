package client;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class ShapedButtonsPanel extends JPanel implements ActionListener {

    private static final int SIDE = 400;
    private static final Rectangle CENTER_R = new Rectangle(SIDE / 4, SIDE / 4, SIDE / 2, SIDE / 2),
            TOP_LEFT_R = new Rectangle(0, 0, SIDE / 2, SIDE / 2),
            TOP_RIGHT_R = new Rectangle(SIDE / 2, 0, SIDE / 2, SIDE / 2),
            BOTTOM_LEFT_R = new Rectangle(0, SIDE / 2, SIDE / 2, SIDE / 2),
            BOTTOM_RIGHT_R = new Rectangle(SIDE / 2, SIDE / 2, SIDE / 2, SIDE / 2);
    private static final Ellipse2D CENTER_C = new Ellipse2D.Float(CENTER_R.x, CENTER_R.y, CENTER_R.width, CENTER_R.height);
    private static final Area CENTER_C_AREA = new Area(CENTER_C);
    private static final String RED = "Red", BLUE = "Blue",
            YELLOW = "Yellow", GREEN = "Green";
    private Timer sTimer;
    private volatile int sVal;
    private ShapedButtonsPanel.ShapedButton b1, b2, b3, b4;

    
    private static WaitGlassPane2 c;
    
    
    public ShapedButtonsPanel() {
        super();
        setLayout(null);

        b1 = createButton(TOP_LEFT_R, Color.red, RED);
        b2 = createButton(TOP_RIGHT_R, Color.blue, BLUE);
        b3 = createButton(BOTTOM_LEFT_R, new Color(255,191,21), YELLOW);
        b4 = createButton(BOTTOM_RIGHT_R, Color.green, GREEN);

        JLabel imgL = new JLabel("Ashutosh") {
            @Override
            public boolean contains(int x, int y) {
                return CENTER_C.contains(x, y);
            }
        };
        imgL.setHorizontalAlignment(SwingUtilities.CENTER);
        imgL.setBounds(CENTER_R);

        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(imgL);
    }

    private ShapedButtonsPanel.ShapedButton createButton(Rectangle r, Color c, final String s) {
        Area butArea = new Area(r);
        butArea.subtract(CENTER_C_AREA);
        AffineTransform trans = new AffineTransform();
        trans.translate(-r.x, -r.y);
        butArea.transform(trans);
        ShapedButtonsPanel.ShapedButton but = new ShapedButtonsPanel.ShapedButton(butArea, c, s);
        but.setActionCommand(s);
        but.addActionListener(this);
        but.setBounds(r);
        return but;
    }

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                javax.swing.JFrame fr = new javax.swing.JFrame("Test");
                fr.setSize(600, 600);
                fr.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                fr.setLocationRelativeTo(null);
                fr.setLayout(null);
                
                c = new WaitGlassPane2();
                fr.setGlassPane(c);
                
                ShapedButtonsPanel panel = new ShapedButtonsPanel();
                panel.setBounds(100, 100, SIDE, SIDE);

                fr.add(panel);

                fr.setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof AbstractButton) {
            b1.selectedButton(Color.red, CENTER_R);
//            b1.setSelectedButtonPainting(true);
//            b2.setSelectedButtonPainting(true);
//            b3.setSelectedButtonPainting(true);
//            b4.setSelectedButtonPainting(true);
            
            
            c.setVisible(true);
            c.start();
            
            int fps = 24;
            int tick = 1000 / fps;
            sTimer = new Timer(tick, this);
            //sColor=
            sVal = 0;
            sTimer.start();

        } else if (e.getSource() instanceof Timer) {
            //b1.selectedButton(Color.red, CENTER_R);
            sVal++;
            System.out.println(sVal);
            
            if (sVal == 100) {
                sTimer.stop();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        c.stop();
                    }
                }).start();
            }
        }
    }

    public static class ShapedButton extends JButton {

        private Shape shape, selectedButtonShape;
        public String name;
        private Color color, selectedButtonColor;
        private float scale = 0.9f, left = 1 - scale;
        private volatile boolean paintBorder, paintSelectedButton;
        private Timer pTimer;
        private int pVal, thickness;
        private final ActionListener AL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                repaint();
            }
        };

        public ShapedButton(Shape s, Color c, String text) {
            super();
            shape = s;
            name = text;
            color = c;
            paintBorder = false;
            thickness = (int) Math.floor(left * s.getBounds().getWidth() / 2);
        }

        @Override
        public boolean isOpaque() {
            return false;
        }

        @Override
        public void paintComponent(Graphics g) {
            int w = getWidth();
            int h = getHeight();

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setPaint(color);
            g2.scale(scale, scale);
            g2.translate(left * w / 2, left * h / 2);
            g2.fill(shape);

            if (paintSelectedButton) {
                g2.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 0.5f));
                g2.setPaint(selectedButtonColor);
                g2.fill(selectedButtonShape);
            }

            g2.dispose();
        }

        @Override
        public void paintBorder(Graphics g) {

            if (paintBorder) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setPaint(color);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Area area = new Area(shape);
                for (int i = 1; i <= thickness; i++) {
                    float scaleI;
                    scaleI = ((float) i) / thickness;
                    if (i == pVal || i == pVal - 1 || i == pVal + 1) {
                        g2.setComposite(AlphaComposite.getInstance(
                                AlphaComposite.SRC_OVER));
                    } else {
                        g2.setComposite(AlphaComposite.getInstance(
                                AlphaComposite.SRC_OVER, scaleI * pVal / thickness));
                    }

                    AffineTransform trans = new AffineTransform();
                    float scaleS = scaleI * left + scale;
                    trans.scale(scaleS, scaleS);
                    trans.translate(thickness - i, thickness - i);
                    Area drawArea = area.createTransformedArea(trans);
                    g2.draw(drawArea);

                }
                if (pTimer.isRunning() && ++pVal == thickness) {
                    pTimer.stop();
                }

                g2.dispose();
            }
        }

        @Override
        public void processMouseEvent(MouseEvent me) {
            switch (me.getID()) {
                case MouseEvent.MOUSE_ENTERED:
                    paintBorder = true;
                    pVal = 0;
                    int fps = 24;
                    int tick = 1000 / fps;
                    pTimer = new Timer(tick, AL);
                    pTimer.start();
                    repaint();
                    break;
                case MouseEvent.MOUSE_EXITED:
                    paintBorder = false;
                    pTimer.stop();
                    repaint();
                    break;
            }

            super.processMouseEvent(me);
        }

        @Override
        public boolean contains(int x, int y) {
            return shape.contains(x, y);
        }

        public void selectedButton(Color c, Shape s) {
            selectedButtonColor = c;
            selectedButtonShape = s;
            //repaint(s.getBounds());
            repaint();
        }

        public void setSelectedButtonPainting(boolean z) {
            paintSelectedButton = z;
        }
    }
}