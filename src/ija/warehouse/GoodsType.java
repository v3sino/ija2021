package ija.warehouse;

import ija.warehouse.Goods;
import java.util.ArrayList;

public class GoodsType {

    private String name;
    private int pocet;
    private ArrayList<Goods> list = new ArrayList<Goods>();

    public GoodsType(String name) {
        this.name = name;
        this.pocet = 0;
    }

    public String getName() {
        return this.name;
    }

    public boolean addItem(Goods goodsItem) {
        this.list.add(goodsItem);
        this.pocet += 1;
        return true;
    }

    public Goods newItem() {
        this.pocet += 1;
        Goods item = new Goods(this);
        this.list.add(item);
        return item;
    }

    public boolean remove(Goods goodsItem) {

        if (this.list.remove(goodsItem)) {
            this.pocet -= 1;
            return true;
        }
        return false;
    }

    public boolean empty() {
        if (this.pocet == 0){
            return true;
        }
        return false;
    }

    public int size() {
        return this.pocet;
    }

}
