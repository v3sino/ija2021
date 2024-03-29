package ija.carts;

import ija.warehouse.Goods;
import ija.warehouse.MapInfo;
import ija.warehouse.Shelf;
import ija.warehouse.Warehouse;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Cart in warehouse (movement, product handling, pathing)
 * @author xbabac02
 */
public class Cart {
	public Planner planner;
	private ArrayList<Goods> cargo = new ArrayList<Goods>();
	private ArrayList<Destination> destinations;
	private ArrayList<Destination> planned_path;
	private int completedDestinations;
	public int x;
	public int y;
	private int maxLoad= 10;
	public int load;
	private MapInfo map;
	private int waitTime;
	private ArrayList<Goods> waitFor;
	private Warehouse wh;
	private Order runningOrder; 
	
	public Cart(Goods[] cargoToLoad, int[] place, Planner plan, MapInfo m, Warehouse warehouse) {
		Collections.addAll(getCargo(), cargoToLoad);
		planner = plan;
		if(place.length>1) {
			x = place[0];
			y = place[1];
		}
		destinations = new ArrayList<>();
		completedDestinations = 0;
		wh = warehouse;
		map=m;
		waitTime=-1;
		waitFor = new ArrayList<>();
		planned_path = new ArrayList<>();
		load = getCargo().size();
	}
	
	/**
	 * function triggers cart to do something (moving, loading, uloading, planning path)
	 */
	public void move() {
		/// Loading
		if(getWaitTime()>0) {
			if(getWaitTime()==1) {
				for (Goods goods : getWaitFor()) {
					getCargo().add(goods);
					load++;
				}
				waitFor.clear();
				waitTime=-1;
			}else {
				waitTime = getWaitTime() - 1;
				return;
			}
		}
		
		//Moving
		if(getPlanned_path().size()>0) {// way is planned, path is cleared 
			if(map.cells[x][y].crossroad) {
				// cart reached crossroad, path to next crossroad is planned, path to following crossroad is been planning
				this.plan();
			}
			
			//Move to next Destination from planned_path
			map.moveCart(x, y, getPlanned_path().get(0).x, getPlanned_path().get(0).y);
			x = getPlanned_path().get(0).x;
			y = getPlanned_path().get(0).y;
			getPlanned_path().remove(0);
			
		}else if(getDestinations().size()!=0 && getDestinations().size()!=completedDestinations){
			if(Math.abs(getDestinations().get(completedDestinations).x-x)==1 && getDestinations().get(completedDestinations).y==y && getDestinations().get(completedDestinations).task==2){
				Shelf shelf = map.getShelf(getDestinations().get(completedDestinations));
				shelf.toString();
				Goods good;
				int subshelf_i;
				for(int i = 0;i<getDestinations().get(completedDestinations).count;i++) {
					subshelf_i = shelf.indexOfSubshelfForRemoveReserved(getDestinations().get(completedDestinations).goodtype);
					good = shelf.removeReserved(getDestinations().get(completedDestinations).goodtype);
					if(good==null){
						break;
					}else{
						if(!this.load(good,subshelf_i)){
							System.out.println("unable to load");
						}
					}
				}
				this.findOrder();
				return;
			}
			if(getDestinations().get(completedDestinations).x==x && getDestinations().get(completedDestinations).y==y && getDestinations().get(completedDestinations).task==1){
				this.findOrder();
				return;
			}
			if(getDestinations().get(completedDestinations).x==x && getDestinations().get(completedDestinations).y==y && getDestinations().get(completedDestinations).task==3){
				if(unload()) {
					load=0;
					this.findOrder();
				}
			}
			else {
				this.plan();
			}
		}else{
			this.findOrder();
		}
		
	}
	
	/**
	 * function called only from function plan!!!
	 * finds next free cell, adds actual position to planned_path or if no free cell is near, Destination with x,y = -1,-1
	 * @param last_x 
	 * @param last_y
	 * @return
	 */
	private Destination recursivePlanner(int last_x, int last_y) {
		if(Math.abs(getDestinations().get(completedDestinations).x-last_x)==1 && getDestinations().get(completedDestinations).y==last_y && getDestinations().get(completedDestinations).task==2) {
			map.reservePath(last_x, last_y);
			Destination d = new Destination(last_x, last_y, 2);
			getPlanned_path().add(d);
			return d;
		}
		if(getDestinations().get(completedDestinations).x==x && getDestinations().get(completedDestinations).y==y && getDestinations().get(completedDestinations).task==3) {
			map.reservePath(last_x, last_y);
			Destination d = new Destination(last_x, last_y, 3);
			getPlanned_path().add(d);
			return d;
		}
		if(getDestinations().get(completedDestinations).x==x && getDestinations().get(completedDestinations).y==y && getDestinations().get(completedDestinations).task==1) {
			map.reservePath(last_x, last_y);
			Destination d = new Destination(last_x, last_y, 1);
			getPlanned_path().add(d);
			return d;
		}
		if(map.cells[last_x][last_y].crossroad) {
			map.reservePath(last_x, last_y);
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			Destination d = new Destination(last_x, last_y, 1);
			return d;
		}
		
		if(getDestinations().get(completedDestinations).x-last_x>1 || (getDestinations().get(completedDestinations).x-last_x>0 && getDestinations().get(completedDestinations).task%2==1)) {
			if(getPlanned_path().get(getPlanned_path().size()-1).x!=last_x+1 && map.isFree(last_x+1,last_y)){
				map.reservePath(last_x, last_y);
				getPlanned_path().add(new Destination(last_x, last_y, 1));
				last_x=last_x+1;
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					getPlanned_path().remove(getPlanned_path().size()-1);
					map.unReservePath(last_x, last_y);
				}
				return d;
			}
		}
		if(getDestinations().get(completedDestinations).x-last_x<-1 || (getDestinations().get(completedDestinations).x-last_x<0 && getDestinations().get(completedDestinations).task%2==1)) {
			if(getPlanned_path().get(getPlanned_path().size()-1).x!=last_x-1 && map.isFree(last_x-1,last_y)){
				map.reservePath(last_x, last_y);
				getPlanned_path().add(new Destination(last_x, last_y, 1));
				last_x=last_x-1;
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					getPlanned_path().remove(getPlanned_path().size()-1);
					map.unReservePath(last_x, last_y);
				}
				return d;
			}
		}
		if(getDestinations().get(completedDestinations).y-last_y>0) {
			if(getPlanned_path().get(getPlanned_path().size()-1).y!=last_y+1 && map.isFree(last_x,last_y+1)){
				map.reservePath(last_x, last_y);
				getPlanned_path().add(new Destination(last_x, last_y, 1));
				last_y=last_y+1;
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					getPlanned_path().remove(getPlanned_path().size()-1);
					map.unReservePath(last_x, last_y);
				}
				return d;
			}
		}
		if(getDestinations().get(completedDestinations).y-last_y<0) {
			if(getPlanned_path().get(getPlanned_path().size()-1).y!=last_y-1 && map.isFree(last_x,last_y-1)){
				map.reservePath(last_x, last_y);
				getPlanned_path().add(new Destination(last_x, last_y, 1));
				last_y=last_y-1;
				Destination d = recursivePlanner(last_x, last_y);
				if(d.x<0 || d.y<0) {
					getPlanned_path().remove(getPlanned_path().size()-1);
					map.unReservePath(last_x, last_y);
				}
				return d;
			}
		}
		if(getPlanned_path().get(getPlanned_path().size()-1).x!=last_x+1 && map.isFree(last_x+1,last_y)){
			map.reservePath(last_x, last_y);
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			last_x=last_x+1;
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				getPlanned_path().remove(getPlanned_path().size()-1);
				map.unReservePath(last_x, last_y);
			}
			return d;
		}
		if(getPlanned_path().get(getPlanned_path().size()-1).x!=last_x-1 && map.isFree(last_x-1,last_y)){
			map.reservePath(last_x, last_y);
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			last_x=last_x-1;
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				getPlanned_path().remove(getPlanned_path().size()-1);
				map.unReservePath(last_x, last_y);
			}
			return d;
		}
		if(getPlanned_path().get(getPlanned_path().size()-1).y!=last_y+1 && map.isFree(last_x,last_y+1)){
			map.reservePath(last_x, last_y);
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			last_y=last_y+1;
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				getPlanned_path().remove(getPlanned_path().size()-1);
				map.unReservePath(last_x, last_y);
			}
			return d;
		}
		if(getPlanned_path().get(getPlanned_path().size()-1).y!=last_y-1 && map.isFree(last_x,last_y-1)){
			map.reservePath(last_x, last_y);
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			last_y=last_y-1;
			Destination d = recursivePlanner(last_x, last_y);
			if(d.x<0 || d.y<0) {
				getPlanned_path().remove(getPlanned_path().size()-1);
				map.unReservePath(last_x, last_y);
			}
			return d;
		}
	return new Destination(-1, -1, -1);
	}
	
	/**
	 * function plans path (cell by cell)
	 */
	private void plan() {
		int last_x;
		int last_y;
		if(getPlanned_path().size()>0) {
			if(map.cells[getPlanned_path().get(getPlanned_path().size()-1).x][getPlanned_path().get(getPlanned_path().size()-1).y].crossroad && !(getPlanned_path().get(getPlanned_path().size()-1).x == getDestinations().get(completedDestinations).x && getPlanned_path().get(getPlanned_path().size()-1).y==getDestinations().get(completedDestinations).y)) {
				// Planned path ends on crossroad -> path is not completed
				last_x = getPlanned_path().get(getPlanned_path().size()-1).x;
				last_y = getPlanned_path().get(getPlanned_path().size()-1).y;
				
				while(true) {
					if(getDestinations().get(completedDestinations).x-last_x>1 || (getDestinations().get(completedDestinations).x-last_x>0 && getDestinations().get(completedDestinations).task%2==1)) {
						if(map.isFree(last_x+1,last_y)){
							Destination d = recursivePlanner(last_x+1, last_y);
							if(!(d.x<0 || d.y<0)) {
								break;
							}
						}
					}
					if(getDestinations().get(completedDestinations).x-last_x<-1 || (getDestinations().get(completedDestinations).x-last_x<0 && getDestinations().get(completedDestinations).task%2==1)) {
						if(map.isFree(last_x-1,last_y)){
							Destination d = recursivePlanner(last_x-1, last_y);
							if(!(d.x<0 || d.y<0)) {
								break;
							}
						}
					}
					if(getDestinations().get(completedDestinations).y-last_y>0) {
						if(map.isFree(last_x,last_y+1)){
							Destination d = recursivePlanner(last_x, last_y+1);
							if(!(d.x<0 || d.y<0)) {
								break;
							}
						}
					}
					if(getDestinations().get(completedDestinations).y-last_y<0) {
						if(map.isFree(last_x,last_y-1)){
							Destination d = recursivePlanner(last_x, last_y-1);
							if(!(d.x<0 || d.y<0)) {
								break;
							}
						}
					}
					if(map.isFree(last_x+1,last_y)){
						Destination d = recursivePlanner(last_x+1, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
					if(map.isFree(last_x-1,last_y)){
						Destination d = recursivePlanner(last_x-1, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
					if(map.isFree(last_x,last_y+1)){
						Destination d = recursivePlanner(last_x, last_y+1);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
					if(map.isFree(last_x,last_y-1)){
						Destination d = recursivePlanner(last_x, last_y-1);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
					break;
				}
			}// Path doesn't end on crossroad -> path is completed

		}else {
			last_x = x;
			last_y = y;
			getPlanned_path().add(new Destination(last_x, last_y, 1));
			
			while(true) {
				if(getDestinations().get(completedDestinations).x-last_x>1 || (getDestinations().get(completedDestinations).x-last_x>0 && getDestinations().get(completedDestinations).task%2==1)) {
					if(map.isFree(last_x+1,last_y)){
						Destination d = recursivePlanner(last_x+1, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(getDestinations().get(completedDestinations).x-last_x<-1 || (getDestinations().get(completedDestinations).x-last_x<0 && getDestinations().get(completedDestinations).task%2==1)) {
					if(map.isFree(last_x-1,last_y)){
						Destination d = recursivePlanner(last_x-1, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(getDestinations().get(completedDestinations).y-last_y>0) {
					if(map.isFree(last_x,last_y+1)){
						Destination d = recursivePlanner(last_x, last_y+1);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(getDestinations().get(completedDestinations).y-last_y<0) {
					if(map.isFree(last_x,last_y-1)){
						Destination d = recursivePlanner(last_x, last_y-1);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(map.isFree(last_x+1,last_y)){
					Destination d = recursivePlanner(last_x+1, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x-1,last_y)){
					Destination d = recursivePlanner(last_x-1, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x,last_y+1)){
					Destination d = recursivePlanner(last_x, last_y+1);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x,last_y-1)){
					Destination d = recursivePlanner(last_x, last_y-1);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				break;
			}
			getPlanned_path().remove(0);
			if(getPlanned_path().size()==0)return;
			last_x = getPlanned_path().get(getPlanned_path().size()-1).x;
			last_y = getPlanned_path().get(getPlanned_path().size()-1).y;
			
			while(true) {
				if(getDestinations().get(completedDestinations).x-last_x>1 || (getDestinations().get(completedDestinations).x-last_x>0 && getDestinations().get(completedDestinations).task%2==1)) {
					if(map.isFree(last_x+1,last_y)){
						Destination d = recursivePlanner(last_x+1, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(getDestinations().get(completedDestinations).x-last_x<-1 || (getDestinations().get(completedDestinations).x-last_x<0 && getDestinations().get(completedDestinations).task%2==1)) {
					if(map.isFree(last_x-1,last_y)){
						Destination d = recursivePlanner(last_x-1, last_y);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(getDestinations().get(completedDestinations).y-last_y>0) {
					if(map.isFree(last_x,last_y+1)){
						Destination d = recursivePlanner(last_x, last_y+1);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(getDestinations().get(completedDestinations).y-last_y<0) {
					if(map.isFree(last_x,last_y-1)){
						Destination d = recursivePlanner(last_x, last_y-1);
						if(!(d.x<0 || d.y<0)) {
							break;
						}
					}
				}
				if(map.isFree(last_x+1,last_y)){
					Destination d = recursivePlanner(last_x+1, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x-1,last_y)){
					Destination d = recursivePlanner(last_x-1, last_y);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x,last_y+1)){
					Destination d = recursivePlanner(last_x, last_y+1);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				if(map.isFree(last_x,last_y-1)){
					Destination d = recursivePlanner(last_x, last_y-1);
					if(!(d.x<0 || d.y<0)) {
						break;
					}
				}
				break;
			}
		}
	}

	/**
	 * function sets next order or fills array of Orders
	 */
	private void findOrder() {
		if(getDestinations().size()==0 || getDestinations().size()==completedDestinations) {
			destinations= new ArrayList<>();
			Order order = planner.getNextOrder();
			if(order==null) {
				System.out.println("order is null");
				completedDestinations=0;
				runningOrder = null;
				getDestinations().add(new Destination(0, 0, 1));
				return;
			}
			int c;
			int to_load = getMaxLoad()-load;
			for(int j = 0;j<order.getGoodTypeObj().length && to_load>0;j++) {
				if(order.getGoodTypeCount()[j]<1) {
					continue;
				}
				for(int i = 0;i<wh.shelves.size();i++) {
					c = wh.shelves.get(i).numberOfUnreservedGoods(order.getGoodTypeObj()[j]);
					if(c>0){
						if(c>to_load) {
							c=to_load;
						}
						Destination d  = map.getDestination(wh.shelves.get(i));
						d.task = 2;
						d.count = c;
						if(c>order.getGoodTypeCount()[j]) {
							d.count=order.getGoodTypeCount()[j];
						}
						wh.shelves.get(i).reserveGoods(order.getGoodTypeObj()[j], d.count);
						d.goodtype=order.getGoodTypeObj()[j];
						getDestinations().add(d);
						to_load-=d.count;
						if(order.loverCount(j, d.count))break;
					}
				}
			}
			Destination a = new Destination(x, y, -1);
			Destination nextD;
			//sorting Destinations in row 
			for(int j = 0;j<getDestinations().size()-1;j++) {
				nextD = getDestinations().get(j);
				for (int i = 1; i < getDestinations().size(); i++) {
					if(a.length(nextD)>a.length(getDestinations().get(i))) {
						nextD = getDestinations().get(i);
					}
				}
				getDestinations().set(getDestinations().indexOf(nextD), getDestinations().get(j));
				getDestinations().set(j, nextD);
			}
			a = map.getExport_window();
			a.task = 3;
			getDestinations().add(a);
			completedDestinations=0;
			this.plan();
			runningOrder = order;
		}else {
			completedDestinations++;
		}
	}
	
	/**
	 * debug print
	 */
	@SuppressWarnings("unused")
	private void printDestinations() {
		System.out.println("Destinations:");
		for(Destination dest : getDestinations()) {
			System.out.println(dest.toStringDestOrder());
		}
	}

	/**
	 * function sets time of loading and setups goods to be add to cart
	 * @param good 
	 * @param subshelf_h index of subshelf, from which is the good, time to load from higher subshelf takes more time 
	 * @return 
	 */
	private boolean load(Goods good, int subshelf_h) {
		if(load<getMaxLoad()) {
			waitTime=subshelf_h+1;
			getWaitFor().add(good);
			return true;
		}else {
			Alert fullCart = new Alert(Alert.AlertType.WARNING);
			fullCart.setTitle("Cart loading problem");
			fullCart.setHeaderText(null);
			fullCart.setContentText("Cart is already full, when it loads next stock");
			fullCart.show();
		}
		return false;
	}
	
	public ArrayList<Goods> getCargo() {
		return cargo;
	}
	
	public String getCargoToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Počet:").append(getCargo().size()).append('\n');
		for(int i = 0;i<getCargo().size();i++) {
			if(i!=0) sb.append(",");
			sb.append(getCargo().get(i).toString());
		}
		return sb.toString();
	}
	
	private Boolean unload() {
		Goods[] outLoad = new Goods[getCargo().size()];
		for (int i = 0; i < outLoad.length; i++) {
			outLoad[i]=getCargo().get(i);
		}
		getCargo().clear();
		return planner.dispatch(outLoad,runningOrder);
	}
	
	public Destination getNextDestination() {
		try {
			return getDestinations().get(completedDestinations);
		} catch (IndexOutOfBoundsException e) {
			return new Destination(0,0,0);
		}
	}
	
	public ArrayList<Destination> getPath() {
		return getDestinations();
	}

	public ArrayList<Destination> getPlanned_path() {
		return planned_path;
	}
	
	public void printPlanned_path() {
		System.out.println("Planned path:");
		for(Destination dest : getPlanned_path()) {
			System.out.println("["+dest.x+","+dest.y+"]");
		}
	}

	public ArrayList<Goods> getWaitFor() {
		return waitFor;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public int getMaxLoad() {
		return maxLoad;
	}

	public ArrayList<Destination> getDestinations() {
		return destinations;
	}

	public Order getRunningOrder() {
		return runningOrder;
	}
}