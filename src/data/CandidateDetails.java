package data;

/**
 * @(#)CandidateDetails.java
 *
 *
 * @author 
 * @version 1.00 2012/6/23
 */

import java.io.Serializable;
import java.awt.Image;

public class CandidateDetails implements Serializable {
	private Image img;
	private String name,cID;
	
    public CandidateDetails(String _cID,Image _img,String _name) {
    	img=_img;
    	name=_name;
    	cID=_cID;
    }
    
    public CandidateDetails(CandidateDetails cd){
    	img=null;
    	name=cd.name;
    	cID=cd.cID;
    }
    
    public String getName(){
    	return name;
    }
    
    public String getID(){
    	return cID;
    }
    
    public Image getImage(){
    	return img;
    }
}