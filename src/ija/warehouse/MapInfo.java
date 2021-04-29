package ija.warehouse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ija.carts.Destination;
import ija.gui.GUI;

public class MapInfo {
	private Shelf shelf;
	private String mapFileName;
	public Cell cells [][];
	private GUI g;
	
	public MapInfo() {
		mapFileName = "/media/xbabac02/DATA_1TB/Programovanie/java-linux/ija2021/data/map1.txt";
	}
	public MapInfo(Shelf s) {
		shelf = s;
		mapFileName = "/media/xbabac02/DATA_1TB/Programovanie/java-linux/ija2021/data/map1.txt";
	}
	public MapInfo(String mapFile) {
		mapFileName = mapFile;
	}
	
	/**
	 * Function read map from file specified in constructor (default map)
	 * Example call from GUI: map.readMapFromFile(this);
	 * @param gui specifies gui, from with will be called functions drawing objects on map (carts, shelves) 
	 * @throws IOException default name of file is incorrect or file is not readable
	 */
	public void readMapFromFile(GUI gui) throws IOException {
		this.readMapFromFile(gui, mapFileName);
	}

	/**
	 * Function read map from file specified parameter 
	 * Example call from GUI: map.readMapFromFile(this);
	 * @warning !!! pixel size of shelf is used 40, no value readable from GUI
	 * @param gui specifies gui, from with will be called functions drawing objects on map (carts, shelves) 
	 * @param mapFile specifies from with map data
	 * @throws IOException gui should handle wrong file name or unreadable file and demand for correct file name/path
	 */
	public void readMapFromFile(GUI gui,String mapFile) throws IOException {
		g=gui;
		BufferedReader br = new BufferedReader(new FileReader(mapFile));
		String line;
		line=br.readLine();
		String a [] = line.split(" ");
		cells = new Cell[Integer.parseInt(a[0])][Integer.parseInt(a[1])];
		int cart_c = 0;
		int shelf_c = 0;
		int x=0;
		while((line=br.readLine())!=null){
			for(int y = 0; y<line.length();y++) {
				cells[x][y] = new Cell(line.charAt(y),0);
				if(line.charAt(y)=='S' || line.charAt(y)=='s') {
					cells[x][y].index=shelf_c;
					shelf_c++;
					gui.PutShelf(x+1,y+1, 40);
					//System.out.println("x="+x+"y="+y);
				}
				if(line.charAt(y)=='C' || line.charAt(y)=='c') {
					cells[x][y].index=cart_c;
					cart_c++;
					System.out.println("x="+x+"y="+y);
				}
			}
			x++;
		}
		
		br.close();
	}
	
	public void moveCart(int x, int y, int x2, int y2) {//MOCK
		// TODO Auto-generated method stub
		System.out.println("Pohyb voziku z "+ Integer.toString(x)+","+Integer.toString(y)+" na: "+Integer.toString(x2)+","+Integer.toString(y2));
		int cart_index = cells[x][y].index;
		if(y==y2) {
			if(x2-x>0) {
				g.CartMoveUp(cart_index, 40, 1);
			}
			if(x2-x<0) {
				g.CartMoveDown(cart_index, 40, 1);
			}
		}
		if(x==x2) {
			if(y2-y>0) {
				g.CartMoveRight(cart_index, 40, 1);
			}
			if(y2-y<0) {
				g.CartMoveLeft(cart_index, 40, 1);
			}
		}
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
