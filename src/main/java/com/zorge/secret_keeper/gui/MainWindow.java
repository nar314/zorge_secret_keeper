package com.zorge.secret_keeper.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.zorge.secret_keeper.core.Config;
import com.zorge.secret_keeper.core.FolderScanner;
import com.zorge.secret_keeper.core.Item;
import com.zorge.secret_keeper.core.SecretFile;
import com.zorge.secret_keeper.gui.dlg.DlgAbout;
import com.zorge.secret_keeper.gui.dlg.DlgLogin;
import com.zorge.secret_keeper.gui.dlg.DlgTitle;
import com.zorge.secret_keeper.gui.dlg.DlgSelectFile;

public class MainWindow {

	private static final String ver = "1.0";
	private static final Boolean FORCE_SAVE = true;
	private static final Boolean CONFIRM_SAVE = false;
	
	JFrame frame = new JFrame("Zorge secret keeper. " + ver);
	private JSplitPane splitPane = null;
	JTree tree;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("My notes");
	DefaultTreeModel model = null;
	
	private static Color colorLock = Color.WHITE;
	private static Color colorUnlock = new Color(252, 222, 251);	
	JTextArea text = new JTextArea();
	private Boolean textLocked = true;
	private Boolean textModified = false;
	private DefaultMutableTreeNode lockedNode = null;
	
	private SecretFile file;
	private MenuHolderFrame mhf;
	private MenuHolderTree mhl;
	private MenuHolderText mht;
	
	private Config config = new Config();

	/**
	 * Constructor.
	 * 
	 * @throws Exception
	 */
	public MainWindow() throws Exception {

		FolderScanner fs = new FolderScanner();
		final ArrayList<String> names = fs.getNames();
		String fileName = null;

		model = new DefaultTreeModel(root);
		tree = new JTree(model);

		mhf = new MenuHolderFrame(this);
		mhl = new MenuHolderTree(this);
		mht = new MenuHolderText(this);
		
		if(names.size() == 0) {
			onNewFile();
		}
		else if(names.size() == 1) {
			fileName = names.get(0);
		}
		else if(names.size() > 1) {
			DlgSelectFile dlg = new DlgSelectFile(names);
			dlg.showDialog();
			if(dlg.OK)
				fileName = dlg.selectedName;
		}
		
		if(fileName != null)
			openFile(fileName);
		
		createGUI();

		if(config.load()) {
			frame.setSize(config.getSizeW(), config.getSizeH());
			splitPane.setDividerLocation(config.getDeviderLocation());
		}
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("res/icon.png"));
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override public void run() {
		    	try {
		    		if(file != null) {
		    			dealWithModifiedText(FORCE_SAVE);
		    			file.saveFile();
		    		}
		    	}
		    	catch(Exception e) {
		    	}
		    	// get coordinates and dimension before exit
		    	config.setDeviderLocation(splitPane.getDividerLocation());
		    	config.setFrameDimension(frame.getSize());
		    	config.save();
		    }
		});
	}	
	
	/**
	 * Show dialog.
	 */
	public void show() {
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		frame.pack();
		if(!config.isLoaded())
			frame.setSize(400, 380);
		else
			frame.setSize(config.getSizeW(), config.getSizeH());
		frame.setMinimumSize(new Dimension(400, 380));
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.expandPath(new TreePath(root));
		
		textLocked = true;
		textModified = false;
		onLockText(true);
		
		text.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				textModified = true;
			}
			public void insertUpdate(DocumentEvent e) {
				textModified = true;
			}
			public void changedUpdate(DocumentEvent e) {
				textModified = true;
			}
		});
		text.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == 83) { // Ctrl-S pressed
					onSaveFile();
				}
			}
		});
	}
	
	private void createGUI() {
		
		JScrollPane scrollText = new JScrollPane (text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scrollTree = new JScrollPane (tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTree, scrollText);
        splitPane.setDividerLocation(100);

        frame.add(splitPane);
        
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.setTransferHandler(new TreeTransferHandler());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        
        tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if(curNode == root || curNode == null) {
					dealWithModifiedText(CONFIRM_SAVE);
					text.setText("");
				}
				else {
					Item curItem = (Item)curNode.getUserObject();
					dealWithModifiedText(CONFIRM_SAVE);				
					text.setText(curItem.text);
				}
			}
		});
	}

	private void buildModel(DefaultMutableTreeNode node, Item item) throws Exception {
		
		int pos = 0;
		for(Item i : item.children) {
			DefaultMutableTreeNode modelNode = new DefaultMutableTreeNode(i);
			model.insertNodeInto(modelNode, node, pos++);			
			buildModel(modelNode, i);
		}
	}
	
	private void loadData() {

		text.setText("");
		if(file == null)
			return;
		
		try {
			Item item = file.getRoot();
			root.setUserObject(item);
			
			clearModel();
			buildModel(root, item);
			expandedStatusToGUI(root);
		}
		catch(Exception e) {
			Utils.MessageBox_Error("Failed to open file.\nUse different password.");
			return;
		}
	}
	
	void onLockText(Boolean lock) {
		
		if(lock) {
			// Locking text
			text.setBackground(colorLock);
			if(textModified && lockedNode != null) {
				try {
					Item curItem = (Item)lockedNode.getUserObject();
					file.updateItemText(curItem, text.getText());
					file.saveFile();
					setModifiedTitle(false);
					lockedNode = null;
				}
				catch(Exception e) {
					Utils.MessageBox_Error(e.getMessage());
				}
			}
			textModified = false;
		}
		else {
			// Unlocking text
			lockedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			text.setBackground(colorUnlock);
			text.requestFocus();
		}
		text.setEditable(!lock);
		textLocked = lock;
	}
	
	void onLockDiscardText() {
		
		if(textLocked)
			return;
		
		text.setBackground(colorLock);
		if(textModified && lockedNode != null) {
			try {
				Item item = (Item)lockedNode.getUserObject();
				text.setText(item.text);
			}
			catch(Exception e) {
				Utils.MessageBox_Error(e.getMessage());
			}
		}
		textLocked = true;
		textModified = false;
		text.setEditable(false);
	}
	
	void onDeleteItem() {
		
		DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(curNode == null || curNode == root) {
			Utils.MessageBox_Error("Can not delete root");
			return;
		}
		Item curItem = (Item)curNode.getUserObject();

		if(Utils.MessageBox_YESNO("Do you want to delete '" + curItem.title + "' ?") == Utils.NO)
			return;
		
		try {
			file.deleteItem(curItem);
			model.removeNodeFromParent(curNode);
			setModifiedTitle(true);
			text.setText("");
		}
		catch(Exception e) {
			Utils.MessageBox_Error(e.getMessage());
		}
	}
	
	private void selectNode(final DefaultMutableTreeNode node) {

		if(node != null) {
			TreeNode[] nodes = model.getPathToRoot(node);
			tree.setExpandsSelectedPaths(true);
			tree.setSelectionPath(new TreePath(nodes));			
		}
	}
	
	void onAddItem() {

		DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(curNode == null)
			curNode = root;
		Item parent = (Item)curNode.getUserObject();
			
		DlgTitle dlg = new DlgTitle();
		dlg.showDialog();
		if(dlg.OK == false)
			return;
		
		try {
			Item newItem = file.addItem(dlg.title, parent);
			int index = parent.getChildIndex(newItem);
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newItem);
			model.insertNodeInto(newNode, curNode, index);
			selectNode(newNode);
			
			setModifiedTitle(true);
		}
		catch(Exception e) {
			Utils.MessageBox_Error(e.getMessage());
		}
	}
	
	private void setModifiedTitle(Boolean isModified) {
		frame.setTitle("Zorge secret keeper. " + ver + (isModified ? "*" : ""));
	}
	
	void onClose() {
		
		try {
			file.saveFile();
			file.close();
			file = null;
			text.setText("");
			
			clearModel();
			
			setOpenForMenus(false);
		}
		catch(Exception ex) {
			Utils.MessageBox_Error(ex.getMessage());
		}		
	}
	
	void onOpen() {
		
		FileDialog dlg = new FileDialog(frame, "Select File to Open");
	    dlg.setMode(FileDialog.LOAD);
	    dlg.setVisible(true);
	    final String file = dlg.getFile();
	    dlg.dispose();
	    
	    if(file != null)
	    	openFile(file);		
	}
	
	void onExit() {
		
		dealWithModifiedText(CONFIRM_SAVE);
		frame.setVisible(false);
		frame.dispose();				
	}
	
	private void dealWithModifiedText(Boolean forceSave) {
		
		if(textLocked != false) 
			return;
		
		if(forceSave) {
			onLockText(true);
			return;
		}

		if(lockedNode == null)
			return;
		
		DefaultMutableTreeNode lockedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		Item item = (Item)lockedNode.getUserObject();

		final String title = item.title;
		if(Utils.MessageBox_YESNO("Do you want to save modified text for '" + title + "' ?") == Utils.YES) {
			onLockText(true);
		}
		else {
			onLockDiscardText();
		}
	}
	
	void onNewFile() {
		
		try {
			DlgLogin dlg = new DlgLogin();
			dlg.showDialog();
			if(!dlg.OK)
				return;

			dealWithModifiedText(CONFIRM_SAVE);
			if(file != null)
				file.saveFile();
			file = new SecretFile(dlg.fileName, dlg.psw);

			clearModel();
			
			Item item = file.getRoot();
			root.setUserObject(item);

			text.setText("");			
			setOpenForMenus(true);
		}
		catch(Exception e) {
			Utils.MessageBox_Error(e.getMessage());
		}
	}
	
	private void setOpenForMenus(Boolean isOpen) {
		
		mhf.open(isOpen);
		mhl.open(isOpen);
		mht.open(isOpen);		
	}
	
	private void openFile(final String fileName) {
		
		try {
			if(!new File(fileName).exists()) {
				Utils.MessageBox_Error("File does not exist.");
				return;
			}
			if(!SecretFile.isZskFile(fileName)) {
				Utils.MessageBox_Error("Wrong file.");
				return;
			}
			
			dealWithModifiedText(CONFIRM_SAVE);
			if(file != null)
				file.saveFile();			
			
			while(true) {
				DlgLogin dlg = new DlgLogin(fileName);
				dlg.showDialog();
				if(!dlg.OK)
					return;
	
				try {
					file = new SecretFile(dlg.fileName, dlg.psw, false);
					setOpenForMenus(true);
					break;
				}
				catch(Exception e) {
					Utils.MessageBox_Error("Failed to open file.\nUse different password.");
				}
			}
			loadData();
		}
		catch(Exception e) {
			Utils.MessageBox_Error(e.getMessage());
		}
	}
	
	void onSaveFile() {
		
		try {		
			if(file == null)
				return;

			expandStatusToCore(root);			
			dealWithModifiedText(FORCE_SAVE);
			file.saveFile();
		}
		catch(Exception e) {
			Utils.MessageBox_Error(e.getMessage());
		}		
	}
	
	void onAbout() {
		
		String path = file != null ? file.getPath() : null;
		DlgAbout dlg = new DlgAbout(path);
		dlg.showDialog();
	}
	
	void onRenameItem() {
		
		DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(curNode == null)
			curNode = root;
		Item curItem = (Item)curNode.getUserObject();

		dealWithModifiedText(CONFIRM_SAVE);
		final String curTitle = curItem.title;
		
		DlgTitle dlg = new DlgTitle(curTitle);
		dlg.showDialog();
		if(!dlg.OK)
			return;
		
		try {
			file.renameItem(curItem, dlg.title);
			selectNode(curNode);			
			setModifiedTitle(true);
		}
		catch(Exception e) {
			Utils.MessageBox_Error(e.getMessage());
		}
	}
	
	private void clearModel() {
		
		root.removeAllChildren();
		model.reload();	
	}
	
	private void expandStatusToCore(DefaultMutableTreeNode curNode) {

		Item item = (Item)curNode.getUserObject();
		TreePath tp = new TreePath(curNode.getPath());
		item.isExpanded = tree.isExpanded(tp);
		
		int n = model.getChildCount(curNode);
		for(int i = 0; i < n; ++i) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(curNode, i);
			expandStatusToCore(node);
		}
	}

	private void expandedStatusToGUI(DefaultMutableTreeNode curNode) {

		Item item = (Item)curNode.getUserObject();
		TreePath tp;
		if(item.isExpanded) {
			tp = new TreePath(curNode.getPath());
			tree.expandPath(tp);
		}
		
		int n = model.getChildCount(curNode);
		for(int i = 0; i < n; ++i) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(curNode, i);
			expandedStatusToGUI(node);
		}
	}	
}
