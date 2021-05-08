package ija.warehouse;

import ija.warehouse.Goods;
import java.util.ArrayList;
import java.util.Objects;
/**
 *  Represents one type of product stored in warehouse
 * @author xzabka04
 */
public class GoodsType {

    private String name;
    private int pocet;
    private ArrayList<Goods> list = new ArrayList<>();

    /**
     * Constructor
     * @param name name of the product type
     */
    public GoodsType(String name) {
        this.name = name;
        this.pocet = 0;
    }
    /**
     * Returns name of this type
     */
    public String getName() {
        return this.name;
    }

    public boolean addItem(Goods goodsItem) {
        this.list.add(goodsItem);
        this.pocet += 1;
        return true;
    }
    /**
     * Adds new item to the list of items of this type
     */
    public Goods newItem() {
        this.pocet += 1;
        Goods item = new Goods(this);
        this.list.add(item);
        return item;
    }
    /**
     * Removes one item of this type
     */
    public boolean remove(Goods goodsItem) {

        if (this.list.remove(goodsItem)) {
            this.pocet -= 1;
            return true;
        }
        return false;
    }
    /**
     * Are there any products of this type ?
     */
    public boolean empty() {
        if (this.pocet == 0){
            return true;
        }
        return false;
    }

    public int size() {
        return this.pocet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoodsType goodsType = (GoodsType) o;
        return name.equals(goodsType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    public String toString() {
        return this.name;
    }
}

