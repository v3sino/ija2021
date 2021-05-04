package ija.carts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import ija.warehouse.Goods;
import ija.warehouse.GoodsType;

public class Planner {
	public ArrayList<Order> orders;
	public Planner() {
		orders = new ArrayList<Order>();
	}
	
	public Boolean dispatch(Goods[] outLoad) {
		System.out.println("Na výdajné okienko bola vyložená objednávka");
		System.out.println("obsahuje:");
		for (int i = 0; i < outLoad.length; i++) {
			System.out.println(outLoad[i].toString());	
		}
		System.out.println("...to je všetko");
		return true;
		
	}

	public Order getNextOrder() {
		System.out.println("In if" + orders.size());
		if(orders.size()!=0) {
			return orders.remove(0);
		}
		return null;
	} 

	public void addOrder(Order order) {
		orders.add(order);
		System.out.println("Order count = "+orders.size());
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
	
	public void readOrderFromFile(String string, ArrayList<GoodsType> types) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(string));
			String line;
			String name = null;
			ArrayList<GoodsType> goods = new ArrayList<GoodsType>();
			ArrayList<Integer> count = new ArrayList<Integer>();
			boolean found = false;
			
			while((line=br.readLine())!=null) {
				line = line.trim();
				System.out.println(line);
				if(line.charAt(0)=='#') {
					// Order file comment
					System.out.println("Line comment");
					
				}else if(line.charAt(0)=='-' && line.charAt(1)=='-') {
					// Order file name of order (new order identifier)
					System.out.println("new order "+line);
					if(name!=null) {
						GoodsType a1 [] = new GoodsType[goods.size()];
						int a2 [] = new int[count.size()];
						for (int i = 0; i < a1.length; i++) {
							a1[i] = goods.get(i);
						}
						for (int i = 0; i < a2.length; i++) {
							a2[i] = count.get(i);
						}
						goods.clear();
						count.clear();
						this.addOrder(new Order(a2, a1, name));
					}
					name = line.substring(2).trim();
					
				}else {
					// Order file one stock order (structure of 1. line is: name of stock)(structure of 2. line: number any_comment with/without spaces)
					found = false;
					for (GoodsType type : types) {
						//System.out.println(type.getName());
						//System.out.println(line);
						if(type.getName().equalsIgnoreCase(line)) {
							goods.add(type);
							found = true;
							break;
						}
					}
					if(!found) {
						System.out.println("not found "+line);
						JOptionPane.showMessageDialog(null,"Unknown name of good type: "+line,"Unknown name of good type: "+line, JOptionPane.ERROR_MESSAGE);
						System.exit(1);
						br.readLine();
						continue;
					}
					if((line=br.readLine())==null) {
						System.out.println("number is null");
						goods.remove(goods.size()-1);
						JOptionPane.showMessageDialog(null,"wrong format of Order file","wrong format of Order file", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
						break;
					}
					try {
						int a = Integer.parseInt(line.trim().split(" ")[0]);
						count.add(a);
					}catch (Exception e) {
						goods.remove(goods.size()-1);
						JOptionPane.showMessageDialog(null,"wrong format of Order file",line.trim() +  "doesn't starts with number representing count", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					}
				}
			}
			if(name!=null) {
				GoodsType a1 [] = new GoodsType[goods.size()];
				int a2 [] = new int[count.size()];
				for (int i = 0; i < a1.length; i++) {
					a1[i] = goods.get(i);
				}
				for (int i = 0; i < a2.length; i++) {
					a2[i] = count.get(i);
				}
				System.out.println("pocet orderov: "+goods.size());
				this.addOrder(new Order(a2, a1, name));
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
