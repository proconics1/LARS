package bsi.lars.gui.editor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bsi.lars.backend.datastore.DataBase;
import bsi.lars.backend.datastore.QueryBuilder;
import bsi.lars.backend.datastore.QueryBuilder.queries;
import bsi.lars.gui.editor.elements.EAssetType;
import bsi.lars.gui.editor.elements.ECategory;
import bsi.lars.gui.editor.elements.EDomain;
import bsi.lars.gui.editor.elements.EMeasure;
import bsi.lars.gui.editor.elements.EMeasure3;
import bsi.lars.gui.editor.elements.Element;
import bsi.lars.gui.editor.elements.mapping.MAssettype;
import bsi.lars.gui.editor.elements.mapping.MAssettypeDomain;
import bsi.lars.gui.editor.elements.mapping.MAssettypeDomainCategory;
import bsi.lars.gui.editor.elements.mapping.MAssettypeDomainCategoryMeasure3;
import bsi.lars.gui.editor.elements.mapping.MAssettypeDomainCategoryMeasure3Measure;
import bsi.lars.gui.editor.elements.mapping.MappingElement;

public class EditorLogic {
	
	private DataBase database;

	private static EditorLogic instance = new EditorLogic();

	private HashMap<Integer, EAssetType> tmpAssetTypes = new HashMap<Integer, EAssetType>();
	private HashMap<Integer, EDomain> tmpDomains = new HashMap<Integer, EDomain>();
	private HashMap<Integer, ECategory> tmpCategories = new HashMap<Integer, ECategory>();
	private HashMap<Integer, EMeasure3> tmpMeasure3s = new HashMap<Integer, EMeasure3>();
	private HashMap<Integer, EMeasure> tmpMeasures = new HashMap<Integer, EMeasure>();
	
	private EditorLogic() {
		database = DataBase.getInstance();
	}
	
	public static EditorLogic getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Element> T[] getElements(Class<T> clazz) {
		if(clazz.equals(EAssetType.class)) {
			return (T[]) getAssetTypes();
		}else if(clazz.equals(EDomain.class)) {
			return (T[]) getDomains();
		}else if(clazz.equals(ECategory.class)) {
			return (T[]) getCategories();
		}else if(clazz.equals(EMeasure3.class)) {
			return (T[]) getMeasures3();
		}else if(clazz.equals(EMeasure.class)) {
			return (T[]) getMeasures();
		}else{
			throw new IllegalAccessError();
		}
	}
	
	private EAssetType[] getAssetTypes() {
		tmpAssetTypes.clear();
		try{
			ResultSet res = database.executeQuery(QueryBuilder.queries.assettypes);
			while(res.next()) {
				EAssetType eAssetType = new EAssetType(res.getInt("id"), res.getString("name"), res.getString("descrpt")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				tmpAssetTypes.put(eAssetType.getId(), eAssetType);
			}
		}catch(SQLException e) {
			exitOnException(e);
		}
		return tmpAssetTypes.values().toArray(new EAssetType[tmpAssetTypes.values().size()]);
	}

	private EDomain[] getDomains() {
		tmpDomains.clear();
		try{
			ResultSet res = database.executeQuery(QueryBuilder.queries.domains);
			while(res.next()) {
				EDomain eDomain = new EDomain(res.getInt("id"), res.getString("name"), res.getString("descrpt")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				tmpDomains.put(eDomain.getId(), eDomain);
			}
		}catch(SQLException e) {
			exitOnException(e);
		}
		return tmpDomains.values().toArray(new EDomain[tmpDomains.values().size()]);
	}

	private ECategory[] getCategories() {
		tmpCategories.clear();
		try{
			ResultSet res = database.executeQuery(QueryBuilder.queries.categories);
			while(res.next()) {
				ECategory eCategory = new ECategory(res.getInt("id"), res.getString("name"), res.getString("descrpt")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				tmpCategories.put(eCategory.getId(), eCategory);
			}
		}catch(SQLException e) {
			exitOnException(e);
		}
		return tmpCategories.values().toArray(new ECategory[tmpCategories.values().size()]);
	}

	private EMeasure3[] getMeasures3() {
		tmpMeasure3s.clear();
		try{
			ResultSet res = database.executeQuery(QueryBuilder.queries.measures3);
			while(res.next()) {
				
				EMeasure3 eMeasure3 = new EMeasure3(res.getInt("id"), res.getString("name"), res.getString("descrpt"), res.getInt("to_measure_id"), res.getString("descrpt_long")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				tmpMeasure3s.put(eMeasure3.getId(), eMeasure3);
			}
		}catch(SQLException e) {
			exitOnException(e);
		}
		return tmpMeasure3s.values().toArray(new EMeasure3[tmpMeasure3s.values().size()]);
	}

	private EMeasure[] getMeasures() {
		tmpMeasures.clear();
		try{
			ResultSet res = database.executeQuery(QueryBuilder.queries.measures);
			while(res.next()) {
				EMeasure eMeasure = new EMeasure(res.getInt("id"), res.getString("name"), res.getString("descrpt"), res.getInt("to_measure_id"), res.getString("descrpt_long"), res.getInt("number"), res.getInt("score")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
				tmpMeasures.put(eMeasure.getId(), eMeasure);
			}
		}catch(SQLException e) {
			exitOnException(e);
		}
		return tmpMeasures.values().toArray(new EMeasure[tmpMeasures.values().size()]);
	}

	public MappingElement<?,?>[] getMapping(MappingElement<?, ?> element) {
		if(element == null) {
			return _getMapping();
		}
		@SuppressWarnings("rawtypes")
		Class<? extends MappingElement> clazz = element.getClass();
		if(clazz.equals(MAssettype.class)) {
			return _getMapping((MAssettype) element);
		}else if(clazz.equals(MAssettypeDomain.class)) {
			return _getMapping((MAssettypeDomain) element);
		}else if(clazz.equals(MAssettypeDomainCategory.class)) {
			return _getMapping((MAssettypeDomainCategory) element);
		}else if(clazz.equals(MAssettypeDomainCategoryMeasure3.class)) {
			return _getMapping((MAssettypeDomainCategoryMeasure3) element);
//		}else if(clazz.equals(MAssettypeDomainCategoryMeasure3Measure.class)) {
//			return _getMapping((MAssettypeDomainCategoryMeasure3Measure) element);
		}else{
			throw new IllegalAccessError();
		}
	}

	private MAssettype[] _getMapping() {
		Vector<MAssettype> result = new Vector<MAssettype>();
		for(EAssetType a : tmpAssetTypes.values()) {//getAssetTypes()) {
			result.add(new MAssettype(a));
		}
		return result.toArray(new MAssettype[result.size()]);
	}

	private MAssettypeDomain[] _getMapping(MAssettype element) {
		Vector<MAssettypeDomain> result = new Vector<MAssettypeDomain>();
		try{
			ResultSet res = database.executeQuery(QueryBuilder.queries.domainfrommassettype, (Object[]) element.getPath());
			while(res.next()) {
				result.add(new MAssettypeDomain(element, 
					new EDomain(res.getInt("id"), res.getString("name"), res.getString("descrpt")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				));
			}
		}catch(SQLException e) {
			exitOnException(e);
		}
		return result.toArray(new MAssettypeDomain[result.size()]);
	}
	
	private MAssettypeDomainCategory[] _getMapping(MAssettypeDomain element) {
		Vector<MAssettypeDomainCategory> result = new Vector<MAssettypeDomainCategory>();
		try{
			ResultSet res = database.executeQuery(QueryBuilder.queries.categoryfromdomain, (Object[]) element.getPath());
			while(res.next()) {
				result.add(new MAssettypeDomainCategory(element,
					new ECategory(res.getInt("id"), res.getString("name"), res.getString("descrpt")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				));
			}
		}catch(SQLException e) {
			exitOnException(e);
		}
		return result.toArray(new MAssettypeDomainCategory[result.size()]);
	}
	
	private MAssettypeDomainCategoryMeasure3[] _getMapping(MAssettypeDomainCategory element) {
		Vector<MAssettypeDomainCategoryMeasure3> result = new Vector<MAssettypeDomainCategoryMeasure3>();
		try{
			ResultSet res = database.executeQuery(QueryBuilder.queries.measurefromcategory, (Object[]) element.getPath());
			while(res.next()) {
				result.add(new MAssettypeDomainCategoryMeasure3(element,
					new EMeasure3(res.getInt("id"), res.getString("name"), res.getString("descrpt"), res.getInt("to_measure_id"), res.getString("descrpt_long")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				));
			}
		}catch(SQLException e) {
			exitOnException(e);
		}
		return result.toArray(new MAssettypeDomainCategoryMeasure3[result.size()]);
	}
	
	private MAssettypeDomainCategoryMeasure3Measure[] _getMapping(MAssettypeDomainCategoryMeasure3 element) {
		Vector<MAssettypeDomainCategoryMeasure3Measure> result = new Vector<MAssettypeDomainCategoryMeasure3Measure>();
		try{
			ResultSet res = database.executeQuery(QueryBuilder.queries.measuresfrommeasure, element.getToElement().getId());//(Object[]) element.getPath());
			while(res.next()) {
				result.add(new MAssettypeDomainCategoryMeasure3Measure(element,
					new EMeasure(res.getInt("id"), res.getString("name"), res.getString("descrpt"), res.getInt("to_measure_id"), res.getString("descrpt_long"), res.getInt("number"), res.getInt("score")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
				));
			}
		}catch(SQLException e) {
			exitOnException(e);
		}
		return result.toArray(new MAssettypeDomainCategoryMeasure3Measure[result.size()]);
	}
	
	public <T> MappingElement<?,?>[] getAllMappingElements(Class<T> clazz) {
		if(clazz.equals(MAssettype.class)) {
			return _getAllMappingA();
		}else if(clazz.equals(MAssettypeDomain.class)) {
			return _getAllMappingA2D();
		}else if(clazz.equals(MAssettypeDomainCategory.class)) {
			return _getAllMappingA2D2C();
		}else if(clazz.equals(MAssettypeDomainCategoryMeasure3.class)) {
			return _getAllMappingA2D2C2M3();
//		}else if(clazz.equals(MAssettypeDomainCategoryMeasure3Measure.class)) {
//			return _getAllMappingA2D2C2M32M();
		}else{
			throw new IllegalAccessError();
		}
	}

	private Integer[][] _getAllMapping(queries query, String ... ids) {
		Vector<Integer[]> result = new Vector<Integer[]>();
		try{
			ResultSet res = database.executeQuery(query);
			while(res.next()) {
				Vector<Integer> tmp = new Vector<Integer>();
				for(String id : ids) {
					tmp.add(res.getInt(id));
				}
				result.add(tmp.toArray(new Integer[tmp.size()]));
			}
		}catch(SQLException e) {
			exitOnException(e);
		}
		return result.toArray(new Integer[result.size()][]);
	}
	
	private MAssettype[] _getAllMappingA() {
		return _getMapping();
	}
	
	private MAssettypeDomain[] _getAllMappingA2D() {
		Integer[][] mapping = _getAllMapping(QueryBuilder.queries.a2d, "asstype_id", "dom_id"); //$NON-NLS-1$ //$NON-NLS-2$
		Vector<MAssettypeDomain> result = new Vector<MAssettypeDomain>();
		for(Integer[] map : mapping) {
			result.add(new MAssettypeDomain(new MAssettype(getEAssetType(map[0])), getEDomain(map[1])));
		}
		return result.toArray(new MAssettypeDomain[result.size()]);
	}

	private MAssettypeDomainCategory[] _getAllMappingA2D2C() {
		Integer[][] mapping = _getAllMapping(QueryBuilder.queries.ad2c, "asstype_id", "dom_id", "cat_id"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Vector<MAssettypeDomainCategory> result = new Vector<MAssettypeDomainCategory>();
		for(Integer[] map : mapping) {
			result.add(new MAssettypeDomainCategory(new MAssettypeDomain(new MAssettype(getEAssetType(map[0])), getEDomain(map[1])), getECategory(map[2])));
		}
		return result.toArray(new MAssettypeDomainCategory[result.size()]);
	}

	private MAssettypeDomainCategoryMeasure3[] _getAllMappingA2D2C2M3() {
		Integer[][] mapping = _getAllMapping(QueryBuilder.queries.adc2m3, "asstype_id", "dom_id", "cat_id", "meas_id"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		Vector<MAssettypeDomainCategoryMeasure3> result = new Vector<MAssettypeDomainCategoryMeasure3>();
		for(Integer[] map : mapping) {
			result.add(new MAssettypeDomainCategoryMeasure3(new MAssettypeDomainCategory(new MAssettypeDomain(new MAssettype(getEAssetType(map[0])), getEDomain(map[1])), getECategory(map[2])), getEMeasure3(map[3])));
		}
		return result.toArray(new MAssettypeDomainCategoryMeasure3[result.size()]);
	}

//	private MappingElement<?, ?>[] _getAllMappingA2D2C2M32M() {
//		// Auto-generated method stub
//		return null;
//	}
	
	private EAssetType getEAssetType(Integer integer) {
		return tmpAssetTypes.get(integer);
	}
	
	private EDomain getEDomain(Integer integer) {
		return tmpDomains.get(integer);
	}
	
	private ECategory getECategory(Integer integer) {
		return tmpCategories.get(integer);
	}

	private EMeasure3 getEMeasure3(Integer integer) {
		return tmpMeasure3s.get(integer);
	}
	
//	private EMeasure getEMeasure(Integer integer) {
//		return tmpMeasures.get(integer);
//	}

	public void putElement(Element elmnt, HashMap<String, Object> values) {
		if(elmnt instanceof EAssetType) {
			_putAssetType(elmnt.getId(), values.get(TElementPanel.NAME).toString(), values.get(TElementPanel.DESCRPT).toString());
		}else if(elmnt instanceof EDomain) {
			_putDomain(elmnt.getId(), values.get(TElementPanel.NAME).toString(), values.get(TElementPanel.DESCRPT).toString());
		}else if(elmnt instanceof ECategory) {
			_putCategory(elmnt.getId(), values.get(TElementPanel.NAME).toString(), values.get(TElementPanel.DESCRPT).toString());
		}else if(elmnt instanceof EMeasure) {
			_putMeasure(elmnt.getId(), values.get(TElementPanel.NAME).toString(), values.get(TElementPanel.DESCRPT).toString(), values.get(TMeasureElementPanel.DESCRPT_LONG).toString(), (Integer) values.get(TMeasureElementPanel.NUMBER), (Integer) values.get(TMeasureElementPanel.SCORE));
		}else if(elmnt instanceof EMeasure3) {//Yes. the order is not as in the model. If you change the EMeasure and the EMeasure3 check it will not work any more as EMeasure is derived from EMeasure3.
			_putMeasure3(elmnt.getId(), values.get(TElementPanel.NAME).toString(), values.get(TElementPanel.DESCRPT).toString(), values.get(TMeasure3ElementPanel.DESCRPT_LONG).toString());
		}
	}

	private void _putAssetType(int id, String name, String descrpt) {
		try {
			database.execute(QueryBuilder.queries.setassettype, id, name, descrpt);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}
	
	private void _putDomain(int id, String name, String descrpt) {
		try {
			database.execute(QueryBuilder.queries.setdomain, id, name, descrpt);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}
	
	private void _putCategory(int id, String name, String descrpt) {
		try {
			database.execute(QueryBuilder.queries.setcategory, id, name, descrpt);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}
	
	private void _putMeasure3(int id, String name, String descrpt, String descrpt_long) {
		try {
			database.execute(QueryBuilder.queries.setmeasure3, id, name, descrpt, descrpt_long);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}
	
	private void _putMeasure(int id, String name, String descrpt, String descrpt_long, Integer number, int score) {
		try {
			database.execute(QueryBuilder.queries.setmeasure, id, name, descrpt, descrpt_long, number, score);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}

	public void putMapping(MappingElement<?, ?> element, Element toMap) {
		changeMapping(element, toMap, false);
	}
	
	public void removeMapping(MappingElement<?, ?> element, Element mapped) {
		changeMapping(element, mapped, true);
	}
	
	private void changeMapping(MappingElement<?, ?> element, Element toMap, boolean remove) {
		if(element instanceof MAssettype) {
			/*if(toMap instanceof EAssetType) {
			}else */if(toMap instanceof EDomain) {
				_changeMappingA2D(element, toMap, remove);
				return;
			}/*else if(toMap instanceof ECategory) {
			}else if(toMap instanceof EMeasure) {
			}else if(toMap instanceof EMeasure3) {
			}*/
		}else if(element instanceof MAssettypeDomain) {
			/*if(toMap instanceof EAssetType) {
			}else if(toMap instanceof EDomain) {
			}else */if(toMap instanceof ECategory) {
				_changeMappingA2D2C(element, toMap, remove);
				return;
			}/*else if(toMap instanceof EMeasure) {
			}else if(toMap instanceof EMeasure3) {
			}*/
		}else if(element instanceof MAssettypeDomainCategory) {
			/*if(toMap instanceof EAssetType) {
			}else if(toMap instanceof EDomain) {
			}else if(toMap instanceof ECategory) {
			}else */if(toMap instanceof EMeasure) {
				_changeMappingA2D2C2M(element, toMap, remove);
				return;
			}else if(toMap instanceof EMeasure3) {
				_changeMappingA2D2C2M3(element, toMap, remove);
				return;
			}
		}else if(element instanceof MAssettypeDomainCategoryMeasure3) {
			/*if(toMap instanceof EAssetType) {
			}else if(toMap instanceof EDomain) {
			}else if(toMap instanceof ECategory) {
			}else */if(toMap instanceof EMeasure) {
				_changeMappingA2D2C2M32M(element, toMap, remove);
				return;
			}/*else if(toMap instanceof EMeasure3) {
			}*/
		}/*else if(element instanceof MAssettypeDomainCategoryMeasure3Measure) {
			if(toMap instanceof EAssetType) {
			}else if(toMap instanceof EDomain) {
			}else if(toMap instanceof ECategory) {
			}else if(toMap instanceof EMeasure) {
			}else if(toMap instanceof EMeasure3) {
			}
		}*/
		throw new IllegalAccessError();
	}
	
	private void _changeMappingA2D(MappingElement<?, ?> element, Element toMap, boolean remove) {
		try {
			database.execute(
					remove ? QueryBuilder.queries.unset_a2d : QueryBuilder.queries.set_a2d,
							element.getPath()[0], toMap.getId()
							);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}

	private void _changeMappingA2D2C(MappingElement<?, ?> element, Element toMap, boolean remove) {
		try {
			Integer[] path = element.getPath();
			database.execute(
					remove ? QueryBuilder.queries.unset_ad2c : QueryBuilder.queries.set_ad2c,
							path[0], path[1], toMap.getId()
							);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}

	private void _changeMappingA2D2C2M3(MappingElement<?, ?> element, Element toMap, boolean remove) {
		try {
			Integer[] path = element.getPath();
			database.execute(
					remove ? QueryBuilder.queries.unset_adc2m3 : QueryBuilder.queries.set_adc2m3,
							path[0], path[1], path[2], toMap.getId()
							);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}

	private void _changeMappingA2D2C2M32M(MappingElement<?, ?> element, Element toMap, boolean remove) {
		try {
			Integer[] path = element.getPath();
			if(remove) {
				database.execute(QueryBuilder.queries.unset_adcm32m,
								toMap.getId()
								);
			}else{
				database.execute(QueryBuilder.queries.set_adcm32m,
								path[3], toMap.getId()
								);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}

	private void _changeMappingA2D2C2M(MappingElement<?, ?> element, Element toMap, boolean remove) {
		try {
			Integer[] path = element.getPath();
			database.execute(
					remove ? QueryBuilder.queries.unset_adc2m : QueryBuilder.queries.set_adc2m,
							path[0], path[1], path[2], toMap.getId()
							);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}
	
	public void exitOnException(SQLException e) {
		JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea("Irgendetwas ist schiefgelaufen: Beende ..." //$NON-NLS-1$
				+ "\n" + e.getMessage())), "Error", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		System.exit(0);
	}
}
