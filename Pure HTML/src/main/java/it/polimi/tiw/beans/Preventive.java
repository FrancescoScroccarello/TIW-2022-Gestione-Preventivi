package it.polimi.tiw.beans;

import java.util.ArrayList;

public class Preventive {

		private int ID;
		private int IDClient;
		private int IDEmployee;
		private int IDProduct;
		private String productName;
		private ArrayList<Integer> options;
		private int price;
		
		public Preventive(int id, int client, int employee, int product, String pn, int cost) {
			this.ID=id;
			this.IDClient=client;
			this.IDEmployee=employee;
			this.IDProduct=product;
			this.productName=pn;
			this.options=null;
			this.price=cost;
		}
	public int getID() {
		return this.ID;
	}
	public int getClient() {
		return this.IDClient;
	}
	public int getEmployee() {
		return this.IDEmployee;
	}
	public int getProduct() {
		return this.IDProduct;
	}
	public String getProductName() {
		return this.productName;
	}
	public ArrayList<Integer> getOptions() {
		return this.options;
	}
	public void setOptions(ArrayList<Integer> list){
		this.options=list;
	}
	public int getPrice() {
		return this.price;
	}
	
}
