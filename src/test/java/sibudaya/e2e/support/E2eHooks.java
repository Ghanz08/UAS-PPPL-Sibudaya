package sibudaya.e2e.support;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import shared.core.DriverFactory;

public class E2eHooks {
    private final E2eContext context;

    public E2eHooks(E2eContext context) {
        this.context = context;
    }

    @Before("@sibudaya")
    public void openBrowser() {
        context.setDriver(DriverFactory.createChromeDriver());
    }

    @After("@sibudaya")
    public void closeBrowser() {
        if (context.getDriver() != null) {
            context.getDriver().quit();
        }
    }
}
