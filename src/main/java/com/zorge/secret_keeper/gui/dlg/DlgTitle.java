package com.zorge.secret_keeper.gui.dlg;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.zorge.secret_keeper.gui.Utils;

public class DlgTitle extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private Boolean isNewTitle = true;
	private JTextField tfTitle = new JTextField("");
	private JButton btnOK = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");
	public Boolean OK = false;
	public String title = "";
	
	/**
	 * Constructor to edit title.
	 * 
	 * @param curTitle
	 */
	public DlgTitle(final String curTitle) {
		
		isNewTitle = false;
		createGUI();
		tfTitle.setText(curTitle);		
	}

	/**
	 * Constructor for new title.
	 * 
	 * @param file
	 */
	public DlgTitle() {
		
		isNewTitle = true;
		createGUI();
	}
	
	/**
	 * Show dialog.
	 */
	public void showDialog() {

		OK = false;
		setModal(true);
		setMinimumSize(new Dimension(250, 110));
		setResizable(true);
		setTitle(isNewTitle ? "Add new title" : "Edit title name");
		
		pack();
		setSize(250, 110);
		setLocationRelativeTo(null);
		setVisible(true);		
	}
	
	private JPanel panelInput() {
		
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("New title:"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0; // 100%
		panel.add(tfTitle, gbc);
		return panel;		
	}

	private JPanel panelButtons() {
		
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 5); // 5 pixel to the next button on the right
		panel.add(btnOK, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(btnCancel, gbc);

		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OK = false;
				dispose();
			}
		});
		
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onOK();
			}
		});
		
		tfTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
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
		
		layout.putConstraint(SpringLayout.EAST, panelButtons, -5, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, panelButtons, -5, SpringLayout.SOUTH, panel);
		
		getContentPane().add(panel);
	}
	
	private void onOK() {
		
		// Same behavior for creating new or modifying existing title.
		title = tfTitle.getText().trim();
		if(title.isEmpty()) {
			Utils.MessageBox_Error("Title can not be empty.");
			return;
		}
		OK = true;
		dispose();
	}
}
