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
 * <h1>Berechnet das Ergebnis für die gegebenen Antworten</h1>
 * <br />
 * <h2>Berechnungsvorschrift:</h2>
 * Bei den Schichten Asset-Typ, Domäne und für den Geamtscore ist der Score<br /> 
 * das Minimum der Scores der Unter-Elemente. <br />
 * Für Gesamtscore sind die Unter-Elemente die Asset-Typen,<br /> 
 * für die Asset-Typen die Domänen und <br />
 * für die Domänen die Kategorien.<br />
 * <br />
 * Für die Kategorien und Maßnahmen wird folgendermaßen vorgegangen:<br />
 * Die Maßnahmen haben eine Kritikalität(Measure#getMeasureScore()) und eine Antwort(Measure#getAnswer()#getComplexAnswer()).<br />
 * Den Kritikalitätsstufen werden Farben zugeordnet:<br />
 * - KO : dunkelrot<br />
 * -  1 : rot<br />
 * -  2 : orange<br />
 * -  3 : gelb<br />
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
 * Es gilt:<br />
 * Wenn eine Maßnahme von Kritikalität X als „nicht umgesetzt“ markiert wurde, kann das Gesamtergebnis nicht besser sein, als Y, mit<br /> 
 * Y = Dunkelrot für X = KO,<br />
 * Y = Rot für X = 1,<br />
 * Y = Orange für X = 2,<br />
 * Y = Gelb für X = 3.<br />
 * <br />
 * <br />
 * <h3>Ergebnis Maßnahme:</h3>
 * <br />
 * Eine Maßnahme kann „umgesetzt“, „teilweise umgesetzt“, „nicht umgesetzt“ oder „nicht relevant“ sein.<br />
 * <br />
 * <pre>
 * Auswahl             | Ergebnis der Maßnahme (bei Kritikalität)
 * --------------------|-----------------------------------------
 * umgesetzt           | Grün (KO, 1, 2, 3)
 * teilweise umgesetzt | Dunkelrot (KO), Rot (1), Orange (2), Gelb (3)
 * nicht umgesetzt     | Dunkelrot (KO), Rot (1), Orange (2), Gelb (3)
 * nicht relevant      | Grün (KO, 1, 2, 3)
 * </pre>
 * <br />
 * <h3>Ergebnis Kategorie:</h3>
 * <br />
 * Eine Kategorie kann „relevant“ oder „nicht relevant“ sein.<br />
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
	 * Hier wird der Score, wie oben beschreiben, berechnet.
	 * @param node
	 * @return Gibt den Score des gegebenen Elements zurück
	 * @throws ScoreIncompleteException
	 */
	private int getScore(MutableTreeNode node)/* throws ScoreIncompleteException*/ {
		int tmpscore = -1;
		
		//Wenn der Score von den Kindern abhängt, hängt er nicht vom Knoten ab
		if(evaluateChildren(node)) {
			if(node instanceof Category) {//Wenn Kategorie, dann sind die Kinder Maßnahmen und es gilt 20% Regel
				tmpscore = 4;
				int[][] countperscore = new int[4][Backend.getInstance().getStati().length];
				for(int i = 0 ; i < node.getChildCount() ; ++i) {
					TreeNode childAt = node.getChildAt(i);
					if(childAt instanceof Measure) {
						Measure m = (Measure) childAt;//m.getScore ist der Wert auf den nach unten gefallen wird, wenn nicht umgesetzt oder mehr als 20% teilweise umgesetzt

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
						
						//setze berechneten Score für die Maßnahme
						if(complexAnswer < 0) {
							m.setScore(-1);
						}else if(complexAnswer == 1 || complexAnswer == 2) {//teilweise oder nicht umgesetzt
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
							if(countperscore[ix][2] > 0) {//nicht umgesetzt
								tmpscore = 0;
								break;
							} else if(countperscore[ix][1] > 0) {//teilweise umgesetzt
								tmpscore = 0;
								break;
							}
						} else {//1, 2, 3
							if(countperscore[ix][2] > 0) {//nicht umgesetzt
								tmpscore = ix;
								break;
							} else if(countperscore[ix][1] > 0) {//teilweise umgesetzt
								//Anzahl der Maßnahmen für die 20% ist die Zahl der Maßnahmen, die nicht "nicht relevant sind", also: 
								int measurecount = countperscore[ix][0] + countperscore[ix][1] + countperscore[ix][2];//umgesetzt, teilweise umgesetzt, nicht umgesetzt. Die "nicht relevant"en Maßnahmen werden übergangen.
								if((((float) countperscore[ix][1])/((float) measurecount)) > (PERCENTAGE / 100)) {
									tmpscore = ix;
									break;
								}
							}
						}
					}
				}
			}else{//Ansonsten wird nur das Minimum berechnet.
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
		throw new IllegalArgumentException("Es gibt eine Schicht, die nicht definiert ist. " + node.getClass());
	}
	
	private int getNodeScore(MutableTreeNode node)/* throws ScoreIncompleteException*/ {
		if(node instanceof Category) {
			if(((Category) node).getAnswer().getAnswer() != 0) {
				return 4;
			}else{
				return -1;
			}
		}
		throw new IllegalArgumentException("Schicht hat kein eigenständiges Ergebnis: " + node.getClass());
	}

}
