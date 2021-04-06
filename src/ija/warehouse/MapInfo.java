package ija.warehouse;

import ija.carts.Destination;

public class MapInfo {
	private Shelf shelf;
	public MapInfo() {
	}
	public MapInfo(Shelf s) {
		shelf = s;
	}
	public void moveCart(int x, int y, int x2, int y2) {//MOCK
		// TODO Auto-generated method stub
		System.out.println("Pohyb voziku z "+ Integer.toString(x)+","+Integer.toString(y)+" na: "+Integer.toString(x2)+","+Integer.toString(y2));
	}

	public boolean isFree(int x, int y) {//STUB
		// TODO Auto-generated method stub
		if(x==1 && y==0)
			return false;
		return true;
	}

	public Shelf getShelf(Destination destination) {
		return shelf;
	}

	public Destination getDestination(Shelf shelf) {
		return new Destination(1, 0, 0);
	}

	public Destination getStartDest() {//STUB
		return new Destination(0,0,0);
	}
	public Destination getWindowDest() {
		// TODO Auto-generated method stub
		return new Destination(2,1,0);
	}

}
