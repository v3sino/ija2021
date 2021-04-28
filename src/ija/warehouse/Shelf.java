package ija.warehouse;

import java.util.ArrayList;

public class Shelf {
    private final ArrayList<SubShelf> subshelves = new ArrayList<SubShelf>();

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

    public boolean reserveGoods(GoodsType type, int count){
        for (SubShelf i : subshelves){
            count -= i.reserveGoods(type, count);
            if (count == 0){
                return true;
            }
        }
        return false;
    }

    public int numberOfUnreservedGoods(GoodsType type) {
        int x = 0;
        for(SubShelf i : subshelves){
            x += i.numberOfUnreservedGoods(type);
        }
        return x;
    }

    public boolean containsGoods(GoodsType type) {
        for(SubShelf i : subshelves){
            if (i.containsGoods(type)){
                return true;
            }
        }
        return false;
    }

    public Goods removeReserved(GoodsType type) {
        for(SubShelf i : subshelves){
            if (i.numberOfGoods(type) - i.numberOfUnreservedGoods(type) > 0){
                return i.removeReserved(type);
            }
        }
        return null;
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

    public int numberOfNonEmptyShelves(){
        int k = 5;

        for(SubShelf s : subshelves)
            if (s.isEmpty())
                k--;

        return k;
    }

    public SubShelf getSubShelf(int i){
        if (-1 < i && i <= subshelves.size()){
            return subshelves.get(i);
        }

        return null;
    }
}
