package ija.carts;

import ija.warehouse.GoodsType;

import java.util.Arrays;

/**
 * Order - goods to be dispached from warehouse
 * @author xbabac02
 */
public class Order {
	private int[] goodTypeCount; //count of types to be plant by carts
	public int[] goodTypeCountExp; //count of types which were already dispatched
	private int[] goodTypeCountAll; //count of types to be dispatched (number given by customer)
	private GoodsType[] goodTypeObj; //types corresponding to counts 
	private String name; // name of the order
	
	/**
	 * constructor with given arrays
	 * @param count number of goods of given type
	 * @param gt given type
	 */
	public Order(int [] count,GoodsType [] gt) {
		this.goodTypeCount=count;
		this.goodTypeCountAll=count;
		Arrays.fill(getGoodTypeCountExp(), 0);
		this.goodTypeObj=gt;
	}
	
	/**
	 * constructor with one value in each parameter
	 * @param count number of goods of given type
	 * @param gt given type
	 */
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
	
	/**
	 * constructor with given arrays
	 * @param count number of goods of given type
	 * @param gt given type
	 * @param name name of the order (better identification of orders)
	 */
	public Order(int[] count, GoodsType[] gt, String name) {
		this.name = name;
		this.goodTypeCount=count;
		this.goodTypeCountAll=count;
		setGoodTypeCountExp(new int [count.length]);
		Arrays.fill(getGoodTypeCountExp(), 0);
		this.goodTypeObj=gt;
	}
	
	public int[] getGoodTypeCount() {
		return goodTypeCount;
	}
	
	public GoodsType[] getGoodTypeObj() {
		return goodTypeObj;
	}
	
	/**
	 * Adds one gootType to the order, goodType can already be a part of Order  
	 * @param count number of goods
	 * @param gt type of goods
	 */
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
			System.arraycopy(goodTypeCount, 0, a, 0, goodTypeCount.length);
			a[goodTypeCount.length]=count;
			goodTypeCount=a;
			GoodsType [] b = new GoodsType[goodTypeObj.length+1];
			System.arraycopy(goodTypeObj, 0, b, 0, goodTypeObj.length);
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
	
	/**
	 * @return true if there is no good to be planned be carts
	 */
	public boolean isEmpty() {
		boolean r = true;
		for (int j : goodTypeCount) {
			if (j > 0) {
				r = false;
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
			sb.append(goodTypeObj[i].toString()).append(" (").append(goodTypeCount[i]).append(")");
		}
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public int[] getGoodTypeCountExp() {
		return goodTypeCountExp;
	}

	public void setGoodTypeCountExp(int[] goodTypeCountExp) {
		this.goodTypeCountExp = goodTypeCountExp;
	}

	public int[] getGoodTypeCountAll() {
		return goodTypeCountAll;
	}

	/**
	 * @return total count of dispatched goods from this order
	 */
	public int dispatched() {
		int sum = 0;
		for (int j : goodTypeCountExp) {
			sum += j;
		}
		return sum;
	}

	/**
	 * @return total count of goods to be dispatched
	 */
	public int all() {
		int sum = 0;
		for (int j : goodTypeCountAll) {
			sum += j;
		}
		return sum;
	}
}
