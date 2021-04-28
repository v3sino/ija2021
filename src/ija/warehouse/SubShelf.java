package ija.warehouse;

import ija.warehouse.Goods;
import ija.warehouse.GoodsType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public int reserveGoods(GoodsType type, int count){
        int x = 0;
        for (Goods g : ThingsOnShelf.get(type)){
            if (!g.isReserved()){
                g.reserve();
                x += 1;
                if (x == count){
                    break;
                }
            }
        }
        return x;
    }

    public int numberOfUnreservedGoods(GoodsType type) {
        List<Goods> list = this.ThingsOnShelf.get(type);
        if (list == null){
            return 0;
        }
        else{
            int count = 0;
            for (Goods g : list){
                if (!g.isReserved()){
                    count += 1;
                }
            }
            return count;
        }
    }

    public Goods removeReserved(GoodsType type) {
        List<Goods> list = this.ThingsOnShelf.get(type);
        if (list == null) {
            return null;
        } else {
            if (list.isEmpty()){
                return null;
            }
            else {
                for (Goods g : list){
                    if (g.isReserved()){
                        count --;
                        list.remove(g);
                        return g;
                    }
                }
            }
        }
        return null;
    }

    public boolean containsGoods(GoodsType type) {
        List<Goods> list = this.ThingsOnShelf.get(type);
        return list != null;
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
        return count == capacity;
    }

    public void print_content(){
        if(count == 0){
            System.out.println("empty");
        }
        else{
            Set<GoodsType> keys = ThingsOnShelf.keySet();
            for(GoodsType t : keys){
                System.out.println(""+t.getName()+" "+ThingsOnShelf.get(t).size());
            }
        }
    }

    public boolean isEmpty(){
        return count==0;
    }

    public String getContent(){
        StringBuilder content = new StringBuilder();
        if(count == 0){
            content.append("empty");
        }
        else{
            Set<GoodsType> keys = ThingsOnShelf.keySet();
            for(GoodsType t : keys){
                content.append(t.getName()).append(": ").append(ThingsOnShelf.get(t).size()).append("\n");
            }
        }

        return content.toString();
    }


}
