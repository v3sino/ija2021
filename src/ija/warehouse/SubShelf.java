package ija.warehouse;

import ija.warehouse.Goods;
import ija.warehouse.GoodsType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 *  Represents one subshelf of a shelf in warehouse
 * @author xzabka04
 */
public class SubShelf {

    private Map<GoodsType, List<Goods>> ThingsOnShelf = new HashMap<GoodsType, List<Goods>>();
    private int capacity = 10;
    private int count = 0;

    public SubShelf() {
    }
    /**
     * Adds a new item onto the subshelf
     * @param goodsItem item to add
     */
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

    /**
     * Reserves items from subshelf so other carts wont try to take them
     * @param type type of product to reserve
     * @param count number of products you want to reserve
     */
    public int reserveGoods(GoodsType type, int count){
        int x = 0;
        if (this.numberOfUnreservedGoods(type) == 0){
            System.out.println("Trying to reserve "+type.getName()+" but there are none unreserved");
            return 0;
        }
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
    /**
     * Number of goods that are not reserved
     * @param type type of goods
     * @return how many goods of the type are not yet reserved
     */
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
    /**
     * Removes one of the reserved items from subshelf
     * @param type type of product to remove
     * @return the product that was removed
     */
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

    /**
     * Does this shelf contain this product type ?
     * @param type type of product
     * @return true/false
     */
    public boolean containsGoods(GoodsType type) {
        List<Goods> list = this.ThingsOnShelf.get(type);
        return list != null;
    }
    /**
     * Removes one item of given type from subshelf
     * @param type type of product to remove
     * @return the product that was removed
     */
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
    /**
     * How many goods of this type are on the subshelf ?
     * @param type type of product
     * @return number of products of given type on this subshelf
     */
    public int numberOfGoods(GoodsType type) {
        List<Goods> list = this.ThingsOnShelf.get(type);
        if (list == null){
            return 0;
        }
        else return list.size();
    }
    /**
     * Returns whether this subshelf is full or not
     */
    public boolean isfull(){
        return count == capacity;
    }

    /**
     * Prints content of subshelf to stdout
     */
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

    /**
     * Returns string representation of all the content inside this shelf
     */
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
