package ija.carts;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JOptionPane;
import ija.carts.Planner;
import ija.warehouse.Goods;
import ija.warehouse.Shelf;
import ija.warehouse.Warehouse;
import ija.warehouse.MapInfo;

public class Cart {
	public Planner planner;
	private ArrayList<Goods> cargo = new ArrayList<Goods>();
	private ArrayList<Destination> destinations;
	private ArrayList<Destination> planned_path;
	private int completedDestinations;
	public int x;
	public int y;
	private int maxLoad= 10;
	public int load = 0;
	private MapInfo map;
	private int waitTime;
	private ArrayList<Goods> waitFor;
	private Warehouse wh;
	
	public Cart(Goods[] cargoToLoad, int[] place, Planner plan, MapInfo m, Warehouse warehouse) {
		Collections.addAll(cargo, cargoToLoad);
		planner = plan;
		if(place.length>1) {
			x = place[0];
			y = place[1];
		}
		destinations = new ArrayList<Destination>();
		completedDestinations = 0;
		wh = warehouse;
		map=m;
		waitTime=-1;
		waitFor = new ArrayList<Goods>();
		planned_path = new ArrayList<Destination>();
		load = cargo.size();
	}
	
	public void move() {
		map.printMap();
		/// Loading
		if(waitTime>0) {
			if(waitTime==1) {
				for (Goods goods : waitFor) {
					cargo.add(goods);
					load++;
				}
				waitTime=-1;
				// Plan the path
				// TODO ..............
				this.plan();
			}else {
				waitTime--;
			}
			return;
		}
		
		//Moving
		if(getPlanned_path().size()>0) {// way is planned, path is cleared 
			this.printPlanned_path();
			if(map.cells[x][y].crossroad) {
				// cart reached crossroad, path to next crossroad is planned, path to following crossroad is been planning
				// TODO .............
				this.plan();
			}
			
			//Move to next Destination from planned_path
			map.moveCart(x, y, getPlanned_path().get(0).x, getPlanned_path().get(0).y);
			x = getPlanned_path().get(0).x;
			y = getPlanned_path().get(0).y;
			getPlanned_path().remove(0);
			
		}else if(destinations.size()!=0 && destinations.size()!=completedDestinations){
			System.out.println("should move");
			if(Math.abs(destinations.get(completedDestinations).x-x)==1 && destinations.get(completedDestinations).y==y && destinations.get(completedDestinations).task==2){
				Shelf shelf = map.getShelf(destinations.get(completedDestinations));
				shelf.toString();
				//Sys TODO
				for(int i = 0;i<wh.shelves.size();i++) {
					if(wh.shelves.get(i).equals(shelf)) {
						System.out.println("shelf index ***"+i+"**");
					}
				}
				System.out.println("shelf on 1,3"+map.getShelf(new Destination(1, 3, 2)));
				System.out.println("shelf on 1,5"+map.getShelf(new Destination(1, 5, 2)));
				System.out.println("shelf on 1,6"+map.getShelf(new Destination(1, 6, 2)));
				System.out.println("shelf on 1,7"+map.getShelf(new Destination(1, 7, 2)));
				System.out.println("shelf has reserved :"+(shelf.numberOfGoods(destinations.get(completedDestinations).goodtype)-shelf.numberOfUnreservedGoods(destinations.get(completedDestinations).goodtype)));
				Goods good;
				for(int i = 0;i<destinations.get(completedDestinations).count;i++) {
					good = shelf.removeReserved(destinations.get(completedDestinations).goodtype);
					if(good==null){
						System.out.println("Shelf '"+shelf+"' can't remove Reserved good number "+i);
						break;
					}else{
						if(!this.load(good)){
							System.out.println("chyba");
						}
					}
				}
				this.findOrder();
				return;
			}
			if(Math.abs(destinations.get(completedDestinations).x-x)==1 && destinations.get(completedDestinations).y==y && destinations.get(completedDestinations).task==3){
				if(unload()) {
					load=0;
					this.findOrder();
				}
				return;
			}
			else {
				
			}
		}else{
			System.out.println("Find Order");
			this.findOrder();
		}
		
	}
	
	private Destination recursivePlanner(int last_x, int last_y) {
		if(Math.abs(destinations.get(completedDestinations).x-last_x)==1 && destinations.get(completedDestinations).y==last_y && destinations.get(completedDestinations).task==2) {
			Destination d = new Destination(last_x, last_y, 2);
			getPlanned_path().add(d);
			return d;
		}
		if(destinations.get(completedDestinations).x==x && destinations.get(completedDestinations).y==y && destinations.get(completedDestinations).task==3) {
			Destination d = new Destination(last_x, last_y, 3);
			getPlanned_path().add(d);
			return d;
		}
		if(map.cells[last_x][last_y].crossroad) {
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			Destination d = new Destination(last_x, last_y, 1);
			return d;
		}
		
		if(destinations.get(completedDestinations).x-last_x>1 || (destinations.get(completedDestinations).x-last_x>0 && destinations.get(completedDestinations).task==3)) {
			if(getPlanned_path().get(getPlanned_path().size()-1).x!=last_x+1 && map.isFree(last_x+1,last_y)){
				getPlanned_path().add(new Destination(last_x, last_y, 1));
				last_x=last_x+1;
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					getPlanned_path().remove(getPlanned_path().size()-1);
				}
				return d;
			}
		}
		if(destinations.get(completedDestinations).x-last_x<-1 || (destinations.get(completedDestinations).x-last_x<0 && destinations.get(completedDestinations).task==3)) {
			if(getPlanned_path().get(getPlanned_path().size()-1).x!=last_x-1 && map.isFree(last_x-1,last_y)){
				getPlanned_path().add(new Destination(last_x, last_y, 1));
				last_x=last_x-1;
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					getPlanned_path().remove(getPlanned_path().size()-1);
				}
				return d;
			}
		}
		if(destinations.get(completedDestinations).y-last_y>0) {
			if(getPlanned_path().get(getPlanned_path().size()-1).y!=last_y+1 && map.isFree(last_x,last_y+1)){
				getPlanned_path().add(new Destination(last_x, last_y, 1));
				last_y=last_y+1;
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					getPlanned_path().remove(getPlanned_path().size()-1);
				}
				return d;
			}
		}
		if(destinations.get(completedDestinations).y-last_y<0) {
			if(getPlanned_path().get(getPlanned_path().size()-1).y!=last_y-1 && map.isFree(last_x,last_y-1)){
				getPlanned_path().add(new Destination(last_x, last_y, 1));
				last_y=last_y-1;
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					getPlanned_path().remove(getPlanned_path().size()-1);
				}
				return d;
			}
		}
		if(getPlanned_path().get(getPlanned_path().size()-1).x!=last_x+1 && map.isFree(last_x+1,last_y)){
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			last_x=last_x+1;
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				getPlanned_path().remove(getPlanned_path().size()-1);
			}
			return d;
		}
		if(getPlanned_path().get(getPlanned_path().size()-1).x!=last_x-1 && map.isFree(last_x-1,last_y)){
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			last_x=last_x-1;
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				getPlanned_path().remove(getPlanned_path().size()-1);
			}
			return d;
		}
		if(getPlanned_path().get(getPlanned_path().size()-1).y!=last_y+1 && map.isFree(last_x,last_y+1)){
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			last_y=last_y+1;
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				getPlanned_path().remove(getPlanned_path().size()-1);
			}
			return d;
		}
		if(getPlanned_path().get(getPlanned_path().size()-1).y!=last_y-1 && map.isFree(last_x,last_y-1)){
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			last_y=last_y-1;
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				getPlanned_path().remove(getPlanned_path().size()-1);
			}
			return d;
		}
	return new Destination(-1, -1, -1);
	}
	
	/**
	 * function plans path (cell by cell)
	 */
	private void plan() {
		// ??? i don't know, if this works
		int last_x;
		int last_y;
		if(getPlanned_path().size()>0) {
			if(map.cells[getPlanned_path().get(getPlanned_path().size()-1).x][getPlanned_path().get(getPlanned_path().size()-1).y].crossroad && !(getPlanned_path().get(getPlanned_path().size()-1).x == destinations.get(0).x && getPlanned_path().get(getPlanned_path().size()-1).y==destinations.get(0).y)) {
				// Planned path ends on crossroad -> path is not completed
				last_x = getPlanned_path().get(getPlanned_path().size()-1).x;
				last_y = getPlanned_path().get(getPlanned_path().size()-1).y;
				
				while(true) {
					if(destinations.get(completedDestinations).x-last_x>1 || (destinations.get(completedDestinations).x-last_x>0 && destinations.get(completedDestinations).task==3)) {
						if(map.isFree(last_x+1,last_y)){
							last_x=last_x+1;
							Destination d = recursivePlanner(last_x, last_y);
							if(!(d.x<0 || d.y<0)) {
								break;
							}
						}
					}
					if(destinations.get(completedDestinations).x-last_x<-1 || (destinations.get(completedDestinations).x-last_x<0 && destinations.get(completedDestinations).task==3)) {
						if(map.isFree(last_x-1,last_y)){
							last_x=last_x-1;
							Destination d = recursivePlanner(last_x, last_y);
							if(!(d.x<0 || d.y<0)) {
								break;
							}
						}
					}
					if(destinations.get(completedDestinations).y-last_y>0) {
						if(map.isFree(last_x,last_y+1)){
							last_y=last_y+1;
							Destination d = recursivePlanner(last_x, last_y);
							if(!(d.x<0 || d.y<0)) {
								break;
							}
						}
					}
					if(destinations.get(completedDestinations).y-last_y<0) {
						if(map.isFree(last_x,last_y-1)){
							last_y=last_y-1;
							Destination d = recursivePlanner(last_x, last_y);
							if(!(d.x<0 || d.y<0)) {
								break;
							}
						}
					}
					if(map.isFree(last_x+1,last_y)){
						last_x=last_x+1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
					if(map.isFree(last_x-1,last_y)){
						last_x=last_x-1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
					if(map.isFree(last_x,last_y+1)){
						last_y=last_y+1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
					if(map.isFree(last_x,last_y-1)){
						last_y=last_y-1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
					break;
				}
			}else{
				// Path doesn't end on crossroad -> path is completed
				return;
			}
		}else {
			last_x = x;
			last_y = y;
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			
			while(true) {
				if(destinations.get(completedDestinations).x-last_x>1 || (destinations.get(completedDestinations).x-last_x>0 && destinations.get(completedDestinations).task==3)) {
					if(map.isFree(last_x+1,last_y)){
						last_x=last_x+1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).x-last_x<-1 || (destinations.get(completedDestinations).x-last_x<0 && destinations.get(completedDestinations).task==3)) {
					if(map.isFree(last_x-1,last_y)){
						last_x=last_x-1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).y-last_y>0) {
					if(map.isFree(last_x,last_y+1)){
						last_y=last_y+1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).y-last_y<0) {
					if(map.isFree(last_x,last_y-1)){
						last_y=last_y-1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(map.isFree(last_x+1,last_y)){
					last_x=last_x+1;
					Destination d = recursivePlanner(last_x, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x-1,last_y)){
					last_x=last_x-1;
					Destination d = recursivePlanner(last_x, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x,last_y+1)){
					last_y=last_y+1;
					Destination d = recursivePlanner(last_x, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x,last_y-1)){
					last_y=last_y-1;
					Destination d = recursivePlanner(last_x, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				break;
			}
			getPlanned_path().remove(0);
			last_x = getPlanned_path().get(getPlanned_path().size()-1).x;
			last_y = getPlanned_path().get(getPlanned_path().size()-1).y;
			
			while(true) {
				if(destinations.get(completedDestinations).x-last_x>1 || (destinations.get(completedDestinations).x-last_x>0 && destinations.get(completedDestinations).task==3)) {
					if(map.isFree(last_x+1,last_y)){
						last_x=last_x+1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).x-last_x<-1 || (destinations.get(completedDestinations).x-last_x<0 && destinations.get(completedDestinations).task==3)) {
					if(map.isFree(last_x-1,last_y)){
						last_x=last_x-1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).y-last_y>0) {
					if(map.isFree(last_x,last_y+1)){
						last_y=last_y+1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).y-last_y<0) {
					if(map.isFree(last_x,last_y-1)){
						last_y=last_y-1;
						Destination d = recursivePlanner(last_x, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(map.isFree(last_x+1,last_y)){
					last_x=last_x+1;
					Destination d = recursivePlanner(last_x, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x-1,last_y)){
					last_x=last_x-1;
					Destination d = recursivePlanner(last_x, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x,last_y+1)){
					last_y=last_y+1;
					Destination d = recursivePlanner(last_x, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x,last_y-1)){
					last_y=last_y-1;
					Destination d = recursivePlanner(last_x, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				break;
			}
		}
	}

	
	private void findOrder() {
		if(destinations.size()==0 || destinations.size()==completedDestinations) {
			destinations=new ArrayList<Destination>();
			Order order = planner.getNextOrder();
			if(order==null) {
				System.out.println("null");
				destinations.add(new Destination(0, 0, 1));
				return;
			}
			int c;
			int to_load = maxLoad-load;
			for(int j = 0;j<order.getGoodTypeObj().length && to_load>0;j++) {
				if(order.getGoodTypeCount()[j]<1) {
					continue;
				}
			for(int i = 0;i<wh.shelves.size();i++) {
				c = wh.shelves.get(i).numberOfUnreservedGoods(order.getGoodTypeObj()[j]);
				if(c!=0){
					if(c>to_load) {
						c=to_load;
					}
					Destination d  = map.getDestination(wh.shelves.get(i));
					d.task = 2;
					d.count = c;
					if(d.count>order.getGoodTypeCount()[j]) {
						d.count=order.getGoodTypeCount()[j];
					}
					wh.shelves.get(i).reserveGoods(order.getGoodTypeObj()[j], d.count);
					System.out.println("***"+i+"***"+order.getGoodTypeObj()[j].toString());
					System.out.println(wh.shelves.get(i).toString());
					d.goodtype=order.getGoodTypeObj()[j];
					destinations.add(d);
					to_load-=d.count;
					if(order.loverCount(j, c))break;
				}
			}
			}
			if(!order.isEmpty()) {
				planner.addOrder(order);
			}
			Destination a = new Destination(x, y, -1);
			Destination nextD;
			//sorting Destinations in row 
			for(int j = 0;j<destinations.size()-1;j++) {
				nextD = destinations.get(j);
				for (int i = 1; i < destinations.size(); i++) {
					if(a.length(nextD)>a.length(destinations.get(i))) {
						nextD = destinations.get(i);
					}
				}
				destinations.set(destinations.indexOf(nextD), destinations.get(j));
				destinations.set(j, nextD);
			}
			a = map.getExport_window();
			a.task = 3;
			destinations.add(a);
			completedDestinations=0;
			System.out.println("should plan");
			this.printDestinations();
			this.plan();
		}else {
			completedDestinations++;
		}
	}
	
	private void printDestinations() {
		// TODO Auto-generated method stub
		System.out.println("Destinations:");
		for(Destination dest : destinations) {
			System.out.println(dest.toStringDestOrder());
		}
	}

	private boolean load(Goods good) {
		if(load<maxLoad) {
			waitTime=1;
			waitFor.add(good);
			return true;
		}else {
			JOptionPane.showMessageDialog(null,"Cart is already full, when it loads next stock","Cart loading problem",JOptionPane.WARNING_MESSAGE);
		}
		return false;
	}
	
	public ArrayList<Goods> getCargo() {
		return cargo;
	}
	
	public String getCargoToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Počet:").append(cargo.size()).append('\n');
		for(int i = 0;i<cargo.size();i++) {
			if(i!=0) sb.append(",");
			sb.append(cargo.get(i).toString());
		}
		return sb.toString();
	}
	
	private Boolean unload() {
		Goods[] outLoad = new Goods[cargo.size()];
		for (int i = 0; i < outLoad.length; i++) {
			outLoad[i]=cargo.get(i);
		}
		cargo.clear();
		return planner.dispatch(outLoad);
	}
	
	public Destination getNextDestination() {
		try {
			return destinations.get(completedDestinations);
		} catch (IndexOutOfBoundsException e) {
			return new Destination(0,0,0);
		}
	}
	
	public ArrayList<Destination> getPath() {
		return destinations;
	}

	public ArrayList<Destination> getPlanned_path() {
		return planned_path;
	}
	
	public void printPlanned_path() {
		System.out.println("Planned path:");
		for(Destination dest : planned_path) {
			System.out.println("["+dest.x+","+dest.y+"]");
		}
	}
}