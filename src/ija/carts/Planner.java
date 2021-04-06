package ija.carts;


import java.util.ArrayList;

import ija.warehouse.Goods;

public class Planner {
	private ArrayList<Order> orders;
	public Planner() {
		// TODO Auto-generated constructor stub
		orders = new ArrayList<Order>();
	}
	public Boolean dispatch(Goods[] outLoad) {
		// TODO Auto-generated method stub
		System.out.println("Na výdajné okienko bola vyložená objednávka");
		System.out.println("obsahuje:");
		for (int i = 0; i < outLoad.length; i++) {
			System.out.println(outLoad[i].toString());	
		}
		System.out.println("...to je všetko");
		return true;
		
	}

	public Order getNextOrder() {
		Order a = orders.get(0);
		orders.remove(0);
		return a;
	} 

	public void addOrder(Order order) {
		orders.add(order);
	}
	public String toString() {
		if(orders.size()==0) {
			return "no order";
		}else {
			StringBuilder sb = new StringBuilder();
			for(int i = 0;i<orders.size();i++) {
				if(i!=0) sb.append(" ; ");
				sb.append("[ "+orders.get(i).toString()+"]");
			}
			return sb.toString();
		}
	}
}
