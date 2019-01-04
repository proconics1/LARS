package bsi.lars.backend.reports;

import java.util.Stack;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import bsi.lars.backend.Backend;
import bsi.lars.backend.datastore.layers.Category;
import bsi.lars.backend.datastore.layers.Layer;
import bsi.lars.backend.datastore.layers.Measure;

public class ICSStatus extends Report {
	
	@Override
	protected String getName() {
		return "Current security level ICS in total";
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
				TreeNode child = l.getChildAt(i);
				if(child instanceof Category) {
					categories.add((Category) child);
				}else{
					layers.push((Layer) child);
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
			Layer child = (Layer) backend.getChildAt(i);
			if(child.getName().contains("ICS ")) {
				layers.push(child);
				break;
			}
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
					if(child instanceof Category) {//Ignore all actions in categories that are "not relevant"
						if(((Category) child).getAnswer().getAnswer() == 0) {//Just add categories for further searches that are not "not relevant". OR. Ignore all actions that are under a "not relevant" category.
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
		int unansweredcount = 0;
		int[] scorecounts = new int[5];//0: KO, 1: K1, 2: K2, 3: K3, 4: OK (K=Criticality)
		for(Measure m : measures) {
			int score = ((Layer) m).getScore();
			if(score >= 0) {
				++scorecounts[score];
			}else{
				++unansweredcount;
			}
		}
		
		int ix = -2;
		if(unansweredcount == 0) {
			for(int i = 0 ; i < scorecounts.length ; ++i) {
				if(scorecounts[i] != 0) {
					ix = i;
					break;
				}
			}
		}else{
			ix = -1;
		}
		if(ix != -2) {
			if(ix < 4) {//If ix == 4, then all actions are marked in green.
				StringBuilder sb = new StringBuilder();
				Vector<Measure> ms = new Vector<Measure>();
				for(Measure m : measures) {
					if(((Layer) m).getScore() == ix) {
						ms.add(m);
					}
				}
				//TODO: maybe sort ms
				for(Measure m : ms) {
					sb.append(m.toXMLString(false, false, true, true, true));
				}
				
				return sb.toString();
			}
		}
		
		return "";
	}
	
	@Override
	public String getDefaultFileName() {
		return "ICS-Status";
	}
}
