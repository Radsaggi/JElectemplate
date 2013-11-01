package client;

import ResourceTools.ResourceDoesNotExistException;
import ResourceTools.Resources;
import data.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.*;

public class ClientFrame extends JFrame implements PropertyChangeListener {

    private Client client;
    private JPanel chooseHouseP;
    private JComboBox<String> chooseHouseCB;
    private JButton chooseHouseB;
    private ClientVotePanel redP, blueP, yellowP, greenP;
    private WaitGlassPane glassPane;
    private VoteCaster worker;
    private CardLayout cardLO;
    private Container cardP;
    private static final String SERVER_IP;
    private static final int PORT;
    private static final String CHOOSER_P = "chooser",
            RED_HOUSE = House.RED.toString(),
            BLUE_HOUSE = House.BLUE.toString(),
            YELLOW_HOUSE = House.YELLOW.toString(),
            GREEN_HOUSE = House.GREEN.toString();

    static {
        String server = null;
        int port = 0;
        try {
            java.io.File file = new java.io.File("E:\\elec.txt");
            Resources r = Resources.createInstance(file, "%");
            server = (String) r.get("server ip");
            port = (Integer) r.get("port");
        } catch (IOException | ResourceDoesNotExistException e) {
            server = "127.0.0.1";
            port = 121;
        }

        SERVER_IP = server;
        PORT = port;
    }

    public ClientFrame() {
        super("Council Elections - Seth M.R. Jaipuria School");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(640, 480);
        setLayout(cardLO = new CardLayout());
        setLocationRelativeTo(null);

        try {
            client = new Client(SERVER_IP, PORT);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, e.getMessage(), 
                    e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        createChooserPanel();

        AllDetails data = client.getDetails();

        redP = new ClientVotePanel(RED_HOUSE, data.getRedHouseDetails(),
                data.getSportsCap(), data.getSportsVCap());
        redP.addPropertyChangeListener(this);
        blueP = new ClientVotePanel(BLUE_HOUSE, data.getBlueHouseDetails(),
                data.getSportsCap(), data.getSportsVCap());
        blueP.addPropertyChangeListener(this);
        yellowP = new ClientVotePanel(YELLOW_HOUSE, data.getYellowHouseDetails(),
                data.getSportsCap(), data.getSportsVCap());
        yellowP.addPropertyChangeListener(this);
        greenP = new ClientVotePanel(GREEN_HOUSE, data.getGreenHouseDetails(),
                data.getSportsCap(), data.getSportsVCap());
        greenP.addPropertyChangeListener(this);

        add(chooseHouseP, CHOOSER_P);
        add(redP, RED_HOUSE);
        add(blueP, BLUE_HOUSE);
        add(yellowP, YELLOW_HOUSE);
        add(greenP, GREEN_HOUSE);

        glassPane = new WaitGlassPane();
        setGlassPane(glassPane);

        cardP = getContentPane();
        cardLO.show(getContentPane(), CHOOSER_P);

        setVisible(true);
    }

    private void createChooserPanel() {
        chooseHouseP = new JPanel(new FlowLayout(FlowLayout.CENTER));
        chooseHouseCB = new JComboBox<>();
        chooseHouseCB.addItem(RED_HOUSE);
        chooseHouseCB.addItem(BLUE_HOUSE);
        chooseHouseCB.addItem(YELLOW_HOUSE);
        chooseHouseCB.addItem(GREEN_HOUSE);
        chooseHouseB = new JButton("Accept");
        chooseHouseB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent et) {
                cardLO.show(cardP, chooseHouseCB.getSelectedItem().toString());
            }
        });
        chooseHouseP.add(chooseHouseCB);
        chooseHouseP.add(chooseHouseB);
    }

    //PropertyChangeListener method
    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        switch (pce.getPropertyName()) {
            case ClientVotePanel.VOTE_PROPERTY:
                glassPane.setVisible(true);
                glassPane.start();

                worker = new VoteCaster(this, (Vote) pce.getNewValue());
                worker.execute();
            case ClientVotePanel.BACK_PROPERTY:
                cardLO.show(cardP, CHOOSER_P);
                try {
                    ClientVotePanel cvp = (ClientVotePanel) pce.getSource();
                    cvp.resetButtons();
                } catch (ClassCastException e) {
                }
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    System.err.println("Unable to use SystemLNF");
                }
                new ClientFrame();
            }
        });
    }

    private class VoteCaster extends SwingWorker<Void, Message> {

        private ClientFrame cf;
        private Vote vote;

        public VoteCaster(ClientFrame _cf, Vote v) {
            cf = _cf;
            vote = v;
        }

        @Override
        public Void doInBackground() {
            try {
                if (vote == null) {
                    throw new NullPointerException("Vote is null!!");
                }
                client.castVote(vote);
            } catch (NullPointerException | ClassNotFoundException | IOException e) {
                e.printStackTrace(System.err);
                return null;
            }

            Message m = null;

            try {
                m = cf.client.waitForReply();
                publish(m);

                m = cf.client.waitForReply();
                System.out.println("Message received");
                System.out.println(m);
                if (!m.equals(Message.UNLOCK_MESSAGE)) {
                    //Do something
                }
                cf.glassPane.stop();
                cf.glassPane.setVisible(false);
            } catch (java.io.IOException e) {
                //Do something
                return null;
            }

            return null;
        }

        @Override
        public void process(java.util.List<Message> mList) {
            Message m = mList.get(0);
            if (m == null) {
                m = Message.FAIL_MESSAGE;
            }

            JOptionPane.showMessageDialog(cf, m.getMessage(), m.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        }

        @Override
        public void done() {
        }
    }
}