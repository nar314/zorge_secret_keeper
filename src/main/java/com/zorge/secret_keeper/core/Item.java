package com.zorge.secret_keeper.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONObject;

public class Item {

	public static int nextId = 0;
	
	public String title;
	public String text;
	
	public Item parent;
	
	public int parentId = 0;
	public int id = 0;
	public Boolean isExpanded = false;
	
	public ArrayList<Item> children = new ArrayList<Item>();
	
	/**
	 * Constructor to GUI flow.
	 * 
	 * @param title
	 * @param text
	 */
	public Item(final String title, final String text, final Item parent) throws Exception {

		Boolean doSort = false;
		if(parent == null) {
			this.parentId = 0; // Root id
			this.parent = null;
			this.id = 0;
		}
		else {
			for(Item i : parent.children) {
				if(i.title.equals(title))
					throw new Exception("Title '" + title + "' already exist.");
			}
			this.id = ++nextId;			
			this.parentId = parent.id;
			this.parent = parent;
			this.parent.children.add(this);
			doSort = true;
		}

		this.title = title;
		this.text = text;
		if(doSort)
			this.parent.sortChildren();
	}
	
	/**
	 * Constructor for loading flow.
	 * 
	 * @param o
	 */
	public Item(final JSONObject o) throws Exception {
		
		if(o == null)
			throw new Exception("JSON object is null.");
		
		id = o.getInt("id");
		parentId = o.getInt("pid");
		isExpanded = o.getBoolean("isExpanded");
		title = o.getString("title");
		text = o.getString("text");		
		
		parent = null; // will be link later.
		children.clear();
	}
	
	/**
	 * For JTree item.
	 */
	public String toString() {
		return title;
	}
	
	/**
	 * Item to JSON. Flow to store item.
	 * 
	 * @return
	 */
	public JSONObject toJson() {
		
		JSONObject out = new JSONObject();
		out.put("id", id).put("pid", parentId).put("isExpanded", isExpanded).put("title", title).put("text", text);
		return out;
	}
	
	public String f1() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id);
		sb.append(", pid=").append(parentId);
		sb.append(", isExp=").append(isExpanded);
		sb.append(", title=").append(title);
		return sb.toString();
	}
	
	/**
	 * Sort children.
	 */
	public void sortChildren() {
		
		if(children.size() > 1) {			
			Collections.sort(children, new Comparator<Item>() {
				@Override
				public int compare(Item a1, Item a2) {
					return a1.title.compareTo(a2.title);
				}
			});
		}
	}
	
	/**
	 * Get child index.
	 * 
	 * @param item
	 * @return
	 */
	public int getChildIndex(Item item) {
	
		int n = children.indexOf(item);
		if(n < 0)
			n = 0;
		return n;
	}
	
	/**
	 * Check if item is one of my children. Used in drag and drop flow.
	 * 
	 * @param item
	 * @return
	 */
	public Boolean isOneOfMyChildren(final Item item) {
		
		if(item == this)
			return true; // Yep
		
		for(Item i : children) {
			if(i.isOneOfMyChildren(item))
				return true;
		}
		return false;
	}
	
	/**
	 * Flow of drag and drop.
	 * 
	 * @param item
	 * @return
	 */
	public Boolean dropToMyChildern(Item item) {
		
		if(children.indexOf(item) != -1)
			return false; // already exist
		
		// Detach from old parent.
		Item oldParent = item.parent;
		if(oldParent == null) {
			System.out.println("Parent not found for " + item.f1());
			return false; // something wrong.
		}
		
		Boolean removed = false;
		for(int i = 0; i < oldParent.children.size(); ++i) {			
			if(oldParent.children.get(i) == item) {
				oldParent.children.remove(i);
				removed = true;
				break;
			}
		}
		if(removed == false)
			System.out.println("Drop item not found in parent " + item.f1());
		
		// Add to my children
		children.add(item);
		item.parent = this;
		item.parentId = id;
		sortChildren();
		
		return true;
	}
}

