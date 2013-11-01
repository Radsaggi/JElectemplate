package server;

import java.io.*;
import java.net.*;
import java.util.Observable;
import java.util.concurrent.CountDownLatch;

import data.*;

public class ElectionsServerThread extends Observable implements Runnable {

    private Socket socket = null;
    private int ID = -1;
    private ObjectInputStream streamIn = null;
    private ObjectOutputStream streamOut = null;
    private Thread t;
    private CountDownLatch latch;
    private PrintStream out;
    private VoteAcceptor va;
    
    private static final String HOUSE_TITLE="Title";
    
    public static interface VoteAcceptor {
    	public boolean castVote(Vote v);
    }

    public ElectionsServerThread(Socket _socket,VoteAcceptor _va) {
        this(_socket,_va,System.out);
    }
    
    public ElectionsServerThread( Socket _socket,
    								VoteAcceptor _va,
    								PrintStream ps) {
        socket = _socket;
        out=ps;
        ID = socket.getPort();
        t=new Thread(this);
        va=_va;
    }

    public void run() {
        out.println("Server Thread " + ID + " running.");
        while (true) {
            try {
                Vote object = (Vote) streamIn.readObject();
                if (processVote(object)){
                	streamOut.writeObject(Message.DONE_MESSAGE);
                } else {
                	streamOut.writeObject(Message.FAIL_MESSAGE);
                }
                streamOut.flush();
                
                setChanged();
                notifyObservers();
                
                out.println ("Waiting for response...");
                latch=new CountDownLatch(1);
                latch.await();
                streamOut.writeObject(Message.UNLOCK_MESSAGE);
                streamOut.flush();
            } catch (Exception e){
            	setChanged();
            	notifyObservers(e);
            	e.printStackTrace(System.err);
            	break;
            }
        }
    }
    
    private boolean processVote(Vote m){
    	out.println(m);
    	return va.castVote(m);
    }
    
    public void sendDetails(AllDetails allData){
    	try {
    		out.println("Sending Data to client:"+getID());
    		streamOut.writeObject(allData);
    		streamOut.flush();
    		out.println("Sending Data to client:"+getID()+" complete");
    	} catch (Exception e){
    		System.out.println ("Error sending details...");
    		e.printStackTrace();
    	}
    }

    public void open() throws IOException {
        streamIn = new ObjectInputStream(socket.getInputStream());
        streamOut = new ObjectOutputStream(socket.getOutputStream());
    }

    public void close() throws IOException {        
        if (streamIn != null) {
            streamIn.close();
        }
        if (socket != null) {
            socket.close();
        }
    }
    
    public Thread getThread(){
    	return t;
    }
    
    public int getID(){
    	return ID;
    }
    
    public String toString(){
    	return Integer.toString(getID());
    }
    
    public void unlockScreen(){
    	latch.countDown();
    }
}
