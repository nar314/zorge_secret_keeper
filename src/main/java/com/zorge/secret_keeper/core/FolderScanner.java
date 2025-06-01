package com.zorge.secret_keeper.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FolderScanner {

	private ArrayList<String> fileNames = new ArrayList<String>();
	
	/**
	 * Constructor.
	 */
	public FolderScanner() {
		
		try {
			File curPath = new File(".");
			for(final File file : curPath.listFiles()) {
				final String name = file.getName();
				if(name.endsWith(".zsk") && file.isFile()) {
					if(SecretFile.isZskFile(name))
						fileNames.add(name);						
				}
			}
			
			if(fileNames.size() > 0) {
				Collections.sort(fileNames, new Comparator<String>() {
					public int compare(String s1, String s2) {
						return s1.compareTo(s2);
					}
				});
			}
		}
		catch(Exception e) {
		}		
	}
	
	/**
	 * Get names.
	 * 
	 * @return
	 */
	public ArrayList<String> getNames() {
		return fileNames;
	}
}
