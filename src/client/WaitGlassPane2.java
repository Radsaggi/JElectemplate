package client;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.CountDownLatch;
import javax.swing.*;

public class WaitGlassPane2 extends JComponent implements ActionListener {

    private volatile boolean mIsRunning, mIsUnlocking, mIsBreaking;
    private Timer mTimer;
    private CountDownLatch latch;
    private volatile int mUnlockCount, mBreakCount;
    private final int mUnlockLimit = 15, mBreakLimit = 15;
    private static final KeyStroke ACTIVATE = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK),
            DEACTIVATE = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK);

    public WaitGlassPane2() {
        addMouseListener(new MouseAdapter() {
        });
        addMouseMotionListener(new MouseAdapter() {
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                KeyStroke ks = KeyStroke.getKeyStrokeForEvent(ke);
                if (ks.equals(ACTIVATE)) {
                    start();
                } else if (ks.equals(DEACTIVATE)) {
                    stop();
                }
            }
        });

        latch = new CountDownLatch(1);
        mIsRunning = mIsBreaking = mIsUnlocking = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        // Paint the view.
        super.paintComponent(g);

        if (!mIsRunning) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, .5f));

        int x = w / 2;
        int y = h / 2;
        g2.setPaint(Color.black);

        int radius = Math.min(w / 40, h / 40);
        Area a = new Area(new RoundRectangle2D.Float(x - w / 4, y, w / 2, h / 3, w / 20, h / 20));
        y += h * 2 / 9;
        Area a2 = new Area(new Ellipse2D.Float(x - radius, y - radius, radius * 2, radius * 2));
        a2.add(new Area(new BasicStroke(10).createStrokedShape(
                new Line2D.Float(x, y, x, y + h / 15))));
        a.exclusiveOr(a2);
        g2.fill(a);

        y = h / 2;

        if (!mIsBreaking) {
            if (mIsUnlocking) {

                double angle = Math.PI / 4;
                angle *= ((double) mUnlockCount) / mUnlockLimit;
                System.out.println("Happy Birthday Angle " + String.valueOf(angle));
                g2.rotate(-angle, x - w / 6, y);
            }
            g2.setStroke(new BasicStroke(20));
            g2.drawArc(x - w / 6, y - h / 4, w / 3, h / 2, 10, 160);
        }

        g2.dispose();
    }

    //ActionListener method
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Hell");
        if (mIsUnlocking) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    repaint();
                }
            });
            System.out.println("Happy Birthday");
            if (!mIsBreaking) {
                mUnlockCount++;
                System.out.println("Happy Birthday2");
                if (mUnlockCount == mUnlockLimit) {
                    mIsBreaking = true;
                }
            } else {
                mBreakCount++;
                if (mBreakCount == mBreakLimit) {
                    mTimer.stop();
                    latch.countDown();
                    latch = new CountDownLatch(1);
                }
            }
        }
    }

    public void start() {
        if (mIsRunning) {
            return;
        }

        if (!isVisible()) {
            setVisible(true);
        }

        mIsRunning = true;
        mIsBreaking = mIsUnlocking = false;
        mTimer = null;

        repaint();
    }

    public void stop() {
        mIsUnlocking = true;
        mUnlockCount = 0;

        int fps = 24;
        int tick = 1000 / fps;
        mTimer = new Timer(tick, this);

        mTimer.start();

        try {
            latch.await();
        } catch (InterruptedException e) {
        }

        mIsRunning = mIsBreaking = mIsUnlocking = false;

        setVisible(false);
    }

    public boolean isRunning() {
        return mIsRunning;
    }
}