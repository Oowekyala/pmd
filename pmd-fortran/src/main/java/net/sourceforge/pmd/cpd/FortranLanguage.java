/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.CpdOnlyHandler;

/**
 * Language implementation for Fortran
 *
 * @author Romain PELISSE belaran@gmail.com
 */
public class FortranLanguage extends BaseLanguageModule {

    /**
     * Create a Fortran Language instance.
     */
    public FortranLanguage() {
        super("Fortran", "fortran", "fortran", ".for", ".f", ".f66", ".f77", ".f90");
        addDefaultVersion("", new CpdOnlyHandler(FortranTokenizer::new));
    }
}
