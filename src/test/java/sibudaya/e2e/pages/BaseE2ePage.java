package sibudaya.e2e.pages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
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

    protected void waitForUrlAndAnyText(String fragment, String... texts) {
        try {
            waitForPage().until(webDriver -> webDriver.getCurrentUrl().contains(fragment) && pageContainsAnyText(texts));
        } catch (TimeoutException exception) {
            throw new AssertionError("Expected URL to contain " + fragment + " and page to contain one of "
                    + String.join(", ", texts) + " but was " + driver.getCurrentUrl()
                    + System.lineSeparator() + visibleText(), exception);
        }
    }

    protected boolean pageContainsAnyText(String... texts) {
        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        for (String text : texts) {
            if (body.contains(text.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    protected String visibleText() {
        String text = driver.findElement(By.tagName("body")).getText();
        return text.length() > 2000 ? text.substring(0, 2000) + "..." : text;
    }

    public void assertVisibleText(String text) {
        try {
            waitForPage().until(ExpectedConditions.visibilityOfElementLocated(textLocator(text)));
        } catch (TimeoutException exception) {
            throw new AssertionError("Expected visible text: " + text + System.lineSeparator() + visibleText(), exception);
        }
    }

    public void assertVisibleAnyText(String... texts) {
        try {
            waitForPage().until(webDriver -> {
                for (String text : texts) {
                    if (webDriver.findElements(textLocator(text)).stream().anyMatch(WebElement::isDisplayed)) {
                        return true;
                    }
                }
                return false;
            });
        } catch (TimeoutException exception) {
            throw new AssertionError("Expected one visible text: " + String.join(", ", texts)
                    + System.lineSeparator() + visibleText(), exception);
        }
    }

    protected void clickTextLinkOrButton(String text) {
        WaitHelper.pauseForVisual();
        waitForPage().until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//a[contains(normalize-space(.), " + xpathLiteral(text) + ")] | //button[contains(normalize-space(.), " + xpathLiteral(text) + ")]"
        ))).click();
    }

    protected void clickVisibleTextLinkOrButton(String text) {
        List<WebElement> elements = driver.findElements(By.xpath(
                "//a[contains(normalize-space(.), " + xpathLiteral(text) + ")] | //button[contains(normalize-space(.), " + xpathLiteral(text) + ")]"
        ));
        for (int i = elements.size() - 1; i >= 0; i--) {
            WebElement element = elements.get(i);
            if (element.isDisplayed() && element.isEnabled()) {
                WaitHelper.pauseForVisual();
                clickElement(element);
                return;
            }
        }
        clickTextLinkOrButton(text);
    }

    protected void clickFirstVisibleTextLinkOrButton(String... texts) {
        for (String text : texts) {
            List<WebElement> elements = driver.findElements(By.xpath(
                    "//a[contains(normalize-space(.), " + xpathLiteral(text) + ")] | //button[contains(normalize-space(.), " + xpathLiteral(text) + ")]"
            ));
            for (int i = elements.size() - 1; i >= 0; i--) {
                WebElement element = elements.get(i);
                if (element.isDisplayed() && element.isEnabled()) {
                    WaitHelper.pauseForVisual();
                    clickElement(element);
                    return;
                }
            }
        }
        StringBuilder xpath = new StringBuilder();
        for (String text : texts) {
            if (!xpath.isEmpty()) {
                xpath.append(" | ");
            }
            xpath.append("//a[contains(normalize-space(.), ").append(xpathLiteral(text)).append(")] | //button[contains(normalize-space(.), ").append(xpathLiteral(text)).append(")]");
        }
        WaitHelper.pauseForVisual();
        waitForPage().until(ExpectedConditions.elementToBeClickable(By.xpath(xpath.toString()))).click();
    }

    protected void clickVisibleTextInScope(String scopeText, String actionText) {
        String scope = xpathLiteral(scopeText);
        String action = xpathLiteral(actionText);
        By locator = By.xpath("//*[contains(normalize-space(.), " + scope + ")]//button[contains(normalize-space(.), " + action + ") or contains(@aria-label, " + action + ")]"
                + " | //*[contains(normalize-space(.), " + scope + ")]//a[contains(normalize-space(.), " + action + ") or contains(@aria-label, " + action + ")]");
        for (WebElement element : driver.findElements(locator)) {
            if (element.isDisplayed() && element.isEnabled()) {
                WaitHelper.pauseForVisual();
                clickElement(element);
                return;
            }
        }
        throw new AssertionError("Could not click action " + actionText + " inside scope " + scopeText + System.lineSeparator() + visibleText());
    }

    protected void typeByLabel(String label, String value) {
        WebElement element = findControlByLabel(label);
        WaitHelper.pauseForVisual();
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(value);
    }

    protected void selectByLabel(String label, String value) {
        WebElement element = findControlByLabel(label);
        if ("select".equalsIgnoreCase(element.getTagName())) {
            new Select(element).selectByValue(value);
            return;
        }
        typeByLabel(label, value);
    }

    protected void selectFirstOptionByNameIfPresent(String name) {
        WebElement element = firstVisibleEnabled(By.name(name));
        if (element == null) {
            return;
        }
        if ("select".equalsIgnoreCase(element.getTagName())) {
            Select select = new Select(element);
            for (WebElement option : select.getOptions()) {
                String value = option.getAttribute("value");
                if (option.isEnabled() && value != null && !value.isBlank()) {
                    select.selectByValue(value);
                    return;
                }
            }
            return;
        }
        typeByNameIfPresent(name, "BRI");
    }

    protected WebElement findControlByLabel(String label) {
        String literal = xpathLiteral(label);
        By[] locators = {
                By.xpath("//*[normalize-space(.)=" + literal + "]/ancestor::label[1]//*[self::input or self::textarea or self::select]"),
                By.xpath("//label[.//*[normalize-space(.)=" + literal + "] or contains(normalize-space(.), " + literal + ")]//*[self::input or self::textarea or self::select]"),
                By.xpath("//label[normalize-space(.)=" + literal + "]/following-sibling::*[self::input or self::textarea or self::select]"),
                By.xpath("//label[normalize-space(.)=" + literal + "]/following-sibling::*//*[self::input or self::textarea or self::select]"),
                By.xpath("//*[normalize-space(.)=" + literal + "]/ancestor::div[1]//*[self::input or self::textarea or self::select]")
        };
        return waitForPage().until(webDriver -> {
            for (By locator : locators) {
                WebElement element = webDriver.findElements(locator).stream()
                        .filter(WebElement::isDisplayed)
                        .filter(WebElement::isEnabled)
                        .findFirst()
                        .orElse(null);
                if (element != null) {
                    return element;
                }
            }
            return null;
        });
    }

    protected void clickElement(WebElement element) {
        try {
            element.click();
        } catch (ElementClickInterceptedException exception) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void typeByNameIfPresent(String name, String value) {
        WebElement input = firstVisibleEnabled(By.name(name));
        if (input == null) {
            return;
        }
        WaitHelper.pauseForVisual();
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(value);
    }

    protected void setDateByNameIfPresent(String name, LocalDate date) {
        WebElement input = firstVisibleEnabled(By.name(name));
        if (input == null) {
            return;
        }
        WaitHelper.pauseForVisual();
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    protected void selectFirstAvailableOptionIfPresent(String name) {
        WebElement element = firstVisibleEnabled(By.name(name));
        if (element == null || !"select".equalsIgnoreCase(element.getTagName())) {
            return;
        }
        Select select = new Select(element);
        for (int i = 0; i < select.getOptions().size(); i++) {
            WebElement option = select.getOptions().get(i);
            String value = option.getAttribute("value");
            if (option.isEnabled() && value != null && !value.isBlank()) {
                select.selectByIndex(i);
                return;
            }
        }
    }

    private WebElement firstVisibleEnabled(By locator) {
        return driver.findElements(locator).stream()
                .filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled)
                .findFirst()
                .orElse(null);
    }

    protected void uploadPdf(Path path) {
        WebElement input = waitForPage().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='file']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block'; arguments[0].classList.remove('hidden');", input);
        input.sendKeys(path.toString());
    }

    protected void waitForAnySuccessText(String... texts) {
        try {
            waitForPage().until(webDriver -> {
                String body = webDriver.findElement(By.tagName("body")).getText().toLowerCase();
                for (String text : texts) {
                    if (body.contains(text.toLowerCase())) {
                        return true;
                    }
                }
                return false;
            });
        } catch (TimeoutException exception) {
            throw new AssertionError("Expected success text: " + String.join(", ", texts)
                    + System.lineSeparator() + visibleText(), exception);
        }
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
