/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokenizer.CpdProperties;
import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;
import net.sourceforge.pmd.lang.java.ast.internal.ReportingStrategy;
import net.sourceforge.pmd.lang.java.cpd.JavaTokenizer;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleViolationFactory;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.GetCommentOnFunction;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.MetricFunction;
import net.sourceforge.pmd.lang.java.rule.xpath.internal.TypeIsFunction;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.internal.AbstractLanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;

public class JavaLanguageHandler extends AbstractPmdLanguageVersionHandler {

    private static final XPathHandler XPATH_HANDLER =
        XPathHandler.getHandlerForFunctionDefs(
            TypeIsFunction.TYPE_IS_EXACTLY,
            TypeIsFunction.TYPE_IS,
            MetricFunction.INSTANCE,
            GetCommentOnFunction.INSTANCE
        );

    private final LanguageLevelChecker<?> levelChecker;
    private final LanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> myMetricsProvider = new JavaMetricsProvider();

    public JavaLanguageHandler(int jdkVersion) {
        this(jdkVersion, false);
    }

    public JavaLanguageHandler(int jdkVersion, boolean preview) {
        super(JavaProcessingStage.class);
        this.levelChecker = new LanguageLevelChecker<>(jdkVersion, preview, ReportingStrategy.reporterThatThrows());
    }

    @Override
    public Tokenizer newCpdTokenizer() {
        return new JavaTokenizer();
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new JavaParser(levelChecker);
    }

    @Override
    public DesignerBindings getDesignerBindings() {
        return JavaDesignerBindings.INSTANCE;
    }

    @Override
    public XPathHandler getXPathHandler() {
        return XPATH_HANDLER;
    }

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return JavaRuleViolationFactory.INSTANCE;
    }


    @Override
    public LanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> getLanguageMetricsProvider() {
        return myMetricsProvider;
    }


    private static class JavaMetricsProvider extends AbstractLanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> {

        JavaMetricsProvider() {
            super(ASTAnyTypeDeclaration.class, MethodLikeNode.class);
        }

        @Override
        public List<? extends MetricKey<ASTAnyTypeDeclaration>> getAvailableTypeMetrics() {
            return Arrays.asList(JavaClassMetricKey.values());
        }


        @Override
        public List<? extends MetricKey<MethodLikeNode>> getAvailableOperationMetrics() {
            return Arrays.asList(JavaOperationMetricKey.values());
        }
    }
}
