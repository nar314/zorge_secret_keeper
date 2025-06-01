package com.zorge.secret_keeper.gui.dlg;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.zorge.secret_keeper.gui.Utils;

public class DlgLogin extends JDialog {

	private static final long serialVersionUID = 1L;
	
	public Boolean OK = false;
	public String psw = "";
	public String fileName = "";
	
	private Boolean modeNewFile = false;
	private JPasswordField pswText = new JPasswordField();
	private JTextField txtName = new JTextField();

	/**
	 * Constructor for flow of "new file".
	 */
	public DlgLogin() {
		
		createGUI();
		
		modeNewFile = true;
		fileName = UUID.randomUUID().toString() + ".zsk";
		txtName.setEditable(true);
		txtName.setText(fileName);
		setTitle("Create new file.");
	}
	
	/**
	 * Constructor for flow of "login into existing file".
	 */
	public DlgLogin(final String fileName) {
		
		createGUI();
		modeNewFile = false;
		txtName.setEditable(false);
		txtName.setText(fileName);
		setTitle("Open file.");		
	}
	
	/**
	 * Show dialog.
	 */
	public void showDialog() {

		OK = false;
		setModal(true);
		setMinimumSize(new Dimension(300, 170));
		setResizable(true);
		
		pack();
		pswText.requestFocus();

		setSize(300, 170);
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
		panel.add(new JLabel("File:"), gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, 5, 0); // +5 to bottom				
		panel.add(txtName, gbc);				

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(0, 0, 5, 0); // +5 to bottom				
		panel.add(new JLabel("Password:"), gbc);				
		
		gbc.gridx = 0;
		gbc.gridy = 3;
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
		
		psw = new String(pswText.getPassword());
		fileName = txtName.getText();

		if(modeNewFile) {
			if(fileName.isEmpty()) {
				Utils.MessageBox_Error("File name can not be empty.");
				txtName.requestFocus();
				return;			
			}
			if(!fileName.endsWith(".zsk")) {
				Utils.MessageBox_Error("File must have \".zsk\" extension.");
				txtName.requestFocus();
				return;				
			}
			File f = new File(fileName);
			if(f.exists()) {
				Utils.MessageBox_Error("File already exist.");
				txtName.requestFocus();
				return;							
			}
		}
		
		if(psw.isEmpty()) {
			Utils.MessageBox_Error("Password can not be empty.");
			pswText.requestFocus();
			return;
		}

		if(modeNewFile) {
			DlgConfirmPassword dlg = new DlgConfirmPassword();
			dlg.showDialog();
			if(!dlg.OK || !dlg.psw.equals(psw)) {
				Utils.MessageBox_Error("Password not confirmed.");
				pswText.requestFocus();
				return;			
			}
		}
		OK = true;
		dispose();
	}
}
