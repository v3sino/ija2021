package ija.warehouse;

import java.util.ArrayList;
/**
 *  Represents one shelf, consists of 5 subshelves
 *  All functions are delegated to the subshelves
 * @author xzabka04
 */
public class Shelf {
    private final ArrayList<SubShelf> subshelves = new ArrayList<>();

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
    /**
     * Adds a new item onto the shelf
     * @param goodsItem item to add
     */
    public void put(Goods goodsItem) {
        for(SubShelf i : subshelves){
            if (!i.isfull()){
                i.put(goodsItem);
                return;
            }
        }
    }
    /**
     * Reserves items from shelf so other carts wont try to take them
     * @param type type of product to reserve
     * @param count number of products you want to reserve
     */
    public boolean reserveGoods(GoodsType type, int count){
        for (SubShelf i : subshelves){
            count -= i.reserveGoods(type, count);
            if (count == 0){
                return true;
            }
        }
        return false;
    }

    /**
     * Number of goods that are not reserved
     * @param type type of goods
     * @return how many goods of the type are not yet reserved
     */
    public int numberOfUnreservedGoods(GoodsType type) {
        int x = 0;
        for(SubShelf i : subshelves){
            x += i.numberOfUnreservedGoods(type);
        }
        return x;
    }

    /**
     * Does this shelf contain this product type ?
     * @param type type of product
     * @return true/false
     */
    public boolean containsGoods(GoodsType type) {
        for(SubShelf i : subshelves){
            if (i.containsGoods(type)){
                return true;
            }
        }
        return false;
    }

    /**
     * Removes one of the reserved items from shelf
     * @param type type of product to remove
     * @return the product that was removed
     */
    public Goods removeReserved(GoodsType type) {
        for(SubShelf i : subshelves){
            if (i.numberOfGoods(type) - i.numberOfUnreservedGoods(type) > 0){
                return i.removeReserved(type);
            }
        }
        return null;
    }
    
    /**
     * Returns index of shelf, from which will be given goodtype removed, if removeReserved will be called as a next command with the same goodtype
     * @param goodtype type of product to remove
     * @return index of subshelf
     */
	public int indexOfSubshelfForRemoveReserved(GoodsType goodtype) {
        for(SubShelf i : subshelves){
            if (i.numberOfGoods(goodtype) - i.numberOfUnreservedGoods(goodtype) > 0){
                for (int j = 0; j < subshelves.size(); j++) {
					if(i.equals(subshelves.get(j))) {
						return j;
					}
				}
            }
        }
        return -1;
	}

    /**
     * Removes one item of given type from shelf
     * @param type type of product to remove
     * @return the product that was removed
     */
    public Goods removeAny(GoodsType type) {
        for(SubShelf i : subshelves){
            if (i.containsGoods(type)){
                return i.removeAny(type);
            }
        }
        return null;
    }

    /**
     * How many goods of this type are on the shelf ?
     * @param type type of product
     * @return number of products of given type on this shelf
     */
    public int numberOfGoods(GoodsType type) {
        int x = 0;
        for(SubShelf i : subshelves){
            x = x + i.numberOfGoods(type);
        }
        return x;
    }

    /**
     * Returns whether this shelf is full or not
     */
    public boolean isfull(){
        for(SubShelf i : subshelves){
            if (!i.isfull()){
                return false;
            }
        }
        return true;
    }

    /**
     * Prints content of shelf to stdout
     */
    public void print_content(){
        for(SubShelf s : subshelves){
            System.out.print("....subshelf: ");
            s.print_content();
        }
    }

    /**
     * Returns how many subshelves are not empty
     */
    public int numberOfNonEmptyShelves(){
        int k = 5;
        for(SubShelf s : subshelves)
            if (s.isEmpty())
                k--;
        return k;
    }

    /**
     * Returns one concrete subshelf
     */
    public SubShelf getSubShelf(int i){
        if (-1 < i && i <= subshelves.size()){
            return subshelves.get(i);
        }
        return null;
    }
}
