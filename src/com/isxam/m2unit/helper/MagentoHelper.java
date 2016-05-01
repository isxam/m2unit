package com.isxam.m2unit.helper;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.Arrays;

public class MagentoHelper {

    private static final String ROOT_MODULE_FILENAME = "registration.php";
    private static final String CLASS_FILENAME = "%s.php";

    public static VirtualFile getFileModuleRoot(VirtualFile file) {
        if (isModuleRoot(file)) {
            return file;
        }
        return getFileModuleRoot(file.getParent());
    }

    private static boolean isModuleRoot (VirtualFile file) {
        for (VirtualFile child : file.getChildren()) {
            if (child.getName().equals(ROOT_MODULE_FILENAME)) {
                return true;
            }
        }
        return false;
    }

    public static String getClassRelativeNamespace(PhpClass source) {
        String [] namespaceParts = source.getNamespaceName().split("\\\\");
        String [] moduleNamespaceParts = Arrays.copyOfRange(namespaceParts, 3, namespaceParts.length);
        return StringUtil.join(moduleNamespaceParts, "\\");
    }

    public static String getModuleNamespace(PhpClass source) {
        String [] namespaceParts = source.getNamespaceName().split("\\\\");
        String [] moduleNamespaceParts = Arrays.copyOfRange(namespaceParts, 1, 3);
        return StringUtil.join(moduleNamespaceParts, "\\");
    }

    public static String getClassFilename(String className) {
        return String.format(CLASS_FILENAME, className);
    }
}
