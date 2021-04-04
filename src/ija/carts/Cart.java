package ija.carts;

import java.util.ArrayList;
import ija.carts.Planner;
import ija.warehouse.Goods;
import ija.warehouse.Shelf;
import ija.warehouse.GoodsType;

public class Cart {
	private Planner planner;
	private ArrayList<Goods> cargo = new ArrayList<Goods>();
	private int [] destination; //TODO type is only example
	//mam ciel, idem tam (v smere x ak je volno a dx !=0 inak v y)
	//ak nie je mozne, tak zoberiem ciel prekazky a posuniem sa do inej strany
	public Cart(Goods[] cargoToLoad, int[] place, Planner plan) {
		// TODO Auto-generated constructor stub
		for (Goods c : cargoToLoad) {
			cargo.add(c);
		}
		planner = plan;
	}
	public void move() {
		
		if(false){
			unload();
		}
	}
	public ArrayList<Goods> getCargo() {
		return cargo;
	}
	private Boolean unload() {
		Goods[] outLoad = (Goods[]) cargo.toArray();
		cargo.clear();
		return planner.dispatch(outLoad);
	}
	public int [] getDestination() {
		return destination;
	}
}
