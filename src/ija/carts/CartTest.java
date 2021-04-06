package ija.carts;

import ija.warehouse.Goods;
import ija.warehouse.GoodsType;
import ija.warehouse.MapInfo;
import ija.warehouse.Shelf;
import ija.warehouse.Warehouse;

/**
 * @author Martin Babača
 *
 */
public class CartTest {
	public static void main(String[] args) {
		Shelf s = new Shelf();
		MapInfo map = new MapInfo(s);
		Warehouse w = new Warehouse();
		w.shelves.add(s);
		w.types.add(new GoodsType("Pokusny tovar"));
		w.shelves.get(w.shelves.size()-1).put(w.types.get(w.types.size()-1).newItem());
		w.shelves.get(w.shelves.size()-1).put(w.types.get(w.types.size()-1).newItem());
		Goods [] cargo = new Goods[1];
		cargo[0]=w.types.get(0).newItem();
		Planner p = new Planner();
		int [] a = {-1,0};
		Cart cart1 = new Cart(cargo,a, p,map, w);
		
		System.out.println("Toto je ukážkový prípad");
		System.out.println("Používame mapu s voľnými políčkami 0,0; 0,1 a 1,1");
		System.out.println("Používame mapu s regálom na políčku 1,0");
		System.out.println("Používame mapu s výdajným okienkom na políčku 2,1");
		System.out.println("Používame mapu, kde vozík začína na políčku -1,0");
		
		System.out.println("Vozík na začiatku obsahuje:");
		System.out.println(cart1.getCargoToString());
		System.out.println("vozík sa nachádza na "+cart1.x+","+cart1.y);
		System.out.println("Čakajúce požiadavky");
		System.out.println(p.toString());
		p.addOrder(new Order(2, w.types.get(w.types.size()-1)));
		System.out.println(">>>>Po pridaní požiadavky na naloženie dvoch kusov tovaru \"Pokusny tovar\"");
		System.out.println("Vozík na začiatku obsahuje:");
		System.out.println(cart1.getCargoToString());
		System.out.println("Čakajúce požiadavky");
		System.out.println(p.toString());

		cart1.move();
		System.out.println(">>>>Po prvej aktivite vozíka (hľadanie cesty)");
		System.out.println("vozík sa nachádza na "+cart1.x+","+cart1.y);
		System.out.println("Vozík obsahuje:");
		System.out.println(cart1.getCargoToString());
		System.out.println("Čakajúce požiadavky");
		System.out.println(p.toString());
		System.out.println("Najbližší ciel vozíku:");
		System.out.println(cart1.getNextDestination().toString());
		
		cart1.move();
		System.out.println(">>>>Po druhej aktivite vozíka (pohyb k regálu)");
		System.out.println("vozík sa nachádza na "+cart1.x+","+cart1.y);
		System.out.println("Vozík obsahuje:");
		System.out.println(cart1.getCargoToString());
		System.out.println("Najbližší ciel vozíku:");
		System.out.println(cart1.getNextDestination().toString());
		System.out.println("Obsah regála:");
		s.print_content();
		
		cart1.move();
		System.out.println(">>>>Po tretej aktivite vozíka (nakladanie)");
		System.out.println("vozík sa nachádza na "+cart1.x+","+cart1.y);
		System.out.println("Vozík obsahuje:"); 
		System.out.println(cart1.getCargoToString());
		System.out.println("Najbližší ciel vozíku:");
		System.out.println(cart1.getNextDestination().toString());
		System.out.println("Obsah regála:");
		s.print_content();
		
		cart1.move();
		System.out.println(">>>>Po štvrtej aktivite vozíka (naloženie)");
		System.out.println("vozík sa nachádza na "+cart1.x+","+cart1.y);
		System.out.println("Vozík obsahuje:");
		System.out.println(cart1.getCargoToString());
		System.out.println("Najbližší ciel vozíku:");
		System.out.println(cart1.getNextDestination().toString());
		System.out.println("Obsah regála:");
		s.print_content();
		
		cart1.move();
		System.out.println(">>>>Po piatej aktivite vozíka pohyb na 0,1");
		System.out.println("vozík sa nachádza na "+cart1.x+","+cart1.y);
		System.out.println("Vozík obsahuje:");
		System.out.println(cart1.getCargoToString());
		System.out.println("Najbližší ciel vozíku:");
		System.out.println(cart1.getNextDestination().toString());
		
		cart1.move();
		System.out.println(">>>>Po šiestej aktivite vozíka (pohyb na 1,1)");
		System.out.println("vozík sa nachádza na "+cart1.x+","+cart1.y);
		System.out.println("Vozík obsahuje:");
		System.out.println(cart1.getCargoToString());
		System.out.println("Najbližší ciel vozíku:");
		System.out.println(cart1.getNextDestination().toString());
		
		cart1.move();
		System.out.println(">>>>Po siedmej aktivite vozíka (vyloženie)");
		System.out.println("vozík sa nachádza na "+cart1.x+","+cart1.y);
		System.out.println("Vozík obsahuje:");
		System.out.println(cart1.getCargoToString());
		
		
		
		
		
		
		//vytvorenie prázdneho vozíku
		//vytvorenie vozíku s nákladom
			//vytvorenie tovarov k naloženiu
		
			//vytvorenie a naloženie vozíku
		
		//zobrazenie obsah vozíkov:
		
	}
}
