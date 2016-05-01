package com.isxam.m2unit.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.PsiNavigateUtil;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.isxam.m2unit.generator.UnitGenerator;

public class GenerateClassUnitAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        PsiFile psiFile = PlatformDataKeys.PSI_FILE.getData(event.getDataContext());
        PhpClass phpClass = PhpPsiUtil.findClass((PhpFile)psiFile, Condition.TRUE);

        try {
            final PsiFile unitTestPsiFile = (new UnitGenerator(event.getProject())).generate(phpClass);
            final CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(event.getProject());
            WriteCommandAction.runWriteCommandAction(event.getProject(), new Runnable() {
                public void run()
                {
                    codeStyleManager.reformat(unitTestPsiFile);
                }
            });
            PsiNavigateUtil.navigate(unitTestPsiFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
