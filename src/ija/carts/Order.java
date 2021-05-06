package ija.carts;

import ija.warehouse.GoodsType;

public class Order {
	private int goodTypeCount[];
	public int goodTypeCountExp[];
	private int goodTypeCountAll[];
	private GoodsType goodTypeObj[];
	private String name;
	
	public Order(int [] count,GoodsType [] gt) {
		this.goodTypeCount=count;
		this.goodTypeCountAll=count;
		for (int i = 0; i < getGoodTypeCountExp().length; i++) {
			getGoodTypeCountExp()[i]=0;
		}
		this.goodTypeObj=gt;
	}
	
	public Order(int count,GoodsType gt) {
		goodTypeCount= new int [1];
		setGoodTypeCountExp(new int [1]);
		goodTypeCountAll= new int [1];
		goodTypeCount[0] = count;
		getGoodTypeCountAll()[0]=count;
		getGoodTypeCountExp()[0]=0;
		goodTypeObj= new GoodsType [1];
		goodTypeObj[0] = gt;
	}
	
	public Order(int[] count, GoodsType[] gt, String name) {
		this.name = name;
		this.goodTypeCount=count;
		this.goodTypeCountAll=count;
		setGoodTypeCountExp(new int [count.length]);
		for (int i = 0; i < getGoodTypeCountExp().length; i++) {
			getGoodTypeCountExp()[i]=0;
		}
		this.goodTypeObj=gt;
	}

	public int[] getGoodTypeCount() {
		return goodTypeCount;
	}
	
	public GoodsType[] getGoodTypeObj() {
		return goodTypeObj;
	}
	
	public void addToOrder(int count,GoodsType gt) {
		boolean p = false;
		for(int i = 0;i<goodTypeObj.length;i++) {
			if(goodTypeObj[i].equals(gt)) {
				p=true;
				goodTypeCount[i]+=count;
				getGoodTypeCountAll()[i]+=count;
				break;
			}
		}
		if(!p) {
			int [] a = new int[goodTypeCount.length+1];
			for(int i = 0;i<goodTypeCount.length;i++)a[i]=goodTypeCount[i];
			a[goodTypeCount.length]=count;
			goodTypeCount=a;
			GoodsType [] b = new GoodsType[goodTypeObj.length+1];
			for(int i = 0;i<goodTypeObj.length;i++)b[i]=goodTypeObj[i];
			b[goodTypeObj.length]=gt;
			goodTypeObj=b;
			int [] c = new int[getGoodTypeCountAll().length+1];
			for(int i = 0;i<getGoodTypeCountAll().length;i++)a[i]=getGoodTypeCountAll()[i];
			a[getGoodTypeCountAll().length]=count;
			goodTypeCountAll=c;
			int [] d = new int[getGoodTypeCountExp().length+1];
			for(int i = 0;i<getGoodTypeCountExp().length;i++)a[i]=getGoodTypeCountExp()[i];
			a[getGoodTypeCountExp().length]=0;
			setGoodTypeCountExp(d);
		}
	}
	
	public boolean isEmpty() {
		boolean r = true;
		for (int i = 0; i < goodTypeCount.length; i++) {
			if(goodTypeCount[i]>0) {
				r =false;
				break;
			}
		}
		return r;
	}
	
	/**
	 * remove some stocks from Order (already shipped)
	 * @param index Type of stock
	 * @param count Count of stock
	 * @return true on no stock remains
	 */
	public boolean loverCount(int index,int count) {
		goodTypeCount[index]-=count;
		if(goodTypeCount[index]>0) {
			return false;
		}else {
			goodTypeCount[index]=0;
			return true;
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<goodTypeCount.length;i++) {
			if(i!=0) sb.append(",");
			sb.append(goodTypeObj[i].toString()+" ("+Integer.toString(goodTypeCount[i])+")");
		}
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public int[] getGoodTypeCountExp() {
		return goodTypeCountExp;
	}

	public void setGoodTypeCountExp(int goodTypeCountExp[]) {
		this.goodTypeCountExp = goodTypeCountExp;
	}

	public int[] getGoodTypeCountAll() {
		return goodTypeCountAll;
	}

	public int dispatched() {
		int sum = 0;
		for (int i = 0; i < goodTypeCountExp.length; i++) {
			sum += goodTypeCountExp[i];
		}
		return sum;
	}

	public int all() {
		int sum = 0;
		for (int i = 0; i < goodTypeCountAll.length; i++) {
			sum += goodTypeCountAll[i];
		}
		return sum;
	}
}
