package bsi.lars.backend.reports;

import java.util.Stack;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import bsi.lars.backend.Backend;
import bsi.lars.backend.datastore.layers.Category;
import bsi.lars.backend.datastore.layers.Domain;
import bsi.lars.backend.datastore.layers.Layer;
import bsi.lars.backend.datastore.layers.Measure;

public class AssetTypeDomainStatus extends Report {
	
	private Domain domain;

	public AssetTypeDomainStatus(Domain domain) {//Angabe der Domäne genügt, da über getParent der Asset-Typ ermittelt werden kann
		this.domain = domain;
	}
	
	@Override
	protected String getName() {
		return "Sicherheitsniveau für Asset-Typ: " + domain.getParent().getName() + " Domäne: " + domain.getName();
	}
	
	@Override
	protected Vector<Category> getFilteredCategories(Backend backend) {
		Vector<Category> categories = new Vector<Category>();

		Stack<Layer> layers = new Stack<Layer>();
		
		for(int i = 0 ; i < backend.getChildCount() ; ++i) {
			layers.push((Layer) backend.getChildAt(i));
		}
		
		while(layers.size() > 0) {
			Layer l = layers.pop();
			for(int i = 0 ; i < l.getChildCount() ; ++i) {
				Layer child = (Layer) l.getChildAt(i);
				if(child instanceof Category) {
					categories.add((Category) child);
				}else{
					layers.add(child);
				}
			}
		}
		
		return categories;
	}
	
	@Override
	protected Vector<Measure> getFilteredMeasures(Backend backend) {
		Vector<Measure> measures = new Vector<Measure>();
		
		Stack<Layer> layers = new Stack<Layer>();
		
		layers.add(domain);

		while(layers.size() > 0) {
			Layer l = layers.pop();
			for(int i = 0 ; i < l.getChildCount() ; ++i) {
				TreeNode child = l.getChildAt(i);
				if(child instanceof Measure && !((Measure) child).isLayer3()) {
					Measure m = (Measure) child;
					if(m.getAnswer().getComplexAnswer() != 4) {
						measures.add(m);
					}
				}else{
					if(child instanceof Category) {//Ignoriert alle Maßnahmen, in Kategorien die "nicht relevant" sind
						if(((Category) child).getAnswer().getAnswer() == 0) {//Füge nur Kategorien zur weiteren Durchsuchung hinzu, die nicht "nicht relevant" sind. BZW. Ignoriere alle Maßnahmen, die unter einer "nicht relevant"en Kategorie sind.
							layers.push((Layer) child);
						}
					}else{
						layers.push((Layer) child);
					}
				}
			}
		}
		
		return measures;
	}

	@Override
	protected String generateAdditionalDetails() {
		return null;
	}
	
	@Override
	protected String generateCategoriesOutput(Vector<Category> categories) throws Exception {
		StringBuilder sb = new StringBuilder();
		for(Category c : categories) {
			sb.append("<category>\n");
			sb.append(c.toXMLString(false, false, true, true));
			sb.append("</category>\n");
		}
		return sb.toString();
	}
	
	@Override
	protected String generateMeasuresOutput(Vector<Measure> measures) throws Exception {
		//Beinahe selber Code wie ICSStatus#generateMeasuresOutput()
		int[] scorecounts = new int[5];//0: KO, 1: K1, 2: K2, 3: K3, 4: OK (K=Kritikalität)
		for(Measure m : measures) {
			++scorecounts[((Measure) m).getScore()];
		}
		int ix = -1;
		for(int i = 0 ; i < scorecounts.length ; ++i) {
			if(scorecounts[i] != 0) {
				ix = i;
				break;
			}
		}
		if(ix != -1) {
			if(ix < 4) {//Wenn ix == 4, dann sind alle Maßnahmen grün markiert.
				StringBuilder sb = new StringBuilder();
				Vector<Measure> ms = new Vector<Measure>();
				for(Measure m : measures) {
					if(((Layer) m).getScore() == ix) {
						ms.add(m);
					}
				}
				//TODO: maybe sort ms
				for(Measure m : ms) {
					//Hier ist in dieser Methode der zweite Unterschied zu ICSStatus#generateMeasuresOutput()
					sb.append(m.toXMLString(false, false, true, true, true));
				}
				
				return sb.toString();
			}
		}
		return "";
	}
	
	@Override
	public String getDefaultFileName() {
		return "AssetTypeDomainStatus-" + domain.getParent().getName() + "-" + domain.getName();
	}
}
