package data;

public class HouseDetails implements java.io.Serializable {
	
	private CandidateDetails[] pref09,pref10,pref12,pref11;
	private CandidateDetails[] houseCap,houseVCap;
	private House house;
	
    public HouseDetails(House _house, CandidateDetails[] pref09,
    						CandidateDetails[] pref10,
    						CandidateDetails[] pref11,
    						CandidateDetails[] pref12,
    						CandidateDetails[] houseCap,
    						CandidateDetails[] houseVCap) {
		this.pref09=pref09;
		this.pref10=pref10;
		this.pref11=pref11;
		this.pref12=pref12;
		this.houseCap=houseCap;
		this.houseVCap=houseVCap;
		house=_house;		
    }
    
    public House getHouse(){
    	return house;
    }    
    public CandidateDetails[] getpref09()
    {
    	return pref09;
    }
    public CandidateDetails[] getpref10()
    {
    	return pref10;
    }
    public CandidateDetails[] getpref11()
    {
    	return pref11;
    }
    public CandidateDetails[] getpref12()
    {
    	return pref12;
    }
    public CandidateDetails[] gethouseCap()
    {
    	return houseCap;
    }
    public CandidateDetails[] gethouseVCap()
    {
    	return houseVCap;
    }
	
}

















