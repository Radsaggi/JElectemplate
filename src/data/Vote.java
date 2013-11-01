package data;
public class Vote implements java.io.Serializable{
	private House house;
	private CandidateDetails capt,vCapt,pref09,pref10,pref11,pref12,sportsCapt,sportsVCap;
	
	public Vote(House house,CandidateDetails capt,CandidateDetails vCapt,
    			CandidateDetails pref09,CandidateDetails pref10,
    			CandidateDetails pref11,CandidateDetails pref12,
    			CandidateDetails sportsCapt,CandidateDetails sportsVCap) {
    	this.house = house;
    	this.capt = capt;
    	this.vCapt = vCapt;
    	this.pref09 = pref09;
    	this.pref10 = pref10;
    	this.pref11 = pref11;
    	this.pref12 = pref12;
    	this.sportsCapt = sportsCapt;
    	this.sportsVCap = sportsVCap;
    }
    public House getHouse()
    {
    	return house;
    }
    public CandidateDetails getCapt()
    {
    	return capt;
    }
    public CandidateDetails getVCapt()
    {
    	return vCapt;
    }
    public CandidateDetails getPref09()
    {
    	return pref09;
    }
    public CandidateDetails getPref10()
    {
    	return pref10;
    }
    public CandidateDetails getPref11()
    {
    	return pref11;
    }
    public CandidateDetails getPref12()
    {
    	return pref12;
    }
    public CandidateDetails getSportsCapt()
    {
    	return sportsCapt;
    }
    public CandidateDetails getSportsVCapt()
    {
    	return sportsVCap;
    }
}