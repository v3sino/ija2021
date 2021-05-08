package ija.carts;

import ija.warehouse.GoodsType;

/**
 * Destination of one cart
 * @author xbabac02
 */
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
	
	/**
	 * @param b second and of line, first is this Destination
	 * @return length between this Destination and given destination b
	 */
	public double length(Destination b) {
		return Math.sqrt((x-b.x)*(x-b.x)+(y-b.y)*(y-b.y));
	}
	
	/**
	 * @return string including only x and y
	 */
	public String toString() {
		return "pozícia: "+Integer.toString(x)+","+Integer.toString(y);
	}
	
	/**
	 * 
	 * @return string including x, y and number representing task
	 */
	public String toStringDestOrder() {
		return "pozícia: "+Integer.toString(x)+","+Integer.toString(y) + "uloha: "+Integer.toString(task);
	}

}
