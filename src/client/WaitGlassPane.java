package client;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.util.concurrent.CountDownLatch;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class WaitGlassPane extends JComponent implements ActionListener {

    private boolean mIsRunning;
    private boolean mIsFadingOut;
    private boolean isWaiting;
    private Timer mTimer;
    private CountDownLatch latch;
    private int mAngle;
    private int mFadeCount;
    private int mFadeLimit = 15;

    public WaitGlassPane() {
        addMouseListener(new MouseAdapter() { });
        addMouseMotionListener(new MouseAdapter() { });
        addKeyListener(new KeyAdapter() { });

        latch = new CountDownLatch(1);
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

        float fade = (float) mFadeCount / (float) mFadeLimit;
        // Gray it out.
        Composite urComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, .5f * fade));
        g2.fillRect(0, 0, w, h);
        g2.setComposite(urComposite);

        // Paint the wait indicator.
        int s = Math.min(w, h) / 5;
        int cx = w / 2;
        int cy = h / 2;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(
                new BasicStroke(s / 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setPaint(Color.white);
        g2.rotate(Math.PI * mAngle / 180, cx, cy);
        for (int i = 0; i < 12; i++) {
            float scale = (11.0f - (float) i) / 11.0f;
            g2.drawLine(cx + s, cy, cx + s * 2, cy);
            g2.rotate(-Math.PI / 6, cx, cy);
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, scale * fade));
        }

        g2.dispose();
    }

    //ActionListener method
    @Override
    public void actionPerformed(ActionEvent e) {
        if (mIsRunning) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    repaint();
                }
            });
            mAngle += 3;
            if (mAngle >= 360) {
                mAngle = 0;
            }
            if (mIsFadingOut) {
                if (--mFadeCount == 0) {
                    mIsRunning = false;
                    mTimer.stop();

                    if (isWaiting) {
                        latch.countDown();
                    }
                    isWaiting = false;
                    latch = new CountDownLatch(1);
                }
            } else if (mFadeCount < mFadeLimit) {
                mFadeCount++;
            }
        }
    }

    public void start() {

        if (mIsRunning) {
            return;
        }

        // Run a thread for animation.
        mIsRunning = true;
        mIsFadingOut = false;
        mFadeCount = 0;
        int fps = 24;
        int tick = 1000 / fps;
        mTimer = new Timer(tick, this);

        mTimer.start();
    }

    public void stop() {
        mIsFadingOut = true;
        isWaiting = true;
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
    }

    public boolean isRunning() {
        return mIsRunning;
    }
    
}
