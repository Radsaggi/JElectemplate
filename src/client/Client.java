package client;

import data.*;
import java.io.*;
import java.net.*;

public class Client {
	private Socket socket = null;
    private ObjectOutputStream streamOut = null;
    private ObjectInputStream streamIn = null;
    private AllDetails data;
    
    public Client(String ip, int port)throws UnknownHostException,
    										 IOException {
    	socket = new Socket(ip, port);
		System.out.println("Connected: " + socket);
        start();
		
        readDetails();
    }
    
    public void castVote(Vote vote)throws ClassNotFoundException,IOException{
    	streamOut.writeObject(vote);
        streamOut.flush();
    }
    
    public Message waitForReply()throws IOException{
    	try {
    		Object ob=streamIn.readObject();
    		Message mess=(Message)ob;
    		return mess;
    	} catch (ClassNotFoundException | ClassCastException ce){
    		return null;
    	}
    }
    
    public final void start() throws IOException {
        streamOut = new ObjectOutputStream(socket.getOutputStream());
        streamIn = new ObjectInputStream(socket.getInputStream());
        System.out.println ("streams connected");
        	
    }
    
    public void stop()throws IOException{
        if (streamOut != null) {
        	streamOut.close();
        }		
        if (streamIn != null) {
            streamIn.close();
		}
		
		if (socket != null) {
        	socket.close();
		}
    }
    
    private void readDetails(){
    	System.out.println ("recieving data");    		
    		
    	try {
    		Object ob=streamIn.readObject();
    		if (!(ob instanceof AllDetails)){
    			System.out.println ("NO DATA RECEIVED!!!");
    			return;
    		}
    		data = (AllDetails) ob;
    		System.out.println ("data recieved");
    	} catch (IOException | ClassNotFoundException e){
    		e.printStackTrace(System.err);
    	}
    }
    
    public AllDetails getDetails(){
    	return data;
    }
}


