package client;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class ImageButton extends JToggleButton implements ActionListener {

    private Image img;
    private double rAngle;
    private volatile boolean isRollover;
    private int thickness;
    private Insets insets;
    private Timer mTimer;
    private int mFadeCount, mFadeLimit = 15;
    private Color mColor;
    public static final double ANGLE1 = 5.0,
            ANGLE2 = -5.0,
            ANGLE3 = 2.5,
            ANGLE4 = -2.5,
            ANGLE5 = 1.0;

    public ImageButton(String str, Image img) {
        this(str, img, 10, Color.red, ANGLE1);
    }

    public ImageButton(String str, Image img, int thickness) {
        this(str, img, thickness, Color.red, ANGLE1);
    }

    public ImageButton(String str, Image img, Color glowColor) {
        this(str, img, 10, glowColor, ANGLE1);
    }

    public ImageButton(String str, Image img, int _thickness, Color glowColor, double angle) {
        super(str);
        this.img = img;
        super.setPreferredSize(new Dimension(img.getWidth(null), img.getHeight(null)));
        isRollover = false;
        thickness = _thickness;
        insets = new Insets(thickness, thickness, thickness, thickness);
        mColor = glowColor;
        rAngle = angle;
    }

    public void setAngle(double angle) {
        rAngle = angle;
    }

    @Override
    public void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        g.setColor(getBackground());
        g.fillRect(0, 0, w, h);

        Insets in = insets;
        w -= in.left + in.right;
        h -= in.top + in.bottom;
        int cx = w / 2;
        int cy = h / 2;
        Point orig = new Point(0, 0);

        Graphics2D g2 = (Graphics2D) g.create(in.left, in.top, w, h);
        
        if (!isSelected() && !isRollover) {
            double trans = 0.9, rem = 0.1;
            g2.rotate(Math.PI * rAngle / 180, cx, cy);
            g2.transform(AffineTransform.getScaleInstance(trans, trans));
            g2.translate(w * rem / 2, h * rem / 2);
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(img, orig.x, orig.y, w, (int) (h * 0.9), null);

        FontMetrics fm = getFontMetrics(getFont());
        String str = getText();
        int len = SwingUtilities.computeStringWidth(fm, str);
        int x = (w - len) / 2;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.black);
        g2.drawString(str, x, h);

        g2.dispose();
    }

    @Override
    public void paintBorder(Graphics g) {
        if (isSelected()) {
            if (mTimer != null) {
                mTimer.stop();
            }
            mFadeCount = mFadeLimit;
        } else {
            if (isRollover) {
                mFadeCount++;
                if (mFadeCount >= mFadeLimit) {
                    mFadeCount = mFadeLimit;
                    if (mTimer != null) {
                        mTimer.stop();
                    }
                }
            } else {
                if (mTimer != null) {
                    mTimer.stop();
                }
                return;
            }
        }

        int x = 0, y = 0;
        int w = getWidth(), h = getHeight();

        w--;
        h--;
        Graphics2D g2 = (Graphics2D) g.create();

        Point orig = new Point(x, y);
        g2.setPaint(mColor);
        float fade = ((float) mFadeCount) / mFadeLimit;

        for (int i = 0; i < thickness; i++) {
            float scale = ((float) i) / thickness;
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, scale * fade));
            g2.drawRect(orig.x, orig.y, w, h);

            orig.x++;
            orig.y++;
            w -= 2;
            h -= 2;
        }
    }

    @Override
    public void processMouseEvent(MouseEvent me) {
        switch (me.getID()) {
            case MouseEvent.MOUSE_ENTERED:
                isRollover = true;
                mFadeCount = 0;
                int fps = 24;
                int tick = 1000 / fps;
                mTimer = new Timer(tick, this);
                mTimer.start();

                repaint();
                break;
            case MouseEvent.MOUSE_EXITED:
                isRollover = false;
                repaint();
                break;
        }
        super.processMouseEvent(me);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        repaint();
    }

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                javax.swing.JFrame fr = new javax.swing.JFrame("Test");
                fr.setSize(640, 480);
                fr.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                fr.setLocationRelativeTo(null);
                fr.setLayout(new java.awt.FlowLayout());

                Image img = new javax.swing.ImageIcon("/media/Filess..../img.jpg").getImage();
                javax.swing.ButtonGroup bg = new javax.swing.ButtonGroup();

                ImageButton tb1 = new ImageButton("Hello1", img, 10, Color.green, ANGLE1);
                ImageButton tb2 = new ImageButton("Hello2", img, 10, Color.red, ANGLE5);
                ImageButton tb3 = new ImageButton("Hello3", img, 10, Color.black, ANGLE3);
                ImageButton tb4 = new ImageButton("Hello4", img, 10, Color.blue, ANGLE4);
                ImageButton tb5 = new ImageButton("Hello5", img, 10, Color.yellow, ANGLE2);
                fr.add(tb1);
                bg.add(tb1);
                fr.add(tb2);
                bg.add(tb2);
                fr.add(tb3);
                bg.add(tb3);
                fr.add(tb4);
                bg.add(tb4);
                fr.add(tb5);
                bg.add(tb5);


                fr.setVisible(true);
            }
        });
    }
}