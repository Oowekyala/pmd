/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala;

import net.sourceforge.pmd.cpd.ScalaTokenizer;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokenizer.CpdProperties;
import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.scala.ast.ScalaParser;

import scala.meta.Dialect;

/**
 * The Scala Language Handler implementation.
 */
public class ScalaLanguageHandler extends AbstractPmdLanguageVersionHandler {

    private final Dialect dialect;

    /**
     * Create the Language Handler using the given Scala Dialect.
     *
     * @param scalaDialect
     *            the language version to use while parsing etc
     */
    public ScalaLanguageHandler(Dialect scalaDialect) {
        this.dialect = scalaDialect;
    }

    @Override
    public Tokenizer getCpdTokenizer(CpdProperties cpdProperties) {
        return new ScalaTokenizer(this.dialect).withProperties(cpdProperties);
    }

    /**
     * Get the Scala Dialect used in this language version choice.
     *
     * @return the Scala Dialect for this handler
     */
    public Dialect getDialect() {
        return this.dialect;
    }


    @Override
    public ScalaParser getParser(ParserOptions parserOptions) {
        return new ScalaParser(dialect);
    }
}
