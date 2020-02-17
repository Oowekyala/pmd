/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xpath.ast;

import java.io.Reader;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;


/**
 * Adapter for the XPathParser.
 */
public class XPathParser extends AbstractParser {

    public XPathParser(ParserOptions parserOptions) {
        super(parserOptions);
    }


    @Override
    public TokenManager createTokenManager(Reader source) {
        return new XPathTokenManager(source);
    }


    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        AbstractTokenManager.setFileName(fileName);
        return new XPathParserImpl(CharStreamFactory.simpleCharStream(source)).XPathRoot();
    }
}
