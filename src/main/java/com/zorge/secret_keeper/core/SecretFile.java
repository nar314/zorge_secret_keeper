package com.zorge.secret_keeper.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class SecretFile {

	private ArrayList<Item> items = new ArrayList<Item>();
	private static String signature = "ZSK";
	private static String version = "1";

	private String fileName;
	private String fullPath;
	private String psw;
	private Boolean isNewFile = false;
	private Boolean isModified = false;

	private Item root = null;
	
	/**
	 * Open existing file.
	 * 
	 * @param fileName
	 * @param psw
	 * @param isNewFile
	 * @throws Exception
	 */
	public SecretFile(final String fileName, final String psw, Boolean isNewFile) throws Exception {
		
		if(fileName == null || fileName.isEmpty() || psw == null || psw.isEmpty())
			throw new Exception("Invalid input parameters.");
		
		File f = new File(fileName);
		if(!f.exists())
			throw new Exception("File does not already exist");
		
		fullPath = f.getCanonicalPath();
		this.fileName = fileName;
		this.psw = psw;
		this.isModified = true;
		this.isNewFile = false;
		
		loadFile();
	}
	
	/**
	 * Create new file.
	 * 
	 * @param fileName
	 * @param psw
	 * @throws Exception
	 */
	public SecretFile(final String fileName, final String psw) throws Exception {
		
		if(fileName == null || fileName.isEmpty() || psw == null || psw.isEmpty())
			throw new Exception("Invalid input parameters.");
		
		File f = new File(fileName);
		if(f.exists())
			throw new Exception("File already exist");
		
		fullPath = f.getCanonicalPath();
		this.fileName = fileName;
		this.psw = psw;
		isModified = true;
		isNewFile = true;
		root = new Item("My notes", "", null);
		items.add(root);
	}

	/**
	 * Get file name.
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Get path to the file.
	 * 
	 * @return
	 */
	public String getPath() {
		return fullPath;
	}
	
	/**
	 * Get items.
	 * 
	 * @return
	 */
	public Item getRoot() {
		return root;
	}

	/**
	 * Add new item.
	 * 
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public Item addItem(final String title, Item parent) throws Exception {
		
		if(parent == null)
			throw new Exception("Parent is null.");
		
		if(title == null || title.isEmpty())
			throw new Exception("NULL or empty");
		
		Item item = new Item(title, "", parent);
		items.add(item);		
		isModified = true;
		return item;
	}
	
	/**
	 * Update item text.
	 * 
	 * @param item
	 * @param text
	 * @throws Exception
	 */
	public void updateItemText(Item item, final String text) throws Exception {
		
		item.text = text;
		isModified = true;
	}
	
	/**
	 * Delete item.
	 * 
	 * @param item to delete
	 * @throws Exception
	 */
	public void deleteItem(Item item) throws Exception {
		
		if(item == null)
			throw new Exception("item is null");
		if(item.children.size() > 0)
			throw new Exception("Can not delete node with children.");
		
		Item parent = item.parent;
		if(parent != null) {
			for(Item i : parent.children)
				if(i == item) {
					parent.children.remove(i);
					break;
				}
		}
		
		items.remove(item);
		isModified = true;		
	}	
	
	/**
	 * Load items from the file.
	 * 
	 * @throws Exception
	 */
	public void loadFile() throws Exception {

		items.clear();		
		if(isNewFile)
			return;
		
		if(fileName == null)
			throw new Exception("File name not set.");
		
		File f = new File(fileName);
		if(!f.isFile())
			throw new Exception("Not a file : " + fileName);

		int sigLen = signature.length();
		byte[] sig = new byte[sigLen];

		int verLen = version.length();
		byte[] ver = new byte[verLen];
		
		int fileSize = (int)f.length() - sigLen - verLen;
		byte[] input = new byte[fileSize];
		try(FileInputStream fis = new FileInputStream(fileName)) {
			
			fis.read(sig);
			if(!new String(sig).equals(signature))
				throw new Exception("Wrong file format.");

			// If I will need to, I will support multiple versions. This is infrastructure for this.
			fis.read(ver);
			if(!new String(ver).equals(version))
				throw new Exception("Wrong file version.");
			
			int read = fis.read(input);
			if(read != fileSize)
				throw new Exception("Failed to read file " + fileName);
		}

		Coder c = new Coder();
		byte[] data = c.decrypt(input, psw);
		
		// Process loaded data
		JSONArray json = new JSONArray(new String(data));
		for(int i = 0; i < json.length(); ++i) {
			JSONObject o = json.getJSONObject(i);
			items.add(new Item(o));
		}
		
		int maxId = 0;
		HashMap<Integer, Item> byId = new HashMap<Integer, Item>();
		for(Item i : items) {
			byId.put(i.id, i);
			if(i.id > maxId)
				maxId = i.id;
		}
		Item.nextId = maxId + 1;
		
		root = byId.get(0);
		
		if(root == null) {
			System.out.println("Root not found. Generating default root.");
			root = new Item("My notes", "", null);
			items.add(root);
		}
		
		for(Item i : items) {
			
			if(i.id == 0 && i.parentId == 0)
				continue;
			
			Item parent = byId.get(i.parentId);
			if(parent != null) {
				i.parent = parent;
				parent.children.add(i);
			}
			else
				System.out.println("Parent not found for node : " + i.f1());
		}
		
		// Check lost and found objects
		for(Item i : items) {
			if(i == root)
				continue;
			if(i.parent == null) {
				i.parentId = 0;
				i.parent = root;
				root.children.add(i);
				System.out.println("Lost and found item " + i.f1());
			}
		}
	
		// Sort children for all nodes.
		for(Item i : items)
			i.sortChildren();
	}
	
	/**
	 * Save items to file.
	 * 
	 * @throws Exception
	 */
	public void saveFile() throws Exception {
		
		if(fileName.isEmpty())
			throw new Exception("No file name.");
		
		if(isModified == false)
			return;
		
		if(psw == null)
			return;
		
		JSONArray json = new JSONArray();
		for(Item item : items)
			json.put(item.toJson());
		
		final String out = json.toString();
		Coder c = new Coder();
		byte[] data = c.encrypt(out, psw);
		
		try(FileOutputStream fos = new FileOutputStream(fileName, false);
			BufferedOutputStream bos = new BufferedOutputStream(fos)) {
			
			bos.write(signature.getBytes());
			bos.write(version.getBytes());
			bos.write(data);
		}
		
		isModified = false;
		//print(root, 0);
	}

	/**
	 * Close file.
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception {
		
		saveFile();
		items.clear();
		fileName = "";
		isModified = false;
	}
	
	/**
	 * Check if selected file has ZKS signature.
	 * 
	 * @param path
	 * @return
	 */
	public static Boolean isZskFile(final String path) {
		
		try {
			File f = new File(path);
			if(!f.isFile())
				return false; // not a file

			int sigLen = signature.length();
			byte[] sig = new byte[sigLen];

			try(FileInputStream fis = new FileInputStream(path)) {				
				fis.read(sig);
				
				if(new String(sig).equals(signature))
					return true; // wrong signature
			}
		}
		catch(Exception e) { // whatever
		}
		
		return false;		
	}

	/**
	 * Rename title.
	 * 
	 * @param curItem
	 * @param newTitle
	 * @throws Exception
	 */
	public void renameItem(final Item curItem, final String newTitle) throws Exception {

		if(curItem == null || newTitle == null || newTitle.isEmpty())
			throw new Exception("NULL or empty");
		
		Item parent = curItem.parent;
		if(parent != null) {
			for(Item i : parent.children)
				if(i.title.equals(newTitle))
					throw new Exception("'" + newTitle + "' already exist.");
		}
		curItem.title = newTitle;
		isModified = true;
		curItem.sortChildren();
	}
/*	
    private void print(Item item, int tab) {
    	
    	String t = "";
    	for(int i = 0; i < tab; ++i)
    		t += "    ";
    	
    	System.out.println(t + item.f1());
    	if(item.children.size() > 0) {
    		for(Item c : item.children) {
    			print(c, tab + 1);
    		}
    	}
    }
*/
}

