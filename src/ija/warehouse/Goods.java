package ija.warehouse;

public class Goods {

    private GoodsType type;
    private boolean reserved;

    public Goods(GoodsType type) {
        this.type = type;
        this.reserved = false;
    }

    public GoodsType get_type() {
        return this.type;
    }

    public boolean isReserved(){
        return reserved;
    }

    public void reserve(){
        reserved = true;
    }

    public boolean sell() {
        return this.type.remove(this);
    }
    public String toString() {
    	return type.toString();
    }
}
