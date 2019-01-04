package bsi.lars.backend;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import bsi.lars.backend.ReportGenerator.formats;

public class FileFormatFilter extends FileFilter {
	private formats format;
	
	public FileFormatFilter(formats format) {
		this.format = format;
	}
	@Override
	public String getDescription() {
		return format.name().toUpperCase() + "-File" + " " + "*." + format.name();
	}
	
	@Override
	public boolean accept(File file) {
		if(file.isDirectory()) {
			return true;
		}
		return file.getName().endsWith(format.name());
	}
	
	public formats getFormat() {
		return format;
	}
}
