package com.zorge.secret_keeper;

import com.zorge.secret_keeper.gui.MainWindow;
import com.zorge.secret_keeper.gui.Utils;

public class App {
	
    public static void main(String[] args) {
    	
    	try {
	        MainWindow mw = new MainWindow();
	        mw.show();
    	}
    	catch(Exception e) {
    		Utils.MessageBox_Error(e.getMessage());
    	}
    }
}
