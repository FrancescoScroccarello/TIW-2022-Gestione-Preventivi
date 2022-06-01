package it.polimi.tiw.beans;

public class Option {

	private int code;
	private int product;
	private String type;
	private String name;
	
	public Option(int id, int pCode, String typ, String nm) {
		this.code = id;
		this.product = pCode;
		this.type = typ;
		this.name = nm;
	}
	
	public int getCode() {
		return this.code;
	}
	public int getProduct() {
		return this.product;
	}
	public String getType() {
		return this.type;
	}
	public String getName() {
		return this.name;
	}
}
