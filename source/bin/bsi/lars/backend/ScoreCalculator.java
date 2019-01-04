package bsi.lars.backend;

import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import bsi.lars.backend.datastore.layers.AssetType;
import bsi.lars.backend.datastore.layers.Category;
import bsi.lars.backend.datastore.layers.Domain;
import bsi.lars.backend.datastore.layers.Layer;
import bsi.lars.backend.datastore.layers.Measure;

/**
 * <h1>Calculates the result for the given Answers</h1>
 * <br />
* <h2> Calculation Rule: </ h2>
  * For the asset type, domain and for the whole score layers, the score is<br />
  * the minimum of scores of the sub-elements.<br />
  * For total score, the sub-items are the asset types,<br />
  * for the asset types the domains and<br />
  * for the domains the categories.<br />
  * <br />
  * The categories and measures are as follows:<br />
  * The actions have a criticality (Measure#getMeasureScore ()) and a response (Measure#getAnswer () # getComplexAnswer ()). <br />
  * The criticality levels are assigned colors:<br />
  * - KO: darkred<br />
  * - 1: red<br />
  * - 2: orange<br />
  * - 3: yellow<br />
 * <br />
 * Wenn keine Kritikalitätsstufe anschlägt (keine Maßnahme der Stufe "nicht umgesetzt" und nicht mehr als je 20% "teilweise umgesetzt"), dann ist das Ergebnis grün.<br />
 * <br />
 * Die Maßnahmen werden nach Kritikalität sortiert in Gruppen eingeteilt.<br />
 * Für jede Kritikalitätsgruppe wird folgendes bestimmt:<br />
 * - Wenn eine Maßnahme "nicht umgesetzt" ist, kann der Score der Kategorie nicht besser sein, als durch die Kritikalität festgelegt.<br />
 * - Wenn keine Maßnahme der Gruppe "nicht umgesetzt" ist und höchstens 20% der Maßnahmen der Gruppe "teilweise umgesetzt" sind, kann der Score besser sein, als durch die Kritikalität festgelegt.<br />
 * <br />
 * Maßnahmen, die "nicht relevant" sind, werden bei der Berechnung der 20% ausgelassen.<br />
 * <br />
 * <br />
 * <br />
 * <br />
 * <br />
 * <h2>Ergebnis-Berechnung:</h2>
 * <br />
 * Die Daten sind als Baum abgebildet:<br />
 * - Asset-Typ<br />
 * &nbsp;- Domäne<br />
 * &nbsp;&nbsp;- Kategorie<br />
 * &nbsp;&nbsp;&nbsp;- Maßnahme<br />
 * <br />
 * Mögliche Ergebnisse:<br />
 * Dunkelrot, Rot, Orange, Gelb, Grün<br />
 * <br />
 * Es ist möglich für jeden Knoten des Baumes ein Ergebnis zu berechnen.<br />
 * <br />
 * <br />
 * Für die Maßnahmen wurden vier Kritikalitätsstufen definiert:<br />
 * - KO,<br />
 * -  1,<br />
 * -  2,<br />
 * -  3<br />
 * <br />
* It applies: <br />
  * If a measure of Criticality X has been marked as "unreacted", the overall result can not be better than Y, with <br />
  * Y = dark red for X = KO, <br />
  * Y = red for X = 1, <br />
  * Y = orange for X = 2, <br />
  * Y = yellow for X = 3. <br />
  * <br />
  * <br />
  * <h3> Result Action: </ h3>
  * <br />
 * A measure may be "implemented", "partially implemented", "not implemented" or "not relevant". <br />
 * <br />
 * <pre>
 * Selection             | Result of the action (in case of Criticality)
 * ----------------------|-----------------------------------------
 * Implemented           | Green (KO, 1, 2, 3)
 * Partially Implemented | Darkred (KO), Red (1), Orange (2), Yellow (3)
 * Not translated        | Darkred (KO), Red (1), Orange (2), Yellow (3)
 * Not relevant          | Green (KO, 1, 2, 3)
 * </pre>
 * <br />
 * <h3>Result Category:</h3>
 * <br />
 * A category can be "relevant" or "not relevant".<br />
 * Die folgenden Fragen werden durchlaufen, bis ein Ergebnis feststeht:<br />
 * Wenn die Kategorie nicht relevant ist, ist das Ergebnis Grün.<br />
 * Wenn eine Maßnahme der Kritikalität KO „teilweise umgesetzt“ oder „nicht umgesetzt“ ist, ist das Ergebnis Dunkelrot.<br />
 * Wenn eine Maßnahme der Kritikalität 1 „nicht umgesetzt“ oder mehr als 20% der Maßnahmen der Kritikalität 1 „teilweise umgesetzt“ sind, ist das Ergebnis Rot.<br />
 * Wenn eine Maßnahme der Kritikalität 2 „nicht umgesetzt“ oder mehr als 20% der Maßnahmen der Kritikalität 2 „teilweise umgesetzt“ sind, ist das Ergebnis Orange.<br />
 * Wenn eine Maßnahme der Kritikalität 3 „nicht umgesetzt“ oder mehr als 20% der Maßnahmen der Kritikalität 3 „teilweise umgesetzt“ sind, ist das Ergebnis Gelb.<br />
 * Wenn keine der bisherigen Fragen zu einem Ergebnis geführt hat, ist das Ergebnis Grün.<br />
 * <br />
 * <br />
 * <h3>Ergebnis Domäne:</h3>
 * <br />
 * Die folgenden Fragen werden durchlaufen, bis ein Ergebnis feststeht:<br />
 * Wenn eine Kategorie das Ergebnis Dunkelrot hat, ist das Ergebnis Dunkelrot.<br />
 * Wenn eine Kategorie das Ergebnis Rot hat, ist das Ergebnis Rot.<br />
 * Wenn eine Kategorie das Ergebnis Orange hat, ist das Ergebnis Orange.<br />
 * Wenn eine Kategorie das Ergebnis Gelb hat, ist das Ergebnis Gelb.<br />
 * Wenn keine der bisherigen Fragen zu einem Ergebnis geführt hat, ist das Ergebnis Grün.<br />
 * <br />
 * <br />
 * <h3>Ergebnis Asset-Typ:</h3>
 * <br />
 * Die folgenden Fragen werden durchlaufen, bis ein Ergebnis feststeht:<br />
 * Wenn eine Domäne das Ergebnis Dunkelrot hat, ist das Ergebnis Dunkelrot.<br />
 * Wenn eine Domäne das Ergebnis Rot hat, ist das Ergebnis Rot.<br />
 * Wenn eine Domäne das Ergebnis Orange hat, ist das Ergebnis Orange.<br />
 * Wenn eine Domäne das Ergebnis Gelb hat, ist das Ergebnis Gelb.<br />
 * Wenn keine der bisherigen Fragen zu einem Ergebnis geführt hat, ist das Ergebnis Grün.<br />
 */
public class ScoreCalculator implements UpdateCalculationListener {

	/**
	 * Der Prozentsatz für die Berechnung
	 */
	private static final int PERCENTAGE = Config.getScorePercentage(20);
	
	private MutableTreeNode rootnode;
	private int score = -1;
	private Vector<ScoreListener> scorelisteners = new Vector<ScoreListener>();

	public ScoreCalculator(MutableTreeNode rootnode) {
		this.rootnode = rootnode;
	}
	
	private void calculate() {
		score = -1;
//		try{
			score = getScore(rootnode);
//		}catch(ScoreIncompleteException e) {
//			score = -1;
//		}
//		if(score == -1) {
//			Stack<TreeNode> layers = new Stack<TreeNode>();
//			layers.add(rootnode);
//			while(layers.size() > 0) {
//				TreeNode node = layers.pop();
//				for(int i = 0 ; i < node.getChildCount() ; ++i) {
//					TreeNode child = node.getChildAt(i);
//					if(child instanceof Layer) {
//						((Layer) child).setScore(-1);
//					}
//					layers.add(child);
//				}
//			}
//		}
	}

	@Override
	public void valueChanged(UpdateCalculationEvent e) {
		calculate();
		for(ScoreListener sl : scorelisteners) {
			sl.valueChanged(score);
		}
	}
	
	public void addScoreListener(ScoreListener sl) {
		scorelisteners.add(sl);
	}
	
	/**
	 * Here the score is calculated as described above.
	 * @param node
	 * @return Returns the score of the given element
	 * @throws ScoreIncompleteException
	 */
	private int getScore(MutableTreeNode node)/* throws ScoreIncompleteException*/ {
		int tmpscore = -1;
		
		//If the score depends on the children, it does not depend on the node
		if(evaluateChildren(node)) {
			if(node instanceof Category) {//If category, then the children are action and it applies 20% rule
				tmpscore = 4;
				int[][] countperscore = new int[4][Backend.getInstance().getStati().length];
				for(int i = 0 ; i < node.getChildCount() ; ++i) {
					TreeNode childAt = node.getChildAt(i);
					if(childAt instanceof Measure) {
						Measure m = (Measure) childAt;//m.getScoreis the value on the fallen down, if not implemented or more than 20% partially implemented

						int complexAnswer = m.getAnswer().getComplexAnswer();
						if(complexAnswer >= 0) {
							complexAnswer--;
						}
						if(complexAnswer < 0) {//nicht ausgefüllt
//							throw new ScoreIncompleteException();
//							return -1;
							tmpscore = -1;
							break;
						}
						countperscore[m.getMeasureScore()][complexAnswer]++;
						
						//set calculated score for the Action
						if(complexAnswer < 0) {
							m.setScore(-1);
						}else if(complexAnswer == 1 || complexAnswer == 2) {//partially or not implemented
							m.setScore(m.getMeasureScore());
						}else{
							m.setScore(4);
						}
					}else{
						throw new IllegalArgumentException("Es gibt eine Schicht, die hier nicht sein darf. " + node.getClass());
					}
				}
				
				if(tmpscore >= 0) {
					for(int ix = 0 ; ix < 4; ++ix) {
						if(ix==0) {//KO
							if(countperscore[ix][2] > 0) {//Not translated
								tmpscore = 0;
								break;
							} else if(countperscore[ix][1] > 0) {//partially implemented
								tmpscore = 0;
								break;
							}
						} else {//1, 2, 3
							if(countperscore[ix][2] > 0) {//Not translated
								tmpscore = ix;
								break;
							} else if(countperscore[ix][1] > 0) {//partially implemented
								//Number of measures for the 20% is the number of measures that are not "not relevant", ie:
								int measurecount = countperscore[ix][0] + countperscore[ix][1] + countperscore[ix][2];//implemented, partially implemented, not implemented. The "not relevant" measures are ignored.
								if((((float) countperscore[ix][1])/((float) measurecount)) > (PERCENTAGE / 100)) {
									tmpscore = ix;
									break;
								}
							}
						}
					}
				}
			}else{//Otherwise, only the minimum is calculated.
				tmpscore = 4;
				for(int i = 0 ; i < node.getChildCount() ; ++i) {
					tmpscore = Math.min(tmpscore, getScore((MutableTreeNode) node.getChildAt(i)));
				}
			}
		}else{
			tmpscore = getNodeScore(node);
		}
		
		if(node instanceof Layer) {
			((Layer) node).setScore(tmpscore);
		}
		
		return tmpscore;
	}
		
	private boolean evaluateChildren(MutableTreeNode node) {
		if(node instanceof Backend) {
			return true;
		}else if(node instanceof AssetType) {
			return true;
		}else if(node instanceof Domain) {
			return true;
		}else if(node instanceof Category) {
			return ((Category) node).getAnswer().getAnswer() == 0; //only if yes
		}else if(node instanceof Measure) {
			if(((Measure) node).isLayer3()) {
				return ((Measure) node).getAnswer().getYesNoAnswer() == 0; //only if yes
			}else{
				return false;
			}
		}
		throw new IllegalArgumentException("There is a layer that is undefined. " + node.getClass());
	}
	
	private int getNodeScore(MutableTreeNode node)/* throws ScoreIncompleteException*/ {
		if(node instanceof Category) {
			if(((Category) node).getAnswer().getAnswer() != 0) {
				return 4;
			}else{
				return -1;
			}
		}
		throw new IllegalArgumentException("Layer has no independent result: " + node.getClass());
	}

}
