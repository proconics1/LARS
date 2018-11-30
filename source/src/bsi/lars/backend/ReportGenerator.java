package bsi.lars.backend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import bsi.lars.backend.reports.ICSStatus;
import bsi.lars.backend.reports.PriorizedMeasures;
import bsi.lars.backend.reports.Report;

public class ReportGenerator {
	
	public static enum formats {
		pdf, xml, rtf, txt, eps, png, fo
	}
	
	private static final String UNSUPPORTED_FORMAT = "Format nicht unterstützt";

	public static void generateReport(Report report, File destinationFile, formats format) throws Exception {
		generateReport(report.getXML(Backend.getInstance()), report.getXSL(), destinationFile, format);
	}
	
	private static void generateReport(String xml, InputStream xsl, File destinationFile, formats format) throws Exception {
//		Piechart
//		http://www.svgopen.org/2003/papers/CreatingSVGPieChartsThroughWebService/
//		SVG in FO
//		<fo:instream-foreign-object>
//			SVG GOES HERE
//		</fo:instream-foreign-object>
//		Misc Resources
//		http://en.wikibooks.org/wiki/XQuery/Generating_PDF_from_XSL-FO_files#Including_SVG_Images_in_your_PDF_files
		
		export(xml, xsl, format, destinationFile);
	}

	private static void export(String xml, InputStream xsl, formats format, File destinationFile) throws IOException, TransformerException, FOPException, TransformerFactoryConfigurationError {
		switch(format) {
		case xml:
			FileWriter fw = new FileWriter(destinationFile);
			fw.write(xml);
			fw.close();
			break;
		case pdf:
		case rtf:
		case txt:
		case eps:
		case png:
		case fo:
			transformXSLFO(new StringReader(xml), xsl, destinationFile, format);
			break;
		default:
			throw new IllegalArgumentException(UNSUPPORTED_FORMAT);
		}
	}

	private static void transformXSLFO(Reader xmlFile, InputStream xslt, File output, formats format) throws TransformerFactoryConfigurationError, FOPException, TransformerException, IOException {
		_transformXSLFO(xmlFile, xslt, output, format);
	}	
	
	private static void transformXSLT(Reader xmlFile, InputStream xsltFile, File out) throws TransformerException {
		_transformXSLT(xmlFile, xsltFile, new StreamResult(out));
	}
	
	private static void _transformXSLT(Reader xmlFile, InputStream xsltFile, StreamResult out) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(xsltFile);
        Transformer transformer = factory.newTransformer(xslt);

        Source text = new StreamSource(xmlFile);
        transformer.transform(text, out);
	}
		
	private static void _transformXSLFO(Reader xmlFile, InputStream xslt, File out, formats format) throws FOPException, TransformerFactoryConfigurationError, TransformerException, IOException {
		switch (format) {
		case pdf:
			__transformXSLFO(xmlFile, xslt, out, MimeConstants.MIME_PDF);
			break;
		case rtf:
			__transformXSLFO(xmlFile, xslt, out, MimeConstants.MIME_RTF);
			break;
		case txt:
			__transformXSLFO(xmlFile, xslt, out, MimeConstants.MIME_PLAIN_TEXT);
			break;
		case eps:
			__transformXSLFO(xmlFile, xslt, out, MimeConstants.MIME_EPS);
			break;
		case png:
			__transformXSLFO(xmlFile, xslt, out, MimeConstants.MIME_PNG);
			break;
		case fo:
//			__transformXSLFO(xmlFile, xslt, out, MimeConstants.MIME_FOP_IF);
//			break;
			transformXSLT(xmlFile, xslt, out);
			break;
		default:
			throw new IllegalArgumentException(UNSUPPORTED_FORMAT);
		}
	}

	private static void __transformXSLFO(Reader xmlFile, InputStream xslt, File output, String mime) throws TransformerFactoryConfigurationError, FOPException, TransformerException, IOException {
		FopFactory fopFactory = FopFactory.newInstance();
		Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xslt));
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		foUserAgent.setOutputFile(output);
		OutputStream out = new FileOutputStream(output);
		Fop fop = fopFactory.newFop(mime, foUserAgent, out);
		Result res = new SAXResult(fop.getDefaultHandler());
		transformer.transform(new StreamSource(xmlFile), res);
		out.close();
	}

	/**
	 * Kann verwendet werden, um die Bearbeitung der Bericht-Templates (.xsl) zu beschleunigen.
	 * @param args
	 */
	public static void main(String[] args) {
		File xmlFile = null;
		formats frmt = null;
		File outFile = null;
		Report report = null;
		
		boolean exit = false;
		try{
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				switch (arg) {
				case "-x":
					xmlFile = new File(args[++i]);
					break;
				case "-r":
					switch(args[++i].toLowerCase()) {
					case "ics":
						report = new ICSStatus();
						break;
					case "measures":
						report = new PriorizedMeasures();
						break;
					}
					break;
				case "-f":
					frmt = formats.valueOf(args[++i]);
					break;
				case "-o":
					outFile = new File(args[++i]);
					break;
				}
			}
		}catch(Exception e) {
			System.out.println("Ungültige Eingebe:");
			exit = true;
		}
		if(xmlFile == null) {
			exit = true;
		}
		if(report == null) {
			exit = true;
		}
		if(outFile == null) {
			exit = true;
		}
		if(frmt == null) {
			exit = true;
		}
		if(exit) {
			usage();
			System.exit(0);
		}
		try{
			java.io.RandomAccessFile raf = new java.io.RandomAccessFile(xmlFile, "r");
			String xml = "", line;
		while((line = raf.readLine()) != null) {
			xml += line;
			xml += "\n";
		}
		raf.close();
		generateReport(
				xml, 
				report.getXSL(), 
				outFile, 
				frmt);
		}catch(Exception e) {
			System.out.println("Irgendetwas ist fehlgeschlagen.");
			e.printStackTrace();
		}
	}
	
	private static void usage() {
		System.out.println("-x XML-DATEI");
		System.out.println("-r REPORT");
		System.out.println("\twobei REPORT eines von");
		System.out.println("\tics: ICS Übersicht");
		System.out.println("\tmeasures: Priorisierter Maßnahmenkatalog");
		System.out.println("-f FORMAT");
		System.out.println("\twobei FORMAT eines von");
		System.out.printf("\t");
		formats[] values = formats.values();
		for (int i = 0; i < values.length; i++) {
			formats f = values[i];
			if(i+1 < values.length) {
				System.out.printf("%s, ", f);
			}else{
				System.out.printf("%s", f);
			}
		}
		System.out.println("-o AUSGABE-DATEI");
	}
}
