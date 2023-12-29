package yg;
public class Job {
	
	//Fields
//	private charachter type;
	private int ID;
	private char type;
	
	
	//Constructor
	public Job(char type, int ID) {
		this.type=type;
		this.ID=ID;
	}

	//getters and setters
	public char getType() {
		return type;
	}


	public void setType(char type) {
		this.type = type;
	}


	public int getID() {
		return ID;
	}


	public void setID(int iD) {
		ID = iD;
	}
	
}
