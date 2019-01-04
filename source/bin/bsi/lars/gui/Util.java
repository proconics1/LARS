package bsi.lars.gui;

public class Util {
	
	public static String breakLines(String string, int maxcharacterperline) {
		if(string.length() < maxcharacterperline) {
			return string;
		}
		StringBuilder result = new StringBuilder();
		
		
		for(String line : string.split("\n")) {
			if(line.length() > maxcharacterperline) {
				StringBuilder newline = new StringBuilder();
				StringBuilder newblock = new StringBuilder();
				for(String word : line.split(" ")) {
					if(newline.length() + word.length() > maxcharacterperline) {
						if(newline.length() == 0) {
							newblock.append(word);
							newblock.append("\n");
						}else{
							newblock.append(newline);
							newblock.append("\n");
							newline = new StringBuilder();
							newline.append(word);
							newline.append(" ");
						}
					}else{
						newline.append(word);
						newline.append(" ");
					}
				}
				result.append(newblock);
				if(newline.length() > 0) {
					result.append(newline);
					result.append("\n");
				}else{
					result.append("\n");
				}
			}else{
				result.append(line);
				result.append("\n");
			}
		}
//		for(String word : string.split(" ")) {
//			if(line.length() + word.length() > maxcharacterperline) {
//				result.append(line);
//				result.append(" \n");
//				line = new StringBuilder();
//				line.append(word);
//				line.append(" ");
//			}else{
//				line.append(word);
//				line.append(" ");
//			}
//		}
		
		return result.toString();
	}

	public static String toHTML(String string) {
//		return "<html><p align=\"justify\">" + string + "</p></html>";
		return "<html><p align=\"justify\">" + string.replaceAll("\n", "<br />") + "</p></html>";
//		return "<html><p align=\"justify\">" + string.replaceAll("\n", " ")/*.replaceAll("\n", "<br />")*/ + "</p></html>";
	}
}
