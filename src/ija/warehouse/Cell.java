package ija.warehouse;

public class Cell {
	public char type;
	public int index;   /// index in List for shelf and cart
						/// for block 0 means permanent, not 0 means temporary
	public Cell(char t,int i) {
		type=t;
		index = i;
	}
	
	public String typeToString(char c) {
		switch (c) {
		case 's':
		case 'S':
			return "shelf";
		case 'c':
		case 'C':
			return "cart";
		case 'b':
		case 'B':
			return "block";
		case 'p':
		case 'P':
		case '-':
		case ' ':
			return "path";
		case 'e':
		case 'E':
			return "expedition window";
		}
		return "unknown";
	}
	
	public String toString() {
		return typeToString(type)+" with index: "+Integer.toString(index);
	}
}
