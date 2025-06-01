package com.zorge.secret_keeper.core;

import java.awt.Dimension;

import com.zorge.secret_keeper.gui.Utils;

public class Config {
	
	private static String fileName = "zsk.config";
	private static String DEVIDER_LOCATION = "deviderLocation";
	private static String SIZE_H = "sizeH";
	private static String SIZE_W = "sizeY";
	
	private int deviderLocation = -1;
	private int sizeH = -1;
	private int sizeW = -1;
	
	private Boolean isLoaded = false;

	/**
	 * Save to file.
	 */
	public void save() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(DEVIDER_LOCATION).append("=").append(deviderLocation).append("\n");		
		sb.append(SIZE_H).append("=").append(sizeH).append("\n");
		sb.append(SIZE_W).append("=").append(sizeW).append("\n");
		
		try {
			Utils.stringToFile(fileName, sb.toString());
		}
		catch(Exception e) {
		}
	}
	
	/**
	 * Load from file.
	 * 
	 * @return
	 */
	public Boolean load() {
		
		isLoaded = true;
		try {
			String[] lines = Utils.fileToString(fileName).split("\n");
			for(String line : lines) {
				if(line.isEmpty())
					continue;
				
				String[] pair = line.split("=");
				if(pair.length != 2)
					continue;
				final String name = pair[0];
				if(name.equals(DEVIDER_LOCATION))
					deviderLocation = Integer.parseInt(pair[1]);
				else if(name.equals(SIZE_H)) 
					sizeH = Integer.parseInt(pair[1]);
				else if(name.equals(SIZE_W)) 
					sizeW = Integer.parseInt(pair[1]);				
			}
			
			if(deviderLocation == -1)
				isLoaded = false;
			if(sizeH == 0 || sizeW == 0)
				isLoaded = false;
		}
		catch(Exception e) {
			isLoaded = false;
		}
		return isLoaded;		
	}
	
	/**
	 * Set divider location.
	 * 
	 * @param n
	 */
	public void setDeviderLocation(int n) {
		deviderLocation = n;
	}
	
	/**
	 * Set frame dimension.
	 * 
	 * @param d
	 */
	public void setFrameDimension(final Dimension d) {
		sizeH = (int)d.getHeight();
		sizeW = (int)d.getWidth();
	}
	
	/**
	 * Set divider location.
	 * 
	 * @return
	 */
	public int getDeviderLocation() {
		return deviderLocation;
	}
	
	/**
	 * Get height.
	 * 
	 * @return
	 */
	public int getSizeH() {
		return sizeH;
	}
	
	/**
	 * Get width.
	 * 
	 * @return
	 */
	public int getSizeW() {
		return sizeW;
	}
	
	/**
	 * Is loaded.
	 * 
	 * @return
	 */
	public Boolean isLoaded() {
		return isLoaded;
	}
}
