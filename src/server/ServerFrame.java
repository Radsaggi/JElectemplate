package server;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ServerFrame extends JFrame implements Observer, ActionListener {

    private ElectionsServer electionsServer;
    private TextAreaOutputStream taWriter;
    private JButton unlockSelectedB, unlockAllB;
    private JList<String> lockedClientsL;
    private LockedClientsListModel lockedClientsLM;
    private JTextArea outputTA;
    private JMenuItem startMI;
    private static final int PORT = 41414;
    private static final KeyStroke START_SERVER_KS = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
            InputEvent.ALT_DOWN_MASK
            + InputEvent.CTRL_DOWN_MASK);
    private static final String UNLOCK_SELECTION_AC = "Selection",
            UNLOCK_ALL_AC = "All",
            START_SERVER_AC = "StartServer";
    private static String INITIAL_STRING = "Server NOT started ...\n";

    static {
        String str = "";
        try {
            java.io.File file = new java.io.File(System.getProperty("user.home"), "Error.log");
            PrintStream ps = new PrintStream(new FileOutputStream(file), true);
            System.setErr(ps);
            str = "LOGGING RUNNING.\n";
        } catch (FileNotFoundException e) {
            str = "LOGGING NOT RUNNING.\n";
        }
        INITIAL_STRING = str + INITIAL_STRING;
    }

    public ServerFrame() {
        super("Elections Server");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                shutdown();
            }
        });

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Server");
        JMenuItem mi = new JMenuItem("Start");
        mi.setActionCommand(START_SERVER_AC);
        mi.setAccelerator(START_SERVER_KS);
        mi.addActionListener(this);
        startMI = mi;
        menu.add(mi);
        mb.add(menu);
        setJMenuBar(mb);

        lockedClientsLM = new LockedClientsListModel();
        lockedClientsL = new JList<>(lockedClientsLM);
        lockedClientsL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lockedClientsL.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                updateButtons();
            }
        });
        JScrollPane jsp1 = new JScrollPane(lockedClientsL);
        jsp1.setPreferredSize(new java.awt.Dimension(100, 100));
        JPanel p1 = new JPanel(new GridLayout(1, 1));
        p1.setBorder(BorderFactory.createTitledBorder("Locked Clients"));
        p1.add(jsp1);
        unlockSelectedB = new JButton("Unlock");
        unlockSelectedB.setActionCommand(UNLOCK_SELECTION_AC);
        unlockSelectedB.setEnabled(false);
        unlockSelectedB.addActionListener(this);
        unlockAllB = new JButton("UnlockAll");
        unlockAllB.setActionCommand(UNLOCK_ALL_AC);
        unlockAllB.addActionListener(this);
        unlockAllB.setEnabled(false);
        outputTA = new JTextArea();
        outputTA.setEditable(false);
        outputTA.append(INITIAL_STRING);
        JScrollPane jsp2 = new JScrollPane(outputTA);
        jsp2.setPreferredSize(new java.awt.Dimension(400, 400));
        JPanel p2 = new JPanel(new GridLayout(1, 1));
        p2.setBorder(BorderFactory.createTitledBorder("Message Board"));
        p2.add(jsp2);
        JSeparator jsep = new JSeparator(JSeparator.VERTICAL);

        GroupLayout gp = new GroupLayout(getContentPane());
        getContentPane().setLayout(gp);
        gp.setAutoCreateGaps(true);
        gp.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = gp.createSequentialGroup();
        hGroup.addComponent(p1);
        hGroup.addGroup(gp.createParallelGroup()
                .addComponent(unlockSelectedB)
                .addComponent(unlockAllB));
        hGroup.addComponent(jsep);
        hGroup.addComponent(p2);
        gp.setHorizontalGroup(hGroup);

        GroupLayout.ParallelGroup vGroup = gp.createParallelGroup();
        vGroup.addComponent(p1);
        vGroup.addGroup(gp.createSequentialGroup()
                .addComponent(unlockSelectedB)
                .addComponent(unlockAllB));
        vGroup.addComponent(jsep);
        vGroup.addComponent(p2);
        gp.setVerticalGroup(vGroup);


        electionsServer = null;
        taWriter = new TextAreaOutputStream(outputTA);


        pack();
        setVisible(true);
    }

    //ActionListener method
    @Override
    public void actionPerformed(ActionEvent ae) {
        String comm = ae.getActionCommand();
        switch (comm) {
            case START_SERVER_AC:
                if (electionsServer != null) {
                    System.err.println("Duplicate StartServer request in ServerFrame.actionPerformed(ActionEvent)-\n " + comm);
                    return;
                }
                try {
                    electionsServer = new ElectionsServer(PORT, new PrintStream(taWriter));
                    electionsServer.addObserver(this);
                    startMI.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
                break;
            case UNLOCK_ALL_AC:
                lockedClientsLM.removeAll();
                updateButtons();
                break;
            case UNLOCK_SELECTION_AC:
                int idx = lockedClientsL.getSelectedIndex();
                lockedClientsLM.remove(idx);
                lockedClientsL.clearSelection();
                updateButtons();
                break;
            default:
                System.err.println("Unknown ACtionCommand received in ServerFrame.actionPerformed(ActionEvent)-\n " + comm);
        }
    }

    //Observer method
    @Override
    public void update(Observable obs, Object ob) {

        if (obs instanceof ElectionsServer) {
            try {
                ElectionsServerThread elec = (ElectionsServerThread) ob;
                elec.addObserver(this);
            } catch (ClassCastException e) {
                System.err.println("Error casting in ServerFrame.update(Observable,Object)");
                e.printStackTrace(System.err);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        if (obs instanceof ElectionsServerThread) {
            try {
                ElectionsServerThread elec = (ElectionsServerThread) obs;
                if (ob instanceof Exception) {
                    Exception e = (Exception) ob;
                    outputTA.append(e.getMessage() + " " + elec.getID() + "\n");
                    return;
                }
                lockedClientsLM.add(elec);
                updateButtons();
            } catch (ClassCastException e) {
                System.err.println("Error casting in ServerFrame.update(Observable,Object)");
                e.printStackTrace(System.err);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private void shutdown() {
        int i = JOptionPane.showConfirmDialog(this, "Are you sure you want to close this?",
                "Election Server Close",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (i != JOptionPane.YES_OPTION) {
            return;
        }

        if (electionsServer != null) {
            electionsServer.stop();
        }

        setVisible(false);
        dispose();
        System.exit(0);
    }

    private void updateButtons() {
        if (lockedClientsLM.isEmpty()) {
            unlockAllB.setEnabled(false);
            unlockSelectedB.setEnabled(false);
        } else if (lockedClientsL.isSelectionEmpty()) {
            unlockAllB.setEnabled(true);
            unlockSelectedB.setEnabled(false);
        } else {
            unlockAllB.setEnabled(true);
            unlockSelectedB.setEnabled(true);
        }
    }

    private class LockedClientsListModel extends AbstractListModel<String> {

        private ArrayList<ElectionsServerThread> list;

        public LockedClientsListModel() {
            list = new ArrayList<>();
        }

        public void add(ElectionsServerThread elec) {
            int idx = list.size();
            list.add(idx, elec);
            fireIntervalAdded(this, idx, idx);
        }

        public void remove(int idx) {
            ElectionsServerThread elec = list.get(idx);
            elec.unlockScreen();
            list.remove(idx);
            fireIntervalRemoved(this, idx, idx);
        }

        public void removeAll() {
            for (ElectionsServerThread elec : list) {
                elec.unlockScreen();
            }
            int idx = list.size();
            idx = idx == 0 ? 0 : idx - 1;
            list.removeAll(list);
            fireIntervalRemoved(this, 0, idx);
        }

        @Override
        public int getSize() {
            return list.size();
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        @Override
        public String getElementAt(int idx) {
            return list.get(idx).toString();
        }
    }

    private class TextAreaOutputStream extends OutputStream {

        private JTextArea ta;

        public TextAreaOutputStream(JTextArea _ta) {
            ta = _ta;
        }

        @Override
        public void write(byte buf[], int off, int len) {
            String str = new String(buf, off, len);
            ta.append(str);
        }

        @Override
        public void write(byte[] buf) {
            String str = new String(buf);
        }

        @Override
        public void write(int b) {
            write(new byte[]{(byte) b});
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
                new ServerFrame();
            }
        });
    }
}