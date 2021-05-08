package ija.carts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import ija.warehouse.Goods;
import ija.warehouse.GoodsType;
import javafx.application.Platform;
import javafx.scene.control.Alert;
/**
 * Planner handles all the orders
 * @author xbabac02
 */
public class Planner {
	public ArrayList<Order> orders;
	private int totalCount = 0;
	private int totalDispatched = 0;
	public Planner() {
		orders = new ArrayList<>();
	}
	
	public Boolean dispatch(Goods[] outLoad, Order o) {
		StringBuilder sb = new StringBuilder();
		// sb.append("Dispatching:\n");
		//System.out.println("Na výdajné okienko bola vyložená objednávka");
		//System.out.println("obsahuje:");
		for (Goods goods : outLoad) {
			sb.append(goods.toString()).append("\n");
			System.out.println(goods);
		}
		//System.out.println("...to je všetko");
		if(o==null) {
			//JOptionPane.showMessageDialog(null, sb.toString(), "Dispatching", JOptionPane.INFORMATION_MESSAGE);
			Platform.runLater(() -> ija.gui.GUI.showAlert(Alert.AlertType.INFORMATION, "Dispatching", "Dispatching order:", sb.toString()));
		}else {
			for (Goods goods : outLoad) {
				for (int j = 0; j < o.getGoodTypeCount().length; j++) {
					if (goods.get_type().equals(o.getGoodTypeObj()[j])) {
						o.goodTypeCountExp[j]++;
					}
				}
			}
			//JOptionPane.showMessageDialog(null, sb.toString(), "Dispatching order:"+o.getName(), JOptionPane.INFORMATION_MESSAGE);
			totalDispatched+=outLoad.length;
			
			// This is how to run the Alert
			Platform.runLater(() -> ija.gui.GUI.showAlert(Alert.AlertType.INFORMATION, "Dispatching", "Dispatching order:", sb.toString()));
			
			if(o.dispatched()==o.all()) {
				//System.out.println("removing");
				orders.remove(o);
			}
		}
		return true;
		
	}

	public Order getNextOrder() {
		//if(orders.size()!=0) {
		//	return orders.remove(0);
		//}
		for(Order o : orders) {
			if(!o.isEmpty()) {
				return o;
			}
		}
		return null;
	} 

	public void addOrder(Order order) {
		orders.add(order);
		totalCount+=order.all();
	}
	
	public String toString() {
		if(orders.size()==0) {
			return "no order";
		}else {
			StringBuilder sb = new StringBuilder();
			for(int i = 0;i<orders.size();i++) {
				if(i!=0) sb.append(" ; ");
				sb.append("[ ").append(orders.get(i).toString()).append("]");
			}
			return sb.toString();
		}
	}
	
	public void readOrderFromFile(String string, ArrayList<GoodsType> types) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(string));
			String line;
			String name = null;
			ArrayList<GoodsType> goods = new ArrayList<>();
			ArrayList<Integer> count = new ArrayList<>();
			boolean found;
			
			while((line=br.readLine())!=null) {
				line = line.trim();
				if(line.charAt(0)=='#') {
					// Order file comment
					
				}else if(line.charAt(0)=='-' && line.charAt(1)=='-') {
					// Order file name of order (new order identifier)
					System.out.println("new order "+line);
					if(name!=null) {
						GoodsType[] a1 = new GoodsType[goods.size()];
						int[] a2 = new int[count.size()];
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
				GoodsType[] a1 = new GoodsType[goods.size()];
				int[] a2 = new int[count.size()];
				for (int i = 0; i < a1.length; i++) {
					a1[i] = goods.get(i);
				}
				for (int i = 0; i < a2.length; i++) {
					a2[i] = count.get(i);
				}
				this.addOrder(new Order(a2, a1, name));
			}
			totalCount=0;
			for(Order ord : orders) {
				totalCount+=ord.all();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getTotalCount(){
		return totalCount;
	}

	public int getTotalDispatched(){
		return totalDispatched;
	}
}
