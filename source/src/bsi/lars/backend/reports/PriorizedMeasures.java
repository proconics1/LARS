package bsi.lars.backend.reports;

import java.util.Stack;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import bsi.lars.backend.Backend;
import bsi.lars.backend.datastore.layers.Category;
import bsi.lars.backend.datastore.layers.Layer;
import bsi.lars.backend.datastore.layers.Measure;

public class PriorizedMeasures extends Report {

	@Override
	protected String getName() {
		return "Priorisierter Ma\u00DFnahmenkatalog";
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
		
		for(int i = 0 ; i < backend.getChildCount() ; ++i) {
			layers.push((Layer) backend.getChildAt(i));
		}
		
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
					if(child instanceof Category) {//Ignoriert alle Maßnahmen, die "nicht relevant" sind
						if(((Category) child).getAnswer().getAnswer() == 0) {//Ignoriere alle Maßnahmen, die unter einer "nicht relevant"en Kategorie sind.
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
			sb.append(c.toXMLString(true, true, true, true));
			sb.append("</category>\n");
		}
		return sb.toString();
	}
	
	@Override
	protected String generateMeasuresOutput(Vector<Measure> measures) throws Exception {
		
		//Erster Index: Kritikalität
		Vector</*Vector<*/Measure/*>*/> unanswered = new Vector</*Vector<*/Measure/*>*/>();
		
		//Erster Index: Kritikalität
		//Zweiter Index: Antwort
		Vector<Vector<Vector<Measure>>> sorted = new Vector<Vector<Vector<Measure>>>();
		
		for(int i = 0 ; i < 4 ; ++i) {
			sorted.add(new Vector<Vector<Measure>>());
//			unanswered.add(new Vector<Measure>());
			for(int j = 0 ; j < Backend.getInstance().getStati().length ; ++j) {
				sorted.get(i).add(new Vector<Measure>());
			}
		}
		
		for(Measure m : measures) {
			int answer = m.getAnswer().getComplexAnswer();
			if(answer > 0) {
				sorted.get(m.getMeasureScore()).get(answer - 1).add(m);
			}else{
				unanswered/*.get(m.getMeasureScore())*/.add(m);
			}
		}
		
		StringBuilder result = new StringBuilder();
		
		int priority = 1;
		
		{
			int i = 0;
			if(sorted.get(i).get(2).size() != 0 || sorted.get(i).get(1).size() != 0) {
				result.append("<priority>\n");
				result.append("<value>");
				result.append(priority++);
				result.append("</value>\n");

				for(Measure m : sorted.get(i).get(2)) {
					result.append(m.toXMLString(true, true, true, true, true));
				}
				for(Measure m : sorted.get(i).get(1)) {
					result.append(m.toXMLString(true, true, true, true, true));
				}
				
				result.append("</priority>\n");
			}
		}
		for(int i = 1 ; i < 4 ; ++i) {
			for(int j = 2 ; j > 0 ; --j) {
				if(sorted.get(i).get(j).size() == 0) {
					continue;
				}
				
				result.append("<priority>\n");
				result.append("<value>");
				result.append(priority++);
				result.append("</value>\n");
				
				for(Measure m : sorted.get(i).get(j)) {
					result.append(m.toXMLString(true, true, true, true, true));
				}

				result.append("</priority>\n");
			}
		}
		
		{
			if(unanswered.size() > 0) {
				result.append("<unanswered>\n");
				for(Measure m : unanswered) {
					result.append(m.toXMLString(true, true, true, true, true));
				}
				result.append("</unanswered>\n");
			}
		}
		
		return result.toString();
	}
	
	@Override
	public String getDefaultFileName() {
		return "Measures";
	}
}
