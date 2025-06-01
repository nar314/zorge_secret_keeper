package com.zorge.secret_keeper.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.zorge.secret_keeper.core.Item;

// kudos to https://coderanch.com/t/346509/java/JTree-drag-drop-tree-Java
class TreeTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode[] nodesToRemove;
    Item sourceItem, targetItem;
    
    /**
     * Constructor.
     */
	public TreeTransferHandler() {

		try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + javax.swing.tree.DefaultMutableTreeNode[].class.getName() + "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } 
		catch(ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
        }
	}
	
	private Item itemFromTreePath(final TreePath tp) {
		
    	Object o = tp.getLastPathComponent();
        if(o == null)
        	return null; // no object
        
    	if(o instanceof DefaultMutableTreeNode == false)
    		return null; // not my type
    	
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
		o = node.getUserObject();
		if(o instanceof Item == false)
			return null;
		
		Item item = (Item)node.getUserObject();
		return item;
	}
	
	/**
	 * Can import.
	 */
	public boolean canImport(TransferHandler.TransferSupport support) {
		
		sourceItem = null;
		targetItem = null;
		
		if(!support.isDrop())
            return false;
        
        support.setShowDropLocation(true);
        if(!support.isDataFlavorSupported(nodesFlavor))
            return false;

        JTree tree = (JTree)support.getComponent();
        TreePath[] paths = tree.getSelectionPaths();
        if(paths.length != 1)
        	return false;
        
        TreePath tp = paths[0];
        sourceItem = itemFromTreePath(tp);
        if(sourceItem == null)
        	return false;

        JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
        tp = dl.getPath();
        targetItem = itemFromTreePath(tp);
        if(targetItem == null)
        	return false;
        
        if(sourceItem.isOneOfMyChildren(targetItem))
        	return false;
                
        return true;
	}
	
	protected Transferable createTransferable(JComponent c) {		
		return new NodesTransferable(null);
	}

	private void buildModel(DefaultTreeModel model, DefaultMutableTreeNode node, Item item) throws Exception {
		
		int pos = 0;
		for(Item i : item.children) {
			DefaultMutableTreeNode modelNode = new DefaultMutableTreeNode(i);
			model.insertNodeInto(modelNode, node, pos++);			
			buildModel(model, modelNode, i);
		}
	}
	
	private void expandedStatusToCore(DefaultTreeModel model, JTree tree, DefaultMutableTreeNode curNode, ArrayList<TreePath> out) {

		TreePath tp = new TreePath(curNode.getPath());
		if(tree.isExpanded(tp))
			out.add(tp);
		
		int n = model.getChildCount(curNode);
		for(int i = 0; i < n; ++i) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)model.getChild(curNode, i);
			expandedStatusToCore(model, tree, node, out);
		}
	}

	protected void exportDone(JComponent source, Transferable data, int action) {
		
        if((action & MOVE) != MOVE)
        	return;

        ArrayList<TreePath> openNodes = new ArrayList<TreePath>();

        JTree tree = (JTree)source;
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel(); 
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)model.getRoot();
        
        expandedStatusToCore(model, tree, rootNode, openNodes);

        Item rootItem = (Item)rootNode.getUserObject();
        rootNode.removeAllChildren();
        model.reload();
        
        try {
        	buildModel(model, rootNode, rootItem);
        }
        catch(Exception e) {
        	System.out.println(e.getMessage());
        }
        
        // This part does not work properly. Not all nodes expand.
        for(TreePath tp : openNodes)
        	tree.expandPath(tp);
    }
	
	public int getSourceActions(JComponent c) {
        return MOVE;
    }
	
	public boolean importData(TransferHandler.TransferSupport support) {
		
		if(!canImport(support))
            return false;
        
		if(sourceItem == null || targetItem == null)
			return false;
		
		// sourceItem - dragging item
		// targetItem - drop inside this item
		Boolean rc = targetItem.dropToMyChildern(sourceItem);
		return rc;
	}
	
	public class NodesTransferable implements Transferable {
		
        DefaultMutableTreeNode[] nodes;
  
        public NodesTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
         }
  
        public Object getTransferData(DataFlavor flavor)
                                 throws UnsupportedFlavorException {
            if(!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);
            return nodes;
        }
  
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }
  
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }		
}
