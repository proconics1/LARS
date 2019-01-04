package bsi.lars.backend.reports;

import java.awt.Toolkit;
import java.io.InputStream;
import java.util.Date;
import java.util.Vector;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import bsi.lars.backend.Backend;
import bsi.lars.backend.data.Case;
import bsi.lars.backend.data.User;
import bsi.lars.backend.datastore.layers.Category;
import bsi.lars.backend.datastore.layers.Measure;
import bsi.lars.gui.TMainFrame;

public abstract class Report {

	public InputStream getXSL() {
		return PriorizedMeasures.class.getResourceAsStream("/resources/" + getClass().getSimpleName() + ".xsl");
	}
	

	protected String generateReport(String name, User user, Case _case, int score, String content) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		sb.append("<report>");
		sb.append("<name>");
		sb.append(toCData(name));
		sb.append("</name>\n");
		sb.append(user.toXML());
		sb.append(_case.toXML());
		sb.append("<score>");
		sb.append(score);
		sb.append("</score>");
		sb.append(content);
		sb.append("</report>");
		
		return sb.toString();
	}

	protected String toCData(String data) {
		return "<![CDATA["+data+"]]>";
	}

	protected abstract Vector<Category> getFilteredCategories(Backend backend);
	protected abstract Vector<Measure> getFilteredMeasures(Backend backend);
	

	/**
	 *Here it is possible to influence the output of the categories.
	 * @param categories
	 * @return Returns the output for the passed categories
	 * @throws Exception
	 */
	protected abstract String generateCategoriesOutput(Vector<Category> categories) throws Exception;
	
	
	/**
	 * Here it is possible to influence the output of the measures.
	 * @param measures
	 * @return Returns the output for the submitted actions
	 * @throws Exception
	 */
	protected abstract String generateMeasuresOutput(Vector<Measure> measures) throws Exception;
	
	/**
	 * Here it is possible to return additional data for the report.
	 * @return null, If no further information is needed. When data is returned, it is written to the <info></ info> block of the generated XML.
	 */
	protected abstract String generateAdditionalDetails();
	
	@SuppressWarnings("unchecked")
	public String getXML(Backend backend) throws Exception {
		Vector<Category> categories = getFilteredCategories(backend);
		Vector<Measure> measures = getFilteredMeasures(backend);
		
		StringBuilder content = new StringBuilder();
		
		content.append("<date>\n");
		{
			content.append(
							toCData(
									java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, java.util.Locale.ENGLISH).format(new Date())
									)
							);
		}
		content.append("</date>\n");
		
/*		content.append("<image>\n");
		{
			content.append(
							toCData(
									Base64.encode(null)
									)
							);
		}
		content.append("</image>\n");
		*/
		String additionalReportDetails = generateAdditionalDetails();
		if(additionalReportDetails != null) {
			content.append(additionalReportDetails);
			content.append("\n");
		}


		content.append("<categories>\n");
		content.append(generateCategoriesOutput((Vector<Category>) categories.clone()));
		content.append("</categories>\n");
		
		
		content.append("<measures>\n");
		content.append(generateMeasuresOutput((Vector<Measure>) measures.clone()));
		content.append("</measures>\n");
		
		int[] answercount = new int[Backend.getInstance().getStati().length];
		int unansweredcount = 0;
//		int[] scorecount = new int[4];
		//                                                                                 Scores
		int[][] measurescorecountbyanswer = new int[Backend.getInstance().getStati().length][4];
		//                                                  Possible answers
		int[] measurescoresbyunanswered = new int[4];
		
		for(Measure m : measures) {
			int answer = m.getAnswer().getComplexAnswer();
			if(answer == 0) {
				System.out.println();
			}
			if(answer > 0) {
				++answercount[answer - 1];
				++measurescorecountbyanswer[answer - 1][m.getMeasureScore()];
			}else{
				++unansweredcount;
				++measurescoresbyunanswered[m.getMeasureScore()];
			}
		}
		
		int answerindex = answercount.length - 1;
		//Hide not existing cake pieces.
		while(answerindex > 0 && answercount[answerindex] > 0) {
			--answerindex;
		}
		if(answerindex >= 0) {
			content.append("<answerstatistic>\n");
			for(int i = 0 ; i <= answerindex ; ++i) {
				content.append("<pieslice name=\"");
				content.append(Backend.getInstance().getStati()[i]);
				content.append("\">");
				content.append(answercount[i]);
				content.append("</pieslice>\n");
			}
			if(unansweredcount > 0) {
				content.append("<pieslice name=\"");
				content.append("Not Answered");
				content.append("\">");
				content.append(unansweredcount);
				content.append("</pieslice>\n");
			}
			content.append("</answerstatistic>\n");
		}

		String[] scorenames = new String[]{"KO", "Criticality 1", "Criticality 2", "Criticality 3"};
		
		for(int answer = 0; answer < measurescorecountbyanswer.length; answer++) {
			int[] scorecount = measurescorecountbyanswer[answer];

			int sum = 0;
			for(int i : scorecount) {
				sum += i;
			}
			if(sum == 0) {
				continue;
			}
			
			content.append("<scorestatistic>\n");
			content.append("<answer>" + Backend.getInstance().getStati()[answer] + "</answer>\n");
			for(int i = 0 ; i < scorecount.length ; ++i) {
				content.append("<pieslice name=\"");
				content.append(scorenames[i]);
				content.append("\">");
				content.append(scorecount[i]);
				content.append("</pieslice>\n");
			}
			content.append("</scorestatistic>\n");
		}
		if(unansweredcount > 0) {
			content.append("<scorestatistic>\n");
			content.append("<answer>" + "Not Answered" + "</answer>\n");
			for(int i = 0 ; i < measurescoresbyunanswered.length ; ++i) {
				content.append("<pieslice name=\"");
				content.append(scorenames[i]);
				content.append("\">");
				content.append(measurescoresbyunanswered[i]);
				content.append("</pieslice>\n");
			}
			content.append("</scorestatistic>\n");
		}
		
		return generateReport(getName(), backend.getCurrentUser(), backend.getCurrentCase(), backend.getScore(), content.toString());
	}

	protected abstract String getName();
	public abstract String getDefaultFileName();
}
