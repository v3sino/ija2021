package ija.warehouse;

import ija.gui.GUI;
import ija.warehouse.Goods;
import ija.warehouse.GoodsType;
import ija.warehouse.Shelf;

public class Warehouse {

    public static void main(String[] args){
        System.out.println("Starting main");

        Shelf sh1 = new Shelf();
        Shelf sh2 = new Shelf();
        GoodsType tp1 = new GoodsType("Jablko");
        GoodsType tp2 = new GoodsType("Voda");
        GoodsType tp3 = new GoodsType("Rozok");
        Goods g1 = new Goods(tp1);
        Goods g2 = new Goods(tp1);
        Goods g3 = new Goods(tp2);
        Goods g4 = new Goods(tp3);

        sh1.put(g1);
        sh1.put(g2);
        sh2.put(g3);
        sh2.put(g4);

        System.out.println("Prva policka obsahuje: " + sh1.numberOfGoods(tp1) + " jablk" );
        System.out.println("Prva policka obsahuje: " + sh1.numberOfGoods(tp2) + " vody" );
        System.out.println("Prva policka obsahuje: " + sh1.numberOfGoods(tp3) + " rozkov" );
        System.out.println("Druha policka obsahuje: " + sh2.numberOfGoods(tp1) + " jablk" );
        System.out.println("Druha policka obsahuje: " + sh2.numberOfGoods(tp2) + " vody" );
        System.out.println("Druha policka obsahuje: " + sh2.numberOfGoods(tp3) + " rozkov" );

        GUI g = new GUI();
        g.main(args);

    }
}
