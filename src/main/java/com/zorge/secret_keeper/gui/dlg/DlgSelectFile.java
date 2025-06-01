package com.zorge.secret_keeper.gui.dlg;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;

public class DlgSelectFile extends JDialog {

	private static final long serialVersionUID = 1L;

	public Boolean OK = false;
	public String selectedName;
	
	private JList<String> list;
	private DefaultListModel<String> model;
	
	/**
	 * Constructor.
	 * 
	 * @param files
	 */
	public DlgSelectFile(final ArrayList<String> files) {

		list = new JList<String>();
		model = new DefaultListModel<String>();
		for(String s : files)
			model.addElement(s);
		list.setModel(model);
		
		createGUI();
	}
	
	/**
	 * Show dialog.
	 */
	public void showDialog() {

		OK = false;
		setModal(true);
		setMinimumSize(new Dimension(350, 250));
		setResizable(true);
		setTitle("Select file.");
		
		pack();
		list.requestFocus();
		list.setSelectedIndex(0);

		setSize(350, 250);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private JPanel panelInput2() {
		
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		
		JLabel label = new JLabel("File to open:");
		panel.add(label);
		
		JScrollPane sp = new JScrollPane(list);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(sp);

		layout.putConstraint(SpringLayout.NORTH, label, 0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, panel);

		layout.putConstraint(SpringLayout.NORTH, sp, 0, SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.SOUTH, sp, 0, SpringLayout.SOUTH, panel);

		list.addMouseListener(new MouseListener() {			
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2)
					onOK();
			}			

			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		});
		return panel;		
	}

	private JPanel panelButtons() {
		
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);

		JButton btnOK = new JButton("OK");
		JButton btnCancel = new JButton("Cancel");

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

		JPanel panelInput = panelInput2();
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
	
	private void onOK() {
		selectedName = list.getSelectedValue();
		OK = true;
		dispose();		
	}
}
