package ija.warehouse;

import ija.gui.GUI;
import ija.warehouse.Goods;
import ija.warehouse.GoodsType;
import ija.warehouse.Shelf;

import java.io.FileReader;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class Warehouse {

    public ArrayList<Shelf> shelves = new ArrayList<Shelf>(10);
    public ArrayList<GoodsType> types = new ArrayList<GoodsType>();

    public Warehouse(){

        for (int i = 0; i < 10; i++){
            shelves.add(new Shelf());
        }

        try {
            Scanner sc = new Scanner(new FileReader("src/ija/warehouse/content.txt"));
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
                for (Goods g : tmp){
                    if (!shelves.get(shelf).isfull()){
                        shelves.get(shelf).put(g);
                    }
                    else{
                        System.out.println("trying to put onto full shelf");
                    }
                }
            }
            sc.close();
        }
        catch(Exception e) {
            System.out.println("file not found");
        }

        print_state();

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



		MapInfo map = new MapInfo();
        GUI g = new GUI();
        g.initIfo(shelves, map);

    }

    private void print_state(){
        for(int i=0; i<10 ; i++){
            System.out.println("SHELF "+i+":");
            shelves.get(i).print_content();
        }
    }
}
