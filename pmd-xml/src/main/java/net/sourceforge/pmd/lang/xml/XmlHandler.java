/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Parser;

/**
 * Implementation of LanguageVersionHandler for the XML.
 */
public class XmlHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public ParserOptions getDefaultParserOptions() {
        return new XmlParserOptions();
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new XmlParser((XmlParserOptions) parserOptions);
    }

}
