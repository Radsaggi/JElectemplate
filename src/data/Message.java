package data;

import java.io.Serializable;

public class Message implements Serializable {

    private String message;
    private String Title;
    
    public static final Message UNLOCK_MESSAGE=new Message("unhide","Done. Proceed."),
    							DONE_MESSAGE=new Message("Vote casted successfully!!!","Vote casted successfully!!!"),
    							FAIL_MESSAGE=new Message("Vote NOT casted!!!","Vote NOT casted!!!");

    public Message(String message, String T) {
        this.message = message;
        this.Title = T;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return Title;
    }
    
    public String toString(){
    	return "Title:"+getTitle()+"\tMessage:"+getMessage();
    }
    
    @Override
    public boolean equals(Object o){
    	if (!(o instanceof Message))
    		return false;
    	
    	Message m=(Message)o;
    	return Title.equals(m.Title)&&message.equals(m.message);
    }
}
