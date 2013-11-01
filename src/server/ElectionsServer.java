package server;

import data.*;
import database.*;
import java.awt.Image;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Observable;

public class ElectionsServer extends Observable implements Runnable, ElectionsServerThread.VoteAcceptor {

    private ServerSocket server = null;
    private Thread thread = null;
    private ElectionsServerThread client = null;
    private PrintStream out;
    private AllDetails alldata;
    private DatabaseConnection databasecon;

    public ElectionsServer(int port, PrintStream pw) {
        try {
            out = pw;
            out.println("Trying to start server...");
            out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            out.println("Server started: " + server);

            databasecon = new DatabaseConnection();
            databasecon.openConnection();

            loadData();

            start();
        } catch (ClassNotFoundException ex) {
            out.println("Please add required jar files for SQL integration.");
            ex.printStackTrace(System.err);
        } catch (SQLException ex) {
            out.println("No Database found.");
            ex.printStackTrace(System.err);
        } catch (Exception ioe) {
            ioe.printStackTrace(System.err);
        }
    }

    public ElectionsServer(int port) {
        this(port, System.out);
    }

    @Override
    public void run() {
        while (thread != null) {
            try {
                out.println("Waiting for a client ...");
                addThread(server.accept());
            } catch (IOException ie) {
                System.err.println("Client Acceptance Error: ");
                ie.printStackTrace(System.err);
            }
        }
    }

    //VoteAcceptor method
    @Override
    public boolean castVote(Vote v) {
        try {

            databasecon.updateVotes(v.getSportsCapt().getID());
            databasecon.updateVotes(v.getSportsVCapt().getID());
            databasecon.updateVotes(v.getCapt().getID());
            databasecon.updateVotes(v.getVCapt().getID());
            databasecon.updateVotes(v.getPref09().getID());
            databasecon.updateVotes(v.getPref10().getID());
            databasecon.updateVotes(v.getPref11().getID());
            databasecon.updateVotes(v.getPref12().getID());

            return true;
        } catch (Exception e) {
            e.printStackTrace(out);
            return false;
        }
    }

    public void addThread(Socket socket) {
        out.println("Client accepted: " + socket);
        client = new ElectionsServerThread(socket, this, out);
        try {
            client.open();
            client.sendDetails(alldata);
            client.getThread().start();

            setChanged();
            notifyObservers(client);

        } catch (IOException ioe) {
            System.err.println("Error opening thread: ");
            ioe.printStackTrace(System.err);
        }
    }

    public final void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            try {
                //TODO Add code for stopping Thread
                //thread.stop();
                databasecon.closeConnection();
            } catch (SQLException ex) {
                out.println(ex.getMessage());
                ex.printStackTrace(System.err);
            }
            thread = null;
        }
    }

    private void loadData() {
        out.println("Starting to load Data");


        CandidateDetails sportsCap[] = null;
        {	//Sports Captain
            Object arr[][] = databasecon.getRecords("select * from candidates where post = 'Sports Captain'");
            int n = arr.length - 1;
            sportsCap = new CandidateDetails[n];
            for (int i = 0; i < n; i++) {
                int idx = i + 1;
                sportsCap[i] = new CandidateDetails(arr[idx][0].toString(),
                        getImageFromURL(""),
                        arr[idx][1].toString());
            }
        }

        CandidateDetails sportsVCap[] = null;
        {	//Sports Vice Captain
            Object arr[][] = databasecon.getRecords("select * from candidates where post = 'Sports Vice Captain'");
            int n = arr.length - 1;
            sportsVCap = new CandidateDetails[n];
            for (int i = 0; i < n; i++) {
                int idx = i + 1;
                sportsVCap[i] = new CandidateDetails(arr[idx][0].toString(),
                        getImageFromURL(""),
                        arr[idx][1].toString());
            }
        }

        HouseDetails red = null;
        {	//Red House
            CandidateDetails pref9[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='1'and STD='9' and post = 'Prefect'");
                int n = arr.length - 1;
                pref9 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref9[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref10[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='1'and STD='10' and post = 'Prefect'");
                int n = arr.length - 1;
                pref10 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref10[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref11[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='1'and STD='11' and post = 'Prefect'");
                int n = arr.length - 1;
                pref11 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref11[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref12[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='1'and STD='12' and post = 'Prefect'");
                int n = arr.length - 1;
                pref12 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref12[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails capt[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='1' and post = 'Captain'");
                int n = arr.length - 1;
                capt = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    capt[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails vcapt[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='1' and post = 'Vice Captain'");
                int n = arr.length - 1;
                vcapt = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    vcapt[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }

            red = new HouseDetails(House.RED, pref9, pref10, pref11, pref12, capt, vcapt);
        }

        HouseDetails blue = null;
        {	//Blue House
            CandidateDetails pref9[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='2'and STD='9' and post = 'Prefect'");
                int n = arr.length - 1;
                pref9 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref9[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref10[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='2'and STD='10' and post = 'Prefect'");
                int n = arr.length - 1;
                pref10 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref10[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref11[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='2'and STD='11' and post = 'Prefect'");
                int n = arr.length - 1;
                pref11 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref11[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref12[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='2'and STD='12' and post = 'Prefect'");
                int n = arr.length - 1;
                pref12 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref12[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails capt[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='2' and post = 'Captain'");
                int n = arr.length - 1;
                capt = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    capt[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails vcapt[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='2' and post = 'Vice Captain'");
                int n = arr.length - 1;
                vcapt = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    vcapt[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }

            blue = new HouseDetails(House.BLUE, pref9, pref10, pref11, pref12, capt, vcapt);
        }

        HouseDetails yellow = null;
        {	//Yellow House
            CandidateDetails pref9[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='3'and STD='9' and post = 'Prefect'");
                int n = arr.length - 1;
                pref9 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref9[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref10[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='3'and STD='10' and post = 'Prefect'");
                int n = arr.length - 1;
                pref10 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref10[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref11[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='3'and STD='11' and post = 'Prefect'");
                int n = arr.length - 1;
                pref11 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref11[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref12[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='3'and STD='12' and post = 'Prefect'");
                int n = arr.length - 1;
                pref12 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref12[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails capt[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='3' and post = 'Captain'");
                int n = arr.length - 1;
                capt = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    capt[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails vcapt[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='3' and post = 'Vice Captain'");
                int n = arr.length - 1;
                vcapt = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    vcapt[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }

            yellow = new HouseDetails(House.YELLOW, pref9, pref10, pref11, pref12, capt, vcapt);
        }

        HouseDetails green = null;
        {	//Green House
            CandidateDetails pref9[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='4'and STD='9' and post = 'Prefect'");
                int n = arr.length - 1;
                pref9 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref9[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref10[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='4'and STD='10' and post = 'Prefect'");
                int n = arr.length - 1;
                pref10 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref10[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref11[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='4'and STD='11' and post = 'Prefect'");
                int n = arr.length - 1;
                pref11 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref11[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails pref12[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='4'and STD='12' and post = 'Prefect'");
                int n = arr.length - 1;
                pref12 = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    pref12[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails capt[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='4' and post = 'Captain'");
                int n = arr.length - 1;
                capt = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    capt[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }
            CandidateDetails vcapt[] = null;
            {
                Object arr[][] = databasecon.getRecords("select * from candidates where HOUSEID ='4' and post = 'Vice Captain'");
                int n = arr.length - 1;
                vcapt = new CandidateDetails[n];
                for (int i = 0; i < n; i++) {
                    int idx = i + 1;
                    vcapt[i] = new CandidateDetails(arr[idx][0].toString(),
                            getImageFromURL(""),
                            arr[idx][1].toString());
                }
            }

            green = new HouseDetails(House.GREEN, pref9, pref10, pref11, pref12, capt, vcapt);
        }

        alldata = new AllDetails(red, blue, yellow, green, sportsCap, sportsVCap);

        out.println("Loading Data completed successfully");
    }

    private Image getImageFromURL(String url) {
        //return javax.imageio.ImageIO.read(new java.io.File(url));
        return null;
    }
}
