package data;

/**
 * @(#)AllDetails.java
 *
 *
 * @author 
 * @version 1.00 2012/6/23
 */


public class AllDetails implements java.io.Serializable{
	
	private HouseDetails red,blue,yellow,green;
	private CandidateDetails[] sportsCap,sportsVCap;

    public AllDetails(HouseDetails red,
    					HouseDetails blue,
    					HouseDetails yellow,
    					HouseDetails green,
    					CandidateDetails[] sportsCap,
    					CandidateDetails[] sportsVCap) {
    	this.red=red;
    	this.blue=blue;
    	this.yellow=yellow;
    	this.green=green;
    	this.sportsCap=sportsCap;
		this.sportsVCap=sportsVCap;
    }
    
    public HouseDetails getRedHouseDetails(){
    	return red;
    }
    
    public HouseDetails getBlueHouseDetails(){
    	return blue;
    }
    
    public HouseDetails getYellowHouseDetails(){
    	return yellow;
    }
    
    public HouseDetails getGreenHouseDetails(){
    	return green;
    }
    
    public CandidateDetails[] getSportsCap() {
    	return sportsCap;
    }
    
	public CandidateDetails[] getSportsVCap(){
		return sportsVCap;
	}
}