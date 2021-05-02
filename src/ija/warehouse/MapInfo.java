package ija.warehouse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import ija.carts.Destination;
import ija.gui.GUI;

/**
 * @implNote file with map, should contain size of cells table; Map contains 1 export window
 */
public class MapInfo {
    public ArrayList<Shelf> shelves;
	private String mapFileName= "data/map1.txt";
	public Cell cells [][];
	private GUI g;
	public int x_size=-1;
	public int y_size=-1;
	private int cart_c;
	private int shelf_c;
	private Destination export_window = null;
	
	public MapInfo(ArrayList<Shelf> s) {
		shelves = s;
		mapFileName = "data/map1.txt";
	}
	public MapInfo(ArrayList<Shelf> s,String mapFile) {
		mapFileName = mapFile;
		shelves = s;
	}
	
	/**
	 * @deprecated 
	 * @param s
	 */
	public MapInfo(Shelf s) {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Function read map from file specified in constructor (default map)
	 * Example call from GUI: map.readMapFromFile(this);
	 * @param gui specifies gui, from with will be called functions drawing objects on map (carts, shelves) 
	 * @throws IOException default name of file is incorrect or file is not readable
	 */
	public void readMapFromFile() throws IOException {
		this.readMapFromFile(mapFileName);
	}

	/**
	 * Function read map from file specified parameter 
	 * Example call from GUI: map.readMapFromFile(this);
	 * @param mapFile specifies from with map data
	 * @throws IOException gui should handle wrong file name or unreadable file and demand for correct file name/path
	 */
	public void readMapFromFile(String mapFile) throws IOException {
		export_window=null;
		BufferedReader br = new BufferedReader(new FileReader(mapFile));
		String line;
		line=br.readLine();
		String a [] = line.split(" ");
		x_size=Integer.parseInt(a[0]);
		y_size=Integer.parseInt(a[1]);
		cells = new Cell[x_size][y_size];
		cart_c = 0;
		shelf_c = 0;
		int x1=0;
		Cell w = new Cell('0', 0);
		while((line=br.readLine())!=null){
			for(int y = 0; y<line.length();y++) {
				cells[x1][y] = new Cell(w.typeCharToValue(line.charAt(y)),0);
				if(line.charAt(y)=='S' || line.charAt(y)=='s') {
					cells[x1][y].index=shelf_c;
					shelf_c++;
				} else
				if(line.charAt(y)=='C' || line.charAt(y)=='c') {
					cells[x1][y].index=cart_c;
					cart_c++;
				}
				if(line.charAt(y)=='E' || line.charAt(y)=='e') {
					if(getExport_window()!=null) {
						JOptionPane.showMessageDialog(null,"multiple export windows are on the screen, should be 1","multiple export windows are on the screen, should be 1", JOptionPane.ERROR_MESSAGE);
					}
					export_window=new Destination(x1, y, -1);
				}
			}
			x1++;
		}
		br.close();

		int count;
		for(int x = 0; x<x_size;x++){
			for(int y = 0; y<y_size;y++) {
				if(cells[x][y].type==0) {
					count=0;
					if(x>0 && (cells[x-1][y].type==0 || cells[x-1][y].type==8)) {
						count++;
					}
					if(x_size-1>x && (cells[x+1][y].type==0 || cells[x+1][y].type==8)) {
						count++;
					}
					if(y>0 && (cells[x][y-1].type==0 || cells[x][y-1].type==8)) {
						count++;
					}
					if(y_size-1>y && (cells[x][y+1].type==0 || cells[x][y+1].type==8)) {
						count++;
					}
					if(count>2) {
						cells[x][y].crossroad=true;
					}
				}
			}
		}
	}

	/**
	 * Function read map from file specified parameter 
	 * Example call from GUI: map.readMapFromFile(this);
	 * @warning !!! pixel size of shelf is used 40, no value readable from GUI
	 * @param gui specifies gui, from with will be called functions drawing objects on map (carts, shelves)
	 */
	public void readMapToGui(GUI gui){
		g=gui;
		for(int x = 0; x<x_size;x++){
			for(int y = 0; y<y_size;y++) {
				if(cells[x][y].type==1) {
					System.out.println("Shelf on ["+x+","+y+"]");
					gui.PutShelf(x+1,y+1, 40);
				}
			}
		}
	}
	
	/**
	 * Function provides change of position of cart in map and in graphic interface 
	 * @param x x value of previous position of cart
	 * @param y y value of previous position of cart
	 * @param x2 x value of new position of cart
	 * @param y2 y value of new position of cart
	 */
	public void moveCart(int x, int y, int x2, int y2) {
		System.out.println("Pohyb voziku z "+ Integer.toString(x)+","+Integer.toString(y)+" na: "+Integer.toString(x2)+","+Integer.toString(y2));
		int cart_index = cells[x][y].index;
		assert(cells[x][y].type==2);
		assert(this.isFree(x,y));
		cells[x2][y2].type=cells[x][y].type;
		cells[x2][y2].index=cells[x][y].index;
		cells[x][y].type=0;
		cells[x][y].index=0;
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

	/**
	 * Returns boolean indicating if cell is a free path
	 * @param x x position of cell
	 * @param y y position of cell
	 * @return if the cell is free path
	 */
	public boolean isFree(int x, int y) {
		if(cells[x][y].type==0 && cells[x][y].index==0) {
			return true;
		}
		return false;
	}

	/**
	 * Returns boolean indicating if cell is a path
	 * @param x x position of cell
	 * @param y y position of cell
	 * @return if the cell is a path
	 */
	public boolean isPath(int x, int y) {
		if(cells[x][y].type==0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Function returns object of shelf on position
	 * @param destination position of wanted shelf
	 * @return shelf on given position
	 */
	public Shelf getShelf(Destination destination) {
		for(int y = 0;y<y_size;y++){
			for (int x = 0; x < x_size; x++) {
				if(cells[destination.x][destination.y].type==1) {
					return shelves.get(cells[x][y].index);
				}
			}
		}
		return null;
	}

	/**
	 * Function returns position of given shelf in structure Destination suitable for carts
	 * @param shelf shelf to be found
	 * @return Destination object with position of given shelf or null, if given shelf is not on map
	 */
	public Destination getDestination(Shelf shelf) {
		for(int y = 0;y<y_size;y++){
			for (int x = 0; x < x_size; x++) {
				if(cells[x][y].type==1 && shelves.get(cells[x][y].index)==shelf) {
					return new Destination(x, y, 0);
				}
			}
		}
		return new Destination(-1, -1, 0);
	}

	/**
	 * Function for 2nd homework
	 * @deprecated
	 * @return 1. position of cart
	 */
	public Destination getStartDest() {//STUB
		return new Destination(0,0,0);
	}
	
	/**
	 * Function for 2nd homework
	 * @deprecated
	 * @return
	 */
	public Destination getWindowDest() {
		return new Destination(2,1,0);
	}

	/**
	 * value is correct if readMapFromFile was already called
	 * @return
	 */
	public int getShelfCount() {
		return shelf_c;
	}
	/**
	 * value is correct if readMapFromFile was already called
	 * @return
	 */
	public int getCartCount() {
		return cart_c;
	}
	
	/**
	 * Function reserving path for cart, path is freed when cart pass trough
	 * @param x x position of path to be reserve
	 * @param y y position of path to be reserve
	 * @return true if path is cleared and path was not reserved and now is reserved
	 */
	public boolean reservePath(int x, int y) {
		if(this.isFree(x, y)) {
			cells[x][y].index=1;
			return true;
		}
		return false;
	}
	public Destination getExport_window() {
		return export_window;
	}

}
