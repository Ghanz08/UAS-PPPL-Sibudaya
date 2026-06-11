package sibudaya.e2e.support;

import org.openqa.selenium.WebDriver;

public class E2eContext {
    private WebDriver driver;
    private String submissionMarker;

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public String getSubmissionMarker() {
        return submissionMarker;
    }

    public void setSubmissionMarker(String submissionMarker) {
        this.submissionMarker = submissionMarker;
    }
}
