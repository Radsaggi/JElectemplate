package data;

public enum House {
	RED("Ganga/Yamuna",1),BLUE("Narmada/Tapti",2),YELLOW("Godavari/Mahanadi",3),GREEN("Krishna/Kaveri",4);
	
	private String name;
	private int id;
	
	House(String str,int _id){
		name=str;
		id=_id;
	}
	
	public String toString(){
		return name;
	}
}