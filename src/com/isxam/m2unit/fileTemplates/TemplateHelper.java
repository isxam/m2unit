package com.isxam.m2unit.fileTemplates;

import com.intellij.ide.fileTemplates.*;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.util.Properties;

public class TemplateHelper {

    private FileTemplateManager fileTemplateManager;

    public static TemplateHelper getInstance(Project project) {
        return new TemplateHelper(project);
    }

    private TemplateHelper(Project project) {
        this.fileTemplateManager = FileTemplateManager.getInstance(project);
    }

    public String generateUnitText (Properties properties) throws IOException {
        return generateText(TemplateFactory.Template.Unit.getFilename(), properties);
    }

    public String generateText(String templateName, Properties properties) throws IOException {
        properties.putAll(fileTemplateManager.getDefaultProperties());
        FileTemplate template = fileTemplateManager.getJ2eeTemplate(templateName);
        return template.getText(properties);
    }

}
