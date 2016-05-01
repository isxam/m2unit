package com.isxam.m2unit.fileTemplates;

import com.intellij.ide.fileTemplates.*;
import com.jetbrains.php.PhpIcons;

public class TemplateFactory implements FileTemplateGroupDescriptorFactory {

    public enum Template {
        Unit("unit_test.php"),
        UnitProperty("unit_test_property.php"),
        UnitMock("unit_test_mock.php");

        String filename;
        Template(String filename) { this.filename = filename; }
        public String getFilename() { return this.filename; }
    }

    public static final String GROUP_TITLE = "M2 Unit";

    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor(GROUP_TITLE, PhpIcons.PHPUNIT);
        for (Template template: Template.values()) {
            group.addTemplate(new FileTemplateDescriptor(template.getFilename(), PhpIcons.PHPUNIT));
        }
        return group;
    }
}
