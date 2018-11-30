package bsi.lars.gui.editor;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import bsi.lars.gui.editor.elements.EAssetType;
import bsi.lars.gui.editor.elements.ECategory;
import bsi.lars.gui.editor.elements.EDomain;
import bsi.lars.gui.editor.elements.EMeasure;
//import bsi.lars.gui.editor.elements.EMeasure3;
import bsi.lars.gui.editor.elements.mapping.MAssettype;
import bsi.lars.gui.editor.elements.mapping.MAssettypeDomain;
import bsi.lars.gui.editor.elements.mapping.MAssettypeDomainCategory;
//import bsi.lars.gui.editor.elements.mapping.MAssettypeDomainCategoryMeasure3;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

public class TEditorPanel extends JPanel {
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("bsi.lars.gui.editor.messages"); //$NON-NLS-1$

	private static final long serialVersionUID = 7885470883127925765L;

	/**
	 * Create the panel.
	 */
	public TEditorPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		JTabbedPane elementsPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab(RESOURCES.getString("TEditorPanel.Elements"), null, elementsPane, null); //$NON-NLS-1$
		
		TElementPanel<EAssetType> assetTypes = new TElementPanel<EAssetType>(EAssetType.class);
		elementsPane.addTab(RESOURCES.getString("TEditorPanel.AssetTypes"), null, assetTypes, null); //$NON-NLS-1$
		
		TElementPanel<EDomain> domains = new TElementPanel<EDomain>(EDomain.class);
		elementsPane.addTab(RESOURCES.getString("TEditorPanel.Domains"), null, domains, null); //$NON-NLS-1$
		
		TElementPanel<ECategory> categories = new TElementPanel<ECategory>(ECategory.class);
		elementsPane.addTab(RESOURCES.getString("TEditorPanel.Categories"), null, categories, null); //$NON-NLS-1$
		
//		Maßnahmen-Ebene-3 wird in der Methodik nicht benötigt und wird deshalb nicht mehr unterstützt.
//		TMeasure3ElementPanel measure3 = new TMeasure3ElementPanel(EMeasure3.class);
//		elementsPane.addTab(RESOURCES.getString("TEditorPanel.Measures3"), null, measure3, null); //$NON-NLS-1$
		
		TMeasureElementPanel measure = new TMeasureElementPanel(EMeasure.class);
		elementsPane.addTab(RESOURCES.getString("TEditorPanel.Measures"), null, measure, null); //$NON-NLS-1$
		
		JTabbedPane mappingPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab(RESOURCES.getString("TEditorPanel.Mapping"), null, mappingPane, null); //$NON-NLS-1$
		
		TMappingPanel<MAssettype, EDomain> a2d = new TMappingPanel<MAssettype, EDomain>(MAssettype.class, EDomain.class);
		mappingPane.addTab(RESOURCES.getString("TEditorPanel.At2D"), null, a2d, null); //$NON-NLS-1$
		
		TMappingPanel<MAssettypeDomain, ECategory> ad2c = new TMappingPanel<MAssettypeDomain, ECategory>(MAssettypeDomain.class, ECategory.class);
		mappingPane.addTab(RESOURCES.getString("TEditorPanel.AtD2C"), null, ad2c, null); //$NON-NLS-1$

//		Maßnahmen-Ebene-3 wird in der Methodik nicht benötigt und wird deshalb nicht mehr unterstützt.
//		TMappingPanel<MAssettypeDomainCategory, EMeasure3> adc2m3 = new TMappingPanel<MAssettypeDomainCategory, EMeasure3>(MAssettypeDomainCategory.class, EMeasure3.class);
//		mappingPane.addTab(RESOURCES.getString("TEditorPanel.AtDC2M3"), null, adc2m3, null); //$NON-NLS-1$
//		
//		Maßnahmen-Ebene-3 wird in der Methodik nicht benötigt und wird deshalb nicht mehr unterstützt.
//		TMappingPanel<MAssettypeDomainCategoryMeasure3, EMeasure> adcm32m = new TMappingPanel<MAssettypeDomainCategoryMeasure3, EMeasure>(MAssettypeDomainCategoryMeasure3.class, EMeasure.class);
//		mappingPane.addTab(RESOURCES.getString("TEditorPanel.AtDCM32M"), null, adcm32m, null); //$NON-NLS-1$
		
		TMappingPanel<MAssettypeDomainCategory, EMeasure> adc2m = new TMappingPanel<MAssettypeDomainCategory, EMeasure>(MAssettypeDomainCategory.class, EMeasure.class);
		mappingPane.addTab(RESOURCES.getString("TEditorPanel.AtDC2M"), null, adc2m, null); //$NON-NLS-1$
		
	}

}
