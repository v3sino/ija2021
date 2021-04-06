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
	private int completedDestinations;
	public int x;
	public int y;
	private int maxLoad= 10000000;
	public int load = 0;
	private MapInfo map;
	private int waitTime;
	private ArrayList<Goods> waitFor;
	private Warehouse wh;
	//mam ciel, idem tam (v smere x ak je volno a dx !=0 inak v y)
	//ak nie je mozne, tak zoberiem ciel prekazky a posuniem sa do inej strany
	public Cart(Goods[] cargoToLoad, int[] place, Planner plan, MapInfo m, Warehouse warehouse) {
		// !!! Constructor still in progress
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
	}
	public void move() {
		if(waitTime>0) {
			if(waitTime==1) {
				for (Goods goods : waitFor) {
					cargo.add(goods);
				}
				waitTime=-1;
			}else {
				waitTime--;
			}
			return;
		}
		if(destinations.size()!=0 && destinations.size()!=completedDestinations){
			if(Math.abs(destinations.get(completedDestinations).x-x)==1 && destinations.get(completedDestinations).y==y && destinations.get(completedDestinations).task==2){
				Shelf shelf = map.getShelf(destinations.get(completedDestinations));
				Goods good;
				for(int i = 0;i<destinations.get(completedDestinations).count;i++) {
					good = shelf.removeAny(destinations.get(completedDestinations).goodtype);
					if(good==null){
						break;
					}else{
						if(!this.load(good)){
							System.out.println("33333333333333chyba");
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
			if(destinations.get(completedDestinations).x-x>1) {
				if(map.isFree(x+1,y)){
					x=x+1;
					map.moveCart(x-1,y,x,y);
					return;
				}
			}
			if(destinations.get(completedDestinations).x-x<1 || destinations.get(completedDestinations).x == x) {
				if(map.isFree(x-1,y)){
					x=x-1;
					map.moveCart(x+1,y,x,y);
					return;
				}
			}
			if(destinations.get(completedDestinations).y-y>0) {
				if(map.isFree(x,y+1)){
					y=y+1;
					map.moveCart(x,y-1,x,y);
					return;
				}
			}
			if(destinations.get(completedDestinations).y-y<0) {
				if(map.isFree(x-1,y)){
					x=x-1;
					map.moveCart(x+1,y,x,y);
					return;
				}
			}
		}else{
			this.findOrder();
		}
	}
	private void findOrder() {
		if(destinations.size()==0 || destinations.size()==completedDestinations) {
			destinations=new ArrayList<Destination>();
			Order order = planner.getNextOrder();
			int c;
			for(int j = 0;j<order.getGoodTypeObj().length;j++) {
			for(int i = 0;i<wh.shelves.size();i++) {
				c = wh.shelves.get(i).numberOfGoods(order.getGoodTypeObj()[j]);
				if(c!=0){
					Destination d  = map.getDestination(wh.shelves.get(i));
					d.task = 2;
					d.count = c;
					if(d.count>order.getGoodTypeCount()[j]) {
						d.count=order.getGoodTypeCount()[j];
					}
					d.goodtype=order.getGoodTypeObj()[j];
					destinations.add(d);
					if(order.loverCount(j, c))break;
				}
			}
			}
			Destination a = map.getStartDest();
			Destination nextD;
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
			a = map.getWindowDest();
			a.task = 3;
			destinations.add(a);
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