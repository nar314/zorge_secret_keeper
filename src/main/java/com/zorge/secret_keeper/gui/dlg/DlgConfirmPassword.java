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
import javax.swing.JPasswordField;
import javax.swing.SpringLayout;

import com.zorge.secret_keeper.gui.Utils;

public class DlgConfirmPassword extends JDialog {

	private static final long serialVersionUID = 1L;
	public Boolean OK = false;
	public String psw = "";
	
	private JPasswordField pswText = new JPasswordField();

	/**
	 * Constructor.
	 */
	public DlgConfirmPassword() {
		
		createGUI();
		setTitle("Confirm password.");
	}
	
	/**
	 * Show dialog.
	 */
	public void showDialog() {

		OK = false;
		psw = "";
		setModal(true);
		setMinimumSize(new Dimension(300, 120));
		setResizable(true);
		
		pack();
		setSize(300, 120);
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
		gbc.insets = new Insets(0, 0, 5, 0); // +5 to bottom		
		panel.add(new JLabel("Password:"), gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0; // 100%
		panel.add(pswText, gbc);
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
			public void actionPerformed(ActionEvent e) {
				psw = "";
				OK = false;
				dispose();
			}
		});
		
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		
		pswText.addActionListener(new ActionListener() {
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
		
		layout.putConstraint(SpringLayout.EAST, panelButtons, 0, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, panelButtons, -5, SpringLayout.SOUTH, panel);
		
		getContentPane().add(panel);		
	}
	
	private void onOK() {
		
		psw = new String(pswText.getPassword()).trim();

		if(psw.isEmpty()) {
			Utils.MessageBox_Error("Password can not be empty.");
			pswText.requestFocus();
			return;			
		}
		OK = true;
		dispose();
	}	
}
