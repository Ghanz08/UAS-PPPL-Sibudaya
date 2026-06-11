package sibudaya.e2e.pages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import shared.core.ConfigLoader;
import shared.utils.WaitHelper;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class BaseE2ePage {
    protected final WebDriver driver;

    protected BaseE2ePage(WebDriver driver) {
        this.driver = driver;
    }

    public void openPath(String path) {
        String baseUrl = ConfigLoader.getBaseUrl().replaceAll("/+$", "");
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        driver.get(baseUrl + normalizedPath);
    }

    protected WebDriverWait waitForPage() {
        return WaitHelper.defaultWait(driver);
    }

    protected void waitForUrlContains(String fragment) {
        try {
            waitForPage().until(ExpectedConditions.urlContains(fragment));
        } catch (TimeoutException exception) {
            throw new AssertionError("Expected URL to contain " + fragment + " but was " + driver.getCurrentUrl()
                    + System.lineSeparator() + visibleText(), exception);
        }
    }

    protected String visibleText() {
        String text = driver.findElement(By.tagName("body")).getText();
        return text.length() > 2000 ? text.substring(0, 2000) + "..." : text;
    }

    public void assertVisibleText(String text) {
        waitForPage().until(ExpectedConditions.visibilityOfElementLocated(textLocator(text)));
    }

    public void assertVisibleAnyText(String... texts) {
        waitForPage().until(webDriver -> {
            for (String text : texts) {
                if (webDriver.findElements(textLocator(text)).stream().anyMatch(WebElement::isDisplayed)) {
                    return true;
                }
            }
            return false;
        });
    }

    protected void clickTextLinkOrButton(String text) {
        waitForPage().until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//a[contains(normalize-space(.), " + xpathLiteral(text) + ")] | //button[contains(normalize-space(.), " + xpathLiteral(text) + ")]"
        ))).click();
    }

    protected void typeByNameIfPresent(String name, String value) {
        List<WebElement> elements = driver.findElements(By.name(name));
        if (elements.isEmpty() || !elements.get(0).isEnabled()) {
            return;
        }
        WebElement input = elements.get(0);
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(value);
    }

    protected void setDateByNameIfPresent(String name, LocalDate date) {
        List<WebElement> elements = driver.findElements(By.name(name));
        if (elements.isEmpty() || !elements.get(0).isEnabled()) {
            return;
        }
        WebElement input = elements.get(0);
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    protected void selectFirstAvailableOptionIfPresent(String name) {
        List<WebElement> elements = driver.findElements(By.name(name));
        if (elements.isEmpty() || !elements.get(0).isEnabled()) {
            return;
        }
        Select select = new Select(elements.get(0));
        for (int i = 0; i < select.getOptions().size(); i++) {
            WebElement option = select.getOptions().get(i);
            String value = option.getAttribute("value");
            if (option.isEnabled() && value != null && !value.isBlank()) {
                select.selectByIndex(i);
                return;
            }
        }
    }

    protected void uploadPdf(Path path) {
        WebElement input = waitForPage().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='file']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block'; arguments[0].classList.remove('hidden');", input);
        input.sendKeys(path.toString());
    }

    protected void abortIfMissing(By locator, String message) {
        Assumptions.assumeFalse(driver.findElements(locator).isEmpty(), message);
    }

    protected void assertCurrentUrlContains(String fragment) {
        Assertions.assertTrue(driver.getCurrentUrl().contains(fragment), "Current URL should contain " + fragment + " but was " + driver.getCurrentUrl());
    }

    protected By textLocator(String text) {
        return By.xpath("//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), "
                + xpathLiteral(text.toLowerCase()) + ")]"
        );
    }

    protected String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        StringBuilder builder = new StringBuilder("concat(");
        String[] parts = value.split("'");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                builder.append(", \"'\", ");
            }
            builder.append("'").append(parts[i]).append("'");
        }
        return builder.append(")").toString();
    }
}
