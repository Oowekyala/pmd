/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;


import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIntersectionType;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTModuleDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTReceiverParameter;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters;
import net.sourceforge.pmd.lang.java.ast.ASTTypeTestPattern;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.SideEffectingVisitorAdapter;

/**
 * Checks that an AST conforms to some language level. The reporting
 * behaviour is parameterized with a {@link ReportingStrategy}.
 *
 * @param <T> Type of object accumulating violations
 */
public class LanguageLevelChecker<T> {

    private static final Pattern SPACE_ESCAPE_PATTERN = Pattern.compile("(?<!\\\\)\\\\s");

    private final int jdkVersion;
    private final boolean preview;
    private final CheckVisitor visitor = new CheckVisitor();
    private final ReportingStrategy<T> reportingStrategy;

    public LanguageLevelChecker(int jdkVersion, boolean preview, ReportingStrategy<T> reportingStrategy) {
        this.jdkVersion = jdkVersion;
        this.preview = preview;
        this.reportingStrategy = reportingStrategy;
    }

    public int getJdkVersion() {
        return jdkVersion;
    }

    public boolean isPreviewEnabled() {
        return preview;
    }


    public void check(JavaNode node) {
        T accumulator = reportingStrategy.createAccumulator();
        for (Node n : node.descendants().crossFindBoundaries()) {
            ((JavaNode) n).jjtAccept(visitor, accumulator);
        }
        reportingStrategy.done(accumulator);
    }

    private boolean check(Node node, LanguageFeature feature, T acc) {
        String message = feature.errorMessage(this.jdkVersion, this.preview);
        if (message != null) {
            reportingStrategy.report(node, message, acc);
            return false;
        }
        return true;
    }

    private static String displayNameLower(String name) {
        return name.replaceAll("__", "-")
                   .replace('_', ' ')
                   .toLowerCase(Locale.ROOT);
    }

    private static String versionDisplayName(int jdk) {
        if (jdk < 8) {
            return "Java 1." + jdk;
        } else {
            return "Java " + jdk;
        }
    }


    /** Those are just for the preview features. */
    private enum PreviewFeature implements LanguageFeature {
        BREAK__WITH__VALUE_STATEMENTS(12, 12, false),

        COMPOSITE_CASE_LABEL(12, 13, true),
        SWITCH_EXPRESSIONS(12, 13, true),
        SWITCH_RULES(12, 13, true),

        TEXT_BLOCK_LITERALS(13, 14, false),
        YIELD_STATEMENTS(13, 13, true),

        /** \s */
        SPACE_STRING_ESCAPES(14, 14, false),
        RECORD_DECLARATIONS(14, 14, false),
        TYPE_TEST_PATTERNS_IN_INSTANCEOF(14, 14, false);


        private final int minPreviewVersion;
        private final int maxPreviewVersion;
        private final boolean wasStandardized;

        PreviewFeature(int minPreviewVersion, int maxPreviewVersion, boolean wasStandardized) {
            this.minPreviewVersion = minPreviewVersion;
            this.maxPreviewVersion = maxPreviewVersion;
            this.wasStandardized = wasStandardized;
        }


        @Override
        public String errorMessage(int jdk, boolean preview) {
            boolean isStandard = wasStandardized && jdk > maxPreviewVersion;
            boolean canBePreview = jdk >= minPreviewVersion && jdk <= maxPreviewVersion;
            boolean isPreview = preview && canBePreview;

            if (isStandard || isPreview) {
                return null;
            }

            String message = StringUtils.capitalize(displayNameLower(name()));
            if (canBePreview) {
                message += " is a preview feature of JDK " + jdk;
            } else if (wasStandardized) {
                message = message + " was only standardized in Java " + (maxPreviewVersion + 1);
            } else if (minPreviewVersion == maxPreviewVersion) {
                message += " is a preview feature of JDK " + minPreviewVersion;
            } else {
                message += " is a preview feature of JDKs " + minPreviewVersion + " to " + maxPreviewVersion;
            }
            return message + ", you should select your language version accordingly";
        }
    }

    /** Those use a max valid version. */
    private enum ReservedIdentifiers implements LanguageFeature {
        ASSERT_AS_AN_IDENTIFIER(4, "assert"),
        ENUM_AS_AN_IDENTIFIER(5, "enum"),
        UNDERSCORE_AS_AN_IDENTIFIER(9, "_"),
        VAR_AS_A_TYPE_NAME(10, "var"),
        RECORD_AS_A_TYPE_NAME(14, "record");

        private final int maxJdkVersion;
        private final String reserved;

        ReservedIdentifiers(int minJdkVersion, String reserved) {
            this.maxJdkVersion = minJdkVersion;
            this.reserved = reserved;
        }

        @Override
        public String errorMessage(int jdk, boolean preview) {
            if (jdk < this.maxJdkVersion) {
                return null;
            }
            String s = displayNameLower(name());
            String usageType = s.substring(s.indexOf(' ') + 1); // eg "as an identifier"
            return "Since " + LanguageLevelChecker.versionDisplayName(maxJdkVersion) + ", '" + reserved + "'"
                + " is reserved and cannot be used " + usageType;
        }
    }

    /** Those use a min valid version. */
    private enum RegularLanguageFeature implements LanguageFeature {

        ASSERT_STATEMENTS(4),

        STATIC_IMPORT(5),
        ENUMS(5),
        GENERICS(5),
        ANNOTATIONS(5),
        FOREACH_LOOPS(5),
        VARARGS_PARAMETERS(5),
        HEXADECIMAL_FLOATING_POINT_LITERALS(5),

        UNDERSCORES_IN_NUMERIC_LITERALS(7),
        BINARY_NUMERIC_LITERALS(7),
        TRY_WITH_RESOURCES(7),
        COMPOSITE_CATCH_CLAUSES(7),
        DIAMOND_TYPE_ARGUMENTS(7),

        DEFAULT_METHODS(8),
        RECEIVER_PARAMETERS(8),
        TYPE_ANNOTATIONS(8),
        INTERSECTION_TYPES_IN_CASTS(8),
        LAMBDA_EXPRESSIONS(8),
        METHOD_REFERENCES(8),

        MODULE_DECLARATIONS(9),
        DIAMOND_TYPE_ARGUMENTS_FOR_ANONYMOUS_CLASSES(9),
        PRIVATE_METHODS_IN_INTERFACES(9),
        CONCISE_RESOURCE_SYNTAX(9);

        private final int minJdkLevel;

        RegularLanguageFeature(int minJdkLevel) {
            this.minJdkLevel = minJdkLevel;
        }


        @Override
        public String errorMessage(int jdk, boolean preview) {
            if (jdk >= this.minJdkLevel) {
                return null;
            }
            return StringUtils.capitalize(displayNameLower(name()))
                + " are a feature of " + versionDisplayName(minJdkLevel)
                + ", you should select your language version accordingly";
        }

    }

    interface LanguageFeature {

        @Nullable
        String errorMessage(int jdk, boolean preview);
    }

    private class CheckVisitor extends SideEffectingVisitorAdapter<T> {

        @Override
        public void visit(JavaNode node, T data) {
            // don't recurse
        }

        @Override
        public void visit(ASTStringLiteral node, T data) {
            if (node.isStringLiteral() && SPACE_ESCAPE_PATTERN.matcher(node.getImage()).find()) {
                check(node, PreviewFeature.SPACE_STRING_ESCAPES, data);
            }
            if (node.isTextBlock()) {
                check(node, PreviewFeature.TEXT_BLOCK_LITERALS, data);
            }
        }

        @Override
        public void visit(ASTImportDeclaration node, T data) {
            if (node.isStatic()) {
                check(node, RegularLanguageFeature.STATIC_IMPORT, data);
            }
        }

        @Override
        public void visit(ASTYieldStatement node, T data) {
            check(node, PreviewFeature.YIELD_STATEMENTS, data);
        }

        @Override
        public void visit(ASTBreakStatement node, T data) {
            if (node.getNumChildren() > 0) {
                check(node, PreviewFeature.BREAK__WITH__VALUE_STATEMENTS, data);
            }
        }

        @Override
        public void visit(ASTSwitchExpression node, T data) {
            check(node, PreviewFeature.SWITCH_EXPRESSIONS, data);
        }

        @Override
        public void visit(ASTRecordDeclaration node, T data) {
            check(node, PreviewFeature.RECORD_DECLARATIONS, data);
        }

        @Override
        public void visit(ASTConstructorCall node, T data) {
            if (node.usesDiamondTypeArgs()) {
                if (check(node, RegularLanguageFeature.DIAMOND_TYPE_ARGUMENTS, data) && node.isAnonymousClass()) {
                    check(node, RegularLanguageFeature.DIAMOND_TYPE_ARGUMENTS_FOR_ANONYMOUS_CLASSES, data);
                }
            }
        }

        @Override
        public void visit(ASTTypeArguments node, T data) {
            check(node, RegularLanguageFeature.GENERICS, data);
        }

        @Override
        public void visit(ASTTypeParameters node, T data) {
            check(node, RegularLanguageFeature.GENERICS, data);
        }

        @Override
        public void visit(ASTFormalParameter node, T data) {
            if (node.isVarargs()) {
                check(node, RegularLanguageFeature.VARARGS_PARAMETERS, data);
            }
        }

        @Override
        public void visit(ASTReceiverParameter node, T data) {
            check(node, RegularLanguageFeature.RECEIVER_PARAMETERS, data);
        }

        @Override
        public void visit(ASTAnnotation node, T data) {
            if (node.getParent() instanceof ASTType) {
                check(node, RegularLanguageFeature.TYPE_ANNOTATIONS, data);
            } else {
                check(node, RegularLanguageFeature.ANNOTATIONS, data);
            }
        }

        @Override
        public void visit(ASTForeachStatement node, T data) {
            check(node, RegularLanguageFeature.FOREACH_LOOPS, data);
        }

        @Override
        public void visit(ASTEnumDeclaration node, T data) {
            check(node, RegularLanguageFeature.ENUMS, data);
        }

        @Override
        public void visit(ASTNumericLiteral node, T data) {
            int base = node.getBase();
            if (base == 16 && !node.isIntegral()) {
                check(node, RegularLanguageFeature.HEXADECIMAL_FLOATING_POINT_LITERALS, data);
            } else if (base == 2) {
                check(node, RegularLanguageFeature.BINARY_NUMERIC_LITERALS, data);
            } else if (node.getImage().indexOf('_') >= 0) {
                check(node, RegularLanguageFeature.UNDERSCORES_IN_NUMERIC_LITERALS, data);
            }
        }

        @Override
        public void visit(ASTMethodReference node, T data) {
            check(node, RegularLanguageFeature.METHOD_REFERENCES, data);
        }

        @Override
        public void visit(ASTLambdaExpression node, T data) {
            check(node, RegularLanguageFeature.LAMBDA_EXPRESSIONS, data);
        }

        @Override
        public void visit(ASTMethodDeclaration node, T data) {
            if (node.hasModifiers(JModifier.DEFAULT)) {
                check(node, RegularLanguageFeature.DEFAULT_METHODS, data);
            }

            if (node.isPrivate() && node.getEnclosingType().isInterface()) {
                check(node, RegularLanguageFeature.PRIVATE_METHODS_IN_INTERFACES, data);
            }

            checkIdent(node, node.getMethodName(), data);
        }

        @Override
        public void visit(ASTAssertStatement node, T data) {
            check(node, RegularLanguageFeature.ASSERT_STATEMENTS, data);
        }

        @Override
        public void visit(ASTTypeTestPattern node, T data) {
            check(node, PreviewFeature.TYPE_TEST_PATTERNS_IN_INSTANCEOF, data);
        }

        @Override
        public void visit(ASTTryStatement node, T data) {
            if (node.isTryWithResources()) {
                if (check(node, RegularLanguageFeature.TRY_WITH_RESOURCES, data)) {
                    for (ASTResource resource : ASTList.orEmpty(node.getResources())) {
                        if (resource.isConciseResource()) {
                            check(node, RegularLanguageFeature.CONCISE_RESOURCE_SYNTAX, data);
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void visit(ASTIntersectionType node, T data) {
            if (node.getParent() instanceof ASTCastExpression) {
                check(node, RegularLanguageFeature.INTERSECTION_TYPES_IN_CASTS, data);
            }
        }

        @Override
        public void visit(ASTCatchClause node, T data) {
            if (node.getParameter().isMulticatch()) {
                check(node, RegularLanguageFeature.COMPOSITE_CATCH_CLAUSES, data);
            }
        }

        @Override
        public void visit(ASTSwitchLabel node, T data) {
            if (IteratorUtil.count(node.iterator()) > 1) {
                check(node, PreviewFeature.COMPOSITE_CASE_LABEL, data);
            }
        }

        @Override
        public void visit(ASTModuleDeclaration node, T data) {
            check(node, RegularLanguageFeature.MODULE_DECLARATIONS, data);
        }

        @Override
        public void visit(ASTSwitchArrowBranch node, T data) {
            check(node, PreviewFeature.SWITCH_RULES, data);
        }

        @Override
        public void visit(ASTVariableDeclaratorId node, T data) {
            checkIdent(node, node.getVariableName(), data);
        }

        @Override
        public void visit(ASTAnyTypeDeclaration node, T data) {
            String simpleName = node.getSimpleName();
            if ("var".equals(simpleName)) {
                check(node, ReservedIdentifiers.VAR_AS_A_TYPE_NAME, data);
            } else if ("record".equals(simpleName)) {
                check(node, ReservedIdentifiers.RECORD_AS_A_TYPE_NAME, data);
            }
            checkIdent(node, simpleName, data);
        }

        private void checkIdent(JavaNode node, String simpleName, T acc) {
            if ("enum".equals(simpleName)) {
                check(node, ReservedIdentifiers.ENUM_AS_AN_IDENTIFIER, acc);
            } else if ("assert".equals(simpleName)) {
                check(node, ReservedIdentifiers.ASSERT_AS_AN_IDENTIFIER, acc);
            } else if ("_".equals(simpleName)) {
                check(node, ReservedIdentifiers.UNDERSCORE_AS_AN_IDENTIFIER, acc);
            }
        }

    }

}
