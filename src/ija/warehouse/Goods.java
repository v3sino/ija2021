package ija.warehouse;

public class Goods {

    private GoodsType type;

    public Goods(GoodsType type) {
        this.type = type;
    }

    public GoodsType get_type() {
        return this.type;
    }

    public boolean sell() {
        return this.type.remove(this);
    }
}
