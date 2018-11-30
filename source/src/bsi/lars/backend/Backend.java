package bsi.lars.backend;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import bsi.lars.answer.Answer;
import bsi.lars.answer.CategoryAnswer;
import bsi.lars.answer.MeasureAnswer;
import bsi.lars.backend.data.Case;
import bsi.lars.backend.data.Comment;
import bsi.lars.backend.data.User;
import bsi.lars.backend.datastore.Asset;
import bsi.lars.backend.datastore.Datastore;
import bsi.lars.backend.datastore.InvalidLayerStackException;
import bsi.lars.backend.datastore.layers.AssetType;
import bsi.lars.backend.datastore.layers.Category;
import bsi.lars.backend.datastore.layers.Domain;
import bsi.lars.backend.datastore.layers.Layer;
import bsi.lars.backend.datastore.layers.Measure;
import bsi.lars.gui.TMainFrame;

/**
 * Diese Klasse enthält die Programmlogik. Die Benutzeroberfläche kommuniziert mit dieser Klasse um Daten zu laden und zu speichern.
 * Zusätzlich ist die Klasse Backend der Oberste Knoten der Struktur, die in {@link Layer} erläutert ist.
 * 
 *
 */
public class Backend extends Observable implements MutableTreeNode, ScoreListener {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.backend.messages"); //$NON-NLS-1$

	private static final boolean CHECKDB = true; //Durch den Baum laufen und auf Konsistenz prüfen
	private Datastore data;
	private Layer currentLayer;
	private Vector<Layer> leafs;
	private Case currentCase;
	private User currentUser;
	private int score = -1;
	
	private static Backend instance;
	
	private Backend() throws InvalidDatabaseStructureException {
		data = Datastore.init(this);
		
		
		
		//Datenbank prüfen
		if(CHECKDB) {
			Vector<Layer> check = new Vector<Layer>();
			check.addAll(Arrays.asList(getAssetTypes()));
			while(check.size() > 0) {
				try {
					check.addAll(Arrays.asList(getElements(check.remove(0))));
				} catch (InvalidLayerStackException e) {
					throw new InvalidDatabaseStructureException(e);
				}
			}
		}
		currentLayer = null;
		
		leafs = new Vector<Layer>();
		for(Layer l : Collections.list(children())) {
			collectLeafs(l, leafs);
		}
		
		//Prüfen, dass nur Kategorien Fragen sind.
		for(Layer l : leafs) {
			if(!(l instanceof Category)) {
				throw new InvalidDatabaseStructureException(l + " ist keine Kategorie.");
			}
		}
	}
	
	public static Backend init() throws InvalidDatabaseStructureException {
		if(instance == null) {
			instance = new Backend();
		}
		return instance;
	}
	
	public static Backend getInstance() {
		return instance;
	}
	
	private void collectLeafs(Layer l, Vector<Layer> leafs) {
		if(l.isQuestion()) {//isLeaf()) {
			leafs.add(l);
		}else{
			for(Layer ll : Collections.list(l.children())) {
				collectLeafs(ll, leafs);
			}
		}
	}

	public Layer getCurrentLayer() {
		return currentLayer;
	}
	
	public User getCurrentUser() {
		return currentUser;
	}
	
	public Case getCurrentCase() {
		return currentCase;
	}
	
	/**
	 * Ruft die Kinder an einer bestimmten Stelle ab
	 * @param path Pfad, an dem die Kinder abgerufen werden sollen
	 * @return Gibt die Kinder des Elementes path zurück
	 * @throws InvalidLayerStackException
	 */
	public Layer[] getElements(Layer path) throws InvalidLayerStackException {
		if(path == null) {
			return data.getAssetTypes();
		}else if(path instanceof AssetType) {
			return data.getDomains((AssetType) path);
		}else if(path instanceof Domain) {
			return data.getCategories((Domain) path);
		}else if(path instanceof Category) {
			return data.getMeasures((Category) path);
		}else if(path instanceof Measure) {
			return data.getMeasures((Measure) path);
		}else{
			throw new IllegalArgumentException("Unbekannte Layer Klasse " + path.getClass());
		}
	}

	public Layer[] getCurrentElements() throws BackendInconsistencyException {
		try {
			return getElements(currentLayer);
		} catch (InvalidLayerStackException e) {
			throw new BackendInconsistencyException("Ungültige Schichtkombination");
		}
	}
	
	public void selectLayer(Layer l) throws BackendInconsistencyException {
		if(currentLayer != null && currentLayer.equals(l)) {
			return;
		}
//INFO: reenable the lines marked with 1 to stop the user selecting anything that is not allowed
//1		if(l != null) {
//1			if(l.isQuestion()) {//isLeaf()) {
				currentLayer = l;
	//		if(l.isRoot()) {
	//			currentLayer = l;
	//		}else if(l.getParent().equals(currentLayer)) {
	//			currentLayer = l;
	//		}else if(nextQuestion(l) != null){
	//			currentLayer = nextQuestion(l);
//1			}else{
//1				throw new BackendInconsistencyException("Selected Layer that should not be selectible.");
//1			}
//1		}
//INFO: reenable the lines marked with 1 to stop the user selecting anything that is not allowed
		setChanged();
		notifyObservers(currentLayer);
	}
	
	
	

	public AssetType[] getAssetTypes() {
		try {
			return data.getAssetTypes();
		} catch (InvalidLayerStackException e) {
			TMainFrame.getInstance().exitOnException(e);
			return new AssetType[0];
		}
	}
	
	
	//TreeNode
	
	@Override
	public Enumeration<Layer> children() {
		return new Vector<Layer>(Arrays.asList(getAssetTypes())).elements();
	}
	
	@Override
	public boolean getAllowsChildren() {
		return true;
	}
	
	@Override
	public TreeNode getChildAt(int childIndex) {
		return getAssetTypes()[childIndex];
	}
	
	@Override
	public int getChildCount() {
		return getAssetTypes().length;
	}
	
	@Override
	public int getIndex(TreeNode node) {
		return Arrays.asList(getAssetTypes()).indexOf(node);
	}
	
	@Override
	public TreeNode getParent() {
		return this;
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}

	//MutableTreeNode
	
	@Override
	public void insert(MutableTreeNode child, int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(MutableTreeNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUserObject(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeFromParent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setParent(MutableTreeNode newParent) {
		throw new UnsupportedOperationException();
	}

	public TreePath pathFromLayer(Layer arg) {
		TreePath result = new TreePath(this);
		Vector<Layer> reversepath = new Vector<Layer>();
		reversepath.add(arg);
		while(reversepath.lastElement().getParent() != null) {
			reversepath.add(reversepath.lastElement().getParent());
		}
		for (int i = reversepath.size() - 1 ; i >= 0 ; i--) {
			Layer l = reversepath.get(i);
			result = result.pathByAddingChild(l);
		}
		return result;
	}

	public Vector<Layer> getAllCategories() {
		return leafs;
	}
	
	public Layer nextLayer() {
		if(currentLayer == null) {
			return leafs.get(0);
		}else if(currentLayer instanceof AssetType) {
			return (Layer) ((AssetType) currentLayer).getChildAt(0).getChildAt(0);
		}else if(currentLayer instanceof Domain) {
			return (Layer) ((Domain) currentLayer).getChildAt(0);
		}
		int ix = leafs.indexOf(currentLayer);
		if(++ix == leafs.size()) {
			ix = 0;
		}
		return leafs.get(ix);
	}

	public Layer previousLayer() {
		if(currentLayer == null) {
			return leafs.get(leafs.size() - 1);
		}else if(currentLayer instanceof AssetType) {
			int ix = Backend.getInstance().getIndex(currentLayer);
//			int ix = currentLayer.getParent().getIndex(currentLayer);
			--ix;
			if(ix < 0) {
				ix = Backend.getInstance().getChildCount() - 1;
			}
			AssetType at = (AssetType) Backend.getInstance().getChildAt(ix);
			Domain d = (Domain) at.getChildAt(at.getChildCount() - 1);
			return (Category) d.getChildAt(d.getChildCount() - 1);
		}else if(currentLayer instanceof Domain) {
			int ix = currentLayer.getParent().getIndex(currentLayer);
			--ix;
			if(ix < 0) {
				//zum vorherigen AssetType
				int atIx = Backend.getInstance().getIndex(currentLayer.getParent());
				--atIx;
				if(atIx < 0) {
					atIx = Backend.getInstance().getChildCount() - 1;
				}
				AssetType at = (AssetType) Backend.getInstance().getChildAt(atIx);
				Domain d = (Domain) at.getChildAt(at.getChildCount() - 1);
				return (Category) d.getChildAt(d.getChildCount() - 1);
			}else{
				Domain d = (Domain) currentLayer.getParent().getChildAt(ix);
				return (Category) d.getChildAt(d.getChildCount() - 1);
			}
		}
		int ix = leafs.indexOf(currentLayer);
		if(--ix <= -1) {
			ix = leafs.size() - 1;
		}
		return leafs.get(ix);
	}
	
	public Layer nextUnfinishedLayer() {
		if(currentLayer == null || currentLayer instanceof AssetType || currentLayer instanceof Domain) {
			currentLayer = nextLayer();
			currentLayer = previousLayer();
		}
		int ix = leafs.indexOf(currentLayer);
		Layer result = null;
		for(int i = 0 ; i < leafs.size() ; ++i) {
			if(++ix == leafs.size()) {
				ix = 0;
			}
			if(!leafs.get(ix).isFullyAnswered()) {
				result = leafs.get(ix);
				break;
			}
		}
		
		return result;
	}
	
	public Layer prevUnfinishedLayer() {
		if(currentLayer == null || currentLayer instanceof AssetType || currentLayer instanceof Domain) {
			currentLayer = previousLayer();
			currentLayer = nextLayer();
		}
		int ix = leafs.indexOf(currentLayer);
		Layer result = null;
		for(int i = leafs.size() - 1 ; i >= 0 ; --i) {
			if(--ix == -1) {
				ix = leafs.size() - 1;
			}
			if(!leafs.get(ix).isFullyAnswered()) {
				result = leafs.get(ix);
				break;
			}
		}
		
		return result;
	}
	
	
	
	public User[] getUsers() {
		return data.getUsers();
	}

	public Case[] getCases() {
		return data.getCases();
	}

	public Comment getComment(Layer layer) throws InvalidDatabaseStructureException, NoCaseSelectedException, NoUserSelectedException {
		checkCase();
		if(layer instanceof Category) {
			return data.getCategoryComment((Category) layer, currentCase.getId());
		}else if(layer instanceof Measure) {
			return data.getMeasureComment((Measure) layer, currentCase.getId());
		}else{
			throw new IllegalArgumentException(layer.getClass() + " darf keine Antwort haben.");
		}
	}

	private void checkCase() throws NoCaseSelectedException, NoUserSelectedException {
		checkUser();
		if(currentCase == null) {
			throw new NoCaseSelectedException();
		}
	}

	public void selectCase(Case c) {
		this.currentCase = c;
	}

	public void createCase(String newCaseName) throws NoUserSelectedException {
		checkUser();
		data.createCase(newCaseName, currentUser, System.currentTimeMillis());
	}

	public void deleteCase(Case _case) {
		data.deleteCase(_case);
	}

	private void checkUser() throws NoUserSelectedException {
		if(currentUser == null) {
			throw new NoUserSelectedException();
		}
	}
	
	public void createUser(String userLongName, String userName) {
		data.createUser(userLongName, userName);
	}
	
	public void deleteUser(User user) {
		data.deleteUser(user);
	}

	public void selectUser(User user) {
		currentUser = user;
	}

	public void writeAnswer(CategoryAnswer answer) throws NoCaseSelectedException, NoUserSelectedException {
		storeAnswer(answer);
	}
	
	private void storeAnswer(Answer answer) throws NoCaseSelectedException, NoUserSelectedException {
		checkCase();
		if(answer instanceof CategoryAnswer) {
			data.storeCategoryAnswer((CategoryAnswer) answer, currentCase.getId(), currentUser.getId(), System.currentTimeMillis());
		}else if(answer instanceof MeasureAnswer) {
			data.storeMeasureAnswer((MeasureAnswer) answer, currentCase.getId(), currentUser.getId(), System.currentTimeMillis());
		}
		for(Answer a : answer.getSubanswers()) {
			storeAnswer(a);
		}
	}


	public Asset[] getAssets(AssetType assetType, Domain domain) throws NoCaseSelectedException, NoUserSelectedException {
		checkCase();
		try {
			return data.getAssets(assetType, domain, currentCase);
		} catch (InvalidLayerStackException e) {
			TMainFrame.getInstance().exitOnException(e);
			return new Asset[0];
		}
	}
	
	public void writeAsset(Asset asset) throws NoCaseSelectedException, NoUserSelectedException {
		checkCase();
		data.storeAsset(asset, currentCase.getId());
	}

	public void deleteAsset(Asset asset) throws NoCaseSelectedException, NoUserSelectedException {
		checkCase();
		data.deleteAsset(asset, currentCase.getId());
	}

	public String[] getStati() {
		return new String[]{RESOURCES.getString("Backend.done"), RESOURCES.getString("Backend.partially"), RESOURCES.getString("Backend.notdone"), RESOURCES.getString("Backend.notneeded")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	public String getUnanswered() {
		return RESOURCES.getString("Backend.unanswered");
	}
	
	public String getStatusDefaultComment(String status) {
		if(status.equals(RESOURCES.getString("Backend.done"))) {
			return RESOURCES.getString("Backend.done.comment");
		}else if(status.equals(RESOURCES.getString("Backend.partially"))) {
			return RESOURCES.getString("Backend.partially.comment");
		}else if(status.equals(RESOURCES.getString("Backend.notdone"))) {
			return RESOURCES.getString("Backend.notdone.comment");
		}else if(status.equals(RESOURCES.getString("Backend.notneeded"))) {
			return RESOURCES.getString("Backend.notneeded.comment");
		}
		throw new IllegalAccessError("Nicht unterstützter Status.");
	}

	public String getUserName(int user_id) throws InvalidDatabaseStructureException {
		User user = data.getUser(user_id);
		if(user == null) {
			throw new InvalidDatabaseStructureException("Kein Benutzer für Id " + user_id);
		}else{
			return user.getLongname();
		}
	}
	
	//ScoreListener
	
	@Override
	public void valueChanged(int score) {
		this.score = score;
	}
	
	public int getScore() {
		return score;
	}
}
