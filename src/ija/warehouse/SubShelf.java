package ija.warehouse;

import ija.warehouse.Goods;
import ija.warehouse.GoodsType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubShelf {

    private Map<GoodsType, List<Goods>> ThingsOnShelf = new HashMap<GoodsType, List<Goods>>();
    private int capacity = 10;
    private int count = 0;

    public SubShelf() {
    }

    public void put(Goods goodsItem) {
        GoodsType type = goodsItem.get_type();
        if (this.ThingsOnShelf.containsKey(type)) {
            this.ThingsOnShelf.get(type).add(goodsItem);
        }
        else{
            ArrayList<Goods> tmp = new ArrayList<Goods>();
            tmp.add(goodsItem);
            this.ThingsOnShelf.put(type, tmp);
        }
        count ++;
    }

    public boolean containsGoods(GoodsType type) {
        List<Goods> list = this.ThingsOnShelf.get(type);
        if (list == null){
            return false;
        }
        return true;
    }

    public Goods removeAny(GoodsType type) {
        List<Goods> list = this.ThingsOnShelf.get(type);
        if (list == null) {
            return null;
        } else {
            if (list.isEmpty()){
                return null;
            }
            else {
                count --;
                return list.remove(0);
            }
        }
    }

    public int numberOfGoods(GoodsType type) {
        List<Goods> list = this.ThingsOnShelf.get(type);
        if (list == null){
            return 0;
        }
        else return list.size();
    }

    public boolean isfull(){
        if (count == capacity){
            return true;
        }
        return false;
    }


}
