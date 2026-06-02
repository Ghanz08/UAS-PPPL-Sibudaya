package qa4.reviewconfig.support;

import org.openqa.selenium.WebDriver;

public class Qa4Context {
    private WebDriver driver;
    private String pengajuanId;
    private TargetStatus targetStatus;

    public WebDriver driver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public String pengajuanId() {
        return pengajuanId;
    }

    public void setPengajuanId(String pengajuanId) {
        this.pengajuanId = pengajuanId;
    }

    public TargetStatus targetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(TargetStatus targetStatus) {
        this.targetStatus = targetStatus;
    }
}
