package ie.ucd.cobweb.semantic.swe4cs;

import ie.ucd.cobweb.semantic.cli.FileCache;
import ie.ucd.cobweb.semantic.template.Freemarker;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class SWE4CS {

	public static final String ID = "ID";
	public static final String PROCEDURE = "procedure";
	public static final String OBSPROP = "observedProperty";
	public static final String FOI = "featureOfInterest";
	public static final String RESULT = "results";
	public static final String ENCODER = "exporter";
	public static final String PROCESS = "process";
	public static final String PROTOCOL = "protocol";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String SHAPE = "shape";
	public static final String DEFINITION = "definition";
	public static final String VALUE = "value";
	public static final String TIME = "time";
	public static final String SAMPLED = "sampled";
	public static final String CAMPAIGN = "campaign";
	public static final String META = "meta";
	public static final String SEPERATOR = "seperator";
	public static final String PEOPLE = "people";

	public static void export(FileCache cache, SWEObservation[] observations)
			throws IOException, TemplateException {
		String data;
		if (observations.length == 1)
			data = exportObservation(cache, observations[0]);
		else
			data = exportCollection(cache, observations);

		try {
			FileWriter fw = new FileWriter("output.xml.txt");
			fw.write(data);
			fw.close();

			validate(data);
			System.out.println(data);
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private static void validate(String xml) throws SAXException, IOException {
		StreamSource[] schemaDocuments = new StreamSource[] { new StreamSource(
				"data/citizenScience.xsd") };
		// Source s = new StreamSource("reference/JapKnotweed.xml");
		Source s = new StreamSource(new StringReader(xml));

		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);// "http://www.w3.org/XML/XMLSchema/v1.1");
		Schema schema = sf.newSchema(schemaDocuments);
		Validator v = schema.newValidator();
		v.validate(s);
	}

	private static String exportObservation(FileCache cache,
			SWEObservation sweObservation) throws IOException,
			TemplateException {
		Template temp = Freemarker.getTemplate(cache, "observation.ftl");
		Writer out = new StringWriter();// new OutputStreamWriter(System.out);

		Map<String, Object> map = sweObservation.collapse(true);

		temp.process(map, out);
		return out.toString();
	}

	private static String exportCollection(FileCache cache,
			SWEObservation[] observations) throws IOException,
			TemplateException {
		Template temp = Freemarker.getTemplate(cache, "collection.ftl");
		Writer out = new StringWriter();// new OutputStreamWriter(System.out);

		Map<String, Object> map = new HashMap<>();
		List<Object> obs = new ArrayList<>();
		Map<String, Object> enc = new HashMap<>();

		Campaign camp = new Campaign();

		for (SWEObservation sweObservation : observations) {

			camp.add(sweObservation);

			obs.add(sweObservation.collapse(false));
			sweObservation.collapseEncoder(enc);
		}

		map.put(SWE4CS.RESULT, obs);
		map.put(SWE4CS.ENCODER, enc.values());

		map.put(SWE4CS.ID, camp.getCollection());
		map.put(SWE4CS.TIME, camp.getTime());
		map.put(SWE4CS.META, camp.meta());
		map.put(SWE4CS.PROCEDURE, camp.procedure());
		map.put(SWE4CS.OBSPROP, camp.getObservedProperty());
		map.put(SWE4CS.FOI, camp.getFeatureOfInterest());

		temp.process(map, out);
		return out.toString();
	}

	/***
	 * Collection Structure:
	 */
	// Collection -id
	// -ResultTimestamp -id
	// -Metadata
	// --Campaign -id
	// -Procedure -id
	// --CampaignProcess
	// ---Sampling Protocol
	// ---CitizenProcess
	// ----Person -id
	// -ObservedProperty
	// -Result
	// --DataArray
	// ---ElementType
	// ----RecordStructure - id
	// -----Name
	// -----Definition
	// ---Encoding
	// -FeatureOfInterest
	// -Member
	// --Observation -id
	// ---PhenomenonTimestamp -id
	// ---FeatureOfInterest -id
	// ----Type -id
	// ----Location -id
	// ---Result-DataArray
	// ----Ref (RecordStructure -id)
	// ----Values
	// -Member....
}
