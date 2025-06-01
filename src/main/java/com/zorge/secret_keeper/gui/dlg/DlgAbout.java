package com.zorge.secret_keeper.gui.dlg;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;

public class DlgAbout extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextArea taText = new JTextArea("");

	/**
	 * Constructor.
	 * 
	 * @param path
	 */
	public DlgAbout(final String path) {
		
		createGUI();
		taText.setEditable(false);
		taText.setText(getTextAbout(path));
		setTitle("About");
		
		taText.requestFocus();
		
		taText.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 27) // Escape
					dispose();
			}
		});
	}
	
	/**
	 * Show dialog.
	 */
	public void showDialog() {

		setModal(true);
		setMinimumSize(new Dimension(480, 300));
		setResizable(true);
		
		pack();
		setSize(480, 300);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private String getTextAbout(final String path) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("Zorge secret keeper.\n\n");
		sb.append("Free and open source.\n");		
		sb.append("Sources location : https://github.com/nar314/zorge_secret_keeper\n");
		
		sb.append("\nIn memory of Richard Sorge.\n");
		sb.append("https://en.wikipedia.org/wiki/Richard_Sorge\n\n");
		
		if(path == null)
			sb.append("No open file\n");
		else
			sb.append("Open file : ").append(path).append("\n");
		
		
		return sb.toString();
	}
	
	private JPanel panelInput() {
		
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		
		JScrollPane sp = new JScrollPane(taText);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(sp);
		
		layout.putConstraint(SpringLayout.NORTH, sp, 0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, sp, 0, SpringLayout.SOUTH, panel);

		return panel;		
	}

	private JPanel panelButtons() {
		
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);

		JButton btnOK = new JButton("OK");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 5); // 5 pixel to the next button on the right
		panel.add(btnOK, gbc);

		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		return panel;		
	}

	private void createGUI() {
		
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		JPanel panelInput = panelInput();
		JPanel panelButtons = panelButtons();
		
		panel.add(panelInput);
		panel.add(panelButtons);
		
		layout.putConstraint(SpringLayout.NORTH, panelInput, 5, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, panelInput, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, panelInput, -5, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, panelInput, -5, SpringLayout.NORTH, panelButtons);
		
		layout.putConstraint(SpringLayout.EAST, panelButtons, 0, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, panelButtons, -5, SpringLayout.SOUTH, panel);
		
		getContentPane().add(panel);
	}
}
