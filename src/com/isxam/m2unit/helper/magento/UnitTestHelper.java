package com.isxam.m2unit.helper.magento;

import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.isxam.m2unit.helper.MagentoHelper;

public class UnitTestHelper {

    private static final String UNIT_TEST_PATH = "%s/Test/Unit/%s";
    private static final String UNIT_TEST_CLASS_NAME = "%sTest";

    public static String getUnitTestDirectoryPath(PhpClass source) {
        VirtualFile moduleRoot = MagentoHelper.getFileModuleRoot(source.getContainingFile().getVirtualFile());
        String sourceRelativeNamespace = MagentoHelper.getClassRelativeNamespace(source);

        return String.format(UNIT_TEST_PATH, moduleRoot.getPath(), sourceRelativeNamespace.replaceAll("\\\\", "/"));
    }

    public static String getUnitTestNamespace(PhpClass source) {
        String namespaceFormat = UNIT_TEST_PATH.replaceAll("/", "\\\\");
        return String.format(
                namespaceFormat,
                MagentoHelper.getModuleNamespace(source),
                MagentoHelper.getClassRelativeNamespace(source)
        );
    }

    public static String getUnitTestClassName(PhpClass source) {
        return String.format(UNIT_TEST_CLASS_NAME, source.getName());
    }
}
