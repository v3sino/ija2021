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

    public static ArrayList<Shelf> shelves = new ArrayList<Shelf>(10);
    public static ArrayList<GoodsType> types = new ArrayList<GoodsType>();
    //public static ArrayList<Goods> goods = new ArrayList<Goods>();

    public static void main(String[] args){

        for (int i = 0; i < 10; i++){
            shelves.add(new Shelf());
        }

        try {
            Scanner sc = new Scanner(new FileReader("src/ija/warehouse/content.txt"));
            while(sc.hasNextLine()){
                String name = sc.next();
                int count = sc.nextInt();
                int shelf = sc.nextInt();
                types.add(new GoodsType(name));
                List<Goods> tmp = new ArrayList<Goods>();
                for (int i=0; i < count; i++){
                    tmp.add(types.get(types.size()-1).newItem());
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
        }
        catch(Exception e) {
            System.out.println("file not found");
        }

        print_state();

        //GUI g = new GUI();
        //g.main(args);
    }

    private static void print_state(){
        for(int i=0; i<10 ; i++){
            System.out.println("SHELF "+i+":");
            shelves.get(i).print_content();
        }
    }
}
