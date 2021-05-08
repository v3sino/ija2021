package ija.warehouse;

import ija.carts.Cart;
import ija.carts.Planner;
import ija.gui.GUI;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *  Has information about all shelves and carts, calls GUI
 * @author xzabka04
 */
public class Warehouse {

    public ArrayList<Shelf> shelves = new ArrayList<>(10);
    public ArrayList<Cart> carts = new ArrayList<>();
    public ArrayList<GoodsType> types = new ArrayList<>();

    @SuppressWarnings("static-access")
	public Warehouse(){

    	MapInfo map = new MapInfo(shelves);
    	try {
			map.readMapFromFile("data/map1.txt");
		} catch (IOException e2) {
			e2.printStackTrace();
		}
    	
    	for (int i = 0; i <map.getShelfCount(); i++){
            shelves.add(new Shelf());
        }

		Planner planner = new Planner();

        try {
            Scanner sc = new Scanner(new FileReader("data/content.txt"));
            while(sc.hasNextLine()){
                String name = sc.next();
                int count = sc.nextInt();
                int shelf = sc.nextInt();

                GoodsType type = new GoodsType(name);
                if (!types.contains(type)){
                    types.add(type);
                }
                List<Goods> tmp = new ArrayList<>();
                for (int i=0; i < count; i++){
                    tmp.add(types.get(types.indexOf(type)).newItem());
                }
                for (Goods go : tmp){
                    if (!shelves.get(shelf).isfull()){
                        shelves.get(shelf).put(go);
                    }
                    else{
                        System.out.println("trying to put "+name+" onto full shelf");
                    }
                }
            }
            sc.close();
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("file not found");
        }
    	for (int y = 0;y<map.y_size; y++) {
			for (int x = 0; x < map.x_size; x++) {
				if(map.cells[x][y].type==2) {
					int [] a = {x,y};
					System.out.println(x+"=="+a[0]+",y=="+a[1]);
					carts.add(new Cart(new Goods[0], a, planner, map, this));
				}
			}
		}

        print_state();

        planner.readOrderFromFile("data/Orders1.txt",types);
        GUI g = new GUI();
        g.initIfo(shelves, map, carts, types);
	    g.main();
    }
    /**
     * Prints state of all shelves in warehouse
     */
    private void print_state(){
        for(int i=0; i<shelves.size() ; i++){
            System.out.println("SHELF "+i+":");
            shelves.get(i).print_content();
        }
    }
}
