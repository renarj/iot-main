package com.oberasoftware.home.web.wizard;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class SimpleTest {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleTest.class);

    public static void main(String[] args) {
        Configuration config = new Configuration(Configuration.VERSION_2_3_32);


        try {
            Map<String, Object> model = new HashMap<>();
            model.put("test", "ItisMeMario");

            Template template = new Template("templateTest", "<div>${test}</div>", config);
            var writer = new StringWriter();
            template.process(model, writer);

            LOG.info("Template: {}", writer.toString());
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }

    }
}
