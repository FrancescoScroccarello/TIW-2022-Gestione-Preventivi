package it.polimi.tiw.beans;

public class User {
	private int id;
	private String username;
	private String name;
	private String surname;
	
	public User(int num,String usr, String nm, String srnm) {
		id=num;
		username=usr;
		name=nm;
		surname=srnm;
	}
	
	public int getID() {return this.id;}
	public String getUsername() {return this.username;}
	public String getName() {return this.name;}
	public String getSurname() {return this.surname;}

}
