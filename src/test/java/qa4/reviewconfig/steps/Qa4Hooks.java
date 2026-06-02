package qa4.reviewconfig.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import qa4.reviewconfig.support.Qa4Context;
import shared.core.DriverFactory;

public class Qa4Hooks {
    private final Qa4Context context;

    public Qa4Hooks(Qa4Context context) {
        this.context = context;
    }

    @Before
    public void setUp() {
        context.setDriver(DriverFactory.createChromeDriver());
    }

    @After
    public void tearDown() {
        if (context.driver() != null) {
            context.driver().quit();
        }
    }
}
