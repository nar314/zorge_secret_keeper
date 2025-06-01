package com.zorge.secret_keeper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class MenuHolderTree {

    private JPopupMenu menu = new JPopupMenu();
    private JMenuItem miNew = new JMenuItem("New");
    private JMenuItem miRename = new JMenuItem("Rename");
    private JMenuItem miDelete = new JMenuItem("Delete");
    private MainWindow parent;
    
    /**
     * Constructor.
     * 
     * @param parent
     */
    public MenuHolderTree(MainWindow parent) {
    	
    	this.parent = parent;
        menu.add(miNew);
        menu.add(miRename);
        menu.addSeparator();
        menu.add(miDelete);
        parent.tree.setComponentPopupMenu(menu);
    
        miNew.setEnabled(false);
        miDelete.setEnabled(false);
        setHandlers();
    }
    
    /**
     * Open file.
     * 
     * @param isOpen
     */
    public void open(Boolean isOpen) {

    	if(isOpen) {
            miNew.setEnabled(true);
            miRename.setEnabled(true);
            miDelete.setEnabled(true);    		            
    	}
    	else {
            miNew.setEnabled(false);
            miRename.setEnabled(false);
            miDelete.setEnabled(false);
    	}
    }
    
    /**
     * Set item count.
     * 
     * @param count
     */
    public void setItemsCount(int count) {
    	
    	if(count == 0) {
    		miRename.setEnabled(false);
            miDelete.setEnabled(false);    		
    	}
    	else {
    		miRename.setEnabled(true);
            miDelete.setEnabled(true);    		
    	}
    }
    
    private void setHandlers() {
    	
        miNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onAddItem();
			}
		});
        
        miDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onDeleteItem();
			}
		});
        
        miRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onRenameItem();
			}
		});         
    }    
}
