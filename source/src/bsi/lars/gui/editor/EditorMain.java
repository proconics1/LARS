package bsi.lars.gui.editor;

import bsi.lars.gui.TMainFrame;

public class EditorMain {

	public static void main(String[] args) {
		TMainFrame.initializeProgram();
		new TEditor().setVisible(true);
	}

}
