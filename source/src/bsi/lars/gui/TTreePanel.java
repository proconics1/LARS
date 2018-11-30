package bsi.lars.gui;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import bsi.lars.backend.Backend;
import bsi.lars.backend.BackendInconsistencyException;
import bsi.lars.backend.UpdateCalculationEvent;
import bsi.lars.backend.UpdateCalculationListener;
import bsi.lars.backend.datastore.layers.AssetType;
import bsi.lars.backend.datastore.layers.Domain;
import bsi.lars.backend.datastore.layers.Layer;

/**
 * Element der Graphischen Oberfläche für den geschichteten Baum vom {@link bsi.lars.backend.datastore.layers.Layer} Objekten.
 * 
 *
 */
public class TTreePanel extends JPanel implements Observer, TreeSelectionListener, UpdateCalculationListener {

	private static final long serialVersionUID = 6604670910384451644L;
	
	private JTree tree;
	private Backend backend;

	/**
	 * Erzeuge das Panel
	 */
	public TTreePanel(Backend backend) {
		setLayout(new BorderLayout(0, 0));
		
		this.backend = backend;
		
		backend.addObserver(this);
		
		tree = new JTree(backend);
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.setCellRenderer(new LayerCellRenderer());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		tree.addTreeSelectionListener(this);
		
		add(tree);

	}

	//Observer
	
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof Backend) {
			if(arg instanceof Layer) {
				TreePath path = backend.pathFromLayer((Layer) arg);
				tree.setSelectionPath(path);
				tree.scrollPathToVisible(path);
			}
		}
	}
	
	//TreeSelectionListener
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Object lastPathComponent = e.getPath().getLastPathComponent();
		if(lastPathComponent instanceof Layer) {
			if(((Layer) lastPathComponent).isQuestion()) {
				try {
					backend.selectLayer((Layer) lastPathComponent);
				} catch (BackendInconsistencyException e1) {
					TMainFrame.getInstance().exitOnException(e1);
				}
			}else if(((Layer) lastPathComponent) instanceof Domain) {
				try {
					backend.selectLayer((Layer) lastPathComponent);
				} catch (BackendInconsistencyException e1) {
					TMainFrame.getInstance().exitOnException(e1);
				}
			}else if(((Layer) lastPathComponent) instanceof AssetType) {
				try {
					backend.selectLayer((Layer) lastPathComponent);
				} catch (BackendInconsistencyException e1) {
					TMainFrame.getInstance().exitOnException(e1);
				}
			}else{
				try {
					backend.selectLayer(null);
				} catch (BackendInconsistencyException e1) {
					TMainFrame.getInstance().exitOnException(e1);
				}
			}
		}
	}
	
	public void resetSelection() {
		tree.setSelectionPath(null);
	}
	
	//UpdateCalculationListener
	
	@Override
	public void valueChanged(UpdateCalculationEvent e) {
		tree.repaint();
	}

}
