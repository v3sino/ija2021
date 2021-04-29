package ija.warehouse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ija.carts.Destination;
import ija.gui.GUI;

/**
 * 
 * @author xbabac02
 * @implNote file with map, should contain size of cells table; Map contains 1 export window
 */
public class MapInfo {
    public ArrayList<Shelf> shelves;
	private String mapFileName;
	public Cell cells [][];
	private GUI g;
	private int x_size=-1;
	private int y_size=-1;
	
	public MapInfo(ArrayList<Shelf> s) {
		shelves = s;
		mapFileName = "data/map1.txt";
	}
	public MapInfo(ArrayList<Shelf> s,String mapFile) {
		mapFileName = mapFile;
		shelves = s;
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
		x_size=Integer.parseInt(a[0]);
		y_size=Integer.parseInt(a[1]);
		cells = new Cell[x_size][y_size];
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
				} else
				if(line.charAt(y)=='C' || line.charAt(y)=='c') {
					cells[x][y].index=cart_c;
					cart_c++;
				}
			}
			x++;
		}
		
		br.close();
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
		assert(cells[x][y].type=='C'||cells[x][y].type=='c');
		assert(this.isFree(x,y));
		cells[x2][y2].type=cells[x][y].type;
		cells[x2][y2].index=cells[x][y].index;
		cells[x][y].type='-';
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
		if(cells[x][y].typeToString(cells[x][y].type)=="path" && cells[x][y].index==0) {
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
				if(cells[0][0].typeToString(cells[destination.x][destination.y].type)=="shelf") {
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
				if(cells[x][y].typeToString(cells[x][y].type)=="shelf" && shelves.get(cells[x][y].index)==shelf) {
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
		// TODO Auto-generated method stub
		return new Destination(2,1,0);
	}

}
