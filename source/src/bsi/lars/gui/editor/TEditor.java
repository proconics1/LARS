package bsi.lars.gui.editor;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TEditor extends JFrame {

	private static final long serialVersionUID = -218010525556137005L;
	
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public TEditor() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 600);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		TEditorPanel editorPanel = new TEditorPanel();
		contentPane.add(editorPanel, BorderLayout.CENTER);
	}

}
