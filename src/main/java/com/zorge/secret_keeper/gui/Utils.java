package com.zorge.secret_keeper.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JOptionPane;

public class Utils {

	/**
	 * Message box with OK message.
	 * 
	 * @param message
	 */
	static public void MessageBox_OK(final String message) {
		JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
	}

	static public int YES = 0;
	static public int NO = 1;
	/**
	 * Message box with Yes/No message.
	 * 
	 * @param message
	 * @return
	 */
	static public int MessageBox_YESNO(final String message) {
		return JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_OPTION);
	}
	
	/**
	 * Message box with error message.
	 * 
	 * @param message
	 */
	static public void MessageBox_Error(final String message) {	
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Load file into string.
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	static public String fileToString(final String fileName) throws Exception {

		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		StringBuilder sb = new StringBuilder();

	    try {
	        while((line = reader.readLine()) != null)
	            sb.append(line).append("\n");
	        return sb.toString();
	    } 
	    finally {
	        reader.close();
	    }		
	}

	/**
	 * Store string into file.
	 * 
	 * @param fileName
	 * @param data
	 * @throws Exception
	 */
	static public void stringToFile(final String fileName, final String data) throws Exception {
		
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName, false));
		out.write(data);
        out.close();
	}
}
