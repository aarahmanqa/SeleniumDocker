
package com.utilities;

import com.api.utilities.Common;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class BaseFunctions extends TestBase {

	// public WebDriver driver = null;
	public static String excel;

	public static String url;

	Actions action;

	static SoftAssert softAssertion = new SoftAssert();
	static ZonedDateTime lastZdt = ZonedDateTime.now();

	static {
	}

	public void minWait() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			logException(e);
			e.printStackTrace();
		}
	}

	public void customWait(long millisecs) {
		try {
			Thread.sleep(millisecs);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logException(e);
			e.printStackTrace();
		}
	}

	public WebElement explicitWait(WebElement Element) {

		WebDriverWait wait = new WebDriverWait(driver, 30);

		return wait.until(ExpectedConditions.elementToBeClickable(Element));

	}

	public boolean clickUsingJavaScript(WebElement element, String desc) {
		checkThreadPauseRequest();

		// waitUntilElementExists(element);
		if (waitUntilElementIsClickable(element, desc)) {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
			logInfo("Click " + desc, "NA", desc + " should be clicked", desc + " is clicked");
			getUILogs();
			return true;
		} else {
			return false;
		}
	}

	public boolean click(WebElement element, String elementName) {
		checkThreadPauseRequest();

		try {
			waitUntilElementIsClickable(element, elementName);
			highLighterMethod(element, "");
			unHighLighterMethod(element, "");
			element.click();
			logInfo("Click " + elementName, elementName + " should be clicked", elementName + " is clicked");
			getUILogs();
			return true;
		} catch (ElementClickInterceptedException e) {
			if (verifyAndcloseWalkme()) {
				return click(element, elementName);
			} else {
				return actionClick(element, elementName);
			}
		} catch (Exception e) {
			if (verifyAndcloseWalkme()) {
				return click(element, elementName);
			} else {
				logFailed("Click " + elementName, "NA", elementName + " should be clicked", getExceptionDetails(e));
				return false;
			}
		}
	}

	public boolean clickUsingActions(WebElement element, String elementName) {
		checkThreadPauseRequest();

		boolean status = false;
		try {
			waitUntilElementIsClickable(element, null);
			Actions builder = new Actions(driver);
			builder.moveToElement(element).click(element).build().perform();
			logInfo("Click " + elementName, "NA", elementName + " should be clicked", elementName + " is clicked");
			getUILogs();
			status = true;
		} catch (Exception E) {
			logFailed("Click " + elementName, "NA", elementName + " should be clicked", getExceptionDetails(E));
			status = false;
		}

		return status;
	}

	public boolean doubleClick(WebElement element, String elementName) {
		checkThreadPauseRequest();

		action = new Actions(driver);
		try {
			customWait(2000);
			Boolean success = waitUntilElementIsClickable(element, elementName);
			if (success) {
				action.doubleClick(element).build().perform();
				logPassed("Double Click " + elementName, "NA", elementName + " should be double clicked",
						elementName + " is double clicked");
				return true;
			}
		} catch (Exception e) {
			// this.test.log(LogStatus.FAIL, "Unable to click on "+ desc);
			logFailed("Double Click " + elementName, "NA", elementName + " should be clicked", getExceptionDetails(e));
			// e.getStackTrace();
		}

		return false;
	}

	public String getText(WebElement element, String desc) {
		checkThreadPauseRequest();

		String text = null;
		try {
			text = element.getText();
		} catch (Exception e) {
			logException(e);
		}
		return text;
	}

	public String returnFromDisabledFields(String E) {
		checkThreadPauseRequest();

		return (String) ((JavascriptExecutor) driver).executeScript(" $(" + E + ").val()");
	}

	/**
	 * @author kirankumar
	 * @createdDate 24-Feb-2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description Use this if getText is not working
	 */
	public String getInnerHTML(WebElement webelement) {
		String text = null;
		try {

			text = webelement.getAttribute("innerHTML");
		} catch (Exception E) {
			logException(E);

		}
		return text;
	}

	public String getTextJS(WebElement element, String desc) {
		checkThreadPauseRequest();

		String text = null;
		try {
			// if (!element.isDisplayed())
			{
				// this.test.log(LogStatus.FAIL, "The "+desc+" is not displayed");
				// logFailed("The "+desc+" is not displayed", "", "", "");
				// return null;
			}
			// else
			{
				// text = element.getText();
				JavascriptExecutor js = ((JavascriptExecutor) driver);
				// text = (String) (js.executeScript("return arguments[0].value;", element));
				text = (String) (js.executeScript("return arguments[0].text;", element));
				Reporting("The value retrieved from " + desc, text, "NA", "NA");

			}
		} catch (Exception e) {
			// this.test.log(LogStatus.INFO, "The text is not retrieved from "+desc+" :
			// "+text +e.getStackTrace());
			logFailed("The text is not retrieved from " + desc + " : " + text, "", "", getExceptionDetails(e));
		}
		return text;
	}

	public String getTextWithoutLog(WebElement element, String desc) {
		checkThreadPauseRequest();

		String text = null;
		try {
			if (!element.isDisplayed()) {
				return null;
			} else {
				text = element.getText();
			}
		} catch (Exception e) {
			logFailed("The text is not retrieved from " + desc + " : " + text, "", "", getExceptionDetails(e));
		}
		return text;
	}

	public Boolean sendText(WebElement element, int textToEnter, String elementName) {
		return sendText(element, String.valueOf(textToEnter), elementName);
	}

	public boolean sendText(WebElement element, String textToEnter, String elementName) {
		checkThreadPauseRequest();

		boolean done = false;
		try {
			boolean success = waitUntilElementIsClickable(element, elementName);

			try {
				if (getAttributeValueUsingJS(element).length() != 0) {
					clearUsingBackSpace(element);
				}
			} catch (Exception E) {

			}
			if (success) {
				Actions actions = new Actions(driver);
				actions.moveToElement(element);
				actions.click();
				actions.sendKeys(textToEnter);
				actions.build().perform();
				done = true;
			}
			if (elementName.contains("password")) {
				textToEnter = "*********";
				Reporting("Entered the text '" + textToEnter + "' into " + elementName, textToEnter, "true", "true");
				// done = true;
			}

			if (elementName.length() > 200) {
				textToEnter = String.valueOf(elementName.length());
				Reporting("Entered the text '" + "String Length is " + textToEnter + "' into " + elementName,
						textToEnter, "true", "true");
				// done = true;
			}

			// Reporting("Entered the text '"+ textToEnter+"' into "+ elementName,
			// textToEnter, "true", "true");

			if (done)
				logInfo("Enter text into " + elementName, textToEnter, "Text should be entered into " + elementName,
						"Text is entered into " + elementName);
			else
				logFailed("Enter text into " + elementName, textToEnter, "Text should be entered into " + elementName,
						"Text is not entered into " + elementName);

		} catch (Exception e) {
			// this.test.log(LogStatus.FAIL, "Not able to enter the text '"+textToEnter+"'
			// into "+desc);
			// Reporting("Entered the text '"+ textToEnter+"' into "+ elementName,
			// textToEnter, "true", "false");
			logFailed("Enter text '" + textToEnter + "' into " + elementName, "NA",
					"Text '" + textToEnter + "' should be entered into " + elementName,
					"Text '" + textToEnter + "' is not entered into " + elementName + "\n" + getExceptionDetails(e));
			// e.getStackTrace();
			// done = false;
		}

		return true;

	}

	public void clearUsingBackSpace(WebElement Element) {
		checkThreadPauseRequest();

		String Length = Element.getAttribute("value");
		for (int i = 0; i < Length.length(); i++) {
			Element.sendKeys(Keys.BACK_SPACE);
		}
	}

	public void webdriverWait(WebElement element) {
		checkThreadPauseRequest();

		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	public boolean enterText(WebElement element, String textToEnter, String elementName) {
		checkThreadPauseRequest();

		boolean done = false;
		;
		try {
			boolean success = waitUntilElementFound(element, elementName);

			if (success) {
				String text = element.getAttribute("value");
				if (text.length() > 0) {
					clearUsingBackSpace(element);
					text = element.getAttribute("value");
					if (text.length() > 0) {
						for (int i = 0; i <= text.length(); i++) {
							element.sendKeys(Keys.BACK_SPACE);
							element.sendKeys(Keys.DELETE);
						}
					}

				}
				element.sendKeys(textToEnter);
				done = true;
			}
			/*
			 * if (properties.getProperty("SCREENSHOT_ON_STEP").equals("true")){ String
			 * screenshot_path=captureScreenshot(); String image=
			 * test.addScreenCapture(screenshot_path); this.test.log(LogStatus.PASS,
			 * "Entered the text '"+ textToEnter+"' into "+ desc+image); }else{
			 * this.test.log(LogStatus.PASS, "Entered the text '"+ textToEnter+"' into "+
			 * desc); }
			 */

			if (done)
				if (elementName.equalsIgnoreCase("password")) {
					textToEnter = "*********";
					Reporting("Entered the text '" + textToEnter + "' into " + elementName, textToEnter, "true",
							"true");
				} else if (elementName.length() > 200) {
					textToEnter = String.valueOf(elementName.length());
					Reporting("Entered the text '" + "String Length is " + textToEnter + "' into " + elementName,
							textToEnter, "true", "true");
				} else
					logInfo("Enter text into " + elementName, textToEnter, "Text should be entered into " + elementName,
							"Text is entered into " + elementName);
			else {
				logFailed("Enter text into " + elementName, textToEnter, "Text should be entered into " + elementName,
						"Text is not entered into " + elementName);
			}
		} catch (Exception e) {
			logFailed("Enter text '" + textToEnter + "' into " + elementName, "NA",
					"Text '" + textToEnter + "' should be entered into " + elementName,
					"Text '" + textToEnter + "' is not entered into " + elementName + "<br/>" + getExceptionDetails(e));
			done = false;
		}

		return done;
	}

	public boolean enterPassword(WebElement element, String textToEnter, String desc) {
		checkThreadPauseRequest();

		boolean done = false;
		try {
			boolean success = waitUntilElementIsClickable(element, desc);

			if (success) {
				String text = element.getAttribute("value");
				if (text.length() > 0) {
					element.clear();
					for (int i = 0; i <= text.length(); i++) {
						element.sendKeys(Keys.BACK_SPACE);
						element.sendKeys(Keys.DELETE);
					}
				}

				element.sendKeys(textToEnter);
				done = true;
			}
			/*
			 * if (properties.getProperty("SCREENSHOT_ON_STEP").equals("true")){ String
			 * screenshot_path=captureScreenshot(); String image=
			 * test.addScreenCapture(screenshot_path); this.test.log(LogStatus.PASS,
			 * "Entered the text '"+ textToEnter+"' into "+ desc+image); }else{
			 * this.test.log(LogStatus.PASS, "Entered the text '"+ textToEnter+"' into "+
			 * desc); }
			 */
			// Reporting("Entered the text '*********' into "+ desc, "********", "true",
			// "true");
			if (done)
				logInfo("Enter text into " + desc, "********", "Text should be entered into " + desc,
						"Text is entered into " + desc);
			else
				logFailed("Enter text into " + desc, "********", "Text should be entered into " + desc,
						"Text is not entered into " + desc);

		} catch (Exception e) {
			// this.test.log(LogStatus.FAIL, "Not able to enter the text '"+textToEnter+"'
			// into "+desc);
			// Reporting("Entered the text '*********' into "+ desc, "********", "true",
			// "false");
			logFailed("Enter text into " + desc, "********", "Text should be entered into " + desc,
					getExceptionDetails(e));
			done = false;
		}

		return done;
	}

	public void focus_element(WebElement element) throws InterruptedException, IOException {
		checkThreadPauseRequest();

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		element.sendKeys(Keys.SHIFT);
		jse.executeScript("element.focus();");
	}

	public void enter_empty_element(WebElement element, String desc) throws InterruptedException, IOException {
		checkThreadPauseRequest();

		enterText(element, "", desc);
		element.sendKeys(Keys.TAB);

	}

	/**
	 * @param element   - Locator
	 * @param desc      - Description
	 * @param attribute - Attribute
	 * @return
	 */
	public String getTextByAttribute(WebElement element, String desc, String attribute) {
		checkThreadPauseRequest();

		String text = null;
		// waitUntilElementFound(element);

		try {
			text = element.getAttribute(attribute);
			logInfo("Retrieve the value from " + desc, text, "The value should be retrieved", "The value is retrieved");
		} catch (Exception e) {
			logFailed("Retrieve the value from " + desc, text, "The value should be retrieved", getExceptionDetails(e));
		}
		return text;
	}

	public boolean isElementExist(WebElement element, String elementName) {
		checkThreadPauseRequest();

		boolean isPresent = false;
		try {
			isPresent = element.isDisplayed();
			if (elementName != null) {
				logInfo("Verify " + elementName, elementName + " should exist", elementName + " exists");
			}
		} catch (NoSuchElementException s) {
			if (elementName != null) {
				logFailed("Verify " + elementName, elementName + " should be displayed",
						elementName + " is not displayed");
			}
			isPresent = false;
		} catch (NullPointerException npe) {
			isPresent = false;
		} catch (Exception e) {
			logException(e);
		}
		return isPresent;
	}

	public boolean isElementNotExist(WebElement element, String elementName) {
		checkThreadPauseRequest();

		boolean isPresent = false;
		try {
			isPresent = element.isDisplayed();
			logFailed("Verify " + elementName + " not exists", elementName + " should not be displayed",
					elementName + " is displayed");
		} catch (Exception s) {
			logPassed("Verify " + elementName + " not exists", elementName + " should not be displayed",
					elementName + " is not displayed");
			isPresent = true;
		}
		return isPresent;
	}

	public boolean isElementDisplay(WebElement element, String elementName) {
		checkThreadPauseRequest();

		boolean isPresent = false;
		try {
			isPresent = element.isDisplayed();
			logInfo("Verify " + elementName + " exists", elementName + " should be displayed",
					elementName + " is displayed");
		} catch (NoSuchElementException s) {
			logInfo("Verify " + elementName + " exists", elementName + " should be displayed",
					elementName + " is not displayed");
			isPresent = false;
		} catch (NullPointerException npe) {
			isPresent = false;
		} catch (Exception e) {
			logException(e);
		}
		return isPresent;
	}

	public String captureScreenshot(String methodName) throws IOException {
		checkThreadPauseRequest();

		String mailscreenshotpath = null;
		try {

			Calendar cal = new GregorianCalendar();
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			int sec = cal.get(Calendar.SECOND);
			int min = cal.get(Calendar.MINUTE);
			int date = cal.get(Calendar.DATE);
			int day = cal.get(Calendar.HOUR_OF_DAY);

			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String cwd = new File(".").getCanonicalPath();
			File f1 = new File(cwd);
			mailscreenshotpath = cwd + "/results/screenshots/" + methodName + "_" + year + "_" + date + "_"
					+ (month + 1) + "_" + day + "_" + min + "_" + sec + ".jpg";
			FileUtils.copyFile(scrFile, new File(mailscreenshotpath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logException(e);
			e.printStackTrace();
		}
		return mailscreenshotpath;
	}

	public String captureScreenshot() {
		checkThreadPauseRequest();

		String mailscreenshotpath = null;
		try {
			Calendar cal = new GregorianCalendar();
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			int sec = cal.get(Calendar.SECOND);
			int min = cal.get(Calendar.MINUTE);
			int date = cal.get(Calendar.DATE);
			int day = cal.get(Calendar.HOUR_OF_DAY);

			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String cwd;

			cwd = new File(".").getCanonicalPath();

			File f1 = new File(cwd);

			mailscreenshotpath = cwd + "/results/screenshots/" + "_" + year + "_" + date + "_" + (month + 1) + "_" + day
					+ "_" + min + "_" + sec + ".jpg";
			FileUtils.copyFile(scrFile, new File(mailscreenshotpath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logException(e);
			e.printStackTrace();
		}
		return mailscreenshotpath;
	}

	public String captureFullPageScreenshot() throws IOException {
		checkThreadPauseRequest();
		Calendar cal = new GregorianCalendar();
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int sec = cal.get(Calendar.SECOND);
		int min = cal.get(Calendar.MINUTE);
		int date = cal.get(Calendar.DATE);
		int day = cal.get(Calendar.HOUR_OF_DAY);
		String mailscreenshotpath = null;
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String cwd = new File(".").getCanonicalPath();
		File f1 = new File(cwd);
		try {
			mailscreenshotpath = cwd + "/results/screenshots/" + "_" + year + "_" + date + "_" + (month + 1) + "_" + day
					+ "_" + min + "_" + sec + ".jpg";
			FileUtils.copyFile(scrFile, new File(mailscreenshotpath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logException(e);
			e.printStackTrace();
		}
		return mailscreenshotpath;
	}

	public void highLighterMethod(WebElement element, String... desc) {
		checkThreadPauseRequest();

		try {
			// element = driver.findElement(By.name("password"));
			JavascriptExecutor js = (JavascriptExecutor) driver;

			js.executeScript("arguments[0].setAttribute('style', 'background:; border: 2px solid red;');", element);
			//// ATUReports.add("HighLighted "+desc, LogAs.INFO, new
			//// CaptureScreen(ScreenshotOf.DESKTOP));
		} catch (Exception e) {
		}

		// JavascriptExecutor js = (JavascriptExecutor) driver;
		// js.executeScript("arguments[0].setAttribute('style', 'background:; border:
		// 2px solid red;');", element);
		// js.executeScript("arguments[0].setAttribute('style',border: 2px solid
		// red;');", element);
		// logInfo(desc, "", "", "");
	}

	public void highLighterMethodWithGreen(WebElement element, String desc) {
		checkThreadPauseRequest();

		try {
			// element = driver.findElement(By.name("password"));
			JavascriptExecutor js = (JavascriptExecutor) driver;

			js.executeScript("arguments[0].setAttribute('style', 'background:; border: 2px solid green;');", element);
		} catch (Exception e) {
		}
	}

	public void unHighLighterMethod(WebElement element, String... desc) {
		checkThreadPauseRequest();
		try {

			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].setAttribute('style', 'background: ; border: 0px;');", element);
		} catch (Throwable t) {
		}
	}

	public String getLocalHostName() throws Exception {
		checkThreadPauseRequest();
		InetAddress inetAddress = InetAddress.getLocalHost();
		String localHostName = inetAddress.getHostName();
		return localHostName;
	}

	public void refreshThePage() {
		checkThreadPauseRequest();

		if (driver != null) {
			minWait();
			// this.driver.navigate().refresh();
			driver.navigate().refresh();

			waitForLoadDocument();
			getUILogs();
			customWait(2000);
		}

	}

	public void closeTheCurrentTab() {
		checkThreadPauseRequest();

		driver.close();

	}

	public void refresh() {
		checkThreadPauseRequest();

		minWait();
		// this.driver.navigate().refresh();
		driver.navigate().refresh();
		getUILogs();
	}

	public Boolean waitUntilElementFound(WebElement element) {
		checkThreadPauseRequest();
		return waitUntilElementFound(element, "");
	}

	public Boolean waitUntilElementFound(WebElement element, Integer timeout) {
		checkThreadPauseRequest();
		return waitUntilElementFound(element, "", timeout);
	}

	public Boolean waitUntilElementFound(WebElement element, String desc) {
		checkThreadPauseRequest();
		return waitUntilElementFound(element, desc, null);
	}

	public Boolean waitUntilElementFound(WebElement element, String desc, Integer timeout) {
		checkThreadPauseRequest();

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		Boolean success = false;
		if (timeout == null)
			timeout = TIMEOUT_SECONDS;

		try {

			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
					.pollingEvery(Duration.ofMillis(200)).ignoring(NoSuchElementException.class);

			WebElement we = wait.until(new Function<WebDriver, WebElement>() {
				public WebElement apply(WebDriver driver) {
					element.isDisplayed();
					return element;
				}
			});

			if (we != null)
				success = true;
		} catch (Exception e) {
		} finally {
			driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return success;
	}

	public Boolean waitUntilElementIsClickable(WebElement element, String desc) {
		return waitUntilElementIsClickable(element, desc, null);
	}

	public Boolean waitUntilElementIsClickable(WebElement element, String desc, Integer timeout) {
		driver = TLDriverFactory.getTLDriver();
		checkThreadPauseRequest();

		if (timeout == null)
			timeout = TIMEOUT_SECONDS;
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		Boolean success = false;
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeout);

			wait.until(ExpectedConditions.elementToBeClickable(element));
			success = true;
		} catch (Exception e) {

		} finally {
			driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return success;
	}

	/*
	 * public Boolean waitUntilElementNotFound(WebElement element,String desc) {
	 * 
	 * driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); Boolean
	 * success = false; try { for(int i=0;i<10;i++) { element.isDisplayed();
	 * Thread.sleep(1000); } success = true; } catch (Exception e) { } finally {
	 * driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
	 * } return success; }
	 */

	/**
	 * @param
	 * @author ahamedabdulrahman
	 * @createdDate 10 Feb 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description waitUntilElementNotFound
	 */
	public Boolean waitUntilElementNotFound(WebElement element, String desc) {
		return waitUntilElementNotFound(element, desc, null);
	}

	/**
	 * @param
	 * @author ahamedabdulrahman
	 * @createdDate 10 Feb 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description The element is not found only when NoSuchElementException is
	 *              thrown.
	 */
	public Boolean waitUntilElementNotFound(WebElement element, String desc, Integer timeout) {
		checkThreadPauseRequest();

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		Boolean success = false;
		if (timeout == null)
			timeout = TIMEOUT_SECONDS;

		String status = null;
		try {

			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
					.pollingEvery(Duration.ofMillis(200)).ignoring(CustomException.class);

			status = wait.until(new Function<WebDriver, String>() {
				public String apply(WebDriver driver) {
					try {
						if (element.isDisplayed() == false)
							throw new NoSuchElementException("Element is not displayed");
						throw new CustomException(null);
					} catch (NoSuchElementException ne) {
						return "ELEMENT_FADED";
					}
				}
			});

			if (status.equals("ELEMENT_FADED"))
				success = true;
		} catch (Exception e) {
		} finally {
			driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return success;
	}

	public Boolean waitForListToPopulate(List<WebElement> webElementList) {
		checkThreadPauseRequest();
		return waitForListToPopulate(webElementList, null);
	}

	public Boolean waitForListToPopulate(List<WebElement> webElementList, Integer timeout) {
		checkThreadPauseRequest();
		return waitForListToPopulate(webElementList, null, 1);
	}

	/**
	 * This method waits for the list to get populated.
	 *
	 * @param webElementList list obtained from @FindAll. Because, these elements
	 *                       are created always.
	 * @return returns true or false
	 */
	public Boolean waitForListToPopulate(List<WebElement> webElementList, Integer timeout, int minSize) {
		checkThreadPauseRequest();

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		Boolean success = false;
		if (timeout == null)
			timeout = TIMEOUT_SECONDS;

		try {

			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
					.pollingEvery(Duration.ofMillis(200)).ignoring(NoSuchElementException.class);

			success = wait.until(new Function<WebDriver, Boolean>() {
				public Boolean apply(WebDriver driver) {
					if (webElementList.size() >= minSize)
						return true;
					else
						throw new NoSuchElementException("List is yet to populate");
				}
			});

		} catch (Exception e) {
		} finally {
			driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return success;
	}

	public Boolean waitUntilElementExists(WebElement element) {
		checkThreadPauseRequest();

		try {
			int count = 0;
			boolean found = false;
			while (!found && count != 3) {
				try {
					if (element.isDisplayed()) {
						return true;
					}
				} catch (Exception e) {
				}
				count++;
			}

			if (count == 3)
				logWarning("Timeout", "Element is not found within the wait time (90 seconds)");

		} catch (Exception e) {
			logInfo(e.toString());
		}
		return false;
	}

	public Boolean waitUntilElementNotExists(WebElement element) {
		checkThreadPauseRequest();
		customWait(2000);
		try {
			int count = 0;
			boolean found = true;
			int waitTime = Integer.parseInt(getProperties().get("ELEMENT_WAITING_TIME") + "");
			while (found && count != waitTime) {
				try {
					driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
					if (element.isDisplayed()) {
						found = true;
					}
				} catch (Exception e) {
					found = false;
				}
				count++;
			}

			if (count == waitTime)
				logWarning("Timeout", "Element is found after the wait time (" + waitTime + " seconds)");

		} catch (Exception e) {
			logInfo(e.toString());
		} finally {
			driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return false;
	}

	public String getAttributeValue(WebElement element, String attribute) {
		checkThreadPauseRequest();

		String valueText = null;
		try {
			valueText = element.getAttribute(attribute);
			highLighterMethod(element, "");
			unHighLighterMethod(element, "");
		} catch (Exception e) {
			logException(e, true);
		}

		return valueText;
	}

	/***
	 *
	 * @author ahamedabdulrahman
	 * @createdDate 19 Nov 2019
	 * @modifiedBy
	 * @modifiedDate
	 * @description The basic difference between this and the prior method is, here
	 *              the exception will be ignored. In the former, the exception will
	 *              be reported.
	 * @param
	 */
	public String getAttribute(WebElement element, String attribute) {
		checkThreadPauseRequest();

		String valueText = null;
		try {
			valueText = element.getAttribute(attribute);
			highLighterMethod(element, "");
			unHighLighterMethod(element, "");
		} catch (Exception e) {
			// Exception is ignored.
		}

		return valueText;
	}

	public String getAttributeValueUsingJS(WebElement e) {
		checkThreadPauseRequest();

		return (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].value;", e);
	}

	public void uploadFile(WebElement element, String filepath) throws IOException {
		checkThreadPauseRequest();

		boolean exists = true;
		if (exists) {
			// WebElement element = this.driver.findElement(by);
			element.sendKeys(filepath);
			try {
				customWait(10);
				/*
				 * if (properties.getProperty("SCREENSHOT_ON_STEP").equals("true")){ String
				 * screenshot_path=captureScreenshot(); String image=
				 * test.addScreenCapture(screenshot_path); this.test.log(LogStatus.PASS,
				 * "The '"+filepath+"' is uploaded"+image); }else{ this.test.log(LogStatus.PASS,
				 * "The '"+filepath+"' is uploaded"); }
				 */
				logPassed("File upload", filepath, "File should be uploaded", "File uploaded");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				// this.test.log(LogStatus.FAIL, "The '"+filepath+"' is not uploaded");
				logFailed("File upload", filepath, "File should be uploaded", "File not uploaded");
				// e.printStackTrace();
			}

		} else {
			// String screenshot_path=captureScreenshot();
			// String image= test.addScreenCapture(screenshot_path);
			// this.test.log(LogStatus.FAIL, "The element does not exist"+image);
			logFailed("The element does not exist", "", "", "");
		}
	}

	public boolean scrollToElement(WebElement ele, String desc) {
		checkThreadPauseRequest();
		Boolean success = false;
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ele);
			if (desc != null)
				logInfo("Scrolled to :" + desc);
			success = true;
		} catch (Exception e) {
			logException(e);
		}
		return success;
	}

	/**
	 * @param ele
	 * @param desc
	 * @return
	 * @author ahamedabdulrahman
	 * @description This method scrolls the element to the center of the screen
	 *              instead of taking it to the top.
	 */
	public boolean scrollToCenter(WebElement ele, String desc) {
		checkThreadPauseRequest();
		Boolean success = false;
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", ele);
			if (desc != null)
				logInfo("Scrolled to :" + desc);
			success = true;
		} catch (Exception e) {
			logException(e);
		}
		return success;
	}

	public void getLogs() {
		checkThreadPauseRequest();

		List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
		System.out.println(entries.size() + " " + LogType.PERFORMANCE + " log entries found");
		for (LogEntry entry : entries) {
			System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
		}
	}

	public void waitForLoadDocument() {
		checkThreadPauseRequest();
		try {

			new WebDriverWait(driver, 30).until(webDriver -> ((JavascriptExecutor) webDriver)
					.executeScript("return document.readyState").equals("complete"));
		} catch (Throwable t) {
		}
	}

	public void moveToElement(WebElement element) {
		checkThreadPauseRequest();

		try {
			Actions actions = new Actions(driver);
			actions.moveToElement(element);
			actions.perform();
			waitUntilElementIsClickable(element, "");
		} catch (Exception e) {
			logFailed("Unable to perform action: move to element", e.getMessage());
		}
	}

	/***
	 * public static void new Tek_Properties().writeToHtml(String StepDesc,String
	 * Expected,String Actual,String status){ String statusImg = null; File f =
	 * TLDriverFactory.getReport(); FileWriter fileWriter = null; String
	 * screenshotName = getScreenShot(); if(status.equalsIgnoreCase("passed")) {
	 * statusImg = "../../logpass.png"; }else if(status.equalsIgnoreCase("failed"))
	 * { statusImg = "../../logfail.png"; }else if(status.equalsIgnoreCase("info"))
	 * { statusImg = "../../loginfo.png"; }else
	 * if(status.equalsIgnoreCase("warning")) { statusImg = "../../logwarning.png";
	 * } try { fileWriter = new FileWriter(f.getAbsolutePath(),true);
	 * fileWriter.append("
	 * <tr>
	 * <td>"+currentTime()+"</td>
	 * <td>"+StepDesc+"</td>
	 * <td></td>
	 * <td>"+Expected+"</td>
	 * <td>"+Actual+"</td>
	 * <td><img src="+statusImg+"style=width:25px></td>
	 * <td><a target=blank blank href=./screenshots/"+screenshotName+"><img
	 * src=./screenshots/"+screenshotName+"style=width:150px></a></td>");
	 * fileWriter.write(System.getProperty("line.separator"));
	 * //fileWriter.write("/n"); fileWriter.close(); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 * 
	 * public static String getScreenShot() { WebDriver String screenshotName=null;
	 * try { if(driver == null) return null; File f =
	 * TLDriverFactory.getScreenShotFolder(); String screenshotPath =
	 * f.getAbsolutePath();
	 * 
	 * Calendar cal = new GregorianCalendar(); int month = cal.get(Calendar.MONTH);
	 * int year = cal.get(Calendar.YEAR); int sec = cal.get(Calendar.SECOND); int
	 * min = cal.get(Calendar.MINUTE); int date = cal.get(Calendar.DATE); int day =
	 * cal.get(Calendar.HOUR_OF_DAY); String screenshot = null; File scrFile =
	 * ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE); screenshotName =
	 * year + "_" + date + "_" + (month + 1) + "_" + day + "_" + min + "_" + sec +
	 * ".jpg";
	 * 
	 * screenshot = screenshotPath + "/" + screenshotName;
	 * FileUtils.copyFile(scrFile, new File(screenshot)); }
	 * 
	 * catch (Exception e) { new BaseFunctions().quit(); e.printStackTrace(); }
	 * return screenshotName;
	 * 
	 * }
	 ***/

	public void jsScrollUp() {
		checkThreadPauseRequest();

		try {
			customWait(1000);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(20,0);", "");

		} catch (Exception e) {
		}
	}

	public void actionKeyDown() {
		checkThreadPauseRequest();

		try {
			Actions actions = new Actions(driver);
			actions.sendKeys(Keys.PAGE_DOWN).build().perform();
		} catch (Exception e) {
			logFailed("Unable to perform action: Keys Page Down", e.getMessage());
		}
	}

	public void actionKeyUp() {
		checkThreadPauseRequest();

		try {
			Actions actions = new Actions(driver);
			actions.sendKeys(Keys.PAGE_UP).build().perform();
		} catch (Exception e) {
			logFailed("Unable to perform action: Keys Page Up", e.getMessage());
		}
	}

	public void scrollToParticularWebElementUsingActions(WebElement Element) {
		checkThreadPauseRequest();

		try {
			Actions actions = new Actions(driver);
			actions.moveToElement(Element).build().perform();
			;
		} catch (Exception e) {
			logFailed("Unable to perform action: Keys Page Up", e.getMessage());
		}
	}

	public void Reporting(String StepDesc, String inputValue, String Expected, String Actual) {
		checkThreadPauseRequest();
		if ((Expected.equalsIgnoreCase("true")) || (Expected.equalsIgnoreCase("false"))) {
			if (Expected.equalsIgnoreCase(Actual)) {
				new Tek_Properties().writeToHtml(StepDesc, inputValue, Expected, Actual, "passed");
				// ATUReports.add(StepDesc, inputValue, Expected, Actual, LogAs.PASSED, new
				// CaptureScreen(ScreenshotOf.DESKTOP));
			} else {
				new Tek_Properties().writeToHtml(StepDesc, inputValue, Expected, Actual, "failed");
				// ATUReports.add(StepDesc, inputValue, Expected, Actual, LogAs.FAILED, new
				// CaptureScreen(ScreenshotOf.DESKTOP));
			}
		}
	}

	/***
	 * public static void new Tek_Properties().writeToHtml(String StepDesc,String
	 * Expected,String Actual,String status){ String statusImg = null; File f =
	 * TLDriverFactory.getReport(); FileWriter fileWriter = null; String
	 * screenshotName = getScreenShot(); if(status.equalsIgnoreCase("passed")) {
	 * statusImg = "../../logpass.png"; }else if(status.equalsIgnoreCase("failed"))
	 * { statusImg = "../../logfail.png"; }else if(status.equalsIgnoreCase("info"))
	 * { statusImg = "../../loginfo.png"; }else
	 * if(status.equalsIgnoreCase("warning")) { statusImg = "../../logwarning.png";
	 * } try { fileWriter = new FileWriter(f.getAbsolutePath(),true);
	 * fileWriter.append("
	 * <tr>
	 * <td>"+currentTime()+"</td>
	 * <td>"+StepDesc+"</td>
	 * <td></td>
	 * <td>"+Expected+"</td>
	 * <td>"+Actual+"</td>
	 * <td><img src="+statusImg+"style=width:25px></td>
	 * <td><a target=blank blank href=./screenshots/"+screenshotName+"><img
	 * src=./screenshots/"+screenshotName+"style=width:150px></a></td>");
	 * fileWriter.write(System.getProperty("line.separator"));
	 * //fileWriter.write("/n"); fileWriter.close(); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 * 
	 * public static String getScreenShot() { WebDriver String screenshotName=null;
	 * try { if(driver == null) return null; File f =
	 * TLDriverFactory.getScreenShotFolder(); String screenshotPath =
	 * f.getAbsolutePath();
	 * 
	 * Calendar cal = new GregorianCalendar(); int month = cal.get(Calendar.MONTH);
	 * int year = cal.get(Calendar.YEAR); int sec = cal.get(Calendar.SECOND); int
	 * min = cal.get(Calendar.MINUTE); int date = cal.get(Calendar.DATE); int day =
	 * cal.get(Calendar.HOUR_OF_DAY); String screenshot = null; File scrFile =
	 * ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE); screenshotName =
	 * year + "_" + date + "_" + (month + 1) + "_" + day + "_" + min + "_" + sec +
	 * ".jpg";
	 * 
	 * screenshot = screenshotPath + "/" + screenshotName;
	 * FileUtils.copyFile(scrFile, new File(screenshot)); }
	 * 
	 * catch (Exception e) { new BaseFunctions().quit(); e.printStackTrace(); }
	 * return screenshotName;
	 * 
	 * }
	 ***/

	private void quit() {
		checkThreadPauseRequest();

		driver.quit();
	}

	public static void logPassed(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='green'>" + StepDesc, "", "", "", "passed");
	}

	public static void logPassed(String StepDesc, String inputValue) {
		new Tek_Properties().writeToHtml("<font color='green'>" + StepDesc + "</font>", inputValue, "", "", "passed");
	}

	public static void logPassed(String StepDesc, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='green'>" + StepDesc + "</font>", "", Expected, Actual, "passed");
	}

	public static void logPassed(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='green'>" + StepDesc + "</font>", inputValue, Expected, Actual,
				"passed");
	}

	public static void logFailed(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='red'>" + StepDesc + "</font>", "", "", "", "failed");
	}

	public static void logFailed(String StepDesc, String inputValue) {
		new Tek_Properties().writeToHtml("<font color='red'>" + StepDesc + "</font>", inputValue, "", "", "failed");
	}

	public static void logFailed(String StepDesc, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='red'>" + StepDesc + "</font>", "", Expected, Actual, "failed");
	}

	public static void logFailed(String StepDesc, String input, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='red'>" + StepDesc + "</font>", input, Expected, Actual,
				"failed");
	}

	public static void logInfo(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='blue'>" + StepDesc + "</font>", "", "", "", "info");
	}

	public static void logAsHighlighter(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='purple'>" + StepDesc + "</font>", "", "", "", "info");
	}

	public static void logInfo(String StepDesc, String input) {
		new Tek_Properties().writeToHtml(StepDesc, input, "", "", "info");
	}

	public static void logInfo(String StepDesc, String expected, String actual) {
		new Tek_Properties().writeToHtml(StepDesc, "", expected, actual, "info");
	}

	public static void logInfoHeading(String StepDesc, String input) {
		new Tek_Properties().writeToHtml("<b><font color='blue'>" + StepDesc + "</font></b>", input, "", "", "info",
				false);
	}

	public static void logInfoSubHeading(String StepDesc, String input) {
		new Tek_Properties().writeToHtml("<b><font color='blue'>" + StepDesc + "</font></b>", input, "", "", "info",
				false);
	}

	public static void logPassedWithOutScreenShot(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='green'>" + StepDesc + "</font>", inputValue, Expected, Actual,
				"passed", false);
	}

	public static void logFailedWithOutScreenShot(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='red'>" + StepDesc + "</font>", inputValue, Expected, Actual,
				"failed", false);
	}

	public static void logInfoWithOutScreenShot(String StepDesc, String input) {
		new Tek_Properties().writeToHtml("<font color='black'>" + StepDesc + "</font>", input, "", "", "info", false);
	}

	public static void logInfoWithOutScreenShot(String StepDesc, String input, String actual) {
		new Tek_Properties().writeToHtml(StepDesc, input, "", actual, "info", false);
	}

	public static void logInfoWithOutScreenShot(String StepDesc, String input, String expected, String actual) {
		new Tek_Properties().writeToHtml(StepDesc, input, expected, actual, "info", false);
	}

	public static void logInfo(String StepDesc, String input, String expected, String actual) {
		new Tek_Properties().writeToHtml(StepDesc, input, expected, actual, "info");
	}

	public static void logBrowserLogs(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='blue'>" + StepDesc + "</font>", "", "", "", "browserLogs");
	}

	public static void logApiLogs(String StepDesc, String input, String expected, String actual) {
		new Tek_Properties().writeToHtml("<font color='blue'>" + StepDesc + "</font>", input, expected, actual,
				"browserLogs", false);
	}

	public static void logMessage(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='blue'>" + StepDesc + "</font>", "", "", "", "message");
	}

	public static void logMessage(String StepDesc, String desc) {
		new Tek_Properties().writeToHtml("<font color='blue'>" + StepDesc + "</font>",
				"<font color='blue'>" + desc + "</font>", "", "", "message");
	}

	public static void logMessageWithOutScreenShot(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='blue'>" + StepDesc + "</font>", "", "", "", "message", false);
	}

	public static void logCheckpoint(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='orange'>" + StepDesc + "</font>", "", "", "", "info");
	}

	public static void logCheckpoint(String StepDesc, String input) {
		new Tek_Properties().writeToHtml("<font color='orange'>" + StepDesc + "</font>", input, "", "", "info");
	}

	public static void logCheckpoint(String StepDesc, String expected, String actual) {
		new Tek_Properties().writeToHtml("<font color='orange'>" + StepDesc + "</font>", "", expected, actual, "info");
	}

	public static void logMessageWithOutScreenShot(String StepDesc, String value) {
		new Tek_Properties().writeToHtml("<font color='blue'>" + StepDesc + "</font>", value, "", "", "message", false);
	}

	public static void logCheckpoint(String StepDesc, String input, String expected, String actual) {
		new Tek_Properties().writeToHtml("<font color='orange'>" + StepDesc + "</font>", input, expected, actual,
				"info");
	}

	public static void logWarning(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='orange'>" + StepDesc + "</font>", "", "", "", "warning");
	}

	public static void logWarning(String StepDesc, String inputValue) {
		new Tek_Properties().writeToHtml("<font color='orange'>" + StepDesc + "</font>", inputValue, "", "", "warning");
	}

	public static void logWarning(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='orange'>" + StepDesc + "</font>", inputValue, Expected, Actual,
				"warning");
	}

	public static void logWarningWithOutScreenShot(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='orange'>" + StepDesc + "</font>", inputValue, Expected, Actual,
				"warning", false);
	}

	public static void logResult(String StepDesc, boolean isPassed) {
		if (isPassed) {
			setTestResultInReport(StepDesc, true);
			new Tek_Properties().writeToHtml("<font color='green'>" + StepDesc + "</font>", "", "", "", "passed");
		} else {
			setTestResultInReport(StepDesc, false);
			new Tek_Properties().writeToHtml("<font color='red'>" + StepDesc + "</font>", "", "", "", "failed");
		}
	}

	public static void logResult(String StepDesc, String value, boolean isPassed) {
		if (isPassed) {
			setTestResultInReport(StepDesc + ": " + value, true);
			new Tek_Properties().writeToHtml("<font color='green'>" + StepDesc + "</font>", value, "", "", "passed");
		} else {
			setTestResultInReport(StepDesc + ": " + value, false);
			new Tek_Properties().writeToHtml("<font color='green'>" + StepDesc + "</font>", value, "", "", "failed");
		}
	}

	public static void logResult(String StepDesc, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='green'>" + StepDesc + "</font>", "", Expected, Actual, "passed");
	}

	public static void logResult(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='green'>" + StepDesc + "</font>", inputValue, Expected, Actual,
				"passed");
	}

	public static void logAPIPassed(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='black'>" + StepDesc + "</font>", "", "", "", "passed", false);
	}

	public static void logAPIPassed(String StepDesc, String inputValue) {
		new Tek_Properties().writeToHtml("<font color='black'>" + StepDesc + "</font>", inputValue, "", "", "passed",
				false);
	}

	public static void logAPIPassed(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='black'>" + StepDesc + "</font>", inputValue, Expected, Actual,
				"passed", false);
	}

	/**
	 * Open New Tab with URL
	 **/
	public void openingNewTab(String URL) throws AWTException, InterruptedException {
		checkThreadPauseRequest();

		String parentWindow = driver.getWindowHandle();
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.open()");
		ArrayList<String> tab = new ArrayList<String>(driver.getWindowHandles());
		for (String childWindow : tab)
			if (!childWindow.equals(parentWindow))
				driver.switchTo().window(childWindow);
		driver.navigate().to(URL);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		waitForLoadDocument();
		customWait(1000);
		logPassed("Open New Tab", "N.A.", "New Tab should be opened and navigating to the new URL",
				"New Tab has been opened and navigated to the new URL");

	}

	public void navigateTo(String URL) {
		checkThreadPauseRequest();

		driver.navigate().to(URL);
	}

	public void openingNewTab() {
		checkThreadPauseRequest();

		String parentWindow = driver.getWindowHandle();
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.open()");
		ArrayList<String> tab = new ArrayList<String>(driver.getWindowHandles());
		for (String childWindow : tab)
			if (!childWindow.equals(parentWindow))
				driver.switchTo().window(childWindow);
		driver.get(getProperties().getProperty("CDMS_URL"));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		logPassed("Open New Tab", "N.A.", "New Tab should be opened and navigating to the new URL",
				"New Tab has been opened and navigated to the new URL");

	}

	public void openingNewWindow() {
		checkThreadPauseRequest();

		String parentWindow = driver.getWindowHandle();
		((JavascriptExecutor) driver).executeScript("window.open('', '', 'width=1366,height=768');");

		ArrayList<String> tab = new ArrayList<String>(driver.getWindowHandles());
		for (String childWindow : tab)
			if (!childWindow.equals(parentWindow))
				driver.switchTo().window(childWindow);
		driver.get(getProperties().getProperty("CDMS_URL"));
		logPassed("Open New Tab", "N.A.", "New Tab should be opened and navigating to the new URL",
				"New Tab has been opened and navigated to the new URL");

	}

	public void openingNewIncognitoWindow() throws AWTException {
		checkThreadPauseRequest();

		String parentWindow = driver.getWindowHandle();

		Robot rb = new Robot();
		rb.keyPress(KeyEvent.VK_META);
		// rb.delay(1000);
		rb.keyPress(KeyEvent.VK_SHIFT);
		// rb.delay(1000);
		rb.keyPress(KeyEvent.VK_N);
		rb.delay(1000);
		rb.keyRelease(KeyEvent.VK_N);
		// rb.delay(1000);
		rb.keyRelease(KeyEvent.VK_SHIFT);
		// rb.delay(1000);
		rb.keyRelease(KeyEvent.VK_META);
		// rb.delay(1000);

		ArrayList<String> tab = new ArrayList<String>(driver.getWindowHandles());

		for (String childWindow : tab)
			if (!childWindow.equals(parentWindow))
				driver.switchTo().window(childWindow);
		driver.get(getProperties().getProperty("CDMS_URL"));
		logPassed("Open New Tab", "N.A.", "New Tab should be opened and navigating to the new URL",
				"New Tab has been opened and navigated to the new URL");

	}

	public String switchToLastTab(String URL) {
		checkThreadPauseRequest();

		customWait(4000);
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		int LastTab = tabs.size();

		driver.switchTo().window(tabs.get(LastTab - 1));
		driver.get(URL);

		return driver.getCurrentUrl();
	}

	public String getCurrentURL() {
		checkThreadPauseRequest();

		return driver.getCurrentUrl();
	}

	public String getCurrentWindow() {
		checkThreadPauseRequest();

		return driver.getWindowHandle();
	}

	/**
	 * Switching to Last Tab without URL
	 **/
	public String switchToLastTabWithOutURL() {
		checkThreadPauseRequest();

		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		int LastTab = tabs.size();
		driver.switchTo().window(tabs.get(LastTab - 1));
		customWait(2000);
		return driver.getCurrentUrl();
	}

	/**
	 * Switching to Parent Window
	 **/
	public void switchToParentWindow() throws InterruptedException {
		checkThreadPauseRequest();

		String ChildWindow = driver.getWindowHandle();
		for (String ParentWindow : driver.getWindowHandles())
			if (!ParentWindow.equals(ChildWindow)) {
				driver.switchTo().window(ParentWindow);
				customWait(3000);
				logPassed("Switching back to the Parent Tab", "N.A.", "Should be switched to the parent tab",
						"Switched to the parent tab");
			}
	}

	public Boolean switchToWindowViaWindowHandle(String CurrentWindowHandle) throws InterruptedException {
		Boolean Status = false;
		try {
			checkThreadPauseRequest();
			TLDriverFactory.getTLDriver().switchTo().window(CurrentWindowHandle);
			customWait(1000);
			if (TLDriverFactory.getTLDriver().getWindowHandle().equals(CurrentWindowHandle)) {
				logPassed("Switching to the mentioned tab", "Should be switched to the desired tab",
						"Switched to the desired tab");
				Status = true;
			} else
				logInfo("Unable to switch the window");
		} catch (Exception E) {
			logException(E);
		}
		return Status;
	}

	public String currentWindowHandle() throws InterruptedException {
		checkThreadPauseRequest();
		logMessage("Getting the current window handle");
		return driver.getWindowHandle();

	}

	public void closeCurrentTab() throws InterruptedException {
		checkThreadPauseRequest();

		driver.close();
	}

	/**
	 * Going back to the previous page
	 **/
	public void navigateBack() throws InterruptedException {
		checkThreadPauseRequest();
		driver.navigate().back();
		logMessage("Navigated Back");
		waitForLoadDocument();
	}

	/**
	 * To switch to a particular frame with index value
	 **/
	public void switchToIframe(int i) {
		checkThreadPauseRequest();

		driver.switchTo().frame(i);
		logPassed("Switching to the iframe", "N.A.", "Should be switched to the frame", "Switched to the frame");
	}

	/**
	 * To switch to a particular frame with locator
	 **/
	public void switchToIframeByLocator(WebElement Element) {
		checkThreadPauseRequest();
		// int size = driver.findElements(By.tagName("iframe")).size();

		driver.switchTo().frame(Element);
		logPassed("Switching to the iframe", "N.A.", "Should be switched to the frame", "Switched to the frame");
	}

	/**
	 * To switch frame via WebElement
	 **/
	public void switchToIframeByWebElement(WebElement E) {
		checkThreadPauseRequest();

		driver.switchTo().frame(E);
		logPassed("Switching to the iframe", "N.A.", "Should be switched to the frame", "Switched to the frame");
	}

	/**
	 * To count number of Iframes
	 **/
	public int noOfIframes() {
		checkThreadPauseRequest();

		List<WebElement> iframeElements = driver.findElements(By.tagName("iframe"));
		return iframeElements.size();
	}

	/**
	 * To change the URL
	 **/
	public void changeURL(String URL) {
		checkThreadPauseRequest();

		driver.get(URL);
	}

	public static void logTestCaseDescripiton(String testCaseDescription) {
		logMessage(testCaseDescription);
	}

	/**
	 * To scroll till a particular element using Action Class
	 **/
	public void scrollToParticularElement(WebElement element) {
		checkThreadPauseRequest();

		/**
		 * Actions actions = new Actions(driver); actions.moveToElement(element);
		 * actions.perform();
		 **/
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollIntoView();", element);
			waitUntilElementIsClickable(element, "");
		} catch (Exception E) {
			logException(E);
		}
	}

	public void mouseHoverJScript(WebElement HoverElement) {
		checkThreadPauseRequest();

		try {
			if (isElementPresent(HoverElement)) {

				String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover', true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
				((JavascriptExecutor) driver).executeScript(mouseOverScript, HoverElement);

			} else {
				logInfo("Element was not visible to hover " + "\n");

			}
		} catch (StaleElementReferenceException e) {
			logInfo("Element with " + HoverElement + "is not attached to the page document" + e.getStackTrace());
		} catch (NoSuchElementException e) {

			logException(e);
			System.out.println("Element " + HoverElement + " was not found in DOM" + e.getStackTrace());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occurred while hovering" + e.getStackTrace());
			logException(e);
		}
	}

	public static boolean isElementPresent(WebElement element) {
		new Tek_Properties().checkThreadPauseRequest();
		boolean flag = false;
		try {
			if (element.isDisplayed() || element.isEnabled())
				flag = true;
		} catch (NoSuchElementException e) {
			flag = false;
		} catch (StaleElementReferenceException e) {
			flag = false;
		}
		return flag;
	}

	public static boolean isElementEnabled(WebElement element) {
		new Tek_Properties().checkThreadPauseRequest();
		boolean flag = false;
		try {
			if (element.isEnabled())
				flag = true;
		} catch (NoSuchElementException e) {
			flag = false;
		} catch (StaleElementReferenceException e) {
			flag = false;
		}
		return flag;
	}

	public WebElement getWebElement(WebElement element) {
		checkThreadPauseRequest();

		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		return element;
	}

	/*
	 * @Description Method to click by action class
	 * 
	 * @Author Paramaguru
	 * 
	 * @Created 23/01/2019
	 * 
	 * @Modified
	 */
	public boolean actionClick(WebElement element, String elementName) {
		checkThreadPauseRequest();
		driver = TLDriverFactory.getTLDriver();
		boolean result = false;
		try {
			Actions action = new Actions(driver);
			action.moveToElement(element).click().build().perform();
			logInfo("Action Click " + elementName, "NA", elementName + " should be clicked",
					elementName + " is clicked");
			getUILogs();
			result = true;

		} catch (Exception e) {
			logFailed("Action Click " + elementName, "NA", elementName + " should be clicked", getExceptionDetails(e));
			result = false;
		}

		return result;
	}

	/*
	 * @Description Method to click by js
	 * 
	 * @Author Paramaguru
	 * 
	 * @Created 23/01/2019
	 * 
	 * @Modified
	 */
	public boolean jsClick(WebElement element, String elementName) {
		checkThreadPauseRequest();
		driver = TLDriverFactory.getTLDriver();
		boolean result = false;
		try {
			customWait(1000);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			// js.executeScript("arguments[0].scrollIntoView(true);", element);
			// js.executeScript("window.scroll(0,20);", "");
			highLighterMethod(element, "");
			unHighLighterMethod(element, "");
			js.executeScript("arguments[0].click();", element);

			logInfo("Click " + elementName, "NA", elementName + " should be clicked", elementName + " is clicked");

			getUILogs();

			result = true;

		} catch (Exception e) {
			logException(e);
		}
		return result;
	}

	/*
	 * @Description Method to scroll to element by js
	 * 
	 * @Author Paramaguru
	 * 
	 * @Created 23/01/2019
	 * 
	 * @Modified
	 */
	public void jsMoveToElement(WebElement element) {
		checkThreadPauseRequest();

		try {
			customWait(1000);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (Exception e) {
		}
	}

	/*
	 * @Description Method to scroll to element by js
	 * 
	 * @Author Paramaguru
	 * 
	 * @Created 23/01/2019
	 * 
	 * @Modified
	 */
	public void jsScrollDown() {
		checkThreadPauseRequest();

		try {
			customWait(1000);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0,20);", "");

		} catch (Exception e) {
		}
	}

	public boolean scrollAndClick(WebElement element, String elementName) {
		checkThreadPauseRequest();

		int i = 0;
		try {
			customWait(1000);
			Boolean success = waitUntilElementIsClickable(element, elementName);
			if (success) {
				scrollToElement(element, elementName);
				waitUntilElementFound(element);
				highLighterMethod(element, "");
				unHighLighterMethod(element, "");
				element.click();
				logPassed("Click " + elementName, "NA", elementName + " should be clicked",
						elementName + " is clicked");
				return true;
			}
		} catch (Exception e) {
			if (i == 0) {
				try {
					scrollToElement(element, elementName);
					element.click();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				i = i + 1;
			} else {
				i = 0;
				// this.test.log(LogStatus.FAIL, "Unable to click on "+ desc);
				logFailed("Click " + elementName, "NA", elementName + " should be clicked",
						elementName + " is not clicked");
				e.getStackTrace();
			}
		}

		return false;
	}

	public boolean isElementExists(WebElement element) {
		checkThreadPauseRequest();

		boolean isPresent = false;
		try {
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			element.isDisplayed();
			isPresent = true;
		} catch (Throwable e) {
			driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
			// logInfo("Element does not exists", "", "", "");
			isPresent = false;
		}
		driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		return isPresent;
	}

	public void typeEnter() {
		checkThreadPauseRequest();

		Actions action = new Actions(driver);

		action.keyDown(Keys.ENTER).keyUp(Keys.ENTER).perform();
	}

	public void switchToWindow(int windowNumber) {
		checkThreadPauseRequest();

		driver.switchTo().window((String) driver.getWindowHandles().toArray()[windowNumber]);
		if (windowNumber == 0)
			logInfo("Navigated To Main Window");
		else
			logInfo("Navigated To Child Window " + windowNumber);

	}

	public void switchToDefaultWindow() {
		checkThreadPauseRequest();

		driver.switchTo().defaultContent();
	}

	public String verifyTextPresent(WebElement we, String text) {
		checkThreadPauseRequest();

		String uiText = null;
		try {
			uiText = we.getText();
			if (uiText.equalsIgnoreCase(text)) {
				logPassed("Verify text is present", "Text should be present: " + text, "Text is present: " + text);
			} else {
				logFailed("Verify text is present", "Text should be present: " + text, "Text is present: " + uiText);
			}

		} catch (Exception e) {
			logException(e);
		}
		return uiText;
	}

	public String verifyTextContains(WebElement we, String text) {
		checkThreadPauseRequest();

		String uiText = null;
		try {
			uiText = we.getText();
			if (uiText.contains(text) || text.contains(uiText)) {
				logPassed("Verify " + text + " is present", text + " should be present: ", text + " is present");
				return uiText;
			} else {
				logFailed("Verify " + text + " is present", text + " should be present: ", text + " is not present");
				return uiText;
			}

		} catch (Exception e) {
			logException(e);
		}
		return uiText;
	}

	public String selectValueFromDropdown(List<WebElement> dropdownList, int value) {
		// pauseOtherThreads();
		checkThreadPauseRequest();
		try {
			String text = dropdownList.get(value).getText();
			logMessage("Value to be selected", text);
			click(dropdownList.get(value), text);
			return text;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean selectValueFromDropdown(List<WebElement> dropdownList, String value) {
		// pauseOtherThreads();
		checkThreadPauseRequest();
		logMessage("Value to be selected", value);
		try {
			customWait(2000);
			// dropdownList = driver.findElements(By.xpath("//span[@role='menuitem']"));
			for (WebElement we : dropdownList) {
				if (we.getText().trim().contains(value)) {
					clickUsingActions(we, value);
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		waitForLoadDocument();
		return false;
	}

	public boolean selectValueFromDropdownWithExactMatch(List<WebElement> dropdownList, String value) {
		checkThreadPauseRequest();
		Boolean Status = true;
		logMessage("Value to be selected", value);
		try {
			customWait(2000);
			for (WebElement we : dropdownList) {
				if (we.getText().trim().equals(value)) {
					Status = clickUsingActions(we, value);
					break;
				}
			}
		} catch (Exception e) {
			Status = false;
		}
		waitForLoadDocument();
		return Status;
	}

	/**
	 * To Check Http Response
	 *
	 * @throws IOException
	 */

	public void checkHTTPResponse(String ImgURL) throws IOException {

		String SuccessMsg;

		if (ImgURL.contains("http")) {
			URL url = new URL(ImgURL);

			HttpURLConnection httpURLConnect = (HttpURLConnection) url.openConnection();

			httpURLConnect.setConnectTimeout(3000);

			httpURLConnect.connect();
			SuccessMsg = (ImgURL + " - " + httpURLConnect.getResponseMessage());

			if (httpURLConnect.getResponseCode() == 200) {

				logPassed("URL has been verified", ImgURL, SuccessMsg, ImgURL + "+Ok");

			} else if (httpURLConnect.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				logFailed("Improper URL", ImgURL, SuccessMsg, ImgURL + "+Ok");
			}

		}

	}

	public void getUILogs() {
		checkThreadPauseRequest();

		if (property_showBrowserLog) {
			LogEntries logs = driver.manage().logs().get(LogType.BROWSER);

			for (LogEntry entries : logs) {
				logBrowserLogs(entries.getMessage());
			}
		}

		if (property_showNetworkLog) {
			getNetworkLogs();
		}
	}

	/**
	 * @author ahamed
	 * @createdDate 12 Jun 2020
	 * @description This method takes the network log data in hashmap.
	 */
	public HashMap<String, NetworkLog> getNetworkLogs() {
		checkThreadPauseRequest();
		driver = TLDriverFactory.getTLDriver();
		try {
			if (mapApiRequests.get() == null) {
				mapApiRequests.set(new HashMap<String, NetworkLog>());
			}

			List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
			for (LogEntry entry : entries) {
				String json = entry.getMessage();
				JsonNode jsonNodes = new ObjectMapper().readValue(json, JsonNode.class);
				Iterator<Map.Entry<String, JsonNode>> it = jsonNodes.fields();
				while (it.hasNext()) {
					Map.Entry<String, JsonNode> fields = it.next();
					JsonNode message = fields.getValue();
					if (message == null)
						continue;

					String method = null;
					String type = null;
					String requestId = null;
					NetworkLog networkLog = new NetworkLog();
					try {
						method = message.get("method") == null ? null : message.get("method").asText();
						requestId = message.get("params").get("requestId") == null ? null
								: message.get("params").get("requestId").asText();
						// networkLog.requestId = requestId;
						type = message.get("params").get("type") == null ? null
								: message.get("params").get("type").asText();
					} catch (Throwable t) {
					}

					if (method != null && method.equalsIgnoreCase("Network.requestWillBeSent") && type != null
							&& type.equalsIgnoreCase("XHR")) {
						String apiUrl = null;
						JsonNode postData = null;
						String apiMethod = null;
						String uiUrl = null;
						networkLog = mapApiRequests.get().get(requestId);
						if (networkLog == null) {
							networkLog = new NetworkLog();
							networkLog.requestId = requestId;
							networkLog.type = type;
						}

						try {
							uiUrl = message.get("params").get("documentURL").asText();
						} catch (Throwable t) {
						}
						networkLog.uiUrl = uiUrl;

						try {
							apiUrl = message.get("params").get("request").get("url").asText();
						} catch (Throwable t) {
						}
						networkLog.apiUrl = apiUrl;

						try {
							apiMethod = message.get("params").get("request").get("method").asText();
						} catch (Throwable t) {
						}
						networkLog.apiMethod = apiMethod;

						try {
							String strData = message.get("params").get("request").get("postData").asText();
							try {
								postData = new ObjectMapper().readValue(strData, JsonNode.class);
							} catch (Throwable t) {
								postData = new ObjectMapper().createObjectNode().put("postData", strData);
							}
						} catch (Throwable t) {
						}
						networkLog.postData = postData;

						mapApiRequests.get().put(requestId, networkLog);
					} else if (method != null && method.equalsIgnoreCase("Network.responseReceivedExtraInfo")) {
						networkLog = mapApiRequests.get().get(requestId);
						if (networkLog != null) {
							String status = message.get("params").get("headers").get("status").asText();
							if (status != null) {
								status = status.split("\n")[0];
							}
							networkLog.apiStatus = status;
							mapApiRequests.get().put(requestId, networkLog);
						}
					}
				}
			}
			return mapApiRequests.get();
		} catch (Throwable t) {
			logException(t);
		}
		return null;
	}

	/**
	 * @param apiUrl Pass the subset of url. In case of api URL like this:
	 *               http://preprodapp.tekioncloud.com/api/settings/u/dealer/dealerMaster,
	 *               pass this subset: /api/settings/u/dealer/dealerMaster
	 * @return List<NetworkLog> null - in case of issues. List<NetworkLog> of size 0
	 *         - No api data available for this.
	 */
	public List<NetworkLog> getNetworkLogs(String apiUrl) {
		try {
			getNetworkLogs();
			ArrayList<NetworkLog> listNetworkLog = new ArrayList<>();
			HashMap<String, NetworkLog> mapNetworkLog = mapApiRequests.get();
			if (mapNetworkLog == null) {
				return listNetworkLog;
			}
			for (Map.Entry<String, NetworkLog> entry : mapApiRequests.get().entrySet()) {
				NetworkLog networkLog = entry.getValue();
				if (entry.getValue().apiUrl == null) {
					continue;
				}
				if (networkLog.apiUrl.contains(apiUrl)) {
					listNetworkLog.add(networkLog);
				}
			}
			return listNetworkLog;
		} catch (Throwable t) {
			logException(t);
		}
		return null;
	}

	public static void setNetworkLogsInReport() {
		try {
			if (!property_showNetworkLog) {
				return;
			}
			HashMap<String, ArrayList<NetworkLog>> mapUrlNetworkLogList = new HashMap<>();
			ArrayList<NetworkLog> networkLogList = new ArrayList<>();

			if (mapApiRequests.get() == null)
				return;
			for (Map.Entry<String, NetworkLog> entry : mapApiRequests.get().entrySet()) {
				NetworkLog networkLog = entry.getValue();
				networkLogList = mapUrlNetworkLogList.get(networkLog.uiUrl);
				if (networkLogList == null) {
					networkLogList = new ArrayList<>();
				}
				networkLogList.add(networkLog);
				mapUrlNetworkLogList.put(networkLog.uiUrl, networkLogList);
			}

			for (Map.Entry<String, ArrayList<NetworkLog>> entry : mapUrlNetworkLogList.entrySet()) {
				String uiUrl = entry.getKey();
				networkLogList = entry.getValue();
				ArrayList<NetworkLog> failedNetworkLogList = new ArrayList<NetworkLog>();
				ArrayList<NetworkLog> passedNetworkLogList = new ArrayList<NetworkLog>();
				for (int i = 0; i < networkLogList.size(); i++) {
					Integer apiStatus = 0;
					try {
						apiStatus = Integer.valueOf(networkLogList.get(i).apiStatus);
					} catch (Throwable t) {
					}
					if ((apiStatus >= 200 && apiStatus < 300) || apiStatus == 0) {
						passedNetworkLogList.add(networkLogList.get(i));
					} else {
						failedNetworkLogList.add(networkLogList.get(i));
					}

				}
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String passedNetworkLogs = objectMapper.writeValueAsString(passedNetworkLogList);
				String failedNetworkLogs = objectMapper.writeValueAsString(failedNetworkLogList);
				if (failedNetworkLogList.size() == 0) {
					Common.writeDataIntoReport("info", "UI API calls made in : <br/>" + uiUrl, "",
							passedNetworkLogList.size() + " Passed API calls", passedNetworkLogs, "No failed API Calls",
							"", false);
				} else {
					Common.writeDataIntoReport("warning", "UI API calls made in : <br/>" + uiUrl, "",
							passedNetworkLogList.size() + " Passed API calls", passedNetworkLogs,
							failedNetworkLogList.size() + " failed API Calls", failedNetworkLogs, false);
				}
			}
		} catch (Throwable t) {
			logException(t);
		}
	}

	/**
	 * For Checking Disabled Elements
	 **/

	public boolean checkDisabledElement(WebElement element, String ElementName) {
		checkThreadPauseRequest();

		boolean ElementStatus = element.isEnabled();

		if (ElementStatus == false) {
			logPassed(ElementName + " is disabled", "false", String.valueOf(ElementStatus));
			return true;
		} else {
			logFailed(ElementName + " is enabled", "false", String.valueOf(ElementStatus));
			return false;
		}
	}

	/**
	 * Fetching the attribute value and comparing it with the required string
	 **/

	public boolean checkAttributeValue(WebElement Element, String ElementName, String AttributeName,
                                       String ExpectedText) {
		checkThreadPauseRequest();

		String ActualText = getAttributeValue(Element, AttributeName);

		if (ActualText.equalsIgnoreCase(ExpectedText)) {
			logPassed(ElementName + " text has been verified", ExpectedText, ActualText);
			return true;
		} else {
			logFailed(ElementName + " text is failed to verify", ExpectedText, ActualText);
			return false;
		}
	}

	/**
	 * @author kirankumar
	 * @createdDate
	 * @modifiedBy
	 * @modifiedDate 13-Apr-2020
	 * @description Getting the Text and comparing it with the required string
	 */
	public Boolean checkGetTextValue(WebElement Element, String ElementName, String ExpectedText) {
		Boolean Status = false;
		checkThreadPauseRequest();
		try {
			String ActualText = getText(Element, ElementName);

			if (ActualText.trim().equalsIgnoreCase(ExpectedText.trim())) {
				logPassed(ElementName + " text has been verified", ExpectedText, ActualText);
				Status = true;
			} else {
				logFailed(ElementName + " text is failed to verify", ExpectedText, ActualText);
			}
		} catch (Exception E) {
			logException(E);
		}
		return Status;
	}

	/**
	 * Get Outer HTML
	 **/
	public String getOuterHtml(WebElement Element, String ElementName) {
		checkThreadPauseRequest();

		String OuterText = null;
		try {
			OuterText = Element.getAttribute("outerHTML");
			logInfo("Outer HTML text has been retrived", OuterText);
		} catch (Exception E) {
			logException(E);
		}
		return OuterText;
	}

	Random random = new Random();
	String SelectedDrpDwnValue;

	/**
	 * @author kirankumar
	 * @createdDate 15-Nov-2019
	 * @modifiedBy kiran kumar
	 * @modifiedDate 03-02-2020 added try catch block
	 * @description Select an random Value from DrpDwn and return the selected drop
	 *              down value
	 */
	public String selectRandomValueFromDrpDwn(int size, List<WebElement> ListOfElements) {
		checkThreadPauseRequest();
		String SelectedDrpDwnValue = "";
		try {
			if (waitUntilElementIsClickable(ListOfElements.get(0), "Drop Down")) {

				customWait(2000);
				int sizenum = random.nextInt(size);
				moveToElement(ListOfElements.get(sizenum));
				customWait(2000);
				waitUntilElementFound(ListOfElements.get(sizenum));
				SelectedDrpDwnValue = ListOfElements.get(sizenum).getText();
				if (!(SelectedDrpDwnValue.equals("M-Memo"))) {
					click(ListOfElements.get(sizenum), "Dropdown index - " + (sizenum + 1));
					customWait(2000);
					return SelectedDrpDwnValue;
				} else {
					click(ListOfElements.get(sizenum + 1), "Dropdown index - " + (sizenum + 2));
					customWait(2000);
					return SelectedDrpDwnValue;
				}

			} else {
				customWait(3000);
				int sizenum = random.nextInt(size);
				moveToElement(ListOfElements.get(sizenum));
				customWait(1000);
				SelectedDrpDwnValue = ListOfElements.get(sizenum).getText();
				click(ListOfElements.get(sizenum), "Dropdown index - " + (sizenum + 1));
				customWait(2000);
			}
		} catch (Exception E) {
			logException(E);
		}
		return SelectedDrpDwnValue;
	}

	/**
	 * @author kirankumar
	 * @createdDate 15-Nov-2019
	 * @modifiedBy
	 * @modifiedDate
	 * @description Select an random Value from DrpDwn and return the selected from
	 *              other tag
	 */
	public String selectRandomValueFromDrpDwnAndFetchTextFromOtherTag(int size, List<WebElement> ListOfElements,
			WebElement Tag) {
		checkThreadPauseRequest();

		int sizenum = random.nextInt(size);
		customWait(3000);
		click(ListOfElements.get(sizenum), String.valueOf(ListOfElements));
		customWait(2000);

		SelectedDrpDwnValue = getText(Tag, String.valueOf(Tag));
		return SelectedDrpDwnValue;
	}

	/**
	 * Select an random Value from DrpDwn and return the selected from other tag's
	 * Attribute Value
	 **/
	public String selectRandomValueFromDrpDwnAndFetchTextFromOtherTagAttributeValue(int size,
                                                                                    List<WebElement> ListOfElements, WebElement Tag, String Attribute) {
		checkThreadPauseRequest();

		customWait(2000);
		int sizenum = random.nextInt(size);
		click(ListOfElements.get(sizenum), "Index - " + (sizenum + 1));
		customWait(2000);

		SelectedDrpDwnValue = getAttributeValue(Tag, Attribute);
		return SelectedDrpDwnValue;
	}

	public static void logException(Throwable e) {
		logException(e, false);
	}

	public static String logException(Throwable e, Boolean asWarning) {
		try {
			String exClassList = getExceptionClassList(e);
			String exceptionMessage = getExceptionMessage(e);
			String exceptionClassName = getExceptionClassName(e);
			if (asWarning)
				logWarning("Exception occured: " + exceptionClassName, exceptionMessage, "", exClassList);
			else
				logFailed("Exception occured: " + exceptionClassName, exceptionMessage, "", exClassList);
		} catch (Exception t) {
			logFailed("Exception occured in logging: ", "", "");
		}

		return null;
	}

	public static String getExceptionClassList(Throwable e) {
		List<String> exClassList = new ArrayList<String>();
		try {
			String ourPackages[] = { "com.web", "com.mobile", "com.api", "com.utilities", "com.helper", "com.tekion",
					"com.jsonmapper", "com.admin", "com.bdc", "com.datamigration", "com.mobileweb" };

			StackTraceElement[] stackTrace = e.getStackTrace();

			for (int i = stackTrace.length - 1; i >= 0; i--) {
				for (int j = 0; j < ourPackages.length; j++) {
					if (stackTrace[i].getClassName().startsWith(ourPackages[j])) {
						exClassList.add(stackTrace[i].toString().substring(stackTrace[i].toString().lastIndexOf("(")));
						break;
					}
				}
			}
		} catch (Throwable t) {
			logFailed("Exception occurred in logging");
		}
		return exClassList.toString();
	}

	public static String getExceptionClassName(Throwable e) {
		String exceptionClassNameWithPackage = e.getClass().getCanonicalName();
		String exceptionClassName = exceptionClassNameWithPackage
				.substring(exceptionClassNameWithPackage.lastIndexOf(".") + 1);
		return exceptionClassName;
	}

	public static String getExceptionMessage(Throwable e) {
		String exceptionMessage = e.getMessage();
		if (exceptionMessage == null) {
			exceptionMessage = "Null Pointer";
		} else if (exceptionMessage.contains("no such element")) {
			exceptionMessage = exceptionMessage.substring(0, exceptionMessage.indexOf("(Session info"));
		}
		return exceptionMessage;
	}

	public static String getExceptionDetails(Throwable e) {
		String exceptionDetails = getExceptionMessage(e) + "<br/>" + getExceptionClassName(e) + "<br/>"
				+ getExceptionClassList(e);
		return exceptionDetails;
	}

	public List<String> getPackageNames() {
		List<String> packageList = new ArrayList<String>();

		for (Package pkg : Package.getPackages()) {
			packageList.add(pkg.getName());
		}
		return packageList;
	}

	public int getColumnNumber(List<WebElement> headerElements, String columnName) {

		int count = 0;
		try {
			for (int i = 0; i < headerElements.size(); i++) {
				if (headerElements.get(i).getText().equalsIgnoreCase(columnName)) {
					count = i + 1;
				}
			}
		} catch (Exception e) {
			logFailed(e.getStackTrace()[0].getMethodName(), e.getMessage());
		}

		return count;
	}

	public String getCurrentDay() {

		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

		int todayInt = calendar.get(Calendar.DAY_OF_MONTH);

		String todayStr = Integer.toString(todayInt);

		return todayStr;

	}

	public String getCurrentDate() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public String getCurrentDate(String format) {

		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date();
		return dateFormat.format(date);
	}

	public String getCurrentTime(String format) {

		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {
			if (file.list().length == 0) {
				file.delete();
			} else {
				String files[] = file.list();

				for (String temp : files) {
					File fileDelete = new File(file, temp);
					delete(fileDelete);
				}
				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			file.delete();
		}
	}

	public void dragAndDrop(WebElement source, WebElement target) {
		checkThreadPauseRequest();

		Actions builder = new Actions(driver);
		builder.keyDown(Keys.CONTROL).click(source).dragAndDrop(source, target).keyUp(Keys.CONTROL);

		Action selected = builder.build();

		selected.perform();
	}

	public String selectValueFromReactDropdown(String value) {
		return selectValueFromReactDropdown(value, true);
	}

	public String selectValueFromReactDropdown(String value, boolean createAsANew) {
		checkThreadPauseRequest();
		logMessage("Value to be selected", value);
		try {
			customWait(2000);
			List<WebElement> reactSelect_Options = driver
					.findElements(By.xpath("//div[contains(@id,'react-select')][contains(@id,'option')]"));
			String dropdownValue = null;
			for (WebElement we : reactSelect_Options) {
				if (we.getText().equalsIgnoreCase(value)) {
					dropdownValue = we.getText();
					jsClick(we, value);
					return dropdownValue;
				}
			}
			for (WebElement we : reactSelect_Options) {
				if (we.getText().contains(value)) {
					dropdownValue = we.getText();
					String temp = removeSpecialChars(dropdownValue);
					if (temp.toLowerCase().contains("create") && temp.toLowerCase().contains("asanew")) {
						if (createAsANew) {
							jsClick(we, value);
						} else
							continue;
					} else {
						jsClick(we, value);
					}
					return dropdownValue;
				}
			}

		} catch (Exception e) {
			logException(e);
		}
		return null;
	}

	public String selectValueFromReactDropdown(int value) {
		// pauseOtherThreads();
		checkThreadPauseRequest();
		try {
			customWait(2000);
			List<WebElement> reactSelect_Options = driver
					.findElements(By.xpath("//div[contains(@id,'react-select')][contains(@id,'option')]"));
			String dropdownValue = reactSelect_Options.get(value - 1).getText();
			jsClick(reactSelect_Options.get(value - 1), dropdownValue);
			return dropdownValue;
		} catch (Exception e) {
			logException(e);
		}
		return null;
	}

	public String selectValueFromTextDropdown(String value) {
		// pauseOtherThreads();
		checkThreadPauseRequest();
		try {
			customWait(2000);
			WebElement we = driver.findElement(By.xpath("//*[contains(text(),'" + value + "')]"));
			// WebElement we =
			// driver.findElements(By.xpath("//div[contains(@id,'react-select')][contains(@id,'option')]")).get(1);
			click(we, value);
		} catch (Exception e) {
			logException(e);
		}
		return null;
	}

	public List<String> getAllValuesFromReactDropdown() {
		checkThreadPauseRequest();

		try {
			customWait(2000);
			List<String> valueList = new ArrayList<>();
			List<WebElement> reactSelect_Options = driver
					.findElements(By.xpath("//div[contains(@id,'react-select')][contains(@id,'option')]"));

			for (WebElement we : reactSelect_Options) {
				valueList.add(we.getText());
			}

			return valueList;

		} catch (Exception e) {
			logException(e);
		}
		return null;
	}

	/**
	 * @author kirankumar
	 * @createdDate 13-Feb-2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description To select a random value from react drop down Modified this
	 *              method since to avoid selecting first option in certain cases
	 */
	public String selectRandomValueFromReactDropdown() {
		checkThreadPauseRequest();
		String dropdownValue = "";
		try {

			customWait(2000);
			List<WebElement> reactSelect_Options = driver
					.findElements(By.xpath("//div[contains(@id,'react-select')][contains(@id,'option')]"));
			waitUntilElementFound(reactSelect_Options.get(0));
			if (reactSelect_Options.size() > 1) {
				int randomvalue = getRandomNumberBetweenRange(1, reactSelect_Options.size() - 1);
				scrollToParticularElement(reactSelect_Options.get(randomvalue));
				dropdownValue = reactSelect_Options.get(randomvalue).getText();
				jsClick(reactSelect_Options.get(randomvalue), "Random drop down");
			} else {
				jsClick(reactSelect_Options.get(0), "First drop down");
			}
		} catch (Exception e) {
			logException(e);
		}
		return dropdownValue;
	}

	public static int getRandomNumberBetweenRange(int low, int high) {
		try {

			Random r = new Random();
			int result = 0;
			int lowNum = low ;
			int highNum = high;

			if(low!=high) {
				result = r.nextInt(highNum-lowNum) + low;
				return result;
			}else {
				result =low;
			}
		}
		// System.out.println(result);
		catch(Exception E) {
			logException(E);
		}
		return low;
	}

	/**
	 * @author kirankumar
	 * @createdDate 12-Apr-2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description getNoOfReactDropDowns
	 */
	public int getNoOfReactDropDowns() {
		List<WebElement> reactSelect_Options = null;
		try {
			reactSelect_Options = driver
					.findElements(By.xpath("//div[contains(@id,'react-select')][contains(@id,'option')]"));
		} catch (Exception E) {
			logException(E);
		}
		return reactSelect_Options.size();
	}

	public boolean equalsIgnoreCase(String str1, String str2) {
		if (str1 != null) {
			if (str2 != null) {
				if (str1.equalsIgnoreCase(str2)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public boolean contains(String str1, String str2) {

		if (str1 != null) {
			if (str2 != null) {
				if (str1.contains(str2) || str2.contains(str1)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public boolean dragAndDrop(WebElement source, WebElement target, String elementName) {
		checkThreadPauseRequest();

		boolean result = false;
		try {
			customWait(1000);
			highLighterMethod(source, "");
			unHighLighterMethod(source, "");
			highLighterMethod(source);
			unHighLighterMethod(source);
			Actions action = new Actions(driver);
			// action.dragAndDrop(source, target).build().perform();
			action.dragAndDropBy(source, 1414, 368).build().perform();
			// action.clickAndHold(source)
			// .pause(Duration.ofSeconds(2))
			// .moveToElement(target)
			// .release()
			// .perform();
			// System.out.println(target.getLocation().getX());
			// System.out.println(target.getLocation().getY());
			logPassed("Move " + elementName, "NA", elementName + " should be moved", elementName + " is moved");
			result = true;

		} catch (Exception e) {
			// this.test.log(LogStatus.FAIL, "Unable to click on "+ desc);
			logFailed("Move " + elementName, "NA", elementName + " should be moved", elementName + " is not moved");
			result = false;
			// e.getStackTrace();
		}

		return result;
	}

	public void dragdrop(WebElement source, WebElement target) {
		checkThreadPauseRequest();

		Actions builder = new Actions(driver);
		builder.keyDown(Keys.CONTROL).click(source).dragAndDrop(source, target).keyUp(Keys.CONTROL);

		Action selected = builder.build();

		selected.perform();
	}

	public static Object nvl(Object ifThisIsNull, String replaceThis) {
		return ifThisIsNull == null ? replaceThis : ifThisIsNull;
	}

	public <T> List<T> removeDuplicatesInList(List<T> list) {
		List<T> updatedList = new ArrayList<T>();
		try {
			Set<T> set = new LinkedHashSet<T>();
			set.addAll(list);
			updatedList.addAll(set);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return updatedList;
	}

	public boolean clickNoWait(WebElement element, String elementName) {
		checkThreadPauseRequest();

		try {
			element.click();
			logInfo("Click " + elementName, "NA", elementName + " should be clicked", elementName + " is clicked");
			getUILogs();
			return true;

		} catch (Exception e) {
			logFailed("Click " + elementName, "NA", elementName + " should be clicked", getExceptionDetails(e));
		}

		return false;
	}

	/**
	 * @author kirankumar
	 * @createdDate
	 * @modifiedBy
	 * @modifiedDate 30-Dec-2019
	 * @description enterTextandClickonenterbutton
	 */
	public Boolean enterTextandClickonenterbutton(WebElement element, String Searchval, String ElementName) {
		checkThreadPauseRequest();

		try {
			customWait(1000);
			element.sendKeys(Searchval + Keys.ENTER);
			Reporting("Entered the text '" + Searchval + "' into " + ElementName, Searchval, "true", "true");
			customWait(1000);
			return true;
		} catch (Exception e) {
			logFailed("Failed to Enter data in searchfield ", getExceptionDetails(e));
			return false;
		}
	}

	/**
	 * It creates the WebElement with the by varialbe sent as parameter.<br/>
	 * It uses ExpectedConditions.elementToBeClickable(by)
	 *
	 * @param by
	 * @return WebElement if not element exists, null will be passed.
	 */
	public WebElement createElement(By by, String desc) {
		checkThreadPauseRequest();
		WebElement element = null;
		try {
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			wait = new WebDriverWait(driver, 5);
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			if (desc != null) {
				highLighterMethodWithGreen(element, desc);
				logInfo(desc + " is identified");
				unHighLighterMethod(element, desc);
			}
		} catch (Exception e) {
			if (desc != null)
				logFailed(desc + " is not identified", "", "", getExceptionDetails(e));
		} finally {
			driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return element;
	}

	public WebElement createElementNoWait(By by, String desc) {
		checkThreadPauseRequest();
		WebElement element = null;
		try {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			element = TLDriverFactory.getTLDriver().findElement(by);
			if (desc != null) {
				highLighterMethodWithGreen(element, desc);
				logInfo(desc + " is identified");
				unHighLighterMethod(element, desc);
			}
		} catch (Exception e) {
			if (desc != null)
				logFailed(desc + " is not identified", "", "", getExceptionDetails(e));
		} finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return element;
	}

	/**
	 * @param
	 * @author ahamedabdulrahman
	 * @createdDate 8 Nov 2019
	 * @modifiedBy
	 * @modifiedDate
	 * @description Create list of elements
	 */
	public List<WebElement> createElements(By by, String desc) {
		checkThreadPauseRequest();
		List<WebElement> list = new ArrayList<WebElement>();
		try {
			list = driver.findElements(by);

			/*
			 * The following code waits for 5 seconds till the list of elements got loaded.
			 * This is vital in case of mobile, in case of web, add if needed. As of now,
			 * not required. int counter = 0; while(list.size()==0 && counter < 5) {
			 * customWait(1000); list = driver.findElements(by); counter++; }
			 */

			if (desc != null)
				logInfo(desc + " is identified");
		} catch (Exception e) {
			if (desc != null)
				logFailed(desc + " is not identified", "", "", getExceptionDetails(e));
		}
		return list;
	}

	/**
	 * This method will compare the text() of the webelement with the text mentioned
	 * in the method.<br/>
	 *
	 * @param textDesc      Description about the test
	 * @param inputValue    The input value that is provided
	 * @param expectedValue Expected String value
	 * @param actualValue   Actual String value
	 */
	public static boolean validateTextWithCase(String textDesc, String inputValue, String expectedValue,
			String actualValue) {
		if (expectedValue != null && actualValue != null) {
			if (expectedValue.trim().equals(actualValue.trim())) {
				logPassed("Validating " + textDesc, inputValue, expectedValue, actualValue);
				return true;
			} else {
				logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
				return false;
			}
		} else if (actualValue == null && expectedValue == null) {
			logPassed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return true;
		} else {
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	/**
	 * This method will compare the text() of the webelement with the text mentioned
	 * in the method ignoring the case<br/>
	 *
	 * @param textDesc      Description about the test
	 * @param inputValue    The input value that is provided
	 * @param expectedValue Expected String value
	 * @param actualValue   Actual String value
	 */
	public static boolean validateTextIgnoringCase(String textDesc, String inputValue, String expectedValue,
			String actualValue) {
		if (expectedValue != null && actualValue != null) {
			if (expectedValue.trim().equalsIgnoreCase(actualValue.trim())) {
				logPassed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return true;
			} else {
				logFailed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return false;
			}
		} else if (actualValue == null && expectedValue == null) {
			logPassed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return true;
		} else {
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	/**
	 * @param element - Locator
	 * @param text    - Description
	 */
	public String jsEnterText(WebElement element, String text) {
		checkThreadPauseRequest();

		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('value', '" + text + "')", element);
			logInfo("Enter text ", "", text, text);
		} catch (Exception e) {
			logFailed("Enter text ", "", text, text);
		}
		return text;
	}

	public static boolean validateTextContains(String textDesc, String inputValue, String expectedValue,
			String actualValue) {
		if (expectedValue != null && actualValue != null) {
			if (expectedValue.trim().toLowerCase().contains(actualValue.trim().toLowerCase())
					|| actualValue.trim().toLowerCase().contains(expectedValue.trim().toLowerCase())) {
				logPassed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return true;
			} else {
				logFailed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return false;
			}
		} else if (actualValue == null && expectedValue == null) {
			logPassed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return true;
		} else {
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	public static boolean validateTextIgnoringSpaces(String textDesc, String inputValue, String expectedValue,
			String actualValue) {
		if (expectedValue != null && actualValue != null) {
			String eValue = expectedValue.replaceAll(" ", "");
			String aValue = actualValue.replaceAll(" ", "");
			if (eValue.trim().equalsIgnoreCase(aValue.trim())) {
				logPassed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return true;
			} else {
				logFailed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return false;
			}
		} else if (actualValue == null && expectedValue == null) {
			logPassed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return true;
		} else {
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	public static boolean validateTextIgnoringSpecialChars(String textDesc, String inputValue, String expectedValue,
			String actualValue) {
		if (expectedValue != null && actualValue != null) {
			String eValue = expectedValue.replaceAll("[^a-zA-Z0-9]", "");
			String aValue = actualValue.replaceAll("[^a-zA-Z0-9]", "");
			if (eValue.trim().equalsIgnoreCase(aValue.trim())) {
				logPassed("Validating " + textDesc + " by ignoring Special Chars", inputValue, expectedValue,
						actualValue);
				return true;
			} else {
				logFailed("Validating " + textDesc + " by ignoring Special Chars", inputValue, expectedValue,
						actualValue);
				return false;
			}
		} else if (actualValue == null && expectedValue == null) {
			logPassed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return true;
		} else {
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	public static boolean validateTextContainsIgnoringSpecialChars(String textDesc, String inputValue,
			String expectedValue, String actualValue) {
		if (expectedValue != null && actualValue != null) {
			String eValue = expectedValue.replaceAll("[^a-zA-Z0-9]", "");
			String aValue = actualValue.replaceAll("[^a-zA-Z0-9]", "");
			if (eValue.trim().toLowerCase().contains(aValue.trim().toLowerCase())
					|| aValue.trim().toLowerCase().contains(eValue.trim().toLowerCase())) {
				logPassed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return true;
			} else {
				logFailed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return false;
			}
		} else if (actualValue == null && expectedValue == null) {
			logPassed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return true;
		} else {
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	public String removeSpecialChars(String text) {
		return text.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
	}

	public String keepOnlyNumbersInTheString(String text) {
		return text.replaceAll("[^0-9]", "");
	}

	/**
	 * @author -kirankumar
	 * @createdDate - 28-May-2020
	 * @modifiedBy -
	 * @modifiedDate -
	 * @description - Sorts the list and then compares
	 */
	public Boolean compareTwoList(List<String> A, List<String> B) {
		Collections.sort(A);
		Collections.sort(B);
		if (A.equals(B))
			return true;
		else
			return false;

	}

	public boolean compareStringRemovingSpecialChars(String st1, String str2) {
		if (st1 != null && str2 != null) {
			String serviceNameInCartTemp = st1.replaceAll("[^a-zA-Z0-9]", "");
			serviceNameInCartTemp = serviceNameInCartTemp.replace("\n", "");
			String serviceNameInJobDetailsTemp = str2.replaceAll("[^a-zA-Z0-9]", "");
			serviceNameInJobDetailsTemp = serviceNameInJobDetailsTemp.replace("\n", "");
			if (serviceNameInCartTemp.toLowerCase().contains(serviceNameInJobDetailsTemp.toLowerCase())
					|| serviceNameInJobDetailsTemp.toLowerCase().contains(serviceNameInCartTemp.toLowerCase()))
				return true;
			else
				return false;
		}
		return false;
	}

	/**
	 * The below method converts the string money to double.<br/>
	 * Eg:<br/>
	 * For strMoney = "$25,453,456.00", it returns 25453456.0
	 *
	 * @param strMoney
	 * @return
	 */
	public double convertMoneyToDouble(String strMoney) {
		double d = -1;
		try {
			strMoney = strMoney.replace(MONEY_SYMBOL, "").trim();
			strMoney = strMoney.replace(",", "").trim();
			d = Double.parseDouble(strMoney);
		} catch (NumberFormatException ne) {
			// can be ignored as -1 will be sent.
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return d;
	}

	public boolean handleAlert() {

		boolean flag = false;
		if (isAlertPresent()) {
			Alert alert = driver.switchTo().alert();
			System.out.println(alert.getText());
			alert.accept();
			customWait(3000);
			flag = true;
		}
		return flag;
	}

	public boolean isAlertPresent() {

		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException ex) {
			return false;
		}
	}

	public String convertDoubleToMoney(double dMoney) {
		String sMoney = "";
		try {
			sMoney = format.format(dMoney);
			sMoney = MONEY_SYMBOL + sMoney;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sMoney;
	}

	public String convertDoubleToMoney(long lMoney) {
		String sMoney = "";
		try {
			sMoney = format.format(lMoney);
			String[] sMoneys = sMoney.split("\\.");
			sMoney = sMoneys[0];
			sMoney = MONEY_SYMBOL + sMoney;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sMoney;
	}

	public String getCurrencySymbol(String currency) {
		try {
			if (currency.equalsIgnoreCase("USD"))
				return "$";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "$";
	}

	/**
	 * @author kirankumar
	 * @createdDate 06-Dec-2019
	 * @modifiedBy
	 * @modifiedDate
	 * @description keep only certain length of decimal values without rounding off
	 */
	public String getDecimalFormat(double value, int length) {

		String getValue = String.valueOf(value).split("[.]")[1];

		if (getValue.length() == 1) {
			return String.valueOf(value).split("[.]")[0] + "." + getValue.substring(0, 1)
					+ String.format("%0" + 1 + "d", 0);
		} else {
			return String.valueOf(value).split("[.]")[0] + "." + getValue.substring(0, length);
		}
	}

	/**
	 * @author kirankumar
	 * @createdDate 09-Dec-2019
	 * @modifiedBy
	 * @modifiedDate
	 * @description countDuplicateCharactersInAString
	 */
	public int countDuplicateCharactersInAString(String ReqString, char ReqCharacter) {

		int count = 0;
		for (int i = 0; i < ReqString.length(); i++) {
			if (ReqString.charAt(i) == ReqCharacter) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @param url - Sample:
	 *            https://xyz-tekioncloud-dms-stage.s3.us-west-1.amazonaws.com/3d5d1f16-0298-4623-a978-bf737c6e3af1/2332_1580299246105.pdf?X-Amz-Security-Token\u003dFwoGZXIvYXdzEE0aDMksNb9E1NvMADObgyLWARoqLzYOwHWxxmq5naDrboFxg1grd06lMRQNXFM5Dz7vliMBTSc2V9PrgXbWFNICWxr216aR%2F3Ipm8IEak9gT1YqcGkuIx6crOiV85Q6FPbb9HjyCjXozmtsKc33hF6Wsgcl7MD3g%2FQCelYX4gPoO8MJ4R6zWbOqeWkZbKXar7%2FefqdTALld5mnkNIxCw4ncgNXw5eT8Dn9vyxDk03jgFblBcOBxA%2FO50AUXO8x%2Bw2cz4zN4QDahkfHyZZDvfl61ty5ymJEMUC4AYWHBsHVL7RZcA0a%2BiSAo9OfF8QUyKcvUTJdOOUzWvLh3gOhU0yszrOXdWGrsic7OO7Br52qMPCzaVs%2FvBUtg\u0026X-Amz-Algorithm\u003dAWS4-HMAC-SHA256\u0026X-Amz-Date\u003d20200129T120052Z\u0026X-Amz-SignedHeaders\u003dhost\u0026X-Amz-Expires\u003d900\u0026X-Amz-Credential\u003dASIARTX6TDNMESU7KP5Y%2F20200129%2Fus-west-1%2Fs3%2Faws4_request\u0026X-Amz-Signature\u003d199dbdba47390889a3946591ec2e41dc55af1a104356ccdf978a63b3bb544651
	 *            <p>
	 *            Here, the file name is 2332_1580299246105.pdf
	 * @author ahamedabdulrahman
	 * @createdDate 29 Jan 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description In case of url having the file name, this method can be used.
	 */
	public String getPdfFileName(String url) {
		try {
			int pdfIndex = url.indexOf(".pdf");
			url = url.substring(0, pdfIndex);
			int slashIndex = url.lastIndexOf("/");
			String fileName = url.substring(slashIndex + 1) + ".pdf";
			return fileName;
		} catch (Throwable t) {
			logException(t);
		}
		return null;
	}

	/**
	 * @param
	 * @author ahamedabdulrahman
	 * @createdDate 29 Jan 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description Returns the corresponding file in downloads folder.
	 */
	public File getDownloadedFile(String fileName) {
		try {
			String filePathWithName = TLDriverFactory.getDownloadFolder().getAbsolutePath() + File.separator
					+ nvl(fileName, "");
			return new File(filePathWithName);
		} catch (Throwable t) {
			logException(t);
		}
		return null;
	}

	/**
	 * @param
	 * @author ahamedabdulrahman
	 * @createdDate 29 Jan 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description This method waits for a file to appear for the mentioned
	 *              seconds.
	 */
	public boolean waitForFileToDownload(File file, int maxSecond) {
		for (int counter = 0; counter < maxSecond; counter++) {
			if (file.exists())
				return true;
			;
			customWait(1000);
		}
		return false;
	}

	/**
	 * Sometimes, list can be null or empty. In both cases, this method will return
	 * true. If not false
	 *
	 * @param list
	 * @return
	 */
	public <T> boolean isListEmpty(List<T> list) {
		try {
			if (list == null)
				return true;
			else if (list.size() == 0)
				return true;
			else
				return false;
		} catch (Exception ex) {
			return true;
		}
	}

	/**
	 * @param
	 * @author ahamedabdulrahman
	 * @createdDate 17 Feb 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description getSizeWithoutWait
	 */
	public int getListSizeWithoutWait(List<WebElement> list) {
		checkThreadPauseRequest();
		int size = 0;
		try {
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			size = list.size();
		} catch (Throwable t) {

		} finally {
			driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return size;
	}

	/**
	 * @author kirankumar
	 * @createdDate 04-Mar-2020
	 * @modifiedBy ahamedabdulrahman
	 * @modifiedDate 10-Mar-2020
	 * @description IsElementPresent
	 * @modification1 : Removed the catch(NoSuchElement e) block as the same is done
	 *                by catch(Exception e) block itself.
	 */
	public int isElementPresentInDOM(By Element) {
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		List<WebElement> NoOfElements = null;

		try {
			NoOfElements = driver.findElements(Element);

			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			return NoOfElements.size();
		} catch (Exception E) {
			return 0;
		} finally {
			driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
	}

	/**
	 * @author kirankumar
	 * @createdDate 01-Apr-2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description verifyDisabledAttributePresentOrNot
	 */
	public Boolean verifyDisabledAttributePresentOrNot(WebElement Element, String ElementName) {
		Boolean Status = false;
		try {
			if (Element.getAttribute("disabled") != null) {
				Status = true;
				logPassed(ElementName + " element is disabled");
			} else {
				logFailed(ElementName + " element is not disabled");
			}
		} catch (Exception E) {
			logException(E);
		}
		return Status;
	}

	/**
	 * @author ahamedabdulrahman
	 * @createdDate 1 Apr 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description This method will verify whether a dialog is present. *
	 */
	public boolean isDialogPresent() {
		return isDialogPresent(null);
	}

	/**
	 *
	 * @author ahamedabdulrahman
	 * @createdDate 1 Apr 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description isDialogPresent
	 * @param timeout - Maximum seconds to wait for the dialog to appear. If null,
	 *                default timeout will be taken.
	 */
	public boolean isDialogPresent(Integer timeout) {
		checkThreadPauseRequest();
		boolean status = false;
		if (timeout == null)
			timeout = TIMEOUT_SECONDS;
		driver = TLDriverFactory.getTLDriver();
		try {
			driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
			status = isElementExist(driver.findElement(By.cssSelector("div.ant-modal-title>div")), null);
		} catch (Throwable t) {
		} finally {
			driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return status;
	}

	/**
	 *
	 * @author ahamedabdulrahman
	 * @createdDate 1 Apr 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description This method returns the title of the dialog. Use this method
	 *              after ensuring that the dialog is present using
	 *              isDialogPresent(). This method won't wait for the dialog to
	 *              appear. This is made to hasten up the execution.
	 * @param
	 */
	public String getDialogTitle() {
		checkThreadPauseRequest();
		driver = TLDriverFactory.getTLDriver();
		String title = null;
		try {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			WebElement element = driver.findElement(By.cssSelector("div.ant-modal-title>div"));
			title = element.getText();
		} catch (Throwable t) {
			logException(t);
		} finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return title;
	}

	/**
	 *
	 * @author ahamedabdulrahman
	 * @createdDate 1 Apr 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description This method returns the message that is present in the dialog.
	 *              Use this method after ensuring that the dialog is present using
	 *              isDialogPresent(). This method won't wait for the dialog to
	 *              appear. This is made to hasten up the execution.
	 * @param
	 */
	public String getDialogMessage() {
		checkThreadPauseRequest();
		driver = TLDriverFactory.getTLDriver();
		String message = null;
		try {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			WebElement element = driver.findElement(By.xpath("//div[contains(@class,'icon-info')]//following::div[1]"));
			message = element.getText();
		} catch (Throwable t) {
			logException(t);
		} finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return message;
	}

	/**
	 *
	 * @author ahamedabdulrahman
	 * @createdDate 1 Apr 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description This method returns the list of buttons in the dialog. Use this
	 *              method after ensuring that the dialog is present using
	 *              isDialogPresent(). This method won't wait for the dialog to
	 *              appear. This is made to hasten up the execution.
	 * @param
	 */
	public List<WebElement> getDialogButtons() {
		checkThreadPauseRequest();
		driver = TLDriverFactory.getTLDriver();
		List<WebElement> elements = new ArrayList<WebElement>();
		try {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			elements = driver.findElements(By.cssSelector("div.ant-modal-footer button"));
		} catch (Throwable t) {
			logException(t);
		} finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return elements;
	}

	/**
	 * @author kirankumar
	 * @createdDate 07-Apr-2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description convertStringToDouble
	 */
	public Double convertStringToDouble(String stringvalue) {
		checkThreadPauseRequest();
		try {
			String payStr = stringvalue.replaceAll("[\\$\\,]", "");
			return Double.valueOf(payStr);
		} catch (Exception E) {
			logException(E);
		}
		return null;
	}

	/**
	 * @author kirankumar
	 * @createdDate 08-Apr-2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description removes all the special character from the string and converts
	 *              into float datatype
	 */
	public Float convertStringToFloat(String stringValue) {
		checkThreadPauseRequest();
		try {
			String var = stringValue.replaceAll("[^\\d.]", "");
			return Float.valueOf(var);
		} catch (Exception E) {
			logException(E);
		}
		return null;
	}

	/**
	 *
	 * @author kirankumar
	 * @createdDate 24-Apr-2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description getThresholdDifference
	 */
	public Boolean validateThresholdDifference(Double thresholdvalue, Double var1, Double var2) {
		checkThreadPauseRequest();
		Double var3;
		if (!(var1 >= var2)) {
			var3 = var1;
			var1 = var2;
			var2 = var3;
		}
		if (Math.abs(var1 - var2) < thresholdvalue) {
			return true;
		}
		return false;
	}

	/**
	 * @ @author anilkumar
	 * @createdDate 19-Apr-2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description select the drop down list for that need to pass the input which
	 *              user want to select
	 */
	public void selectDropdownList(String locator, String searchInputXpath, String dropdownValue) {

		customWait(2000);
		try {
			WebElement ele = driver.findElement(By.xpath(locator));
			ele.click();
			driver.findElement(By.xpath(searchInputXpath)).sendKeys(dropdownValue);
			driver.findElement(By.xpath(searchInputXpath)).sendKeys(Keys.ENTER);
			logPassed("Entered " + dropdownValue);
		} catch (Exception e) {
			logException(e);
		}
	}

	/**
	 * @ @author anilkumar
	 * @createdDate 19-Apr-2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description select the drop down list from multi drop down list, which user
	 *              need to pass the input to select the drop down value at a time
	 *              only one value will be able to select.
	 */

	public void selectMultiDropdownList(String locator, String listXpath, String dropdownValue) {
		String actualElement = "";
		customWait(2000);
		boolean status = false;
		driver.findElement(By.xpath(locator)).click();
		try {
			List<WebElement> ele = driver.findElements(By.xpath(listXpath));
			int size = ele.size();
			for (int i = 0; i < size; i++) {
				actualElement = ele.get(i).getText().trim();
				if (actualElement.trim().equalsIgnoreCase(dropdownValue.trim())) {
					ele.get(i).click();
					status = true;
					break;
				}

				// ele = driver.findElements(By.xpath(listXpath));
			}

			if (status == true) {
				logPassed("Selected : " + dropdownValue);
			} else {
				logFailed("Doesn't available value in the dropdown, actual : " + actualElement + ", and expected : "
						+ dropdownValue);
			}

			// logPassed("Selected : " + dropdownValue);
		} catch (Exception e) {
			logException(e);
		}
	}

	/**
	 * @author kirankumar
	 * @createdDate 08-Nov-2019
	 * @modifiedBy
	 * @modifiedDate
	 * @description Rounds Off according to the requirement with the given range and
	 *              by passing either a Double or Float Variable also the required
	 *              Rounding Mode
	 */
	public BigDecimal roundingOff(Double doubleNumber, Float floatNumber, RoundingMode bigDecimal, int range) {

		if (floatNumber == null) {
			BigDecimal bd = new BigDecimal(doubleNumber);
			bd = bd.setScale(range, bigDecimal);
			return bd;
		} else if (doubleNumber == null) {
			BigDecimal bd = new BigDecimal(floatNumber);
			bd = bd.setScale(range, bigDecimal);
			return bd;
		} else {
			BaseFunctions.logInfo("Send Proper Parameters");
			return null;
		}
	}

	/**
	 *
	 * @author amarjit das
	 * @createdDate 11 june 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description click on the download and store the pdf
	 * @param
	 */
	public void getPDFContent() {
		try {
			// To switch to aws url tab (you can give your tab id)
			ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(tabs2.get(1));
			// Click on download icon on switched tab using JavascriptExecutor(get the
			// download element from javascript source code)
			WebElement element = driver.findElement(By.xpath("//cr-icon-button[@id='download']"));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0]. click();", element);
		} catch (Exception e) {
			System.out.println("File downloaded");
		}
	}

	/**
	 *
	 * @author amarjit das
	 * @createdDate 11 june 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description Returns the String data from pdf file located inside doanload
	 *              folder
	 * @param
	 */
	public List<String> getDownloadedFile() throws IOException {

		List<String> content = new ArrayList<>();
		File dir = new File(TLDriverFactory.getDownloadFolder().getAbsolutePath());
		String[] extensions = new String[] { "pdf" };
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);
			PDDocument document = PDDocument.load(file);
			// Instantiate PDFTextStripper class
			PDFTextStripper pdfStripper = new PDFTextStripper();
			content.add(pdfStripper.getText(document));
			document.close();
		}
		return content;

	}

	/**
	 *
	 * @author amarjit das
	 * @createdDate 11 june 2020
	 * @modifiedBy
	 * @modifiedDate 19 june 2020
	 * @description Returns the String data from Api request parameter as single api
	 *              can be called multi time so current method will only consider
	 *              last api call
	 * @param

	public String validateApiWithJSonArray(String response, String query) {
		System.out.println(response);
		Object obj;
		String data = null;
		try {
			JSONArray jsonArr = new JSONArray(response);
			obj = jsonArr.getJSONObject(jsonArr.length() - 1);
			data = getJsonData(obj.toString(), query).toString();
		} catch (Exception e) {
			System.out.println("Wrong json path");
		}
		return data;
	}
	*/

	/**
	 *
	 * @author amarjit das
	 * @createdDate 19 june 2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description Returns the data from api response parameter
	 * @param
	 * @example: query $.postData.Requests[0].Vehicle.CurrentMileage


	public Object getJsonData(String responsebody, String query) {
		Object obj = null;
		try {
			obj = JsonPath.read(responsebody, query);
			System.out.println(responsebody);
			System.out.println("JSON Query =" + query);
			System.out.println("Data =" + obj);
			System.out.println("Data Type =" + obj.getClass());
		} catch (Exception e) {
			System.out.println("Invalid json query " + e.getMessage());
		}
		return obj;
	}*/

	/**
	 * @ @author anilkumar
	 * @createdDate 06-July-2020
	 * @modifiedBy
	 * @modifiedDate
	 * @description handle the walkme popup
	 */

	public boolean verifyAndcloseWalkme() {
		try {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			if (driver.findElements(By.xpath(
					"//div[@class='walkme-click-and-hover walkme-custom-balloon-close-button walkme-action-close walkme-inspect-ignore']"))
					.size() > 0) {
				driver.findElement(By.xpath(
						"//div[@class='walkme-click-and-hover walkme-custom-balloon-close-button walkme-action-close walkme-inspect-ignore']"))
						.click();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logException(e);
		} finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return false;
	}

}