package ie.ucd.cobweb.semantic;
import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SurveyVerifier {
	private Document definition;

	public SurveyVerifier(String location) throws IOException {
		File survey = new File(location);
		definition = Jsoup.parse(survey, null);
	}

	public boolean verify() {
		return definition != null;
	}
}
