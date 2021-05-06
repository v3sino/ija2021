package ija.carts;

import ija.warehouse.GoodsType;

public class Destination {

	public int x;
	public int y;
	public int task; //valid values 1==driveThrough , 2==load, 3==unload
	public int count; //count of goods when task =2, when task = 1 count represents if path is crossroad (1)
	public GoodsType goodtype;
	public Destination(int x,int y,int task) {
		this.x = x;
		this.y = y;
		this.task = task;
	}
	
	public double length(Destination b) {
		return Math.sqrt((x-b.x)*(x-b.x)+(y-b.y)*(y-b.y));
	}
	
	public String toString() {
		return "pozícia: "+Integer.toString(x)+","+Integer.toString(y);
	}
	
	public String toStringDestOrder() {
		return "pozícia: "+Integer.toString(x)+","+Integer.toString(y) + "uloha: "+Integer.toString(task);
	}

}
