package com.zorge.secret_keeper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class MenuHolderText {

    private JPopupMenu menu = new JPopupMenu();
    private JMenuItem miUnlock = new JMenuItem("Unlock for edit");
    private JMenuItem miLockSave = new JMenuItem("Save and lock");
    private JMenuItem miLockDiscard = new JMenuItem("Discard changes and lock");
    private MainWindow parent;
    
    /**
     * Constructor.
     * 
     * @param parent
     */
    public MenuHolderText(MainWindow parent) {
    
    	this.parent = parent;
        menu.add(miUnlock);
        menu.addSeparator();
        menu.add(miLockSave);
        menu.add(miLockDiscard);
        parent.text.setComponentPopupMenu(menu); 

        miUnlock.setEnabled(false);
        miLockSave.setEnabled(false);
        miLockDiscard.setEnabled(false);
        
        setHandlers();
    }
    
    /**
     * Open file.
     * 
     * @param isOpen
     */
    public void open(Boolean isOpen) {

    	if(isOpen) {
            miUnlock.setEnabled(true);
            miLockSave.setEnabled(true);
            miLockDiscard.setEnabled(true);
    	}
    	else {
            miUnlock.setEnabled(false);
            miLockSave.setEnabled(false);
            miLockDiscard.setEnabled(false);    		
    	}
    }

    private void setHandlers() {
    	
        miUnlock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onLockText(false);
			}
		});
        
        miLockSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onLockText(true);
			}
		});
        
        miLockDiscard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onLockDiscardText();
			}
		});    	
    }
}
