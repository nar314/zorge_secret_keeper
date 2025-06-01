package com.zorge.secret_keeper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuHolderFrame {

	JMenuBar menuBar = new JMenuBar();
	
	private JMenu menuFile = new JMenu("File");
	private JMenuItem miNew = new JMenuItem("New");
	private JMenuItem miExit = new JMenuItem("Exit");
	private JMenuItem miOpen = new JMenuItem("Open");
	
	private JMenu menuWOFile = new JMenu("With open file");
	private JMenuItem miSave = new JMenuItem("Save");
	private JMenuItem miClose = new JMenuItem("Close");
	
	private JMenu menuHelp = new JMenu("Help");
	private JMenuItem miAbout = new JMenuItem("About");
	
	MainWindow parent = null;
	
	/**
	 * Constructor.
	 * 
	 * @param parent
	 */
	public MenuHolderFrame(MainWindow parent) {
		
		miSave.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		
		this.parent = parent;
		menuFile.add(miNew);
		menuFile.add(miOpen);
		menuFile.addSeparator();
		menuFile.add(miExit);
		
		menuWOFile.add(miSave);
		menuWOFile.add(miClose);		
		
		menuHelp.add(miAbout);
		
		menuBar.add(menuFile);
		menuBar.add(menuWOFile);
		menuBar.add(menuHelp);
		parent.frame.setJMenuBar(menuBar);
		
		setHandlers();
		enableDefault();
	}
	
	/**
	 * Open file.
	 * 
	 * @param isOpen
	 */
	public void open(Boolean isOpen) {

		if(isOpen) {
			miClose.setEnabled(true);
			miSave.setEnabled(true);
		}
		else {
			miClose.setEnabled(false);
			miSave.setEnabled(false);			
		}		
	}

	private void enableDefault() {
		
		miNew.setEnabled(true);
		miExit.setEnabled(true);		
		miOpen.setEnabled(true);
		
		miClose.setEnabled(false);
		miSave.setEnabled(false);
	}
		
	private void setHandlers() {
		
		// File
		miNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onNewFile();
			}
		});
		
		miSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onSaveFile();
			}
		});

		miExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onExit();
			}
		});
		
		miOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onOpen();
			}
		});
		
		miClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onClose();
			}
		});	
		
		// Help
		miAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.onAbout();
			}
		});		
	}
}
