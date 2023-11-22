package yg;
public class Job {
	
	//Fields
	private Type type;
	private int ID;
	
	
	//Constructor
	public Job(Type type,int ID) {
		this.type=type;
		this.ID=ID;
	}


	public Type getType() {
		return type;
	}


	public void setType(Type type) {
		this.type = type;
	}


	public int getID() {
		return ID;
	}


	public void setID(int iD) {
		ID = iD;
	}
	
}
