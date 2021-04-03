package ija.warehouse;

import java.util.ArrayList;

public class Shelf {
    private ArrayList<SubShelf> subshelves = new ArrayList<SubShelf>();

    public Shelf() {
        SubShelf sub1 = new SubShelf();
        SubShelf sub2 = new SubShelf();
        SubShelf sub3 = new SubShelf();
        SubShelf sub4 = new SubShelf();
        SubShelf sub5 = new SubShelf();
        subshelves.add(sub1);
        subshelves.add(sub2);
        subshelves.add(sub3);
        subshelves.add(sub4);
        subshelves.add(sub5);
    }

    public void put(Goods goodsItem) {
        for(SubShelf i : subshelves){
            if (!i.isfull()){
                i.put(goodsItem);
                return;
            }
        }
    }

    public boolean containsGoods(GoodsType type) {
        for(SubShelf i : subshelves){
            if (i.containsGoods(type)){
                return true;
            }
        }
        return false;
    }

    public Goods removeAny(GoodsType type) {
        for(SubShelf i : subshelves){
            if (i.containsGoods(type)){
                return i.removeAny(type);
            }
        }
        return null;
    }

    public int numberOfGoods(GoodsType type) {
        int x = 0;
        for(SubShelf i : subshelves){
            x = x + i.numberOfGoods(type);
        }
        return x;
    }

    public boolean isfull(){
        for(SubShelf i : subshelves){
            if (!i.isfull()){
                return false;
            }
        }
        return true;
    }

    public void print_content(){
        for(SubShelf s : subshelves){
            System.out.print("....subshelf: ");
            s.print_content();
        }
    }


}
