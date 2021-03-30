/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;

public class DefaultRulesetTest {
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Test
    public void loadDefaultRuleset() {
        RuleSet ruleset = rulesetLoader().loadFromResource("rulesets/apex/ruleset.xml");
        Assert.assertNotNull(ruleset);
    }

    @After
    public void cleanup() {
        Handler[] handlers = Logger.getLogger(RuleSetLoader.class.getName()).getHandlers();
        for (Handler handler : handlers) {
            Logger.getLogger(RuleSetLoader.class.getName()).removeHandler(handler);
        }
    }

    @Test
    public void loadQuickstartRuleset() {
        Logger.getLogger(RuleSetLoader.class.getName()).addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                Assert.fail("No Logging expected: " + record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });
        RuleSet ruleset = rulesetLoader().loadFromResource("rulesets/apex/quickstart.xml");
        Assert.assertNotNull(ruleset);
    }

    private RuleSetLoader rulesetLoader() {
        return new RuleSetLoader().enableCompatibility(false);
    }
}
