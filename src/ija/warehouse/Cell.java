package ija.warehouse;

public class Cell {
	public char type;
	public int index;   /// index in List for shelf and cart
						/// for block 0 means permanent, not 0 means temporary
	public boolean crossroad = false;
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
}
