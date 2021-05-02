package ija.carts;

import java.lang.Math;
import java.util.AbstractList;
import java.util.ArrayList;
import ija.carts.Planner;
import ija.warehouse.Goods;
import ija.warehouse.Shelf;
import ija.warehouse.Warehouse;
import ija.warehouse.MapInfo;

public class Cart {
	private Planner planner;
	private ArrayList<Goods> cargo = new ArrayList<Goods>();
	private ArrayList<Destination> destinations;
	private ArrayList<Destination> planned_path;
	private int completedDestinations;
	public int x;
	public int y;
	private int maxLoad= 10000000;
	public int load = 0;
	private MapInfo map;
	private int waitTime;
	private ArrayList<Goods> waitFor;
	private Warehouse wh;
	
	public Cart(Goods[] cargoToLoad, int[] place, Planner plan, MapInfo m, Warehouse warehouse) {
		for (Goods c : cargoToLoad) {
			cargo.add(c);
		}
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
	}
	
	public void move() {
		/// Loading
		if(waitTime>0) {
			if(waitTime==1) {
				for (Goods goods : waitFor) {
					cargo.add(goods);
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
		if(planned_path.size()>0) {// way is planned, path is cleared 
			
			if(planned_path.get(0).task==1 || planned_path.get(0).count==1) {
				// cart reached crossroad, path to next crossroad is planned, path to following crossroad is been planning
				// TODO .............
				this.plan();
			}
			
			//Move to next Destination from planned_path
			map.moveCart(x, y, planned_path.get(0).x, planned_path.get(0).y);
			x = planned_path.get(0).x;
			y = planned_path.get(0).y;
			planned_path.remove(0);
			
		}else if(destinations.size()!=0 && destinations.size()!=completedDestinations){
			if(Math.abs(destinations.get(completedDestinations).x-x)==1 && destinations.get(completedDestinations).y==y && destinations.get(completedDestinations).task==2){
				Shelf shelf = map.getShelf(destinations.get(completedDestinations));
				Goods good;
				for(int i = 0;i<destinations.get(completedDestinations).count;i++) {
					good = shelf.removeReserved(destinations.get(completedDestinations).goodtype);
					if(good==null){
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
				unload(); //TODO check returned boolean
				this.findOrder();
				return; 
			}
			
		}else{
			this.findOrder();
		}
		
	}
	
	private Destination recursivePlanner(int last_x, int last_y) {
		if(map.cells[last_x][last_y].crossroad) {
			planned_path.add(new Destination(last_x, last_y, 1));
			Destination d = new Destination(last_x, last_y, 1);
			return d;
		}
		if(Math.abs(destinations.get(completedDestinations).x-x)==1 && destinations.get(completedDestinations).y==y && destinations.get(completedDestinations).task==1) {
			planned_path.add(new Destination(last_x, last_y, 2));
		}
		if(destinations.get(completedDestinations).x==x && destinations.get(completedDestinations).y==y && destinations.get(completedDestinations).task==3) {
			planned_path.add(new Destination(last_x, last_y, 3));
		}
		
		if(destinations.get(completedDestinations).x-last_x>1) {
			if(planned_path.get(planned_path.size()-1).x==last_x+1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x+1,last_y)){
				last_x=last_x+1;
				planned_path.add(new Destination(last_x, last_y, 1));
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					planned_path.remove(planned_path.size()-1);
					return d;
				}
			}
		}
		if(destinations.get(completedDestinations).x-last_x<-1) {
			if(planned_path.get(planned_path.size()-1).x==last_x-1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x-1,last_y)){
				last_x=last_x-1;
				planned_path.add(new Destination(last_x, last_y, 1));
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					planned_path.remove(planned_path.size()-1);
					return d;
				}
			}
		}
		if(destinations.get(completedDestinations).y-last_y>0) {
			if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y+1 && map.isFree(last_x,last_y+1)){
				last_y=last_y+1;
				planned_path.add(new Destination(last_x, last_y, 1));
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					planned_path.remove(planned_path.size()-1);
					return d;
				}
			}
		}
		if(destinations.get(completedDestinations).y-last_y<0) {
			if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y-1 && map.isFree(last_x,last_y-1)){
				last_y=last_y-1;
				planned_path.add(new Destination(last_x, last_y, 1));
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					planned_path.remove(planned_path.size()-1);
					return d;
				}
			}
		}
		if(planned_path.get(planned_path.size()-1).x==last_x+1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x+1,last_y)){
			last_x=last_x+1;
			planned_path.add(new Destination(last_x, last_y, 1));
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				planned_path.remove(planned_path.size()-1);
				return d;
			}
		}
		if(planned_path.get(planned_path.size()-1).x==last_x-1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x-1,last_y)){
			last_x=last_x-1;
			planned_path.add(new Destination(last_x, last_y, 1));
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				planned_path.remove(planned_path.size()-1);
				return d;
			}
		}
		if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y+1 && map.isFree(last_x,last_y+1)){
			last_y=last_y+1;
			planned_path.add(new Destination(last_x, last_y, 1));
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				planned_path.remove(planned_path.size()-1);
				return d;
			}
		}
		if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y-1 && map.isFree(last_x,last_y-1)){
			last_y=last_y-1;
			planned_path.add(new Destination(last_x, last_y, 1));
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				planned_path.remove(planned_path.size()-1);
				return d;
			}
		}
	return new Destination(-1, -1, -1);
	}
	
	private void plan() {
		// TODO ??? i don't know, if this works
		int last_x;
		int last_y;
		if(planned_path.size()>0) {
			if(planned_path.get(planned_path.size()-1).count==1) {
				// Planned path ends on crossroad -> path is not completed
				last_x = planned_path.get(planned_path.size()-1).x;
				last_y = planned_path.get(planned_path.size()-1).y;
				
				while(true) {
					if(destinations.get(completedDestinations).x-last_x>1) {
						if(planned_path.get(planned_path.size()-1).x==last_x+1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x+1,last_y)){
							last_x=last_x+1;
							planned_path.add(new Destination(last_x, last_y, 1));
							Destination d = recursivePlanner(last_x, last_y);
							if(d.x<0 || d.y<0) {
								planned_path.remove(planned_path.size()-1);
							}else {
								break;
							}
						}
					}
					if(destinations.get(completedDestinations).x-last_x<-1) {
						if(planned_path.get(planned_path.size()-1).x==last_x-1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x-1,last_y)){
							last_x=last_x-1;
							planned_path.add(new Destination(last_x, last_y, 1));
							Destination d = recursivePlanner(last_x, last_y);
							if(d.x<0 || d.y<0) {
								planned_path.remove(planned_path.size()-1);
							}else {
								break;
							}
						}
					}
					if(destinations.get(completedDestinations).y-last_y>0) {
						if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y+1 && map.isFree(last_x,last_y+1)){
							last_y=last_y+1;
							planned_path.add(new Destination(last_x, last_y, 1));
							Destination d = recursivePlanner(last_x, last_y);
							if(d.x<0 || d.y<0) {
								planned_path.remove(planned_path.size()-1);
							}else {
								break;
							}
						}
					}
					if(destinations.get(completedDestinations).y-last_y<0) {
						if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y-1 && map.isFree(last_x,last_y-1)){
							last_y=last_y-1;
							planned_path.add(new Destination(last_x, last_y, 1));
							Destination d = recursivePlanner(last_x, last_y);
							if(d.x<0 || d.y<0) {
								planned_path.remove(planned_path.size()-1);
							}else {
								break;
							}
						}
					}
					if(planned_path.get(planned_path.size()-1).x==last_x+1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x+1,last_y)){
						last_x=last_x+1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
							break;
						}
					}
					if(planned_path.get(planned_path.size()-1).x==last_x-1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x-1,last_y)){
						last_x=last_x-1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
							break;
						}
					}
					if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y+1 && map.isFree(last_x,last_y+1)){
						last_y=last_y+1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
							break;
						}
					}
					if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y-1 && map.isFree(last_x,last_y-1)){
						last_y=last_y-1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
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
			
			while(true) {
				if(destinations.get(completedDestinations).x-last_x>1) {
					if(planned_path.get(planned_path.size()-1).x==last_x+1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x+1,last_y)){
						last_x=last_x+1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).x-last_x<-1) {
					if(planned_path.get(planned_path.size()-1).x==last_x-1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x-1,last_y)){
						last_x=last_x-1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).y-last_y>0) {
					if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y+1 && map.isFree(last_x,last_y+1)){
						last_y=last_y+1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).y-last_y<0) {
					if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y-1 && map.isFree(last_x,last_y-1)){
						last_y=last_y-1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
							break;
						}
					}
				}
				if(planned_path.get(planned_path.size()-1).x==last_x+1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x+1,last_y)){
					last_x=last_x+1;
					planned_path.add(new Destination(last_x, last_y, 1));
					Destination d = recursivePlanner(last_x, last_y);
					if(d.x<0 || d.y<0) {
						planned_path.remove(planned_path.size()-1);
					}else {
						break;
					}
				}
				if(planned_path.get(planned_path.size()-1).x==last_x-1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x-1,last_y)){
					last_x=last_x-1;
					planned_path.add(new Destination(last_x, last_y, 1));
					Destination d = recursivePlanner(last_x, last_y);
					if(d.x<0 || d.y<0) {
						planned_path.remove(planned_path.size()-1);
					}else {
						break;
					}
				}
				if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y+1 && map.isFree(last_x,last_y+1)){
					last_y=last_y+1;
					planned_path.add(new Destination(last_x, last_y, 1));
					Destination d = recursivePlanner(last_x, last_y);
					if(d.x<0 || d.y<0) {
						planned_path.remove(planned_path.size()-1);
					}else {
						break;
					}
				}
				if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y-1 && map.isFree(last_x,last_y-1)){
					last_y=last_y-1;
					planned_path.add(new Destination(last_x, last_y, 1));
					Destination d = recursivePlanner(last_x, last_y);
					if(d.x<0 || d.y<0) {
						planned_path.remove(planned_path.size()-1);
					}else {
						break;
					}
				}
				break;
			}
			
			last_x = planned_path.get(planned_path.size()-1).x;
			last_y = planned_path.get(planned_path.size()-1).y;
			
			while(true) {
				if(destinations.get(completedDestinations).x-last_x>1) {
					if(planned_path.get(planned_path.size()-1).x==last_x+1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x+1,last_y)){
						last_x=last_x+1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).x-last_x<-1) {
					if(planned_path.get(planned_path.size()-1).x==last_x-1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x-1,last_y)){
						last_x=last_x-1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).y-last_y>0) {
					if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y+1 && map.isFree(last_x,last_y+1)){
						last_y=last_y+1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
							break;
						}
					}
				}
				if(destinations.get(completedDestinations).y-last_y<0) {
					if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y-1 && map.isFree(last_x,last_y-1)){
						last_y=last_y-1;
						planned_path.add(new Destination(last_x, last_y, 1));
						Destination d = recursivePlanner(last_x, last_y);
						if(d.x<0 || d.y<0) {
							planned_path.remove(planned_path.size()-1);
						}else {
							break;
						}
					}
				}
				if(planned_path.get(planned_path.size()-1).x==last_x+1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x+1,last_y)){
					last_x=last_x+1;
					planned_path.add(new Destination(last_x, last_y, 1));
					Destination d = recursivePlanner(last_x, last_y);
					if(d.x<0 || d.y<0) {
						planned_path.remove(planned_path.size()-1);
					}else {
						break;
					}
				}
				if(planned_path.get(planned_path.size()-1).x==last_x-1 && planned_path.get(planned_path.size()-1).y==last_y && map.isFree(last_x-1,last_y)){
					last_x=last_x-1;
					planned_path.add(new Destination(last_x, last_y, 1));
					Destination d = recursivePlanner(last_x, last_y);
					if(d.x<0 || d.y<0) {
						planned_path.remove(planned_path.size()-1);
					}else {
						break;
					}
				}
				if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y+1 && map.isFree(last_x,last_y+1)){
					last_y=last_y+1;
					planned_path.add(new Destination(last_x, last_y, 1));
					Destination d = recursivePlanner(last_x, last_y);
					if(d.x<0 || d.y<0) {
						planned_path.remove(planned_path.size()-1);
					}else {
						break;
					}
				}
				if(planned_path.get(planned_path.size()-1).x==last_x && planned_path.get(planned_path.size()-1).y==last_y-1 && map.isFree(last_x,last_y-1)){
					last_y=last_y-1;
					planned_path.add(new Destination(last_x, last_y, 1));
					Destination d = recursivePlanner(last_x, last_y);
					if(d.x<0 || d.y<0) {
						planned_path.remove(planned_path.size()-1);
					}else {
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
			int c;
			for(int j = 0;j<order.getGoodTypeObj().length;j++) {
			for(int i = 0;i<wh.shelves.size();i++) {
				c = wh.shelves.get(i).numberOfUnreservedGoods(order.getGoodTypeObj()[j]);
				if(c!=0){
					Destination d  = map.getDestination(wh.shelves.get(i));
					d.task = 2;
					d.count = c;
					wh.shelves.get(i).reserveGoods(order.getGoodTypeObj()[j], c);
					if(d.count>order.getGoodTypeCount()[j]) {
						d.count=order.getGoodTypeCount()[j];
					}
					d.goodtype=order.getGoodTypeObj()[j];
					destinations.add(d);
					if(order.loverCount(j, c))break;
				}
			}
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
		}else {
			completedDestinations++;
		}
	}
	
	private boolean load(Goods good) {
		if(load<maxLoad) {
			waitTime=1;
			waitFor.add(good);
			return true;
		}
		return false;
	}
	
	public ArrayList<Goods> getCargo() {
		return cargo;
	}
	
	public String getCargoToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Počet:" + cargo.size()+'\n');
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
			return new Destination(-1,-1,0);
		}
	}
	
	public AbstractList<Destination> getPath() {
		return destinations;
	}
}