package ija.carts;
import java.lang.Math;
import java.util.ArrayList;

import ija.carts.Planner;
import ija.warehouse.Goods;
import ija.warehouse.Shelf;
import ija.warehouse.MapInfo;

public class Cart {
	private Planner planner;
	private ArrayList<Goods> cargo = new ArrayList<Goods>();
	private Destination destination; //TODO type is only example
	private SingleOrder order;
	public int x;
	public int y;
	private int maxLoad;
	public int load;
	private MapInfo map;
	private boolean isWaiting;
	private DoubleIG waitFor;
	//mam ciel, idem tam (v smere x ak je volno a dx !=0 inak v y)
	//ak nie je mozne, tak zoberiem ciel prekazky a posuniem sa do inej strany
	public Cart(Goods[] cargoToLoad, int[] place, Planner plan) {
		// TODO Auto-generated constructor stub
		// !!! Constructor still in progress
		for (Goods c : cargoToLoad) {
			cargo.add(c);
		}
		planner = plan;
		if(place.length>1) {
			x = place[0];
			y = place[1];
		}
		destination = null;
	}
	public void move() {
		if(destination!=null){
			if(Math.abs(destination.x-x)==1 && destination.y==y && destination.task==2){
				Shelf shelf = planner.getShelf(map.getShelfIndex(destination));
				Goods good;
				for(int i = 0;i<order.count;) {
					good = shelf.removeAny(order.goodtype);
					if(good==null){
						break;
					}else{
						if(!this.load(good)){
							
						}
					}
				}
				this.findOrder();
				isWaiting=false;
				return;
			}
			if(Math.abs(destination.x-x)==1 && destination.y==y && destination.task==3){
				unload(); //TODO check returned boolean
				this.findOrder();
				isWaiting=false;
				return;
			}
			if(destination.x-x>1) {
				if(map.isFree(x+1,y)){
					x=x+1;
					map.moveCart(x-1,y,x,y);
					isWaiting=false;
					return;
				}
			}
			if(destination.x-x<1 || destination.x == x) {
				if(map.isFree(x-1,y)){
					x=x-1;
					map.moveCart(x+1,y,x,y);
					isWaiting=false;
					return;
				}
			}
			if(destination.y-y>1) {
				if(map.isFree(x,y+1)){
					y=y+1;
					map.moveCart(x,y-1,x,y);
					isWaiting=false;
					return;
				}
			}
			if(destination.y-y<1 || destination.y == y) {
				if(map.isFree(x-1,y)){
					x=x-1;
					map.moveCart(x+1,y,x,y);
					isWaiting=false;
					return;
				}
			}
			isWaiting=true;
		}else{
			this.findOrder();
		}
	}
	private void findOrder() {
		// TODO Auto-generated method stub
		
	}
	private boolean load(Goods good) {
		// TODO Auto-generated method stub
		if(this.load<this.maxLoad) {
			waitFor.count=1;
			waitFor.good=good;
			return true;
		}
		return false;
	}
	public ArrayList<Goods> getCargo() {
		return cargo;
	}
	private Boolean unload() {
		Goods[] outLoad = (Goods[]) cargo.toArray();
		cargo.clear();
		return planner.dispatch(outLoad);
	}
	public Destination getDestination() {
		return destination;
	}
}
