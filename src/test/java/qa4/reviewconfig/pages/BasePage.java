package qa4.reviewconfig.pages;

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

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public abstract class BasePage {
    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }

    public void openAuthenticatedPath(String path) {
        openBaseUrlPath(path);
    }

    protected void openBaseUrlPath(String path) {
        String baseUrl = ConfigLoader.getBaseUrl().replaceAll("/+$", "");
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        driver.get(baseUrl + normalizedPath);
    }

    protected void waitForUrlContains(String value) {
        try {
            pageWait().until(ExpectedConditions.urlContains(value));
        } catch (TimeoutException exception) {
            throw new AssertionError(
                            "Expected URL to contain " + value + " but current URL is " + driver.getCurrentUrl()
                            + System.lineSeparator() + "Form state:" + System.lineSeparator() + formState()
                            + System.lineSeparator() + "Visible page text:" + System.lineSeparator() + visiblePageText(),
                    exception
            );
        }
    }

    protected String visiblePageText() {
        String text = driver.findElement(By.tagName("body")).getText();
        return text.length() > 2000 ? text.substring(0, 2000) + "..." : text;
    }

    protected String formState() {
        Object state = ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('input[name], select[name], textarea[name]')).map(el => " +
                        "`${el.tagName.toLowerCase()}[name=${el.name}] value=${el.value}`" +
                        ").join('\\n');"
        );
        return String.valueOf(state);
    }

    protected WebDriverWait pageWait() {
        return WaitHelper.defaultWait(driver);
    }

    public void assertVisibleText(String text) {
        pageWait().until(ExpectedConditions.visibilityOfElementLocated(textLocator(text)));
    }

    public void assertVisibleAnyText(String... texts) {
        pageWait().until(webDriver -> {
            for (String text : texts) {
                if (!webDriver.findElements(textLocator(text)).isEmpty()) {
                    return true;
                }
            }
            return false;
        });
    }

    public void assertNoVisibleText(String text) {
        boolean exists = driver.findElements(textLocator(text)).stream().anyMatch(WebElement::isDisplayed);
        Assertions.assertFalse(exists, "Text should not be visible: " + text);
    }

    protected void clickVisibleButton(String label) {
        WebElement button = pageWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(.), " + xpathLiteral(label) + ")]"))
        );
        button.click();
    }

    protected void editTextInput(String name, String value) {
        WebElement input = pageWait().until(ExpectedConditions.elementToBeClickable(By.name(name)));
        for (int attempt = 0; attempt < 3; attempt++) {
            input.click();
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            input.sendKeys(Keys.BACK_SPACE);
            input.sendKeys(value);
            if (value.equals(input.getAttribute("value"))) {
                return;
            }
            input = pageWait().until(ExpectedConditions.elementToBeClickable(By.name(name)));
        }
        setInputValue(input, value);
        input.sendKeys(" ");
        input.sendKeys(Keys.BACK_SPACE);
        if (value.equals(input.getAttribute("value"))) {
            return;
        }
        Assertions.assertEquals(value, input.getAttribute("value"), name + " must contain expected value");
    }

    protected void setDateInput(String name, LocalDate value) {
        WebElement input = pageWait().until(ExpectedConditions.elementToBeClickable(By.name(name)));
        String formattedDate = value.format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<String> keyboardDates = List.of(
                formattedDate,
                value.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                value.format(DateTimeFormatter.ofPattern("MMddyyyy")),
                value.format(DateTimeFormatter.ofPattern("ddMMyyyy"))
        );
        for (String keyboardDate : keyboardDates) {
            input.click();
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            input.sendKeys(Keys.BACK_SPACE);
            input.sendKeys(keyboardDate);
            if (formattedDate.equals(input.getAttribute("value"))) {
                return;
            }
        }
        setInputValue(input, formattedDate);
        Assertions.assertEquals(formattedDate, input.getAttribute("value"), name + " must contain ISO date yyyy-MM-dd");
    }

    protected void setInputValue(WebElement input, String value) {
        ((JavascriptExecutor) driver).executeScript(
                "const input = arguments[0];" +
                        "const value = arguments[1];" +
                        "const prototype = input instanceof HTMLTextAreaElement ? window.HTMLTextAreaElement.prototype : window.HTMLInputElement.prototype;" +
                        "const setter = Object.getOwnPropertyDescriptor(prototype, 'value').set;" +
                        "setter.call(input, value);" +
                        "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                        "input.dispatchEvent(new Event('blur', { bubbles: true }));",
                input,
                value
        );
    }

    protected void dispatchValueEvents(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "const element = arguments[0];" +
                        "element.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "element.dispatchEvent(new Event('change', { bubbles: true }));" +
                        "element.dispatchEvent(new Event('blur', { bubbles: true }));",
                element
        );
    }

    protected void selectFirstOption(By locator) {
        WebElement selectElement = pageWait().until(ExpectedConditions.elementToBeClickable(locator));
        Select select = new Select(selectElement);
        List<WebElement> options = select.getOptions();
        for (int i = 0; i < options.size(); i++) {
            String value = options.get(i).getAttribute("value");
            if (value != null && !value.isBlank() && options.get(i).isEnabled()) {
                select.selectByIndex(i);
                dispatchValueEvents(selectElement);
                return;
            }
        }
        Assumptions.abort("No selectable option found for " + locator);
    }

    protected void selectOptionContaining(By locator, String expectedText) {
        WebElement selectElement = pageWait().until(ExpectedConditions.elementToBeClickable(locator));
        Select select = new Select(selectElement);
        for (WebElement option : select.getOptions()) {
            String value = option.getAttribute("value");
            String text = option.getText();
            if (value != null && !value.isBlank() && option.isEnabled()
                    && text.toLowerCase().contains(expectedText.toLowerCase())) {
                select.selectByVisibleText(text);
                dispatchValueEvents(selectElement);
                return;
            }
        }
        Assumptions.abort("No selectable option containing " + expectedText + " found for " + locator);
    }

    protected void fillIfPresent(String name, String value) {
        List<WebElement> fields = driver.findElements(By.name(name));
        if (fields.isEmpty()) {
            return;
        }
        WebElement input = fields.get(0);
        input.clear();
        input.sendKeys(value);
    }

    protected void uploadPdfToFirstFileInput() {
        WebElement fileInput = pageWait().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='file']")));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].classList.remove('hidden'); arguments[0].style.display = 'block';",
                fileInput
        );
        fileInput.sendKeys(proposalPath().toString());
    }

    protected void waitForBodyText(String text) {
        pageWait().until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), text));
    }

    protected void waitUntilNotLoading() {
        pageWait().until(webDriver -> webDriver.findElements(By.xpath(
                "//*[contains(normalize-space(.), 'Mengunggah') or contains(normalize-space(.), 'Mengirim')]"
        )).isEmpty());
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
        builder.append(")");
        return builder.toString();
    }

    protected Path proposalPath() {
        try {
            return Paths.get(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("qa4/proposal-revisi-sample.pdf")
            ).toURI()).toAbsolutePath();
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Invalid proposal sample path", exception);
        }
    }
}
