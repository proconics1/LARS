package bsi.lars.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import bsi.lars.answer.Answer;
import bsi.lars.answer.MeasureAnswer;
import bsi.lars.backend.Backend;
import bsi.lars.backend.datastore.layers.Measure;

/**
 * Element der Graphischen Oberfläche für eine {@link Measure}
 * 
 *
 */
public class MeasurePanel extends JPanel {
	
	private static final long serialVersionUID = 2913670099311191169L;
	
//	private static final int TOOLTIPDURATION = 30*60*1000;//Pre-Overlay solution

	private static final boolean SEPARATORAFTERMEASURE = true;

	private static ImageIcon questionIcon;

	/**
	 * Panel zusammenbauen
	 * Nur vorhanden, damit im Eclipse WindowsBuilder etwas angezeigt wird
	 */
	public MeasurePanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		generateMeasurePanel(this, 0, null, new Answer(null){
			@Override
			public void load() {}
			@Override
			protected String specificXML() throws Exception {return "";}
		}, new HideGroup());
		
	}
	

	/**
	 * 
	 * @param parent
	 * @param startgridy
	 * @param meas
	 * @param hgm 
	 * @return returns next startgridy
	 */
	public static int generateMeasurePanel(JPanel parent, int startgridy, Measure meas, Answer parentAnswer, HideGroup hgm) {
		int subMeasureCount = meas.getChildCount();
		
		if(subMeasureCount > 0) {
			JPanel separator_upper = new JPanel();
			separator_upper.setForeground(Color.BLACK);
			GridBagConstraints gbc_separator_upper = new GridBagConstraints();
			gbc_separator_upper.fill = GridBagConstraints.HORIZONTAL;
			gbc_separator_upper.gridwidth = 4;
			gbc_separator_upper.insets = new Insets(0, 5, 5, 5);
			gbc_separator_upper.gridx = 0;
			gbc_separator_upper.gridy = startgridy ++;
			parent.add(separator_upper, gbc_separator_upper);
			
			MatteBorder mb_upper = new MatteBorder(1, 0, 0, 0, (Color) new Color(0, 0, 0));
			separator_upper.setBorder(new TitledBorder(mb_upper, meas.getName(), TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
			
			hgm.add(separator_upper);
		}
		
		JLabel lblName = new JLabel(/*indent(meas) + meas.getNumber() + *//*" to_id " + meas.getTo_measure_id() + *//*' ' + */meas.getName()/* + " Score: " + meas.getScore()*/);
		lblName.setFont(lblName.getFont().deriveFont(Font.BOLD));
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblName.insets = new Insets(0, 5, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = startgridy + 0;
		gbc_lblName.gridwidth = 2;
		parent.add(lblName, gbc_lblName);
		
		hgm.add(lblName);
		
		JLabel/*JTextArea*/ lblShortDescription = new JLabel/*JTextArea*/(Util.toHTML(/*Util.breakLines(*/meas.getDescription()/*, MAXCHARACTERPERDESCRIPTIONLINE)*/));
		lblShortDescription.setBackground(new Color(240, 240, 240));
//		lblShortDescription.setColumns(70);
//		lblShortDescription.setWrapStyleWord(true);
//		lblShortDescription.setLineWrap(true);
//		lblShortDescription.setEditable(false);
		lblShortDescription.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblShortDescription = new GridBagConstraints();
		gbc_lblShortDescription.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblShortDescription.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblShortDescription.insets = new Insets(0, 5, 5, 5);
		gbc_lblShortDescription.gridx = 0;
		gbc_lblShortDescription.gridy = startgridy + 1;
		gbc_lblShortDescription.gridwidth = 2;
		gbc_lblShortDescription.weightx = 1.0;
		gbc_lblShortDescription.weighty = 1.0;
		parent.add(lblShortDescription, gbc_lblShortDescription);
		
		hgm.add(lblShortDescription);
		
		if(meas.getDescriptionLong() != null && meas.getDescriptionLong().length() > 0) {
			if(questionIcon == null) {
				Icon tmpIcon = UIManager.getIcon("OptionPane.questionIcon");
				
				Image image = null;
				int iconsize = 15;
				if(tmpIcon instanceof ImageIcon) {
					image = ((ImageIcon) tmpIcon).getImage().getScaledInstance(iconsize, iconsize, Image.SCALE_SMOOTH);
				}else{
					iconsize *= 2;
					image = new BufferedImage(iconsize, iconsize, BufferedImage.TYPE_INT_ARGB);
					Graphics g = image.getGraphics();
					g.setColor(Color.BLACK);
					g.drawOval(0, 0, iconsize, iconsize);
					g.setColor(Color.WHITE);
					g.drawOval(1, 1, iconsize - 2, iconsize - 2);
					g.setColor(Color.BLUE);
					g.fillOval(2, 2, iconsize - 4, iconsize - 4);
					g.setColor(Color.WHITE);
					int fontsize = 5;//is not fontsize fontsize would be float, but works
					iconsize /= 2;
					Image circle = image.getScaledInstance(iconsize, iconsize, Image.SCALE_SMOOTH);
					image = new BufferedImage(iconsize, iconsize, BufferedImage.TYPE_INT_ARGB);
					g = image.getGraphics();
					g.drawImage(circle, 0, 0, null);
					g.setColor(Color.WHITE);
					g.setFont(Font.getFont("Arial", g.getFont()).deriveFont(fontsize).deriveFont(Font.BOLD));
					g.drawString("?", (int) ((((float)iconsize) - 1.*fontsize) / 2.), (int) ((((float)iconsize) + 2.*fontsize) / 2.));
				}
				questionIcon = new ImageIcon(image);
			}
		}

		ImageIcon icon = questionIcon;
		
		LongDescriptionLabel lblDescriptionlong = new LongDescriptionLabel(icon);
		GridBagConstraints gbc_lblDescriptionlong = new GridBagConstraints();
		gbc_lblDescriptionlong.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescriptionlong.gridx = 2;
		gbc_lblDescriptionlong.gridy = startgridy + 0;
		parent.add(lblDescriptionlong, gbc_lblDescriptionlong);
		
		hgm.add(lblDescriptionlong);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridheight = 3;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 3;
		gbc_scrollPane.gridy = startgridy + 0;
		parent.add(scrollPane, gbc_scrollPane);
		
		hgm.add(scrollPane);
		
		JTextArea txtComment = new JTextArea();
		txtComment.setRows(5);
		txtComment.setLineWrap(true);
		scrollPane.setViewportView(txtComment);
//		txtComment.setText("Kommentar");
		txtComment.setColumns(10);
		
		hgm.add(txtComment);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.anchor = GridBagConstraints.EAST;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = startgridy + 2;
		parent.add(panel, gbc_panel);
		
		hgm.add(panel);
		
		//Add long description View Icon if long description is available.
		if(meas.getDescriptionLong() != null) {
			String text = 
					"<html>" + 
//					"<p"+" align=\"justify\">" +
					"<pre>" +
					Util.breakLines(
					meas.getDescriptionLong()
					,130)
//					.replaceAll("\r\n", "<br />")
//					.replaceAll("\r", "<br />")
//					.replaceAll("\n", "<br />")
					+ 
					"</pre>" +
//					"</p>" +
					"</html>";

			lblDescriptionlong.setLongDescription(text);
			lblDescriptionlong.setMeasureName(meas.getName());
			
			//ToolTipText //Pre-Overlay-Solution
//			lblDescriptionlong.setToolTipText(text);
//			
//			lblDescriptionlong.addMouseListener(new MouseListener() {
//				final int defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
//				final int defaultDismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
//				
//				@Override
//				public void mouseReleased(MouseEvent e) {}
//				@Override
//				public void mousePressed(MouseEvent e) {}
//				@Override
//				public void mouseClicked(MouseEvent e) {}
//				
//				@Override
//				public void mouseEntered(MouseEvent e) {
//					ToolTipManager.sharedInstance().setInitialDelay(0);
//					ToolTipManager.sharedInstance().setDismissDelay(TOOLTIPDURATION);
//				}
//				
//				@Override
//				public void mouseExited(MouseEvent e) {
//					ToolTipManager.sharedInstance().setInitialDelay(defaultInitialDelay);
//					ToolTipManager.sharedInstance().setDismissDelay(defaultDismissDelay);
//				}
//			});
			lblDescriptionlong.addMouseListener(TMainFrame.getInstance().getLongDescriptionHandler());
			
		}else{
			lblDescriptionlong.setVisible(false);
		}

//		PossibleMeasure pm = null;
		
		MeasureAnswer answer = null;
		
		if(subMeasureCount > 0) {
			throw new IllegalAccessError("Measure3 is never used.");
			//INFO: Measure3 is never used. So this is not necessary
//			GridBagLayout gbl_panel = new GridBagLayout();
//			gbl_panel.columnWidths = new int[]{37, 47, 0};
//			gbl_panel.rowHeights = new int[]{23, 0};
//			gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
//			gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
//			panel.setLayout(gbl_panel);
//		
//			TButtonGroup buttongroup = new TButtonGroup();
//
//			JRadioButton rdbtnYes = new JRadioButton("Ja");
//			GridBagConstraints gbc_rdbtnYes = new GridBagConstraints();
//			gbc_rdbtnYes.anchor = GridBagConstraints.NORTHWEST;
//			gbc_rdbtnYes.gridx = 0;
//			gbc_rdbtnYes.gridy = 0;
//			panel.add(rdbtnYes, gbc_rdbtnYes);
//			buttongroup.add(rdbtnYes);
//			
//			JRadioButton rdbtnNo = new JRadioButton("Nein");
//			GridBagConstraints gbc_rdbtnNo = new GridBagConstraints();
//			gbc_rdbtnNo.anchor = GridBagConstraints.NORTHWEST;
//			gbc_rdbtnNo.gridx = 1;
//			gbc_rdbtnNo.gridy = 0;
//			panel.add(rdbtnNo, gbc_rdbtnNo);
//			buttongroup.add(rdbtnNo);
//			
//			if(parent instanceof ActionListener) {
//				buttongroup.addActionListener((ActionListener) parent);
//			}
//			
//			answer = meas.answer(txtComment, rdbtnYes, rdbtnNo);
//			parentAnswer.add(answer);
//			
//			pm = new PossibleMeasure(rdbtnYes, rdbtnNo);
		}else{
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{75, 119, 101, 91, 0};
			gbl_panel.rowHeights = new int[]{23, 0};
			gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			
			String[] stati = Backend.getInstance().getStati();
			
			JRadioButton[] statusselection = new JRadioButton[stati.length];
			TButtonGroup buttongroup = new TButtonGroup();
			
			int gridx = 0;
			for (int i = 0; i < stati.length; i++) {
				String status = stati[i];
				JRadioButton rdbtn = new JRadioButton(status);
				GridBagConstraints gbc_rdbtnDone = new GridBagConstraints();
				gbc_rdbtnDone.anchor = GridBagConstraints.NORTHWEST;
				gbc_rdbtnDone.insets = new Insets(0, 0, 0, 5);
				gbc_rdbtnDone.gridx = gridx++;
				gbc_rdbtnDone.gridy = 0;
				panel.add(rdbtn, gbc_rdbtnDone);
				statusselection[i] = rdbtn;
				buttongroup.add(rdbtn);
			}
			
			buttongroup.setCommentField(txtComment);
			
			if(parent instanceof ActionListener) {
				buttongroup.addActionListener((ActionListener) parent);
			}
			
			answer = meas.answer(txtComment, statusselection);
			parentAnswer.add(answer);
		}

		int gridy = startgridy + 3;
		if(subMeasureCount > 0) {
			
			throw new IllegalAccessError("Measure3 is never used.");
			//INFO: Measure3 is never used. So this is not necessary
//			for(Layer m : meas.getChildren()) {
//				if(m instanceof Measure) {
//					HideGroup hgsm = new HideGroup(true);
//					hgm.add(hgsm);
//					gridy++;
//					pm.add(((Measure) m).getTo_measure_id(), hgsm);
//					gridy = MeasurePanel.generateMeasurePanel(parent, gridy, ((Measure) m), answer, hgsm);
//				}
//			}
//			
//			JSeparator separator_lower = new JSeparator();
//			separator_lower.setForeground(Color.BLACK);
//			GridBagConstraints gbc_separator_lower = new GridBagConstraints();
//			gbc_separator_lower.fill = GridBagConstraints.HORIZONTAL;
//			gbc_separator_lower.gridwidth = 4;
//			gbc_separator_lower.insets = new Insets(0, 5, 5, 5);
//			gbc_separator_lower.gridx = 0;
//			gbc_separator_lower.gridy = gridy++;
//			parent.add(separator_lower, gbc_separator_lower);
//			
//			hgm.add(separator_lower);
//			
//			//answer yes or no?
		}else{
			//answer yes, no, partial, unneeded
		}
		
		if(SEPARATORAFTERMEASURE) {
			JSeparator separator_lower = new JSeparator();
			separator_lower.setForeground(Color.BLACK);
			GridBagConstraints gbc_separator_lower = new GridBagConstraints();
			gbc_separator_lower.fill = GridBagConstraints.HORIZONTAL;
			gbc_separator_lower.gridwidth = 4;
			gbc_separator_lower.insets = new Insets(0, 5, 5, 5);
			gbc_separator_lower.gridx = 0;
			gbc_separator_lower.gridy = gridy++;
			parent.add(separator_lower, gbc_separator_lower);
			
			hgm.add(separator_lower);
		}
		
		return gridy;
	}

}
