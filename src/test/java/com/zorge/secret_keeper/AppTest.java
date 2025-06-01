package com.zorge.secret_keeper;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zorge.secret_keeper.core.Item;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase {
	
    public AppTest(String testName) {
        super( testName );
    }

    public static Test suite() {
        return new TestSuite( AppTest.class );
    }

    public void testApp() throws Exception {
/*    	
    	ArrayList<Item> all = new ArrayList<Item>();
    	
    	{
	    	Item root = new Item("root", "root", null); all.add(root);
	    	Item i1 = new Item("A", "", root); all.add(i1);
	    	Item i2 = new Item("AA", "", i1); all.add(i2);
	    	Item i3 = new Item("B", "", root); all.add(i3);
	    	Item i4 = new Item("BB", "", i3); all.add(i4);
	    	
	    	Item i5 = new Item("C", "", root); all.add(i5);
	    	Item i6 = new Item("D", "", root); all.add(i6);
    	}

		JSONArray jsonStore = new JSONArray();
		for(Item item : all)
			jsonStore.put(item.toJson());

		String jsonItemsStored = jsonStore.toString();
		System.out.println(jsonItemsStored);
		
		ArrayList<Item> loaded = new ArrayList<Item>();
		JSONArray jsonLoad = new JSONArray(jsonItemsStored);
		for(int i = 0; i < jsonLoad.length(); ++i) {
			JSONObject o = jsonLoad.getJSONObject(i);
			loaded.add(new Item(o));
		}

		System.out.println("\nLoaded objects:");
		for(Item i : loaded)
			System.out.println(i.f1());
		
		// Link objects.
		HashMap<Integer, Item> byId = new HashMap<Integer, Item>();
		for(Item i : loaded)
			byId.put(i.id, i);
		Item root = byId.get(0);
		
		for(Item i : loaded) {
			
			if(i.id == 0 && i.parentId == 0)
				continue;
			
			Item parent = byId.get(i.parentId);
			if(parent != null) {
				i.parent = parent;
				parent.childer.add(i);
			}
			else
				System.out.println("Parent not found for " + i.f1());
		}
		
		// Check lost and found objects
		for(Item i : loaded) {
			if(i == root)
				continue;
			if(i.parent == null) {
				i.parentId = 0;
				i.parent = root;
				root.childer.add(i);
				System.out.println("Lost and found item " + i.f1());
			}
		}
		
		System.out.println("------------------------ tree");
		print(root, 0);
*/
    }
/*    
    private void print(Item item, int tab) {
    	
    	String t = "";
    	for(int i = 0; i < tab; ++i)
    		t += "    ";
    	
    	System.out.println(t + item.f1());
    	if(item.childer.size() > 0) {
    		for(Item c : item.childer) {
    			print(c, tab + 1);
    		}
    	}
    }
*/
}
