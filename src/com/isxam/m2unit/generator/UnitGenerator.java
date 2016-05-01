package com.isxam.m2unit.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.isxam.m2unit.fileTemplates.TemplateHelper;
import com.isxam.m2unit.helper.MagentoHelper;
import com.isxam.m2unit.helper.magento.UnitTestHelper;
import com.jetbrains.php.lang.psi.elements.Parameter;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.isxam.m2unit.fileTemplates.TemplateFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UnitGenerator {

    private static final String ARGUMENT_MOCK_FORMAT = "'%s' => $this->%sMock";

    private TemplateHelper templateHelper;

    public UnitGenerator(Project project) {
        this.templateHelper = TemplateHelper.getInstance(project);
    }

    public PsiFile generate(PhpClass source) throws Exception {

        final String filename = MagentoHelper.getClassFilename(UnitTestHelper.getUnitTestClassName(source));
        final String fileContent = getUnitTestText(source);

        VirtualFile directory = VfsUtil.createDirectories(UnitTestHelper.getUnitTestDirectoryPath(source));
        final PsiDirectory psiDirectory = PsiManager.getInstance(source.getProject()).findDirectory(directory);

        if (psiDirectory == null) {
            throw new Exception();
        }

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run()
            {
                PsiFile file = psiDirectory.createFile(filename);
                try {
                    file.getVirtualFile().setBinaryContent(fileContent.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return psiDirectory.findFile(filename);
    }

    private String getUnitTestText(PhpClass source) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("NAMESPACE", UnitTestHelper.getUnitTestNamespace(source));
        properties.setProperty("CLASSNAME", UnitTestHelper.getUnitTestClassName(source));
        properties.setProperty("PROPERTIES", getPropertiesText(source));
        properties.setProperty("TEST_SETUP", getMockText(source));
        properties.setProperty("ORIGINAL_CLASSNAME", source.getFQN());
        properties.setProperty("ORIGINAL_PROPERTY_NAME", StringUtil.wordsToBeginFromLowerCase(source.getName()));
        properties.setProperty("ORIGINAL_OBJ_ARGUMENTS", getOriginalObjectArgumentsText(source));

        return TemplateHelper.getInstance(source.getProject()).generateUnitText(properties);
    }

    private String getOriginalObjectArgumentsText(PhpClass source) {
        Parameter[] parameters = getMockParameters(source);
        List<String> arguments = new ArrayList<String>();
        for (Parameter parameter : parameters) {
            arguments.add(String.format(ARGUMENT_MOCK_FORMAT, parameter.getName(), parameter.getName()));
        }
        return StringUtil.join(arguments, ",\n");
    }

    private String getPropertiesText(PhpClass source) {
       return generateParametersText(source, TemplateFactory.Template.UnitProperty.getFilename());
    }

    private String getMockText(PhpClass source) {
        return generateParametersText(source, TemplateFactory.Template.UnitMock.getFilename());
    }

    private String generateParametersText(PhpClass source, String template) {
        String result = "";
        Parameter[] parameters = getMockParameters(source);
        for (Parameter parameter : parameters) {
            Properties properties = new Properties();
            properties.setProperty("CLASSNAME", parameter.getDeclaredType().toString());
            properties.setProperty("ARG_NAME", parameter.getName());
            try {
                result += templateHelper.generateText(template, properties);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Parameter[] getMockParameters(PhpClass source) {
        if (source.getConstructor() == null) {
            return Parameter.EMPTY_ARRAY;
        }
        Parameter[] parameters = source.getConstructor().getParameters();
        List<Parameter> mockParameterList = new ArrayList<Parameter>();
        for (Parameter parameter : parameters) {
            if (!parameter.getType().filterPrimitives().isEmpty()) {
                mockParameterList.add(parameter);
            }
        }
        Parameter[] mockParameters = new Parameter[mockParameterList.size()];
        return mockParameterList.toArray(mockParameters);
    }
}
