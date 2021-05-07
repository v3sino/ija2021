package ija.warehouse;

public class Cell {
	public char type;
	public int index;   /// index in List for shelf and cart
						/// for block 0 means permanent, not 0 means temporary
	public boolean crossroad = false;
	public int trafficIntensity = 0;
	private int maxTrafficIntensity = 5; /// to scale trafficIntensity to color 0-255 should traficIntensity be multiplied by 32
	public Cell(char t,int i) {
		type=t;
		index = i;
	}
	
	public String typeToString(char c) {
		switch (c) {
		case 1:
			return "shelf";
		case 2:
			return "cart";
		case 8:
			return "block";
		case 0:
			return "path";
		case 4:
			return "expedition window";
		}
		return "unknown";
	}
	
	public char typeToChar(char c) {
		switch (c) {
		case 1:
			return 's';
		case 2:
			return 'c';
		case 8:
			return 'b';
		case 0:
			return '-';
		case 4:
			return 'E';
		}
		return 'U';
	}
	
	public char typeCharToValue(char c) {
		switch (c) {
		case 's':
		case 'S':
			return 1;
		case 'c':
		case 'C':
			return 2;
		case 'p':
		case 'P':
		case '-':
		case ' ':
			return 0;
		case 'e':
		case 'E':
			return 4;
		case 'b':
		case 'B':
			return 8;
		}
		return 8;
	}
	
	public String toString() {
		return typeToString(type)+" with index: "+Integer.toString(index);
	}
	
	public boolean increaseTraffic() {
		this.trafficIntensity++;
		return !(this.trafficIntensity<this.maxTrafficIntensity);
	}
	
	public void scaleTraffic() {
		this.trafficIntensity/=2;
	}
}
