package client;

import data.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class ClientVotePanel extends JPanel implements ActionListener {

    public ClientVotePanel(String hname, HouseDetails hDetails,
            CandidateDetails sportsCapCD[], CandidateDetails sportsVCapCD[]) {
        super(new BorderLayout());

        house = hDetails.getHouse();

        JPanel panel = new JPanel();
        JLabel label = new JLabel(hname);
        panel.add(label);

        JPanel panelgrid = new JPanel();
        panelgrid.setLayout(new GridLayout(0, 2));
        panelgrid.add(sportsCap = new CategoryChoicePanel(sportsCapCD, "Sports Captain"));
        panelgrid.add(sportsVCap = new CategoryChoicePanel(sportsVCapCD, "Sports Vice Captain"));
        panelgrid.add(cap = new CategoryChoicePanel(hDetails.gethouseCap(), "House Captain"));
        panelgrid.add(vCap = new CategoryChoicePanel(hDetails.gethouseVCap(), "House Vice Captain"));
        panelgrid.add(pre9 = new CategoryChoicePanel(hDetails.getpref09(), "Cl 9 Prefects"));
        panelgrid.add(pre10 = new CategoryChoicePanel(hDetails.getpref10(), "Cl 10 Prefects"));
        panelgrid.add(pre11 = new CategoryChoicePanel(hDetails.getpref11(), "Cl 11 Prefects"));
        panelgrid.add(pre12 = new CategoryChoicePanel(hDetails.getpref12(), "Cl 12 Prefects"));

        JPanel buttonPanel = new JPanel();
        JButton vote = new JButton(VOTE_AC);
        vote.setActionCommand(VOTE_AC);
        vote.addActionListener(this);
        JButton back = new JButton(BACK_AC);
        back.setActionCommand(BACK_AC);
        back.addActionListener(this);
        buttonPanel.add(back);
        buttonPanel.add(vote);

        add(panel, BorderLayout.NORTH);
        add(panelgrid, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    //ActionListener method
    @Override
    public void actionPerformed(ActionEvent ae) {
        switch (ae.getActionCommand()) {
            case VOTE_AC:
                Vote vote = new Vote(house,
                        cap.getSelectedCandidate(),
                        vCap.getSelectedCandidate(),
                        pre9.getSelectedCandidate(),
                        pre10.getSelectedCandidate(),
                        pre11.getSelectedCandidate(),
                        pre12.getSelectedCandidate(),
                        sportsCap.getSelectedCandidate(),
                        sportsVCap.getSelectedCandidate());
                firePropertyChange(VOTE_PROPERTY, null, vote);
                break;
            case BACK_AC:
                firePropertyChange(BACK_PROPERTY, null, null);
        }

    }

    public void resetButtons() {
        sportsCap.resetButtons();
        sportsVCap.resetButtons();
        cap.resetButtons();
        vCap.resetButtons();
        pre10.resetButtons();
        pre11.resetButtons();
        pre12.resetButtons();
        pre9.resetButtons();
    }
    private int FWIDTH = 800, FHEIGHT = 600;
    private CategoryChoicePanel sportsCap, sportsVCap, cap, vCap, pre9, pre10, pre11, pre12;
    private House house;
    public static final String VOTE_PROPERTY = "VOTE CASTED",
            BACK_PROPERTY = "BACK";
    private static final String VOTE_AC = "Cast Vote", BACK_AC = "< Back";

    private class OvalBorder implements Border {

        protected int ovalWidth = 6;
        protected int ovalHeight = 6;
        protected Color lightColor = Color.white;
        protected Color darkColor = Color.gray;

        public OvalBorder() {
            ovalWidth = 6;
            ovalHeight = 6;
        }

        public OvalBorder(int w, int h) {
            ovalWidth = w;
            ovalHeight = h;
        }

        public OvalBorder(int w, int h, Color topColor, Color bottomColor) {
            ovalWidth = w;
            ovalHeight = h;
            lightColor = topColor;
            darkColor = bottomColor;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(ovalHeight, ovalWidth, ovalHeight, ovalWidth);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width,
                int height) {
            width--;
            height--;

            g.setColor(lightColor);
            g.drawLine(x, y + height - ovalHeight, x, y + ovalHeight);
            g.drawArc(x, y, 2 * ovalWidth, 2 * ovalHeight, 180, -90);
            g.drawLine(x + ovalWidth, y, x + width - ovalWidth, y);
            g.drawArc(x + width - 2 * ovalWidth, y, 2 * ovalWidth, 2 * ovalHeight,
                    90, -90);

            g.setColor(darkColor);
            g.drawLine(x + width, y + ovalHeight, x + width, y + height
                    - ovalHeight);
            g.drawArc(x + width - 2 * ovalWidth, y + height - 2 * ovalHeight,
                    2 * ovalWidth, 2 * ovalHeight, 0, -90);
            g.drawLine(x + ovalWidth, y + height, x + width - ovalWidth, y
                    + height);
            g.drawArc(x, y + height - 2 * ovalHeight, 2 * ovalWidth,
                    2 * ovalHeight, -90, -90);
        }
    }

    private class CategoryChoicePanel extends JPanel {

        private CandidateDetails candidates[];
        private JRadioButton candidatesRBs[];
        private ButtonGroup bg;
        private int n;

        public CategoryChoicePanel(CandidateDetails _candidates[], String title) {
            super(new GridLayout(0, 2));

            TitledBorder titledBorder = BorderFactory.createTitledBorder(new OvalBorder(), title);
            this.setBorder(titledBorder);

            n = _candidates.length;
            candidates = _candidates;
            candidatesRBs = new JRadioButton[n];
            bg = new ButtonGroup();
            for (int i = 0; i < n; i++) {
                String str = candidates[i].getName();
                candidatesRBs[i] = new JRadioButton(str, true);
                candidatesRBs[i].setActionCommand(str);
                bg.add(candidatesRBs[i]);
                this.add(candidatesRBs[i]);
            }
        }

        public CandidateDetails getSelectedCandidate() {
            String comm = bg.getSelection().getActionCommand();
            for (CandidateDetails cd : candidates) {
                if (cd.getName().equals(comm)) {
                    return cd;
                }
            }
            return null;
        }

        public void resetButtons() {
            bg.clearSelection();
        }
    }
}
