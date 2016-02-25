package ie.ucd.cobweb.semantic.mapping;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import ie.ucd.cobweb.semantic.DataPoint;
import ie.ucd.cobweb.semantic.cli.FileCache;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExportType {

	private static freemarker.template.Configuration cfg;

	static {
		cfg = new freemarker.template.Configuration(
				freemarker.template.Configuration.VERSION_2_3_22);
		try {
			cfg.setDirectoryForTemplateLoading(new File("."));
			cfg.setDefaultEncoding("UTF-8");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, ValueMap> maps;
	private Set<ValueConstant> constants;

	private Configuration conf;
	public final String name;
	public final String extension;

	public ExportType(String name) {
		this.extension = ".xml";// TODO Lookup
		this.name = name;
		this.maps = new HashMap<>();
		this.constants = new HashSet<>();
	}

	public void add(ValueMap map) {
		this.maps.putIfAbsent(map.id, map);
	}

	public void add(ValueConstant vc) {
		constants.add(vc);
	}

	public void setConfig(Configuration conf) {
		this.conf = conf;
	}

	public void export(DataPoint.Property prop, String[] mappings, Exporter ex) {
		for (String mapping : mappings) {
			System.out.println("      Mapping -> " + mapping);
			ValueMap map = this.maps.get(mapping);
			if (map != null)
				map.export(prop, ex);
		}
	}

	public class Exporter {
		private HashMap<String, String> map;

		public Exporter() {
			this.map = new HashMap<>();
			for (ValueConstant c : constants) {
				map.put(c.key, c.val);
			}
		}

		public void set(String key, String value) {
			map.put(key, value);
		}

		public void export(String extension, String template, FileCache cache) {
			System.out.println();
			String editor = "editor";// map.get("editor");
			String id = "id";// map.get("id");

			System.out.println("Exported Properties To");
			System.out.print("  " + editor);
			System.out.print("_" + id);
			System.out.println(extension);

			try {
				Template temp = cache.getTemplate(template, cfg);
				// Template temp = cfg.getTemplate(template);

				Writer out = new OutputStreamWriter(System.out);
				System.out.println("======Start Export======");
				temp.process(map, out);
				// System.out.println();
				System.out.println("======End   Export======");
				System.out.println();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		}
	}

	public Configuration getConf() {
		return conf;
	}
}
