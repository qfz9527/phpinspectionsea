package com.kalessil.phpStorm.phpInspectionsEA.inspectors.semanticalAnalysis.classes;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.kalessil.phpStorm.phpInspectionsEA.openApi.BasePhpElementVisitor;
import com.kalessil.phpStorm.phpInspectionsEA.openApi.BasePhpInspection;
import com.kalessil.phpStorm.phpInspectionsEA.utils.NamedElementUtil;
import com.kalessil.phpStorm.phpInspectionsEA.utils.OpenapiResolveUtil;
import com.kalessil.phpStorm.phpInspectionsEA.utils.hierarhy.InterfacesExtractUtil;
import org.jetbrains.annotations.NotNull;

/*
 * This file is part of the Php Inspections (EA Extended) package.
 *
 * (c) Vladimir Reznichenko <kalessil@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

public class EmptyClassInspector extends BasePhpInspection {
    private static final String message = "Class does not contain any properties or methods.";

    @NotNull
    public String getShortName() {
        return "EmptyClassInspection";
    }

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BasePhpElementVisitor() {
            @Override
            public void visitPhpClass(@NotNull PhpClass clazz) {
                final PsiElement nameNode = NamedElementUtil.getNameIdentifier(clazz);
                if (nameNode != null) {
                    final boolean isEmpty = (clazz.getOwnFields().length + clazz.getOwnMethods().length == 0);
                    if (isEmpty && !this.canBeEmpty(clazz)) {
                        holder.registerProblem(nameNode, message);
                    }
                }
            }

            private boolean canBeEmpty(@NotNull PhpClass clazz) {
                boolean result = false;
                if (clazz.isInterface() || clazz.isDeprecated() || clazz.getTraits().length > 0) {
                    result = true;
                } else {
                    final PhpClass parent = OpenapiResolveUtil.resolveSuperClass(clazz);
                    if (parent != null) {
                        if (parent.isAbstract()) {
                            /* inheriting abstract classes - we can be forced to have it empty */
                            result = true;
                        } else {
                            /* an exception */
                            for (final PhpClass candidate : InterfacesExtractUtil.getCrawlInheritanceTree(clazz, true)) {
                                if (candidate.getFQN().equals("\\Exception")) {
                                    result = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                return result;
            }
        };
    }
}
