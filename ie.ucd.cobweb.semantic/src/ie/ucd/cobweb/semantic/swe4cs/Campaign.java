package ie.ucd.cobweb.semantic.swe4cs;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Campaign {

	private String collection = null;
	private String protocol = null;
	private List<String> people = new ArrayList<>();
	private String observed = null;
	private String foi = null;

	public void add(SWEObservation sweObservation) throws IllegalStateException {
		setCollection(sweObservation);
		setMeta();

		protocol = sweObservation.procedure.protocol;
		observed = sweObservation.observedProperty;
		foi = sweObservation.foi.sampled;

		addPerson(sweObservation);
	}

	public String getTime() {
		// TODO: First timestamp?
		return ZonedDateTime.now().toLocalDateTime().toString();
	}

	public String getCollection() {
		return IDGen.getID("cid", "campaignID");
	}

	private void setMeta() {
	}

	private void addPerson(SWEObservation sweObservation) {
		people.add(sweObservation.procedure.name);
		// sweObservation.procedure.id;
	}

	private void setCollection(SWEObservation sweObservation) {
		String collection = sweObservation.collection.replace(".edtr", "")
				.replace("-", "");

		if (this.collection == null) {
			this.collection = collection;
		} else if (!this.collection.equals(collection))
			throw new IllegalStateException("Multiple collections present.");
	}

	public Map<String, Object> meta() {
		Map<String, Object> map = new HashMap<>();

		String id = IDGen.getID("meta", collection);
		String campaign = "http://prophet.ucd.ie/ontology/cobweb/campaign/"
				+ collection;

		map.put(SWE4CS.ID, id);
		map.put(SWE4CS.CAMPAIGN, campaign);

		return map;
	}

	public Map<String, Object> procedure() {
		Map<String, Object> map = new HashMap<>();

		String id = IDGen.getID("campaign", collection);

		map.put(SWE4CS.ID, id);
		map.put(SWE4CS.PROTOCOL, protocol);

		map.put(SWE4CS.PEOPLE, getPeople());

		return map;
	}

	private List<Object> getPeople() {
		List<Object> list = new ArrayList<>();

		for (String person : people) {
			Map<String, Object> map = new HashMap<>();

			map.put(SWE4CS.NAME, person);
			map.put(SWE4CS.ID, IDGen.getID("cs", person));

			list.add(map);
		}

		return list;
	}

	public String getObservedProperty() {
		return observed;
	}

	public String getFeatureOfInterest() {
		return foi;
	}
}
