package ie.ucd.cobweb.semantic.template;

import ie.ucd.cobweb.semantic.cli.FileCache;

import java.io.File;
import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class Freemarker {
	public static Configuration cfg;

	static {
		Freemarker.cfg = new Configuration(Configuration.VERSION_2_3_23);
		try {
			Freemarker.cfg.setDirectoryForTemplateLoading(new File("templates/"));
			Freemarker.cfg.setDefaultEncoding("UTF-8");
			Freemarker.cfg
					.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Template getTemplate(FileCache cache, String template)
			throws IOException {
		return cache.getTemplate(template, cfg);
	}
}
