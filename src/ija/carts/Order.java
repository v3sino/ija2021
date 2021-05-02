package ija.carts;

import ija.warehouse.GoodsType;

public class Order {
	private int goodTypeCount[];
	private GoodsType goodTypeObj[];
	private String name;
	
	public Order(int [] count,GoodsType [] gt) {
		this.goodTypeCount=count;
		this.goodTypeObj=gt;
	}
	
	public Order(int count,GoodsType gt) {
		goodTypeCount= new int [1];
		goodTypeCount[0] = count;
		goodTypeObj= new GoodsType [1];
		goodTypeObj[0] = gt;
	}
	
	public Order(int[] count, GoodsType[] gt, String name) {
		this.name = name;
		this.goodTypeCount=count;
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
}
