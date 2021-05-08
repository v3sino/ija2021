package ija.warehouse;

/**
 *  One exact product in warehouse
 * @author xzabka04
 */
public class Goods {

    private GoodsType type;
    private boolean reserved;

    /**
     * Constructor
     * @param type the type of this product
     */
    public Goods(GoodsType type) {
        this.type = type;
        this.reserved = false;
    }
    /**
     * Returns the type of this product
     */
    public GoodsType get_type() {
        return this.type;
    }

    /**
     * Is this concrete item reserved ?
     */
    public boolean isReserved(){
        return reserved;
    }

    /**
     * Reserve this item
     */
    public void reserve(){
        reserved = true;
    }

    /**
     * Remove this item
     */
    public boolean sell() {
        return this.type.remove(this);
    }
    /**
     * Returns string representation of type
     */
    public String toString() {
    	return type.toString();
    }
}
