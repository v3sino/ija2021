package ija.warehouse;

import ija.carts.Cart;
import ija.carts.Planner;
import ija.gui.GUI;
import ija.warehouse.Goods;
import ija.warehouse.GoodsType;
import ija.warehouse.Shelf;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class Warehouse {

    public ArrayList<Shelf> shelves = new ArrayList<Shelf>(10);
    public ArrayList<Cart> carts = new ArrayList<Cart>(); 
    public ArrayList<GoodsType> types = new ArrayList<GoodsType>();

    @SuppressWarnings("static-access")
	public Warehouse(){

    	MapInfo map = new MapInfo(shelves);
    	try {
			map.readMapFromFile("data/map1.txt");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
    	for (int i = 0; i <map.getShelfCount(); i++){
            shelves.add(new Shelf());
        }
    	types.add(new GoodsType("banana"));
    	types.add(new GoodsType("milk"));
    	types.add(new GoodsType("apple"));
    	types.add(new GoodsType("pineapple"));
    	types.add(new GoodsType("plum"));
    	types.add(new GoodsType("grapes"));
    	types.add(new GoodsType("kiwi"));
    	types.add(new GoodsType("orange"));

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
                List<Goods> tmp = new ArrayList<Goods>();
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
        	System.out.println(e);
            System.out.println("file not found");
        }
		Planner planner = new Planner();
    	for (int y = 0;y<map.y_size; y++) {
			for (int x = 0; x < map.x_size; x++) {
				if(map.cells[x][y].type==2) {
					int [] a = {x,y};
					carts.add(new Cart(new Goods[0], a, planner, map, this));
				}
			}
		}

        print_state();
	
/* -------------------------------------------------------demonstration how to reserve goods on shelf--------------------------
        GoodsType apple = new GoodsType("jablko");
        GoodsType orange = new GoodsType("pomaranc");

        System.out.println("number of apples on shelf 1: "+shelves.get(1).numberOfGoods(apple));
        System.out.println("number of oranges on shelf 1: "+shelves.get(1).numberOfGoods(orange));

        System.out.println("number of unreserved apples on shelf 1: "+shelves.get(1).numberOfUnreservedGoods(apple));
        System.out.println("number of unreserved oranges on shelf 1: "+shelves.get(1).numberOfUnreservedGoods(orange));

        //---------------how to reserve goods...first chceck if there are enough-------------------
        System.out.println("RESERVING 3 APPLES AND 7 ORANGES");
        int applecount = 3;
        int orangecount = 7;

        if (shelves.get(1).numberOfUnreservedGoods(apple) >= applecount){
            shelves.get(1).reserveGoods(apple,applecount);
        }
        else{
            System.out.println("Not enough unreserved goods on the shelf");
        }

        if (shelves.get(1).numberOfUnreservedGoods(orange) >= orangecount){
            shelves.get(1).reserveGoods(orange,orangecount);
        }
        else{
            System.out.println("Not enough unreserved goods on the shelf");
        }


        System.out.println("number of apples on shelf 1: "+shelves.get(1).numberOfGoods(apple));
        System.out.println("number of oranges on shelf 1: "+shelves.get(1).numberOfGoods(orange));

        System.out.println("number of unreserved apples on shelf 1: "+shelves.get(1).numberOfUnreservedGoods(apple));
        System.out.println("number of unreserved oranges on shelf 1: "+shelves.get(1).numberOfUnreservedGoods(orange));

        //------ALWAYS USE removeReserved() IF YOU ARE REMOVING RESERVED GOODS WITH A CART... NOT removeAny() !!---------------------
        //-----------------how to remove reserved goods from shelf.....you can also chceck if there are some before you remove, or check if return from removeReserved is null-------
        System.out.println("REMOVING RESERVED 3 APPLES AND 6 ORANGES");
        for (int i=0; i<3; i++){
            shelves.get(1).removeReserved(apple);
        }
        for (int i=0; i<6; i++){
            shelves.get(1).removeReserved(orange);
        }

        System.out.println("number of apples on shelf 1: "+shelves.get(1).numberOfGoods(apple));
        System.out.println("number of oranges on shelf 1: "+shelves.get(1).numberOfGoods(orange));

        System.out.println("number of unreserved apples on shelf 1: "+shelves.get(1).numberOfUnreservedGoods(apple));
        System.out.println("number of unreserved oranges on shelf 1: "+shelves.get(1).numberOfUnreservedGoods(orange));

--------------------------------------------------end of demonstration-----------------------------
*/	
        planner.readOrderFromFile("data/Orders1.txt",types);
        GUI g = new GUI();
        g.initIfo(shelves, map, carts);
	g.main();
    }

    private void print_state(){
        for(int i=0; i<shelves.size() ; i++){
            System.out.println("SHELF "+i+":");
            shelves.get(i).print_content();
        }
    }
}
