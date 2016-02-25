package ie.ucd.cobweb.semantic.cli;

import freemarker.template.Configuration;
import freemarker.template.Template;
import ie.ucd.cobweb.semantic.jsonld.JSON;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.CacheManagerBuilder;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.CacheConfigurationBuilder;
import org.ehcache.config.ResourcePoolsBuilder;
import org.ehcache.config.persistence.CacheManagerPersistenceConfiguration;
import org.ehcache.config.units.EntryUnit;

import com.google.gson.JsonElement;

public class FileCache {

	private static final boolean INIT = true;
	private static final String C_FILE = "FileCache";
	private static final String C_ONTO = "OntoCache";

	private CacheManager manager;
	private Cache<String, String> cache;
	private Cache<String, Ontology> ontologies;
	private Map<String, String> map;
	private Map<String, Template> templates;

	public FileCache(String location, Map<String, String> substitutions) {
		ResourcePoolsBuilder resourcePool = ResourcePoolsBuilder
				.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES)
		// .offheap(10, MemoryUnit.MB) -- Requires parameter fiddling.
		// .disk(100, MemoryUnit.MB)
		;
		CacheConfiguration<String, String> configuration = CacheConfigurationBuilder
				.newCacheConfigurationBuilder().withResourcePools(resourcePool)
				.buildConfig(String.class, String.class);
		CacheConfiguration<String, Ontology> configuration_o = CacheConfigurationBuilder
				.newCacheConfigurationBuilder().withResourcePools(resourcePool)
				.buildConfig(String.class, Ontology.class);

		CacheManagerPersistenceConfiguration persistance = new CacheManagerPersistenceConfiguration(
				new File(location));
		manager = CacheManagerBuilder.newCacheManagerBuilder()
				.with(persistance).build(INIT);
		if (!INIT)
			manager.init();

		cache = manager.createCache(C_FILE, configuration);
		ontologies = manager.createCache(C_ONTO, configuration_o);
		templates = new HashMap<>();

		map = new HashMap<>();
		map.putAll(substitutions);
	}

	public void close() {
		// cache.clear();
		manager.close();
	}

	public String getDocument(String file) {
		file = stripTail(file);
		if (cache.containsKey(file))
			return cache.get(file);

		if (map.containsKey(file)) {
			return loadFile(file);
		}

		return loadURL(file);
	}

	private String stripTail(String file) {
		int idx = file.indexOf("#");
		if (idx == -1)
			return file;
		file = file.substring(0, idx);
		return file;
	}

	private String loadFile(String file) {
		try {
			File f = new File(map.get(file));
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			StringBuilder b = new StringBuilder();

			while (br.ready())
				b.append(br.readLine()).append(System.lineSeparator());

			br.close();

			String contents = b.toString();
			cache.put(file, contents);
			return contents;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String loadURL(String file) {
		System.out.println(file);
		System.exit(1);
		try {
			URL url = new URL(file);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Accept-Header", "application/json-ld");

			conn.connect();

			StringBuilder sb = new StringBuilder();

			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			InputStreamReader isr = new InputStreamReader(bis);
			BufferedReader br = new BufferedReader(isr);
			while (br.ready()) {
				String line = br.readLine();
				sb.append(line);
				sb.append(System.lineSeparator());
			}

			String contents = sb.toString();
			cache.put(file, contents);
			return contents;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JsonElement getJSONDocument(String file) {
		String contents = getDocument(file);
		JsonElement jo = JSON.parse(contents);
		return jo;
	}

	public Ontology getOntology(String base) {
		base = stripTail(base);

		Ontology o = ontologies.get(base);
		if (o == null) {
			o = new Ontology();
			o.load(base, this);
			ontologies.put(base, o);
		}

		return o;
	}

	public Template getTemplate(String template, Configuration cfg) throws IOException {
		Template temp = templates.get(template);
		if (temp == null) {
			String content = getDocument(template);
			StringReader reader = new StringReader(content);
			temp = new Template(template, reader, cfg);
			templates.put(template, temp);
		}
		return temp;
	}
}
