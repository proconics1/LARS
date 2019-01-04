package bsi.lars.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;

import bsi.lars.backend.Backend;
import bsi.lars.backend.BackendInconsistencyException;
import bsi.lars.backend.Config;
import bsi.lars.backend.FileFormatFilter;
import bsi.lars.backend.InvalidDatabaseStructureException;
import bsi.lars.backend.NoCaseSelectedException;
import bsi.lars.backend.NoUserSelectedException;
import bsi.lars.backend.ReportGenerator;
import bsi.lars.backend.ScoreCalculator;
import bsi.lars.backend.ScoreListener;
import bsi.lars.backend.UpdateCalculationEvent;
import bsi.lars.backend.UpdateCalculationListener;
import bsi.lars.backend.datastore.DataBase;
import bsi.lars.backend.datastore.InvalidLayerStackException;
import bsi.lars.backend.datastore.QueryBuilder;
import bsi.lars.backend.datastore.layers.AssetType;
import bsi.lars.backend.datastore.layers.Category;
import bsi.lars.backend.datastore.layers.Domain;
import bsi.lars.backend.datastore.layers.Layer;
import bsi.lars.backend.reports.AssetTypeDomainStatus;
import bsi.lars.backend.reports.DomainStatus;
import bsi.lars.backend.reports.ICSStatus;
import bsi.lars.backend.reports.PriorizedMeasures;
import bsi.lars.backend.reports.Report;

import java.awt.Toolkit;

/**
 * Hauptklasse. Baut die graphische Oberfläche auf und initialisiert die Logik.
 * 
 *
 */
public class TMainFrame extends JFrame implements Observer, ActionListener, WindowListener, ScoreListener {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.messages"); //$NON-NLS-1$

//	private static final String VERSION = "1.0"; //$NON-NLS-1$

	private static final long serialVersionUID = 4437544711040307907L;

	private static final String LARS_LICENSE_FILENAME = "LARS-ICS-1.0.BSI.txt";
	private static final String JAVA_LICENSE_FILENAME = "Oracle_BCL_Java_SE.txt";
	
	private JPanel contentPane;
	
	private Backend backend;
	private JLabel lblStatus;

	private JPanel mainPanel;

	private JPanel centerPanel;

	private CategoryPanel category;

	private TTreePanel treePanel;
	
	private ScoreCalculator scoreCalculator;

	//Menu Items
	private JMenuItem mntmCaseManager;
	private JMenuItem mntmUserManager;
	private JMenuItem mntmAssetManager;
	
	//Berichte
	private JMenuItem	mntmGenerateReport_ICSStatus,
						mntmGenerateReport_AssetTypeDomainStatus,
						mntmGenerateReport_DomainStatus,
						mntmGenerateReport_PriorizedMeasures,
						mntmAllReports;

	private JMenuItem mntmExit;
	private JMenuItem mntmAbout;

	//Next/Previous Buttons
	private JButton btnNext;
	private JButton btnPrevious;
	private JButton btnNextUnfinished;
	private JButton btnPrevUnfinished;

	private static TMainFrame instance;
	
	/**
	 * Starte das Programm
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		URL jarPath = TMainFrame.class.getProtectionDomain().getCodeSource().getLocation();
		final URL larsLicenseURL = new URL(jarPath, LARS_LICENSE_FILENAME);
		final URL javaLicenseURL = new URL(jarPath, JAVA_LICENSE_FILENAME);

		// Display License information and security warning
		String licenseHead = RESOURCES.getString("TMainFrame.warningHead");
		String licenseMiddle = RESOURCES.getString("TMainFrame.warningMiddle");
		String licenseTail = RESOURCES.getString("TMainFrame.warningTail");
		String licenseText = licenseHead + larsLicenseURL.toString() + licenseMiddle + javaLicenseURL.toString() + licenseTail;		

		JEditorPane ep = new JEditorPane("text/html", licenseText);
		ep.addHyperlinkListener(new HyperlinkListener() {
	        @Override
	        public void hyperlinkUpdate(HyperlinkEvent e) {
	        	if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
	        }
		});
		ep.setEditable(false);
		if (JOptionPane.showConfirmDialog(null, ep, "LARS Lizenz",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null) != JOptionPane.OK_OPTION) {
			System.exit(0);
		}

		// Start main program
		initializeProgram();
		
		try{
			QueryBuilder.getInstance();
		}catch (Exception e){
			JOptionPane.showMessageDialog(null, "<html>" + e.getMessage() + "<br /><br />" + e.toString().replaceAll("\n", "<br />") + "<br /><br />Exiting.</html>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			System.exit(0);
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TMainFrame frame = new TMainFrame();
					frame.setVisible(true);
					
					frame.refresh();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void initializeProgram() {
		try {
			look_and_feel();
		} catch (Exception e1) {
			System.out.println(RESOURCES.getString("TMainFrame.couldNotChangeLNF")); //$NON-NLS-1$
		}
		
		Locale.setDefault(Locale.GERMANY);
		
		//Init DB
		String dbPath = Config.getDataBasePath();
		if(dbPath == null) {
			dbPath = "lars.db3"; //$NON-NLS-1$
		}
		boolean aborted = false;
		boolean finished = false;
		do{
			try {
				DataBase.getInstance().initDB(dbPath);
				Config.setDataBasePath(dbPath);
				finished = true;
			} catch (Exception e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "LARS ICS Datenbank (*.db3)"; //$NON-NLS-1$
					}
					
					@Override
					public boolean accept(File f) {
						if(f.isDirectory()) {
							return true;
						}else{
							return f.getName().endsWith(".db3"); //$NON-NLS-1$
						}
					}
				});
				switch(chooser.showOpenDialog(null)) {
				case JFileChooser.CANCEL_OPTION:
					finished = true;
					aborted = true;
					break;
				case JFileChooser.APPROVE_OPTION:
					dbPath = chooser.getSelectedFile().getPath();
					break;
				}
			}
		}while(!finished);
		if(aborted) {
			System.exit(0);
		}
	}

	public static TMainFrame getInstance() {
		return instance;
	}
	
	Vector<UpdateCalculationListener> updateCalculationListeners = new Vector<UpdateCalculationListener>();

	public void addUpdateCalculationListener(UpdateCalculationListener ucl) {
		updateCalculationListeners.add(ucl);
	}
	
	private void notifyUpdateCalculationListeners() {
		for(UpdateCalculationListener ucl : updateCalculationListeners) {
			ucl.valueChanged(new UpdateCalculationEvent(this));
		}
	}
	
	/**
	 * Erzeuge das Fenster
	 * @throws InvalidDatabaseStructureException 
	 */
	private TMainFrame() throws InvalidDatabaseStructureException {
		setIconImage(Toolkit.getDefaultToolkit().getImage(TMainFrame.class.getResource("/resources/Lars.png"))); //$NON-NLS-1$
		if(instance == null) {
			instance = this;
		}else{
			throw new IllegalAccessError();
		}
		setTitle(RESOURCES.getString("TMainFrame.this.title")); //$NON-NLS-1$
		if(!designerMode()) {//Wenn vom WindowBuilder instanziert
			backend = Backend.init();
		}
		
		if(backend.getUsers().length == 0) {
			openUserManager(null);
		}
		if(backend.getUsers().length == 0) {
			JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.noUserCreatedExit")); //$NON-NLS-1$
			System.exit(0);
		}
		if(backend.getCurrentUser() == null) {
			openUserManager(null);
		}
		if(backend.getCurrentUser() == null) {
			System.exit(0);
		}
		if(backend.getCurrentCase() == null) {
			openCaseManager(null);
		}
		if(backend.getCurrentCase() == null) {
			System.exit(0);
		}
		
		// Originally shown here, but now moved to main() so that license will get displayed
		// at program start.
		// JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.warning")); //$NON-NLS-1$
		
		//Prüfe den Antwortstatus aller Kategorien
		for(Layer c : backend.getAllCategories()) {
			if(c instanceof Category) {
				try {
					new CategoryPanel((Category) c);
				} catch (NoCaseSelectedException e) {
				} catch (NoUserSelectedException e) {
				}
			}
		}
		
		scoreCalculator = new ScoreCalculator(backend);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setSize(1280, 700);
		setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu(RESOURCES.getString("TMainFrame.mnFile.text")); //$NON-NLS-1$
		menuBar.add(mnFile);
		
		mntmCaseManager = new JMenuItem(RESOURCES.getString("TMainFrame.mntmCaseManager.text")); //$NON-NLS-1$
		mnFile.add(mntmCaseManager);
		
		mntmUserManager = new JMenuItem(RESOURCES.getString("TMainFrame.mntmUserManager.text")); //$NON-NLS-1$
		mnFile.add(mntmUserManager);
		
		mntmAssetManager = new JMenuItem(RESOURCES.getString("TMainFrame.mntmAssetmanager.text")); //$NON-NLS-1$
		mnFile.add(mntmAssetManager);
		
		mnReports = new JMenu(RESOURCES.getString("TMainFrame.mnReports.text")); //$NON-NLS-1$
		mnFile.add(mnReports);

		//Einträge für die verschiedenen Berichte
		mntmGenerateReport_ICSStatus = new JMenuItem(RESOURCES.getString("TMainFrame.mntmGenerateReport_ICSStatus.text")); //$NON-NLS-1$
		mnReports.add(mntmGenerateReport_ICSStatus);
		mntmGenerateReport_ICSStatus.addActionListener(this);
		
		mntmGenerateReport_AssetTypeDomainStatus = new JMenuItem(RESOURCES.getString("TMainFrame.mntmGenerateReport_AssetTypeDomainStatus.text")); //$NON-NLS-1$
		mnReports.add(mntmGenerateReport_AssetTypeDomainStatus);
		mntmGenerateReport_AssetTypeDomainStatus.addActionListener(this);

		mntmGenerateReport_DomainStatus = new JMenuItem(RESOURCES.getString("TMainFrame.mntmGenerateReport_DomainStatus.text")); //$NON-NLS-1$
		mnReports.add(mntmGenerateReport_DomainStatus);
		mntmGenerateReport_DomainStatus.addActionListener(this);

		mntmGenerateReport_PriorizedMeasures = new JMenuItem(RESOURCES.getString("TMainFrame.mntmGenerateReport_PriorizedMeasures.text")); //$NON-NLS-1$
		mnReports.add(mntmGenerateReport_PriorizedMeasures);
		mntmGenerateReport_PriorizedMeasures.addActionListener(this);
		
		if(!Config.isOpenReportAfterCreation()) {
			mntmAllReports = new JMenuItem(RESOURCES.getString("TMainFrame.mntmAlleBerichte.text")); //$NON-NLS-1$
			mnReports.add(mntmAllReports);
			mntmAllReports.addActionListener(this);
		}
		
		mntmExit = new JMenuItem(RESOURCES.getString("TMainFrame.mntmExit.text")); //$NON-NLS-1$
		mnFile.add(mntmExit);
		
		mntmCaseManager.addActionListener(this);
		mntmUserManager.addActionListener(this);
		mntmAssetManager.addActionListener(this);
		mntmExit.addActionListener(this);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		menuBar.add(horizontalGlue);
		
		JMenu mnHelp = new JMenu(RESOURCES.getString("TMainFrame.mnHelp.text")); //$NON-NLS-1$
		menuBar.add(mnHelp);
		
		mntmAbout = new JMenuItem(RESOURCES.getString("TMainFrame.mntmAbout.text")); //$NON-NLS-1$
		mntmAbout.addActionListener(this);
		mnHelp.add(mntmAbout);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel statusBar = new JPanel();
		statusBar.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(statusBar, BorderLayout.SOUTH);
		GridBagLayout gbl_statusBar = new GridBagLayout();
		gbl_statusBar.columnWidths = new int[] {1248, 1, 0};
		gbl_statusBar.rowHeights = new int[]{14, 0};
		gbl_statusBar.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_statusBar.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		statusBar.setLayout(gbl_statusBar);
		
		lblStatus = new JLabel(RESOURCES.getString("TMainFrame.lblStatus.text")); //$NON-NLS-1$
		GridBagConstraints gbc_lblStatus = new GridBagConstraints();
		gbc_lblStatus.anchor = GridBagConstraints.NORTH;
		gbc_lblStatus.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblStatus.insets = new Insets(3, 5, 3, 5);
		gbc_lblStatus.gridx = 0;
		gbc_lblStatus.gridy = 0;
		statusBar.add(lblStatus, gbc_lblStatus);
		
		ResultLight statusLight = new ResultLight();
		Dimension lightSize = new Dimension(90, 14);
		statusLight.setMinimumSize(lightSize);
		statusLight.setPreferredSize(lightSize);
		GridBagConstraints gbc_lblLight = new GridBagConstraints();
		gbc_lblLight.anchor = GridBagConstraints.WEST;
		gbc_lblLight.fill = GridBagConstraints.VERTICAL;
		gbc_lblLight.gridx = 1;
		gbc_lblLight.gridy = 0;
		statusBar.add(statusLight, gbc_lblLight);
		
		JSplitPane splitawayTree = new JSplitPane();
		contentPane.add(splitawayTree, BorderLayout.CENTER);
		
		mainPanel = new JPanel();
		splitawayTree.setRightComponent(mainPanel);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		treePanel = new TTreePanel(backend);
		JScrollPane scrollTree = new JScrollPane(treePanel);
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout(0, 0));
		leftPanel.add(scrollTree);
		splitawayTree.setLeftComponent(leftPanel);
		
		JPanel logoPanel = new JPanel();
		logoPanel.setBackground(Color.WHITE);
		logoPanel.setBorder(UIManager.getBorder("ScrollPane.border")); //$NON-NLS-1$
		
		leftPanel.add(logoPanel, BorderLayout.SOUTH);
		GridBagLayout gbl_logoPanel = new GridBagLayout();
		gbl_logoPanel.columnWidths = new int[]{0};
		gbl_logoPanel.rowHeights = new int[]{0};
		gbl_logoPanel.columnWeights = new double[]{0.0, 1.0};
		gbl_logoPanel.rowWeights = new double[]{0.0};
		logoPanel.setLayout(gbl_logoPanel);
		
		JLabel lblLogo = new JLabel();
		lblLogo.setBorder(UIManager.getBorder("ComboBox.border")); //$NON-NLS-1$
		lblLogo.setBackground(Color.WHITE);
		lblLogo.setIcon(new ImageIcon(TMainFrame.class.getResource("/resources/Combined.png"))); //$NON-NLS-1$
		
		GridBagConstraints gbc_lblLogo = new GridBagConstraints();
		gbc_lblLogo.anchor = GridBagConstraints.WEST;
		gbc_lblLogo.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblLogo.gridx = 1;
		gbc_lblLogo.gridy = 0;
		logoPanel.add(lblLogo, gbc_lblLogo);
		
		splitawayTree.setDividerLocation(286);
		
		backend.addObserver(this);
		
		scoreCalculator.addScoreListener(statusLight);
		scoreCalculator.addScoreListener(this);
		
		addUpdateCalculationListener(scoreCalculator);
		addUpdateCalculationListener(treePanel);

		notifyUpdateCalculationListeners();
		
		try {
			init();
			refresh();
		} catch (BackendInconsistencyException e) {
			exitOnException(e);
		}
	}

	/**
	 * Prüfe, ob der WindowBuilder verwendet wird
	 * @return Gibt true zurück, wenn die graphische Oberfläche in WindowBuilder geöffnet wird
	 */
	private boolean designerMode() {
		java.io.StringWriter sw = new java.io.StringWriter();
		java.io.PrintWriter pw = new java.io.PrintWriter(sw);
		new RuntimeException("Nur, um den Stacktrace zu erhalten.").printStackTrace(pw); //$NON-NLS-1$
		return sw.toString().contains("com.instantiations.designer"); //$NON-NLS-1$
	}

	protected void openCaseManager(JFrame parent) {
		CaseManagerDialog caseManagerDialog = new CaseManagerDialog(parent);
		caseManagerDialog.setLocationRelativeTo(parent);
		caseManagerDialog.setVisible(true);
		
		//Prüfe den Antwortstatus aller Kategorien
		for(Layer c : backend.getAllCategories()) {
			if(c instanceof Category) {
				try {
					new CategoryPanel((Category) c);
				} catch (NoCaseSelectedException e) {
				} catch (NoUserSelectedException e) {
				}
			}
		}
		
		notifyUpdateCalculationListeners();
	}

	protected void openUserManager(JFrame parent) {
		UserManagerDialog userManagerDialog = new UserManagerDialog(parent);
		userManagerDialog.setLocationRelativeTo(parent);
		userManagerDialog.setVisible(true);
	}

	protected void openAssetManager(JFrame parent) {
		treePanel.resetSelection();
		try {
			backend.selectLayer(null);
		} catch (BackendInconsistencyException e) {
			JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.errorOnLastSave") + "\n" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		AssetManagerDialog assetManagerDialog = new AssetManagerDialog(parent);
		assetManagerDialog.setLocationRelativeTo(parent);
		assetManagerDialog.setVisible(true);
	}
	
	public static void openAssetManager_UI(JFrame parentComponent) {
		AssetManagerDialog assetManagerDialog = new AssetManagerDialog(parentComponent);
		assetManagerDialog.setLocationRelativeTo(parentComponent);
		assetManagerDialog.setVisible(true);
	}

	protected void exit() {
		try {
			backend.selectLayer(null);
		} catch (BackendInconsistencyException e) {
			JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.errorOnLastSave") + "\n" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		System.exit(0);
	}
	
	protected void about() {
		JPanel about = new JPanel();
		about.setLayout( new BoxLayout(about, BoxLayout.Y_AXIS) );

		URL jarPath = TMainFrame.class.getProtectionDomain().getCodeSource().getLocation();
		URL javaLicenseURL = null;

		try {
			javaLicenseURL = new URL(jarPath, JAVA_LICENSE_FILENAME);
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		}
		
		String aboutHead = RESOURCES.getString("TMainFrame.ABOUTHead");
		String aboutTail = RESOURCES.getString("TMainFrame.ABOUTTail");
		String aboutText = aboutHead + javaLicenseURL.toString() + aboutTail;
		
		JEditorPane ep = new JEditorPane("text/html", aboutText);
		ep.addHyperlinkListener(new HyperlinkListener() {
	        @Override
	        public void hyperlinkUpdate(HyperlinkEvent e) {
	        	if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
	        }
		});
		ep.setEditable(false);
		about.add(ep);
		
		JPanel p = new JPanel(new FlowLayout());
		p.setBackground(Color.WHITE);
		p.add(
				new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(TMainFrame.class.getResource("/resources/Sirrixlogo.png")).getScaledInstance(250, -1, Image.SCALE_SMOOTH))) //$NON-NLS-1$
		);
		p.add(
				new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(TMainFrame.class.getResource("/resources/Lars.png")))) //$NON-NLS-1$
		);
		p.add(
				new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(TMainFrame.class.getResource("/resources/ProconicsIcon.png")))) //$NON-NLS-1$
		);
		about.add(p);
		JOptionPane.showMessageDialog(this, about);
	}

	protected JLabel getLblStatus() {
		return lblStatus;
	}
	
	public void init() throws BackendInconsistencyException {
		
		mainPanel.removeAll();

		centerPanel = new JPanel();
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel nextPanel = new JPanel();
		mainPanel.add(nextPanel, BorderLayout.SOUTH);
		nextPanel.setLayout(new BorderLayout(0, 0));
		
		btnNext = new JButton(RESOURCES.getString("TMainFrame.btnNext.text")); //$NON-NLS-1$
		btnNext.setToolTipText(RESOURCES.getString("TMainFrame.btnNext.toolTipText")); //$NON-NLS-1$
		
		JPanel navPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) navPanel.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		
		btnPrevious = new JButton(RESOURCES.getString("TMainFrame.btnPrevious.text")); //$NON-NLS-1$
		btnPrevious.setToolTipText(RESOURCES.getString("TMainFrame.btnPrevious.toolTipText")); //$NON-NLS-1$
		navPanel.add(btnPrevious);
		navPanel.add(btnNext);
		nextPanel.add(navPanel, BorderLayout.WEST);
		
		JPanel navPanel2 = new JPanel();
		FlowLayout flowLayout2 = (FlowLayout) navPanel2.getLayout();
		flowLayout2.setVgap(0);
		flowLayout2.setHgap(0);
		
		btnPrevUnfinished = new JButton(RESOURCES.getString("TMainFrame.btnPrevunfinished.text")); //$NON-NLS-1$
		btnPrevUnfinished.setToolTipText(RESOURCES.getString("TMainFrame.btnPrevUnfinished.toolTipText")); //$NON-NLS-1$
		btnNextUnfinished = new JButton(RESOURCES.getString("TMainFrame.btnNextunfinished.text")); //$NON-NLS-1$
		btnNextUnfinished.setToolTipText(RESOURCES.getString("TMainFrame.btnNextUnfinished.toolTipText")); //$NON-NLS-1$
		navPanel2.add(btnPrevUnfinished);
		navPanel2.add(btnNextUnfinished);
		nextPanel.add(navPanel2, BorderLayout.EAST);
		
		btnNext.addActionListener(this);
		btnPrevious.addActionListener(this);
		btnNextUnfinished.addActionListener(this);
		btnPrevUnfinished.addActionListener(this);
	}
	
	public void refresh() throws BackendInconsistencyException {
		try{
			centerPanel.removeAll();
			Layer currentLayer = backend.getCurrentLayer();
			if(currentLayer != null) {
				getLblStatus().setText(currentLayer.toString());
			}else{
				getLblStatus().setText(RESOURCES.getString("TMainFrame.lblStatus.text")); //$NON-NLS-1$
			}
			
			//Wenn das zuletzt gewählte Element eine Kategorie war, speichere die Eingaben
			if(category != null) {
				//Sammle Ergebnisse
				backend.writeAnswer(category.getAnswer());
				category = null;
			}
			
			if(currentLayer instanceof Category) {
				category = new CategoryPanel((Category) currentLayer);
				category.addUpdateCalculationListener(treePanel);
				category.addUpdateCalculationListener(scoreCalculator);
				
				centerPanel.add(new JScrollPane(category));
			}else if(currentLayer instanceof Domain) {
				centerPanel.add(new JScrollPane(new DomainPanel((Domain) currentLayer)));
			}else{
				JLabel welcome = new JLabel(RESOURCES.getString("TMainFrame.nothingSelected"));
				welcome.setVerticalAlignment(JLabel.TOP);
				centerPanel.add(welcome, BorderLayout.CENTER);
			}
			revalidate();
		}catch(NoCaseSelectedException e) {
			JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.noCaseSelected")); //$NON-NLS-1$
		} catch (NoUserSelectedException e) {
			JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.noUserSelected")); //$NON-NLS-1$
		}
	}

	private static void look_and_feel() throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		if(System.getProperty("os.name").toLowerCase().contains("linux")) { //$NON-NLS-1$ //$NON-NLS-2$
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"); //$NON-NLS-1$
		}
	}
	
	//Observer
	
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof Backend) {
			if(arg == null || arg instanceof Layer) {
				try {
					refresh();
				} catch (BackendInconsistencyException e) {
					exitOnException(e);
				}
			}
		}
	}
	
	
	public void exitOnException(BackendInconsistencyException e) {
		_exitOnException(e);
	}
	
	public void exitOnException(InvalidDatabaseStructureException e) {
		_exitOnException(e);
	}
	
	public void exitOnException(InvalidLayerStackException e) {
		_exitOnException(e);
	}
	
	public void exitOnException(SQLException e) {
		_exitOnException(e);
	}
		
	private void _exitOnException(Exception e) {
		JOptionPane.showMessageDialog(instance, new JScrollPane(new JTextArea(RESOURCES.getString("TMainFrame.exceptionandExit") //$NON-NLS-1$
				+ "\n" + e.getMessage() + "\n" + (e.getStackTrace().length > 0 ? e.getStackTrace()[0] : ""))), RESOURCES.getString("TMainFrame.errorMessageTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		System.exit(0);
	}

	//Report
	private void generateReport(Report report) {
		try {//Eingaben speichern
			Layer l = backend.getCurrentLayer();
			backend.selectLayer(null);
			backend.selectLayer(l);
		} catch (BackendInconsistencyException e) {
			JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.errorOnLastSave") + "\n" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		JFileChooser choose = new JFileChooser();
		choose.setMultiSelectionEnabled(false);
		choose.setAcceptAllFileFilterUsed(false);
		for(final ReportGenerator.formats format : ReportGenerator.formats.values()) {
			choose.addChoosableFileFilter(new FileFormatFilter(format));
		}
		choose.setSelectedFile(new File(escape(report.getDefaultFileName() + "-" + backend.getCurrentCase().getName()))); //$NON-NLS-1$
		int result = choose.showSaveDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			File destinationFile = choose.getSelectedFile();
			
			ReportGenerator.formats format = null;
			
			FileFilter fileFilter = choose.getFileFilter();
			if(fileFilter instanceof FileFormatFilter) {
				format = ((FileFormatFilter) fileFilter).getFormat();
			}
			
			if(format == null) {
				for(ReportGenerator.formats frmt : ReportGenerator.formats.values()) {
					String substring = destinationFile.getName().substring(destinationFile.getName().lastIndexOf(".") + 1); //$NON-NLS-1$
					if(substring.toLowerCase().equals(frmt.name())) {
						format = ReportGenerator.formats.valueOf(substring);
						break;
					}
				}
				if(format == null) {
					JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.noReportFormatSelected"), RESOURCES.getString("TMainFrame.errorMessageTitle"), JOptionPane.DEFAULT_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
			}
			
			String extension = "." + format.name(); //$NON-NLS-1$
			if(!destinationFile.getName().endsWith(extension)) {
				destinationFile = new File(destinationFile.getAbsolutePath() + extension);
			}
			boolean stop = false;
			if(destinationFile.exists()) {
				result = JOptionPane.showConfirmDialog(this, RESOURCES.getString("TMainFrame.OverwriteReport") + "\n" + destinationFile.getName(), RESOURCES.getString("TMainFrame.questionMessageTitle"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if(result != JOptionPane.YES_OPTION) {
					stop = true;
				}
			}
			if(!stop) {
				try {
					ReportGenerator.generateReport(report, destinationFile, format);
					if(Config.isOpenReportAfterCreation()) {
						Desktop.getDesktop().open(destinationFile);
					}
					JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.reportDone")); //$NON-NLS-1$
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.reportFailed") + "\n" + e.getMessage(), RESOURCES.getString("TMainFrame.errorMessageTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		}
	}
	
	private String escape(String str) {
		return str.replaceAll("[/\\()<>]", "_"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	//ActionListener
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(mntmCaseManager)){
			openCaseManager(this);
		}else if(e.getSource().equals(mntmUserManager)) {
			openUserManager(this);
		}else if(e.getSource().equals(mntmAssetManager)) {
			openAssetManager(this);
		}else if(e.getSource().equals(mntmGenerateReport_ICSStatus)) {
			generateReport(new ICSStatus());
		}else if(e.getSource().equals(mntmGenerateReport_AssetTypeDomainStatus)) {
			Domain selectedDomain = null;
			try {
				selectedDomain = selectAssetTypeDomain();
			} catch (NoDomainAvailableException e1) {
				JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.0")); //$NON-NLS-1$
			}
			if(selectedDomain != null) {
				generateReport(new AssetTypeDomainStatus(selectedDomain));
			}
		}else if(e.getSource().equals(mntmGenerateReport_DomainStatus)) {
			Domain selectedDomain = null;
			try {
				selectedDomain = selectDomain();
			} catch (NoDomainAvailableException e1) {
				JOptionPane.showMessageDialog(this, RESOURCES.getString("TMainFrame.1")); //$NON-NLS-1$
			}
			if(selectedDomain != null) {
				generateReport(new DomainStatus(selectedDomain));
			}
		}else if(e.getSource().equals(mntmGenerateReport_PriorizedMeasures)) {
			generateReport(new PriorizedMeasures());
		}else if(e.getSource().equals(mntmAllReports)){
			generateReport(new ICSStatus());
			generateReport(new PriorizedMeasures());
			
			{
				Vector<Domain> domainsVector = new Vector<Domain>();
				
				Vector<Domain> removeDomains = new Vector<Domain>();
				
				for(AssetType a : Backend.getInstance().getAssetTypes()) {
					for(Layer d : a.getChildren()) {
						if(d instanceof Domain) {
							boolean alreadyAdded = false;
							for(Domain dd : domainsVector) {
								if(dd.getName().equals(d.getName()) && (dd.getId() == d.getId())) {
									alreadyAdded = true;
									break;
								}
							}
							if(d.getScore() < 0) {
								removeDomains.add((Domain) d);
							}
							if(!alreadyAdded) {
								//Es ist bekannt, dass die hinzugefügten Domänen Objekte zu zufälligen Asset-Typen gehören. Da in folgenden jedoch nur die Domänenkomponente ausgewertet wird, kann dies akzeptiert werden.
								if(d.getScore() >= 0) {
									domainsVector.add((Domain) d);
								}
							}
						}
					}
				}
				
				//Remove domains that are not answered everywhere
				for(Domain rd : removeDomains) {
					Vector<Integer> removeIndices = new Vector<Integer>();
					for (int i = 0; i < domainsVector.size(); i++) {
						Domain d = domainsVector.get(i);
						if(rd.getName().equals(d.getName()) && (rd.getId() == d.getId())) {
							removeIndices.add(i);
						}
					}
					for(int i : removeIndices) {
						domainsVector.remove(i);
					}
				}
				for(Domain selectedDomain : domainsVector) {
					generateReport(new DomainStatus(selectedDomain));
				}
			}
			{
				Vector<Domain> domainsVector = new Vector<Domain>();
				
				for(AssetType a : Backend.getInstance().getAssetTypes()) {
					for(Layer d : a.getChildren()) {
						if(d instanceof Domain) {
							if(d.getScore() >= 0) {
								domainsVector.add((Domain) d);
							}
						}
					}
				}
				for(Domain selectedDomain : domainsVector) {
					generateReport(new AssetTypeDomainStatus(selectedDomain));
				}
			}
			
			
		}else if(e.getSource().equals(mntmExit)) {
			exit();
		}else if(e.getSource().equals(mntmAbout)) {
			about();
		}
		else if(e.getSource().equals(btnNext)) {
			try {
				backend.selectLayer(backend.nextLayer());
			} catch (BackendInconsistencyException ex) {
				exitOnException(ex);
			}
		}else if(e.getSource().equals(btnPrevious)) {
			try {
				backend.selectLayer(backend.previousLayer());
			} catch (BackendInconsistencyException ex) {
				exitOnException(ex);
			}
		}else if(e.getSource().equals(btnNextUnfinished)) {
			try {
				backend.selectLayer(backend.nextUnfinishedLayer());
			} catch (BackendInconsistencyException ex) {
				exitOnException(ex);
			}
		}else if(e.getSource().equals(btnPrevUnfinished)) {
			try {
				backend.selectLayer(backend.prevUnfinishedLayer());
			} catch (BackendInconsistencyException ex) {
				exitOnException(ex);
			}
		}
	}
	
	private Domain selectDomain() throws NoDomainAvailableException {
		Vector<Domain> domainsVector = new Vector<Domain>();
		
		Vector<Domain> removeDomains = new Vector<Domain>();
		
		for(AssetType a : Backend.getInstance().getAssetTypes()) {
			for(Layer d : a.getChildren()) {
				if(d instanceof Domain) {
					boolean alreadyAdded = false;
					for(Domain dd : domainsVector) {
						if(dd.getName().equals(d.getName()) && (dd.getId() == d.getId())) {
							alreadyAdded = true;
							break;
						}
					}
					if(d.getScore() < 0) {
						removeDomains.add((Domain) d);
					}
					if(!alreadyAdded) {
						//Es ist bekannt, dass die hinzugefügten Domänen Objekte zu zufälligen Asset-Typen gehören. Da in folgenden jedoch nur die Domänenkomponente ausgewertet wird, kann dies akzeptiert werden.
						if(d.getScore() >= 0) {
							domainsVector.add((Domain) d);
						}
					}
				}
			}
		}
		
		//Remove domains that are not answered everywhere
		for(Domain rd : removeDomains) {
			Vector<Integer> removeIndices = new Vector<Integer>();
			for (int i = 0; i < domainsVector.size(); i++) {
				Domain d = domainsVector.get(i);
				if(rd.getName().equals(d.getName()) && (rd.getId() == d.getId())) {
					removeIndices.add(i);
				}
			}
			for(int i : removeIndices) {
				domainsVector.remove(i);
			}
		}
		
		return _selectDomainAndOrAssetType(domainsVector, false);
	}

	private Domain selectAssetTypeDomain() throws NoDomainAvailableException {
		Vector<Domain> domainsVector = new Vector<Domain>();
		
		for(AssetType a : Backend.getInstance().getAssetTypes()) {
			for(Layer d : a.getChildren()) {
				if(d instanceof Domain) {
					if(d.getScore() >= 0) {
						domainsVector.add((Domain) d);
					}
				}
			}
		}
		
		return _selectDomainAndOrAssetType(domainsVector, true);
	}
	
	private Domain _selectDomainAndOrAssetType(Vector<Domain> domainsVector, boolean selectAssetType) throws NoDomainAvailableException {
		String[] domainStrings = new String[domainsVector.size()];
		for(int i = 0 ; i < domainStrings.length ; ++i) {
			domainStrings[i] = selectAssetType ? domainsVector.get(i).toString() : domainsVector.get(i).getName();
		}
		
		if(domainStrings.length == 0) {
			throw new NoDomainAvailableException();
		}
		
		String result = null;
		{
			JList<String> domainsList = new JList<String>(domainStrings);
			int selection = JOptionPane.showConfirmDialog(this, new Object[]{new JLabel(RESOURCES.getString("TMainFrame.selectAssetDomain.message")), new JScrollPane(domainsList)}, RESOURCES.getString("TMainFrame.selectAssetDomain.title"), JOptionPane.OK_CANCEL_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			if(selection == JOptionPane.OK_OPTION) {
				result = domainsList.getSelectedValue();
			}
		}
		for(int i = 0 ; i < domainStrings.length ; ++i) {
			if(domainStrings[i].equals(result)) {
				return domainsVector.get(i);
			}
		}
		return null;
	}

	//Overlay
	
	private void showLongDescription(String measureName, String longDescription) {
		JEditorPane txt = new JEditorPane("text/html", longDescription); //$NON-NLS-1$
		txt.setEditable(false);
		JScrollPane scrl = new JScrollPane(txt);
		
		JDialog d = new JDialog(this);
		d.setTitle(measureName);
		d.setModalityType(ModalityType.APPLICATION_MODAL);
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		d.getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		contentPanel.add(scrl);
		
		d.setSize(getWidth() - 40, getHeight() - 40);
		d.setLocationRelativeTo(this);
		d.setVisible(true);
	}
	
	//Description View
	private void showDescription(String measureName, String text) {
		showLongDescription(measureName, text);
	}

	private MouseListener descriptionHandler = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			if(e.getSource() instanceof LongDescriptionLabel) {
				showDescription(((LongDescriptionLabel) e.getSource()).getMeasureName(), ((LongDescriptionLabel) e.getSource()).getLongDescription());
			}
		};
	};
	private JMenu mnReports;
	
	public MouseListener getLongDescriptionHandler() {
		return descriptionHandler;
	}

	//WindowListener
	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowClosing(WindowEvent arg0) {
		exit();
	}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
	
	//ScoreListener
	@Override
	public void valueChanged(int score) {
	}

}
