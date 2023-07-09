package it.polimi.tiw.beans;

public class Product {
	private int code;
	private String name;
	private String path;
	
	public Product(int id, String nm, String pth) {
		this.code=id;
		this.name=nm;
		this.path=pth;
	}
	
	public int getID() {
		return this.code;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPath() {
		return this.path;
	}
}
