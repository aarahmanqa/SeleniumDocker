package com.utilities;

import com.google.common.base.Function;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.ios.IOSTouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import static io.appium.java_client.touch.offset.PointOption.point;


public class Mobile_BaseFunctions extends Mobile_TestBase {

	//Day of week starts from 1.
	public String[] dayOfWeekArray = {"","Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	public String[] monthArray = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	WebDriverWait wait;
	int WAIT_INTERVAL = 10;
	boolean presence = false;
	public static String STR_CUSTOMER_TYPE[] = {"Personal","Business"};
	public static String STR_PREFERRED_CONTACT[] = {"Call","Email","Text"};
	public static String STR_CUSTOMER_STATUS[] = {"Normal","VIP"};
	public static ZonedDateTime lastZdt = ZonedDateTime.now();
	public static SoftAssert softAssertion = new SoftAssert();

	/** This method calls Thread.sleep(2000)
	 * If custom wait time needed, call customWait(TimeInMilliSeconds);
	 */
	public void minWait()  {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Wait till the secs mentioned below
	 * @param secs
	 */
	public void customWait(long secs) {
		//CustomWait is mentioned as it is being used by startBrowser method here.
		try {
			Thread.sleep(secs);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Click the webelement using javascript.
	 * Pass the webelement object and the description about the object for reporting.
	 * @param element - webelement object that needs to be clicked.
	 * @param desc - Description about the object
	 */
	public void clickUsingJavaScript(WebElement element, String desc) {
		JavascriptExecutor executor = (JavascriptExecutor)getIOSDriver();
		executor.executeScript("arguments[0].click();", element);
	}

	/**
	 * This method uses elementToBeClickable() and returns true or false.<br/>
	 * @param element
	 * @return true in case item is available else false.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public  Boolean waitUntilElementClickable(WebElement element)
	{
		TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			if(wait == null)
				wait = new WebDriverWait(getIOSDriver(), WAIT_INTERVAL);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			return true;
		}catch (Exception e)
		{}
		finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return false;
	}

	/**
	 * This method waits until an element visible.
	 * @param element
	 * @param elementName
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public Boolean waitUntilElementVisible(WebElement element)
	{
		TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			if(wait == null)
				wait = new WebDriverWait(getIOSDriver(), WAIT_INTERVAL);
			wait.until(ExpectedConditions.visibilityOf(element));
			return true;
		}catch (Exception e)
		{}
		finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return false;
	}

	public boolean waitForFluent(WebElement element) {
		return waitForFluent(element,null);
	}

	public boolean waitForFluent(WebElement element, Integer timeout)
	{
		if(timeout == null)
			timeout = MOBILE_TIMEOUT_SECONDS;
		TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		presence = false;
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(getIOSDriver())
					.withTimeout(Duration.ofSeconds(timeout))
					.pollingEvery(Duration.ofMillis(200))
					.ignoring(NoSuchElementException.class);

			wait.until(new Function<WebDriver, Boolean>()
			{
				public Boolean apply(WebDriver driver)
				{
					try {
						element.isDisplayed();
						presence = true;
					}catch(NoSuchElementException nse)
					{
						throw nse;
					}
					catch(Exception ex)
					{}
					return presence;
				}
			});
		}catch(Exception e)
		{
		}
		finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return presence;
	}

	public WebElement waitForFluent(By by)
	{
		return waitForFluent(by,WAIT_INTERVAL);
	}

	public WebElement waitForFluent(By by, int waitInterval)
	{
		TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		WebElement element = null;
		Wait<WebDriver> wait = new FluentWait<WebDriver>(getIOSDriver())
				.withTimeout(Duration.ofSeconds(waitInterval))
				.pollingEvery(Duration.ofMillis(200))
				.ignoring(NoSuchElementException.class);

		try {
			element = wait.until(new Function<WebDriver, WebElement>()
			{
				public WebElement apply(WebDriver driver)
				{
					return getIOSDriver().findElement(by);
				}
			});
		}catch(Throwable t)
		{}
		finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return element;
	}

	public boolean waitForInvisible(WebElement element)
	{
		TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean result = false;
		try {
			result = new WebDriverWait(getIOSDriver(),WAIT_INTERVAL).until(ExpectedConditions.invisibilityOf(element));
		}catch(Exception ex)
		{}
		finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return result;
	}

	public boolean waitForInvisible(By by){
		return waitForInvisible(by,WAIT_INTERVAL);
	}

	public boolean waitForInvisible(By by, Integer timeout)
	{
		TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean result = false;
		try {
			result = new WebDriverWait(getIOSDriver(),timeout).until(ExpectedConditions.invisibilityOfElementLocated(by));
		}catch(Exception ex)
		{}
		finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return result;
	}


	/**
	 * This method waits for the list to get populated.
	 * @param webElementList list obtained from @FindAll. Because, these elements are created always.
	 * @return returns true or false
	 */
	public boolean waitForListToPopulate(List<WebElement> webElementList)
	{
		for(int i=0;i<5;i++)
		{
			try
			{
				if(webElementList.size() >  0)
					return true;
			}catch(Exception ex)
			{}
			customWait(500);
		}
		return false;
	}

	public List<WebElement> waitForListToPopulate(By webElementList, int minSize)
	{
		List<WebElement> list = new ArrayList<WebElement>();
		try {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);

			Wait<WebDriver> wait = new FluentWait<WebDriver>(getIOSDriver())
					.withTimeout(Duration.ofSeconds(10))
					.pollingEvery(Duration.ofMillis(200))
					.ignoring(NoSuchElementException.class);

			list = wait.until(new Function<WebDriver, List<WebElement>>()
			{
				public List<WebElement> apply(WebDriver driver)
				{
					List<WebElement> elements = TLDriverFactory.getTLDriver().findElements(webElementList);
					if(elements.size() >= minSize)
						return elements;
					else
						throw new NoSuchElementException("List not yet identified");
				}
			});

			return list;

		}catch (Exception e) {
			logApiLogs("Waiting for list to load failed with " + e.getMessage(),"","","");
		}
		finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return list;
	}

	/**
	 * This method will click on the element.<br/>
	 * @param element Webelement to be clicked.
	 * @param elementName Name of the button for reporting purpose.
	 * @return true in case click() executes successfully else false.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean click(WebElement element, String elementName)
	{
		//waitForFluent(element);
		//Click on the element and return true if successful and false if unsuccessful.
		boolean result = false;
		try
		{
			element.click();
			if(elementName != null)
				logInfo("Click "+ elementName);
			result = true;

		} catch (Exception e) {
			//this.test.log(LogStatus.FAIL, "Unable to click on "+ desc);
			if(elementName != null)
				logFailed("Unable to Click "+ elementName,"","",getExceptionDetails(e));
			e.getStackTrace();
		}
		return false;
	}

	public  boolean actionClick(WebElement element, String elementName) {

		boolean result = false;
		IOSElement iosElement = (IOSElement) element;
		try {
			IOSTouchAction touch = new IOSTouchAction (getIOSDriver());
			touch.tap(PointOption.point(iosElement.getCenter())).perform();
			if(elementName != null)
				logPassed("Click "+ elementName, "", elementName+" should be clicked", elementName+" is clicked");
			result = true;
		} catch (Exception e) {
			if(elementName != null)
				logFailed("Click "+ elementName, "", "", getExceptionDetails(e));
			result = false;
		}

		return result;
	}


	public  boolean jsClick(WebElement element, String elementName) {

		boolean result = false;
		try {
			customWait(1000);
			//Boolean success = waitUntilElementFound(element, elementName);
			//if(success)
			{
				JavascriptExecutor js = (JavascriptExecutor)getIOSDriver();
				//js.executeScript("arguments[0].scrollIntoView(true);", element);
				//js.executeScript("window.scroll(0,20);", "");
				//highLighterMethod(element, "");
				//unHighLighterMethod(element, "");
				js.executeScript("arguments[0].click();", element);
				logInfo("Click "+ elementName, "NA", elementName+" should be clicked", elementName+" is clicked");
				result = true;
			}
		} catch (Exception e) {
			//this.test.log(LogStatus.FAIL, "Unable to click on "+ desc);
			logFailed("Click "+ elementName, "NA", elementName+" should be clicked", getExceptionDetails(e));
			result = false;
			//e.getStackTrace();
		}

		return result;
	}


	public boolean justClick(WebElement element, String elementName)
	{
		try
		{
			element.click();
			logInfo("Click "+ elementName, "NA", elementName+" should be clicked", elementName+" is clicked");
			return true;

		} catch (Exception e) {
		}
		return false;
	}


	/**
	 * Checks whether the element is displayed using element.isDisplayed()
	 * Then, get the text of webelement
	 * @param element For which the text needs to be retrieved
	 * @param elementName The elementName just for reporting purpose.
	 * @return string that is retrieved else null
	 */
	public String getText(WebElement element, String elementName)
	{
		String text =null;
		try{
			if (!element.isDisplayed())
			{
				logFailed("The "+elementName+" is not displayed", "", "", "");
				return null;
			}
			else
			{
				text = element.getText();
				logInfo("The value retrieved from "+ elementName, text, "true", "true");
			}
		}catch(Exception e){
			logFailed("The text is not retrieved from "+elementName+" : "+text, "", "", getExceptionDetails(e));
		}
		return text;
	}

	/**
	 * This method returns the attribute.<br/>
	 * The valid cases are:<br/>
	 * 1. The element is not available, in this case null will be sent.<br/>
	 * 2. The element is available but there is no such attribute, in this case null will be sent.<br/>
	 * 3. The element is available and this attribute is present, in this case the actual value will be sent.<br/>
	 * @param element<br/>
	 * @param attribute<br/>
	 * @return String value  of the attribute
	 */
	public String getAttribute(WebElement element, String attribute)
	{
		String str = null;
		try {
			str = element.getAttribute(attribute);
			if(str!=null)
				str = str.trim();
		}catch(Exception ex)
		{
			str = null;
		}
		return str;
	}

	/**
	 * This method does the following:<br/>
	 * 1. Click on the element - eleement.click()<br/>
	 * 2. Clears the content - element.clear()<br/>
	 * 3. Enters the text in the webelement.
	 * In the logs, the textToEnter will be taken as '**********' in case the elementName is given as 'password'
	 * @param element webelement where we need to enter text.
	 * @param textToEnter String that needs to be entered.
	 * @param elementName name of the input field for reporting purpose.
	 * @return boolean to indicate successful text enter.
	 */
	public boolean enterText(WebElement element, String textToEnter, String elementName)
	{
		boolean done = false;
		//waitForFluent(element);
		if(textToEnter != null)
		{
			try {
				element.sendKeys(textToEnter);
				//((MobileElement)element).setValue(textToEnter);

				if(elementName.toLowerCase().contains("password"))
					textToEnter = "*********";
				logInfo("Enter text into "+ elementName, textToEnter);
				done = true;
			}catch (Exception e) {
				//this.test.log(LogStatus.FAIL, "Not able to enter the text '"+textToEnter+"' into "+desc);
				logFailed("Unable to enter text into "+ elementName, textToEnter,"",getExceptionDetails(e));
				e.printStackTrace();
				done = false;
			}
		}
		else
			System.out.println("'null' is passed for " + elementName);
		return done;
	}


	public boolean enterPhoneNumber(WebElement element, String phoneNumber, String elementName)
	{
		boolean done = false;
		if(phoneNumber!= null)
		{
			try
			{
				String enteredValue = null;
				int counter = 0;
				do {
					element.sendKeys(phoneNumber.substring(0, 3));
					element.sendKeys(phoneNumber.substring(3, 6));
					element.sendKeys(phoneNumber.substring(6, 9));
					element.sendKeys(phoneNumber.substring(9));
					enteredValue = getAttribute(element,"value");
					enteredValue = removeSymbolsInPhoneNumber(enteredValue);
					if(phoneNumber.equals(enteredValue))
						logInfo("Enter phone number into " + elementName, phoneNumber);
					else
						clearText(element);
					if(++counter > 3) break;
				}while(phoneNumber.equals(enteredValue) == false);
			}catch(Exception e)
			{
				logFailed("Unable to enter text into "+ elementName, phoneNumber,"",getExceptionDetails(e));
			}
		}
		return done;
	}

	public void clearText(WebElement element)
	{
		try {
			element.clear();
		}catch(Exception ex)
		{}
	}

	public void clearText(WebElement element, String desc)
	{
		try {
			element.clear();
			logInfo(desc + " text is cleared");
		}catch(Exception ex)
		{}
	}

	/**
	 * This method hides the keyboard incase it is open
	 * @param key - denotes the key which needs to be clicked to hide keyboard.<br/>
	 * Sometimes, we may need to enter "Search" key to close the keyboard.
	 */
	public void hideKeyboard(String key)
	{
		try {
			if(key == null)
				getIOSDriver().hideKeyboard();
			else
				getIOSDriver().hideKeyboard(key);
			//logInfo("Keypad is hidden");
		}catch(Exception ex)
		{
			logInfo("Unable to hide keyboard");
		}
	}

	public static Object nvl(Object ifThisIsNull,String replaceThis)
	{
		return ifThisIsNull == null ? replaceThis : ifThisIsNull;
	}

	public static String nvl(String ifThisIsNull,String replaceThis)
	{
		return ifThisIsNull == null ? replaceThis : ifThisIsNull;
	}

	/**
	 * This method clears the text box by clicking on the clear button 'x' at the right hand side of the text box.
	 * @param element
	 * @param strDesc
	 */
	public void clearTextBox(WebElement element, String strDesc)
	{
		element.click();
		Point p = element.getLocation();
		Dimension d = element.getSize();
		int x = p.x + d.getWidth() - 15;
		int y = p.y + d.getHeight() - 15;
		//clickWithCoordinates(p.x+400, p.y+70, strDesc);
		clickWithCoordinates(x, y, strDesc);
	}

	/**
	 * This method validates the Alert. It takes the input data from Mobile_ChceckInDetails.mapAlert<br/>
	 * The buttonText is the one which will be clicked after the validations are done.
	 * @return returns whether the alert is present.

	public boolean validatePrompt(String buttonText)
	{
		boolean isAlertPresent = false;
		try {
			//List<WebElement> alert = createElements(By.className("XCUIElementTypeAlert"),"Alert");
			List<WebElement> alert = ((WebDriver)getIOSDriver()).findElements(By.className("XCUIElementTypeAlert"));
			if(alert.size()>0)
			{
				//Validating the alert header and description
				List<WebElement> attentionTexts = alert.get(0).findElements(By.className("XCUIElementTypeStaticText"));
				List<WebElement> attentionButtons = alert.get(0).findElements(By.className("XCUIElementTypeButton"));
				//If the keyword matches or if the keyword data is null, proceed the flow, else return false.
				//This means this is not the exact dialog which we are validating.
				String expectedKeyword = Mobile_CheckInDetails.mapAlert.get().get(ALERT_KEYS.ALERT_KEYWORD);
				String expectedTitle = Mobile_CheckInDetails.mapAlert.get().get(ALERT_KEYS.ALERT_TITLE);
				String expectedDescription = Mobile_CheckInDetails.mapAlert.get().get(ALERT_KEYS.ALERT_DESCRIPTION);
				String expectedButton1 = Mobile_CheckInDetails.mapAlert.get().get(ALERT_KEYS.ALERT_BUTTON1);
				String expectedButton2 = Mobile_CheckInDetails.mapAlert.get().get(ALERT_KEYS.ALERT_BUTTON2);

				String actualTitle = "";
				try
				{
					actualTitle = getAttribute(attentionTexts.get(0),"label");
				}catch(Exception ex)
				{}
				String actualDescription = "";
				try {
					actualDescription = getAttribute(attentionTexts.get(1),"label");
				}catch(Exception ex)
				{}
				String actualButton1 = "";
				try {
					actualButton1 = getAttribute(attentionButtons.get(0),"label");
				}catch(Exception ex)
				{}
				String actualButton2 = "";
				try {
					actualButton2 = getAttribute(attentionButtons.get(1),"label");
				}catch(Exception ex)
				{}

				if(expectedKeyword != null && actualTitle.toLowerCase().contains(expectedKeyword.toLowerCase()) == false)
				{
					return false;
				}

				if(expectedTitle != null)
					validateTextIgnoringSpecialChars(expectedKeyword + " Alert Header", "", expectedTitle, actualTitle);
				if(expectedDescription != null)
					validateTextIgnoringSpecialChars(expectedKeyword + " Alert Description", "", expectedDescription, actualDescription);

				//Validating whether Yes, No buttons are present.
				if(expectedButton1 != null)
					validateTextIgnoringSpecialChars(expectedKeyword + " Button1", "", expectedButton1, actualButton1);
				if(expectedButton2 != null)
					validateTextIgnoringSpecialChars(expectedKeyword + " Button2", "", expectedButton2, actualButton2);

				for(int i=0;i<attentionButtons.size();i++)
				{
					String label = attentionButtons.get(i).getAttribute("label");
					if(label.equalsIgnoreCase(buttonText))
					{
						isAlertPresent = true;
						clickWithCoordinates(attentionButtons.get(i),buttonText + " button");
						break;
					}

				}
			}
			else
				logInfo(Mobile_CheckInDetails.mapAlert.get().get(ALERT_KEYS.ALERT_KEYWORD) + " Alert is not available");
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return isAlertPresent;
	}*/

	/**
	 * This method validates whether the phone number has 000 in the front and it has exactly 10 digits.
	 * @param phoneNumber
	 * @return
	 */
	public String formatPhoneNumber(String phoneNumber)
	{
		phoneNumber = removeSymbolsInPhoneNumber(phoneNumber);
		while(phoneNumber.length()<10)
			phoneNumber = "0"+phoneNumber;
		if(phoneNumber.length()>10)
			phoneNumber = phoneNumber.substring(0, 10);
		if(phoneNumber.length()==10)
		{
			String sub = phoneNumber.substring(0, 3);
			if(sub.equals("000"))
				return phoneNumber;
			else
				return "000"+phoneNumber.substring(0,7);
		}
		return phoneNumber;
	}

	/**
	 * This method removes the -, (, ) symbols that are added in the phone numbers.
	 * @param phoneNumber
	 * @return
	 */
	public String removeSymbolsInPhoneNumber(String phoneNumber)
	{
		try {
			phoneNumber = getNumbers(phoneNumber);
		}catch(Exception ex)
		{}
		return phoneNumber;
	}

	/**
	 * This method will handle the alert.<br/>
	 * It also compares the alert text with expectedAlertText.<br/>
	 * If alert text is not available, pass expectedAlertText as null
	 * @param summary short description about the alert eg: Email Alert
	 * @param expectedAlertText The expected text that should be in alert.
	 */
	public boolean handleAlert(String summary,String expectedAlertText)
	{
		try {
			List<IOSElement> alert = getIOSDriver().findElements(By.className("XCUIElementTypeAlert"));
			if(alert.size() > 0)
			{
				String actualAlertText = alert.get(0).getText().trim();
				if(actualAlertText.toLowerCase().contains(summary.toLowerCase()))
				{
					if(actualAlertText.equalsIgnoreCase(expectedAlertText.trim()))
						logPassed("Validating "+summary,"",expectedAlertText,actualAlertText);
					else
						logFailed(summary + " has invalid text","",expectedAlertText,actualAlertText);
					List<IOSElement> buttons = getIOSDriver().findElements(By.className("XCUIElementTypeButton"));
					WebElement element = buttons.get(0);
					element.click();
					logInfo(summary + " Alert is handled", "OK", "", "");
					return true;
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			logFailed("Unable to handle alert - "+ summary,"","",getExceptionDetails(ex));
		}
		return false;
	}

	public boolean handleAnyAlert()
	{
		boolean alertPresent = false;
		try {
			String buttonTexts[] = {"ALLOW","OK","NOT NOW","TRUST","CANCEL"};
			WebElement element = null;
			String actualAlertText = null;
			String buttonLabel = null;

			if(getIOSDriver() == null)
				return false;
			while(true)
			{
				List<IOSElement> alert = getIOSDriver().findElements(By.className("XCUIElementTypeAlert"));
				if(alert.size() > 0)
				{
					logInfoSubHeading("Handling alert", "");
					for(int i=0;i<alert.size();i++)
					{
						alertPresent = false;
						List<IOSElement> buttons = getIOSDriver().findElements(By.className("XCUIElementTypeButton"));
						actualAlertText = alert.get(0).getText().trim();
						for(int j=0;j<buttons.size();j++)
						{
							element = buttons.get(j);
							//Sometimes, a dialog is appearing above another one. So, if any dialog is appearing before another one and if we looking for the one inside, this code, will continue the loop.
							if(element.isDisplayed() == false)
								continue;
							buttonLabel = getAttribute(element,"label");

							for(int buttonTextIndex = 0;buttonTextIndex < buttonTexts.length;buttonTextIndex++)
							{
								if(removeSpecialChars(buttonLabel).contains(removeSpecialChars(buttonTexts[buttonTextIndex]))
										&& removeSpecialChars(buttonLabel).contains(removeSpecialChars("dont allow")) == false)
								{
									alertPresent = true;
									clickWithCoordinates(element,null);
									logInfo(actualAlertText + " alert is handled",buttonLabel,"","");
									break;
								}
							}
							if(alertPresent)
								break;
						}
						//If no buttons matches, choose the first one
						if(alertPresent == false)
						{
							buttons.get(0).click();
						}
					}
					waitForInvisible(By.className("XCUIElementTypeAlert"),5);
					alert = getIOSDriver().findElements(By.className("XCUIElementTypeAlert"));
				}
				else
					break;
			}
		}catch(Throwable t)
		{
			t.printStackTrace();
			logFailed("Unable to handle alert","","",getExceptionDetails(t));
		}
		return alertPresent;
	}

	/**
	 * If element is visible, e.isDisplaye() itself will return true.<br/>
	 * If element is not visible, e.isDisplayed() will return false, since we just want to know the element existance, we are making it as true.<br/>
	 * If element is not available, e.Displayed() will throw exception.<br/>
	 * This method won't do any reporting.<br/>
	 * @param e webelement
	 * @return boolean true in case the element is available(both visible and invisible).
	 */
	public boolean isElementExist(WebElement e) {
		boolean isPresent = false;
		try
		{
			e.isDisplayed();
			isPresent = true;
		} catch (Exception s) {
			isPresent = false;
		}
		return isPresent;
	}

	/**
	 * It creates the WebElement with the by varialbe sent as parameter.<br/>
	 * It uses ExpectedConditions.elementToBeClickable(by)
	 * @param by
	 * @return WebElement if not element exists, null will be passed.
	 */
	public WebElement createElement(By by, String desc)
	{
		WebElement element = null;
		try {
			wait = new WebDriverWait(getIOSDriver(), 5);
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			if(desc != null)
				logInfo(desc + " is identified");
		}catch (Exception e)
		{
			if(desc != null)
				logWarning(desc + " is not identified","","",getExceptionDetails(e));
		}
		return element;
	}

	public WebElement createElementWithoutWait(By by, String desc)
	{
		WebElement element = null;
		try {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			element = TLDriverFactory.getTLDriver().findElement(by);
			if(desc != null)
				logInfo(desc + " is identified");
		}catch (Exception e)
		{
			if(desc != null)
				logWarning(desc + " is not identified","","",getExceptionDetails(e));
		}
		finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return element;
	}

	public WebElement createElement(By by)
	{
		WebElement element = null;
		try {
			element = getIOSDriver().findElement(by);
		}catch (Exception e)
		{
		}
		return element;
	}

	/**
	 * It creates the List<WebElement> with the by variable sent as parameter.<br/>
	 * @param by
	 * @return List<WebElement>
	 */
	public List<WebElement> createElements(By by, String desc)
	{
		List<WebElement> weList = waitForListToPopulate(by, 1);
		if(desc != null) {
			logInfo(desc + " is identified");
		}
		else {
			logInfo(desc + " is not identified");
		}
		return weList;
	}

	public List<WebElement> createElements(By by)
	{
		return waitForListToPopulate(by, 1);
	}

	public List<WebElement> createElementsWithoutWait(By by) {
		try
		{
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			return ((WebDriver)getIOSDriver()).findElements(by);
		}catch(Exception e)
		{}
		finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return new ArrayList<WebElement>();
	}

	public void highLighterMethod(WebDriver driver, WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", element);
	}

	/**
	 * Getting element with dynamic xpath.
	 * @param xpathValue
	 * @param replaceValues
	 * @return
	 */
	public WebElement getElementWithDynamicXpath(String xpathValue, String... replaceValues) {

		int i = 1;
		for (String replaceValue : replaceValues) {
			xpathValue = xpathValue.replace("x" + i, replaceValue);
			i++;
		}
		List<IOSElement> list = getIOSDriver().findElements(By.xpath(xpathValue));
		if (list.size() == 0)
			return null;
		return list.get(0);
	}

	public String getLocalHostName() throws Exception {
		InetAddress inetAddress = InetAddress.getLocalHost();
		String localHostName = inetAddress.getHostName();
		return localHostName;
	}

	public void refreshThePage() throws InterruptedException {
		minWait();
		getIOSDriver().navigate().refresh();
		customWait(3000);
	}

	/**
	 * Find an element in the list of WebElements.
	 * @param e List<WebElement>
	 * @param model_name String test data
	 * @return
	 */
	public boolean findAndClickOnElementInList(List<WebElement> e, String model_name) {
		int i = 0;
		if (e == null) {
			return false;
		} else {
			for (WebElement element : e) {
				String modelName = element.getText();
				if (modelName.contains(model_name)) {
					click(element,model_name +"in list");
					break;
				} else {
					i++;
				}
			}
		}
		return true;

	}

	/**
	 * The below method click on the coordinates mentioned.<br/>
	 * if the desc is null, the reporting won't be done.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean clickWithCoordinates(int x, int y, String desc)
	{
		try {
			TouchAction tc = new TouchAction(getIOSDriver());
			tc.tap(point(x,y)).perform();
			if(desc != null)
				logInfo("Clicked "+ desc,x+","+y);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			logFailed("Unable to Click " + desc,x+","+y);
		}
		return false;
	}

	public void clickWithCoordinates(WebElement we, String desc)
	{
		if(we != null)
		{
			Point p = we.getLocation();
			Dimension d = we.getSize();
			int x = p.x + d.width/2;
			int y = p.y + d.height/2;
			clickWithCoordinates(x, y, desc);
		}
		else
			logFailed("Unable to click as the '"+desc+"' is not available");
	}

	public void clickByPressRelease(WebElement we, String desc)
	{
		if(we!=null)
		{
			Point p = we.getLocation();
			Dimension d = we.getSize();
			int x = p.x + d.width/2;
			int y = p.y + d.height/2;
			new TouchAction(getIOSDriver()).press(PointOption.point(x, y))
			.waitAction(new WaitOptions().withDuration(Duration.ofMillis(1000)))
			.release().perform();
			if(desc!=null)
				logInfo(desc + " is clicked");
		}
	}


	public String getPageSource() {
		String pageSource = getIOSDriver().getPageSource();
		return pageSource;
	}

	public String longPressAndSelectAll(WebElement element)
	{
		String text = null;
		Point p = element.getLocation();
		Dimension d = element.getSize();
		int x = p.x + d.width/2;
		int y = p.y + d.height - 10;
		TouchAction ta = new TouchAction(getIOSDriver());
		ta.press(PointOption.point(x,y)).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2))).perform();
		//ta.longPress(new LongPressOptions().withDuration(Duration.ofSeconds(5)).withPosition(PointOption.point(x,y))).perform();

		try {
			By bySelectAll = By.xpath("//XCUIElementTypeMenuItem[@name='Select All']");
			getIOSDriver().findElement(bySelectAll).click();
			//By byCopy = By.xpath("//XCUIElementTypeMenuItem[@name='Copy']");
			By byCut = By.xpath("//XCUIElementTypeMenuItem[@name='Cut']");
			getIOSDriver().findElement(byCut).click();
		}catch(Exception ex)
		{
		}
		return text;

	}

	/**
	 * This method performs the swipe opration.
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void swipe(int startX,int startY,int endX,int endY)
	{
		new TouchAction(getIOSDriver()).press(PointOption.point(startX, startY))
		.waitAction(new WaitOptions().withDuration(Duration.ofMillis(1000)))
		.moveTo(PointOption.point(endX, endY))
		.release().perform();
	}

	public void swipeToDirection(String direction)
	{
		swipeTillElementFound(null, direction);
	}

	/**
	 * Swipe till the element found
	 * @param we if not available, pass null. In this case, one swipe will be done.
	 * @param direction values can be <br/>
	 * "u" - UP : To scroll down, use this<br/>
	 * "d" - DOWN<br/>
	 * "r" - RIGHT<br/>
	 * "l" - LEFT<br/>
	 */
	public void swipeTillElementFound(WebElement we, String direction)
	{
		int counter = 0;
		Dimension size = getIOSDriver().manage().window().getSize();
		int endPoint = (int) (size.height * 0.30);
		int startPoint = (int) (size.height * 0.70);
		int anchor = size.width/2;
		int endPoint2 = (int) (size.width*0.20);
		int startPoint2 = (int) (size.width*0.80);
		int anchor2 = size.height/2;

		if(we!=null)
		{
			if(direction.equals("r") || direction.equals("l"))
			{
				while(we.isDisplayed()==false)
				{
					swipe(direction,anchor,startPoint,endPoint,anchor2,startPoint2,endPoint2);
					counter++;
					if(counter == 10)
						break;
				}
			}
			else
				swipeToCenter(we);
		}
		else //webelement is not available
			swipe(direction,anchor,startPoint,endPoint,anchor2,startPoint2,endPoint2);
	}

	public void swipe(String direction,int anchor,int startPoint,int endPoint,int anchor2,int startPoint2,int endPoint2)
	{
		if(direction.equalsIgnoreCase("u"))
			swipe(anchor,startPoint,anchor,endPoint);
		else if(direction.equalsIgnoreCase("d"))
			swipe(anchor,endPoint,anchor,startPoint);
		else if(direction.equalsIgnoreCase("l"))
			swipe(startPoint2,anchor2,endPoint2,anchor2);
		else
			swipe(endPoint2,anchor2,startPoint2,anchor2);
	}

	public void swipeToCenter(WebElement we)
	{
		if(we!=null)
		{
			swipeToCenter(null,we);
		}
	}
	/**
	 * This method moves the element to the center
	 * @param we
	 */
	public void swipeToCenter(WebElement containerElement, WebElement we)
	{
		if(we != null)
		{
			Dimension screenSize = null;
			if(containerElement != null)
				screenSize = containerElement.getSize();
			else
				screenSize = getIOSDriver().manage().window().getSize();
			int anchor = screenSize.width/2;
			Dimension elementSize= we.getSize();
			Point p = we.getLocation();
			int midHeightOfElement = p.y + elementSize.height/2;
			int midHeightOfScreen = screenSize.height/2;

			if(midHeightOfElement > midHeightOfScreen)
			{
				int diff = midHeightOfElement - midHeightOfScreen;
				int i=0;
				for(i=0;i<diff-200;i=i+200)
					swipe(anchor,midHeightOfScreen,anchor,midHeightOfScreen-200);
				if(diff-i>100)
					swipe(anchor,midHeightOfScreen,anchor,midHeightOfScreen-(diff-i));
			}
			else
			{
				int diff = midHeightOfScreen - midHeightOfElement;
				int i=0;
				for(i=0;i<diff-200;i=i+200)
					swipe(anchor,midHeightOfScreen,anchor,midHeightOfScreen+200);
				if(diff-i>100)
					swipe(anchor,midHeightOfScreen,anchor,midHeightOfScreen+(diff-i));
			}
		}
	}

	/**
	 * This method moves the element to the center
	 * @param we
	 */
	public void horinzontalSwipeToCenter(WebElement containerElement, WebElement we)
	{
		if(we != null)
		{
			Dimension screenSize = getIOSDriver().manage().window().getSize();
			Dimension containerSize = null;;
			int anchor = 0;
			if(containerElement != null) {
				containerSize = containerElement.getSize();
				Point containerPoint = containerElement.getLocation();
				anchor = containerPoint.y + containerSize.getHeight()/2;
			}
			else {
				anchor = screenSize.width/2;
			}

			Dimension elementSize= we.getSize();
			Point p = we.getLocation();
			int midWidthOfElement = p.x + elementSize.width/2;
			int midWidthOfScreen = screenSize.width/2;

			if(midWidthOfElement > midWidthOfScreen)
			{
				int diff = midWidthOfElement - midWidthOfScreen;
				int i=0;
				for(i=0;i<diff-200;i=i+200)
					swipe(midWidthOfScreen+100,anchor,midWidthOfScreen-100,anchor);
				if(diff-i>100)
					swipe(midWidthOfScreen,anchor,midWidthOfScreen-(diff-i),anchor);
			}
			else
			{
				int diff = midWidthOfScreen - midWidthOfElement;
				int i=0;
				for(i=0;i<diff-200;i=i+200)
					swipe(midWidthOfScreen-100,anchor,midWidthOfScreen+100,anchor);
				if(diff-i>100)
					swipe(midWidthOfScreen,anchor,midWidthOfScreen+(diff-i),anchor);
			}
		}
	}

	/**
	 * This method is similar to the swipeTillElementFound().
	 * The only difference is, it takes the size of the WebElement on which scroll needs to be done instead of the driver instance.
	 * @param containerElement - parent element on which scroll needs to be done.
	 * @param elementToBeFound - child element which needs to be found out. If null, one swipe will be done.
	 * @param direction the possible value are: <br/>
	 * "u" - Up - To scroll down.<br/>
	 * "d" - DOWN <br/>
	 * "r" - Right - To go to the left screen, we need to swipe right<br/>
	 * "l" - Left - To go to the right screen, we need to swipe left
	 */
	public void swipeOnElement(WebElement containerElement, WebElement elementToBeFound, String direction)
	{
		int counter = 0;
		if(containerElement!=null)
		{
			Dimension size = containerElement.getSize();
			Point p = containerElement.getLocation();
			int endPoint = (int) (size.height * 0.30);
			int startPoint = (int) (size.height * 0.70);
			int anchor = size.width/2 + p.x;
			int endPoint2 = (int) (size.width * 0.20);
			int startPoint2 = (int) (size.width*0.80);
			int anchor2 = size.height/2 + p.y;

			if(elementToBeFound == null)
				swipe(direction,anchor,startPoint,endPoint,anchor2,startPoint2,endPoint2);
			else
				while(elementToBeFound.isDisplayed()==false)
				{
					swipe(direction,anchor,startPoint,endPoint,anchor2,startPoint2,endPoint2);
					counter++;
					if(counter == 10)
						break;
				}
		}
	}

	/**
	 * In case of list getting loaded on scroll, this method scrolls until it finds the indexth WebElement.
	 * @param byList
	 * @param index
	 * @return list with size 0 = no elements match the by locator.
	 * 			null = The list is available but it do not contain the required index.
	 */
	public List<WebElement> swipeTheListToParticularIndex(By byList, int index)
	{
		WebElement element = null;
		boolean exceptionThrown = false;
		int oldSize = 0;

		List<WebElement> listElement = createElements(byList);
		if(index < 0)
			return listElement;
		if(listElement.size() == 0)
			return listElement;

		do {
			try
			{
				exceptionThrown = false;
				element = listElement.get(index);
			}catch(Exception t)
			{
				if(listElement.size() == 0)
					break;
				//Exception throws if the index gets out of bounds
				swipeToCenter(listElement.get(listElement.size()-1));
				//Only when we scroll, the elements will be loaded.
				listElement = createElements(byList);
				if(oldSize == listElement.size())
					return null;
				oldSize = listElement.size();
				exceptionThrown = true;
			}
		}while(exceptionThrown);
		swipeToCenter(element);
		return listElement;
	}

	/**
	 * To scroll down, use "d" - down.
	 * @param el Pass null if not available.
	 * @param direction
	 */
	public void scrollToDirection(WebElement el, String direction) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) getIOSDriver();
			HashMap<String, String> scrollObject = new HashMap<String, String>();
			if (direction.equals("d")) {
				scrollObject.put("direction", "down");
			} else if (direction.equals("u")) {
				scrollObject.put("direction", "up");
			} else if (direction.equals("l")) {
				scrollObject.put("direction", "left");
			} else if (direction.equals("r")) {
				scrollObject.put("direction", "right");
			}
			if(el!=null)
			{
				RemoteWebElement remoteElement = (RemoteWebElement)el;
				scrollObject.put("element", remoteElement.getId());
			}
			js.executeScript("mobile:scroll", scrollObject);
		} catch (Exception e) {

		}
	}

	/**
	 * The below method converts the string money to double.<br/>
	 * Eg:<br/>
	 * For strMoney = "$25,453,456.00", it returns 25453456.0
	 * @param strMoney
	 * @return
	 */
	public double convertMoneyToDouble(String strMoney)
	{
		double d = -1;
		try {
			strMoney = strMoney.replace(MONEY_SYMBOL, "").trim();
			strMoney = strMoney.replace(",","").trim();
			d = Double.parseDouble(format.format(Double.parseDouble(strMoney)));
		}catch(NumberFormatException ne)
		{
			//can be ignored as -1 will be sent.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return d;
	}

	public String convertDoubleToMoney(double dMoney)
	{
		String sMoney = "";
		try
		{
			sMoney = format.format(dMoney);
			sMoney = MONEY_SYMBOL + sMoney;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return sMoney;
	}

	/**
	 *
	 * @author ahamedabdulrahman
	 * @createdDate 19 Nov 2019
	 * @modifiedBy
	 * @modifiedDate
	 * @description convertDoubleToMoney
	 * @param currency - valid values are USD, INR
	 */
	public String convertDoubleToMoney(double dMoney,String currency)
	{
		String sMoney = "";
		try
		{
			sMoney = format.format(dMoney);
			sMoney = getCurrencySymbol(currency) + sMoney;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return sMoney;
	}

	/**
	 * This method generates the random number when we pass on the number of digits <br/>
	 * To generate 5 digit random number, pass on 5 here.
	 * @param digits
	 * @return
	 */
	public int randomNumberGenerator(int digits)
	{
		Random random = new Random();
		int Result = random.nextInt(upperBound(digits)-lowerBound(digits)) + lowerBound(digits);
		return Result;

	}

	/**
	 * Used by the randomNumberGenerator for getting upperBound.
	 * @param digits
	 * @return
	 */
	public int upperBound(int digits)
	{
		String s = "1";
		for(int i = 1;i<digits+1;i++)
			s+="0";
		return Integer.parseInt(s);
	}

	/**
	 * Used by the randomNumberGenerator for getting upperBound.
	 * @param digits
	 * @return
	 */
	public int lowerBound(int digits)
	{
		String s = "1";
		for(int i = 1;i<digits;i++)
			s+="0";
		return Integer.parseInt(s);
	}

	public void changeOrientationToPortrait()
	{
		(getIOSDriver()).rotate(ScreenOrientation.PORTRAIT);
	}

	/**
	 * This method picks a VIN from excel
	 * @return

	public static String pick_a_VIN(String path) {
		String sheetName = "Sheet1";
		String VIN = "";
		try {
			ExcelReader excelReader = new ExcelReader(path);
			int maxColNum = excelReader.getRowCount(sheetName);
			logApiLogs("pick_a_VIN maxColNum = " + maxColNum,"","","");
			int sl_num = RandomDataGenerator.getRandomNumberBetweenRange(2, maxColNum-1);
			logApiLogs("pick_a_VIN slNum chosen = " + sl_num,"","","");
			VIN= excelReader.getCellData(sheetName, 0, sl_num);
			logApiLogs("pick_a_VIN VIN chosen = " + VIN,"","","");
			excelReader.closeWorkbook();
		} catch (Exception e)
		{}
		return VIN;
	}

	/*
	public static String pick_a_VIN() throws Exception {
		return pick_a_VIN(new Random().nextBoolean());
	}
	public static String pick_a_VIN(boolean isExisting) throws Exception
	{
		String VIN = null;
		int  counter = 0;

		//Choose either existing VIN or new VIN
		do {
			if(isExisting) {
				VIN = chooseExistingVin();
			}
			else {
				VIN = chooseNewVin();
			}

			if(new NewVinDms().isPresentInInventory(VIN)) {
				logBrowserLogs(VIN + " already present in Vehicle Inventory");
				VIN = null;
			}

			if(++counter > 5)
				break;
		}while(VIN == null);

		if(VIN == null)
			throw new Exception("Unable to choose VIN");
		else {
			if(isExisting) {
				new Tek_Properties().setTestDataInHtml("Existing VIN is chosen", VIN);
			}
			else {
				new Tek_Properties().setTestDataInHtml("New VIN is chosen", VIN);
			}
		}
		return VIN;
	}

	public static String chooseExistingVin()
	{
		String VIN = null;
		try {
			if(ProductFeatureKeysAndValues.objDealerResponse == null)
				new DealerModule().getProductFeatures();
			int randomInt = new Random().nextInt(ProductFeatureKeysAndValues.objDealerResponse.getData().getMakeCode().size());
			String searchText = ProductFeatureKeysAndValues.objDealerResponse.getData().getMakeCode().get(randomInt);
			SearchCustomerResponse objSearchCustomerResponse = new SearchCustomerModule().callSearchCustomerAPI(searchText);
			List<String> vinList = new ArrayList<String>();
			for(int i=0;i<objSearchCustomerResponse.getData().size();i++)
			{
				if(nvl(objSearchCustomerResponse.getData().get(i).getVehicle().getVIN(),"").trim().equals("") == false)
					vinList.add(objSearchCustomerResponse.getData().get(i).getVehicle().getVIN());
			}

			if(vinList.size() > 0)
			{
				randomInt = new Random().nextInt(vinList.size());
				VIN = vinList.get(randomInt);
			}
			else
				VIN = chooseNewVin();
		}catch(Throwable t)
		{
			logException(t);
		}
		return VIN;
	}

	public static String chooseNewVin()
	{
		String VIN = null;
		try
		{
			VIN = new NewVIN().getnewVin();
		}catch(Throwable t)
		{
			logException(t);
		}
		return VIN;
	}

	public static String pick_a_VIN(String year, String make, String model,String path)
	{
		String sheetName = "Sheet1";
		List<String> VINList = new ArrayList<String>();
		try {
			ExcelReader excelReader = new ExcelReader(ExcelConstants.getVINExcelFilePath());
			String[][] excelData = excelReader.getAllDataFromSheet(sheetName);

			for(int i=0;i<excelData.length;i++)
			{
				int result = 0;
				String VIN = excelData[i][0];
				String thisYear = excelData[i][1];
				String thisMake = excelData[i][2];
				String thisModel = excelData[i][3];

				if(year == null)
					result += 1;
				else if(year.trim().equalsIgnoreCase(thisYear.trim()))
					result += 1;

				if(make == null)
					result += 1;
				else if(make.trim().equalsIgnoreCase(thisMake.trim()))
					result += 1;

				if(model == null)
					result += 1;
				else if(model.trim().equalsIgnoreCase(thisModel.trim()))
					result += 1;

				if(result == 3)
					VINList.add(VIN);
			}

			int randomNumber = new Random().nextInt(VINList.size());
			return VINList.get(randomNumber);
		}catch(Exception e)
		{}
		return null;
	}
*/
	/**
	 * Used for ATU reporting.
	 * @param StepDesc
	 * @param inputValue
	 * @param Expected
	 * @param Actual
	 */
	public void Reporting(String StepDesc, String inputValue, String Expected, String Actual) {

		if ((Expected.equalsIgnoreCase("true")) || (Expected.equalsIgnoreCase("false"))) {
			if (Expected.equalsIgnoreCase(Actual)) {
				// ATUReports.add(StepDesc, inputValue, Expected, Actual, LogAs.PASSED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
			} else {
				// ATUReports.add(StepDesc, inputValue, Expected, Actual, LogAs.FAILED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
			}
		}
	}

	public static void logPassed(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='green'>"+StepDesc+"</font>","","","","passed");
		// ATUReports.add("<font color='green'>"+StepDesc+"</font>", LogAs.PASSED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}
	public static void logPassed(String StepDesc, String inputValue) {
		new Tek_Properties().writeToHtml("<font color='green'>"+StepDesc+"</font>",inputValue,"","","passed");
		// ATUReports.add("<font color='green'>"+StepDesc+"</font>", inputValue, LogAs.PASSED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}
	public static void logPassed(String StepDesc, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='green'>"+StepDesc+"</font>","",Expected,Actual,"passed");
		// ATUReports.add("<font color='green'>"+StepDesc+"</font>", Expected, Actual, LogAs.PASSED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}
	public static void logPassed(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='green'>"+StepDesc+"</font>",inputValue,Expected,Actual,"passed");
		// ATUReports.add("<font color='green'>"+StepDesc+"</font>", inputValue, Expected, Actual, LogAs.PASSED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}

	public static void logFailed(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='red'>"+StepDesc+"</font>","","","","failed");
		// ATUReports.add("<font color='red'>"+StepDesc+"</font>", LogAs.FAILED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}
	public static void logFailed(String StepDesc, String inputValue) {
		new Tek_Properties().writeToHtml("<font color='red'>"+StepDesc+"</font>",inputValue,"","","failed");
		// ATUReports.add("<font color='red'>"+StepDesc+"</font>", inputValue, LogAs.FAILED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}
	public static void logFailed(String StepDesc, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='red'>"+StepDesc+"</font>","",Expected,Actual,"failed");
		// ATUReports.add("<font color='red'>"+StepDesc+"</font>", Expected, Actual, LogAs.FAILED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}
	public static void logFailed(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='red'>"+StepDesc+"</font>",inputValue,Expected,Actual,"failed");
		// ATUReports.add("<font color='red'>"+StepDesc+"</font>", inputValue, Expected, Actual, LogAs.FAILED, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}

	public static void logInfo(String StepDesc) {
		new Tek_Properties().writeToHtml(StepDesc,"","","","info");
		// ATUReports.add(StepDesc, LogAs.INFO, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}
	public static void logInfo(String StepDesc,String input) {
		new Tek_Properties().writeToHtml(StepDesc,input,"","","info");
		// ATUReports.add(StepDesc , input, LogAs.INFO, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}
	public static void logInfo(String StepDesc,String expected, String actual) {
		new Tek_Properties().writeToHtml(StepDesc,"",expected,actual,"info");
		// ATUReports.add(StepDesc, expected, actual, LogAs.INFO, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}

	public static void logInfoHeading(String StepDesc,String input) {
		new Tek_Properties().writeToHtml("<b><font color='blue'>"+StepDesc+"</font></b>" ,input,"","","info",false);
		// ATUReports.add("<b><font color='blue'>"+StepDesc+"</font></b>" , input, LogAs.INFO, null);
	}

	public static void logInfoSubHeading(String StepDesc,String input)
	{
		new Tek_Properties().writeToHtml("<b><font color='blue'>"+StepDesc+"</font></b>" ,input,"","","info",false);
		// ATUReports.add("<b><font color='blue'>"+StepDesc+"</font></b>" , input, LogAs.INFO, null);
	}

	public static void logInfoParagraph(String StepDesc,String input)
	{
		new Tek_Properties().writeToHtml("<font color='blue'>"+StepDesc+"</font>" ,input,"","","info",false);
		// ATUReports.add("<font color='blue'>"+StepDesc+"</font>" , input, LogAs.INFO, null);
	}

	public static void logInfo(String StepDesc,String input, String expected, String actual)
	{
		new Tek_Properties().writeToHtml(StepDesc ,input,"","","info");
		// ATUReports.add(StepDesc, input, expected, actual, LogAs.INFO, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}

	public static void logBrowserLogs(String StepDesc) {
		new Tek_Properties().writeToHtml("<font color='blue'>"+StepDesc+"</font>","","","","browserLogs");
	}


	public static void logPassedWithOutScreenShot(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='green'>"+StepDesc+"</font>" ,inputValue,Expected,Actual,"passed",false);
		// ATUReports.add("<font color='green'>"+StepDesc+"</font>", inputValue ,Expected, Actual, LogAs.PASSED,null);
	}

	public static void logFailedWithOutScreenShot(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml("<font color='red'>"+StepDesc+"</font>" ,inputValue,Expected,Actual,"failed",false);
		// ATUReports.add("<font color='red'>"+StepDesc+"</font>", inputValue, Expected, Actual, LogAs.FAILED, null);
	}


	public static void logInfoWithOutScreenShot(String StepDesc, String inputValue, String Expected, String Actual) {
		new Tek_Properties().writeToHtml(StepDesc ,inputValue,Expected,Actual,"info",false);
		// ATUReports.add(StepDesc, inputValue ,Expected, Actual, LogAs.PASSED,null);
	}
	public static void logInfoWithOutScreenShot(String StepDesc,String input) {
		new Tek_Properties().writeToHtml(StepDesc ,input,"","","info",false);
		// ATUReports.add(StepDesc, input, LogAs.INFO, null);
	}

	public static void logMessage(String StepDesc) {
		new Tek_Properties().writeToHtml("<b><font color='blue'>"+StepDesc+"</font></b>" ,"","","","message");
		// ATUReports.add("<b><font color='blue'>"+StepDesc+"</font></b>", LogAs.INFO, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}

	public static void logMessageWithOutScreenShot(String StepDesc) {
		new Tek_Properties().writeToHtml("<b><font color='blue'>"+StepDesc+"</font></b>" ,"","","","message",false);
		// ATUReports.add("<b><font color='blue'>"+StepDesc+"</font></b>", LogAs.INFO,null);
	}

	public static void logWarning(String StepDesc)
	{
		new Tek_Properties().writeToHtml("<font color='orange'>"+StepDesc+"</font>" ,"","","","warning");
		// ATUReports.add("<b><font color='yellow'>"+StepDesc+"</font>", LogAs.WARNING, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}
	public static void logWarning(String StepDesc, String inputValue)
	{
		new Tek_Properties().writeToHtml("<font color='orange'>"+StepDesc+"</font>" ,inputValue,"","","warning");
		// ATUReports.add("<font color='yellow'>"+StepDesc+"</font>", inputValue, LogAs.WARNING, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}
	public static void logWarning(String StepDesc, String inputValue, String Expected, String Actual)
	{
		new Tek_Properties().writeToHtml("<font color='orange'>"+StepDesc+"</font>" ,inputValue,Expected,Actual,"warning");
		// ATUReports.add("<font color='yellow'>"+StepDesc+"</font>", inputValue, Expected, Actual, LogAs.WARNING, new CaptureScreen(ScreenshotOf.BROWSER_PAGE));
	}

	public static void logApiLogs(String StepDesc,String input, String expected,String actual) {
		new Tek_Properties().writeToHtml("<font color='blue'>"+StepDesc+"</font>",input,expected,actual,"browserLogs",false);
	}


	/**
	 * The expected value would be map.get(key)<br/>
	 * The actual value would be the element.getAttribute("value");
	 * @param stepDesc a short description about the step. Used for reporting.
	 * @param map Hashmap object that has the value
	 * @param key The key in hashmap that has the expected value
	 * @param element The WebElement object that needs to be compared with
	 */
	public void validateWebElementValueWithMap(String stepDesc,HashMap<String,String> map,String key, WebElement element)
	{
		//waitUntilElementClickable(element);
		String expectedValue = map.get(key);
		String actualValue = element.getAttribute("value");
		validateTextWithCase("Comparing "+stepDesc, "", expectedValue, actualValue);
	}

	/**
	 * This method will compare the text() of the webelement with the text mentioned in the method.<br/>
	 * @param textDesc Description about the test
	 * @param inputValue The input value that is provided
	 * @param expectedValue Expected String value
	 * @param actualValue Actual String value
	 *
	 */
	public boolean validateTextWithCase(String textDesc,String inputValue,String expectedValue,String actualValue)
	{
		if(expectedValue != null && actualValue != null)
		{
			if(expectedValue.trim().equals(actualValue.trim()))
			{
				logPassed("Validating " + textDesc,inputValue, expectedValue, actualValue);
				return true;
			}
			else
			{
				logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
				return false;
			}
		}
		else if(actualValue == null && expectedValue == null)
		{
			logPassed("Validating " + textDesc,inputValue, expectedValue, actualValue);
			return true;
		}
		else
		{
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	/**
	 * This method will compare the text() of the webelement with the text mentioned in the method ignoring the case<br/>
	 * @param textDesc Description about the test
	 * @param inputValue The input value that is provided
	 * @param expectedValue Expected String value
	 * @param actualValue Actual String value
	 *
	 */
	public static Boolean validateTextIgnoringCase(String textDesc,String inputValue,String expectedValue,String actualValue)
	{
		if(expectedValue != null && actualValue != null)
		{
			if(expectedValue.trim().equalsIgnoreCase(actualValue.trim())) {
				logPassed("Validating " + textDesc + " by ignoring case",inputValue, expectedValue, actualValue);
				return true;
			}
			else {
				logFailed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return false;
			}
		}
		else if(actualValue == null && expectedValue == null) {
			logPassed("Validating " + textDesc,inputValue, expectedValue, actualValue);
			return true;
		}
		else {
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	public static Boolean validateTextContains(String textDesc,String inputValue,String expectedValue,String actualValue)
	{
		if(expectedValue != null && actualValue != null)
		{
			if(expectedValue.trim().toLowerCase().contains(actualValue.trim().toLowerCase()) || actualValue.trim().toLowerCase().contains(expectedValue.trim().toLowerCase())) {
				logPassed("Validating " + textDesc + " by ignoring case",inputValue, expectedValue, actualValue);
				return true;
			}
			else {
				logFailed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return false;
			}
		}
		else if(actualValue == null && expectedValue == null) {
			logPassed("Validating " + textDesc,inputValue, expectedValue, actualValue);
			return true;
		}
		else {
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	public boolean validateTextIgnoringSpecialChars(String textDesc,String inputValue,String expectedValue,String actualValue)
	{
		if(expectedValue != null && actualValue != null)
		{
			String eValue = expectedValue.replaceAll("[^a-zA-Z0-9]", "");
			String aValue = actualValue.replaceAll("[^a-zA-Z0-9]", "");
			if(eValue.trim().equalsIgnoreCase(aValue.trim())) {
				logPassed("Validating " + textDesc + " by ignoring case",inputValue, expectedValue, actualValue);
				return true;
			}
			else {
				logFailed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return false;
			}
		}
		else if(actualValue == null && expectedValue == null) {
			logPassed("Validating " + textDesc,inputValue, expectedValue, actualValue);
			return true;
		}
		else {
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	public Boolean validateTextIgnoringSpaces(String textDesc,String inputValue,String expectedValue,String actualValue)
	{
		if(expectedValue != null && actualValue != null)
		{
			String eValue = expectedValue.replaceAll(" ", "");
			String aValue = actualValue.replaceAll(" ", "");
			if(eValue.trim().equalsIgnoreCase(aValue.trim())) {
				logPassed("Validating " + textDesc + " by ignoring case",inputValue, expectedValue, actualValue);
				return true;
			}
			else {
				logFailed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return false;
			}
		}
		else if(actualValue == null && expectedValue == null) {
			logPassed("Validating " + textDesc,inputValue, expectedValue, actualValue);
			return true;
		}
		else {
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	public Boolean validateTextContainsIgnoringSpecialChars(String textDesc,String inputValue,String expectedValue,String actualValue)
	{
		if(expectedValue != null && actualValue != null)
		{
			String eValue = expectedValue.replaceAll("[^a-zA-Z0-9]", "");
			String aValue = actualValue.replaceAll("[^a-zA-Z0-9]", "");
			if(eValue.trim().toLowerCase().contains(aValue.trim().toLowerCase()) || aValue.trim().toLowerCase().contains(eValue.trim().toLowerCase())) {
				logPassed("Validating " + textDesc + " by ignoring case",inputValue, expectedValue, actualValue);
				return true;
			}
			else {
				logFailed("Validating " + textDesc + " by ignoring case", inputValue, expectedValue, actualValue);
				return false;
			}
		}
		else if(actualValue == null && expectedValue == null) {
			logPassed("Validating " + textDesc,inputValue, expectedValue, actualValue);
			return true;
		}
		else {
			logFailed("Validating " + textDesc, inputValue, expectedValue, actualValue);
			return false;
		}
	}

	public String removeSpecialChars(String text)
	{
		if(text != null)
			return text.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
		return text;
	}


	public boolean compareStringRemovingSpecialChars(String st1,String str2)
	{
		if(st1!=null && str2!=null)
		{
			String serviceNameInCartTemp = st1.replaceAll("[^a-zA-Z0-9]", "");
			serviceNameInCartTemp = serviceNameInCartTemp.replace("\n", "");
			String serviceNameInJobDetailsTemp = str2.replaceAll("[^a-zA-Z0-9]", "");
			serviceNameInJobDetailsTemp = serviceNameInJobDetailsTemp.replace("\n", "");
			if(serviceNameInCartTemp.toLowerCase().contains(serviceNameInJobDetailsTemp.toLowerCase())
					|| serviceNameInJobDetailsTemp.toLowerCase().contains(serviceNameInCartTemp.toLowerCase()))
				return true;
			else
				return false;
		}
		return false;
	}

	public boolean compareStringRemovingSpaces(String st1,String str2)
	{
		if(st1!=null && str2!=null)
		{
			String serviceNameInCartTemp = st1.replaceAll("\\s+", "");
			serviceNameInCartTemp = serviceNameInCartTemp.replace("\n", "");
			String serviceNameInJobDetailsTemp = str2.replaceAll("\\s+", "");
			serviceNameInJobDetailsTemp = serviceNameInJobDetailsTemp.replace("\n", "");
			if(serviceNameInCartTemp.toLowerCase().contains(serviceNameInJobDetailsTemp.toLowerCase())
					|| serviceNameInJobDetailsTemp.toLowerCase().contains(serviceNameInCartTemp.toLowerCase()))
				return true;
			else
				return false;
		}
		return false;
	}

	public void waitForProgressBarToClose(){
		waitForProgressBarToClose(120);
	}

	/**
	 * This method waits for the progress bar to close.
	 * @return
	 */
	public void waitForProgressBarToClose(int timeout)
	{
		By byVar1 = By.className("XCUIElementTypeActivityIndicator");

		TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);

		/**This fluent wait waits for Progress bar to appear as sometimes it takes time for it to appear**/
		presence = false;
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(getIOSDriver())
					.withTimeout(Duration.ofSeconds(5))
					.pollingEvery(Duration.ofMillis(500))
					.ignoring(NoSuchElementException.class);

			System.out.println("Waiting for progress bar to appear");

			presence = wait.until(new Function<WebDriver, Boolean>()
			{
				public Boolean apply(WebDriver driver)
				{
					if(createElement(byVar1) != null) {
						System.out.println("Progress Bar appeared");
						return true;
					}
					return false;
				}
			});
		}catch(Exception e)
		{
		}

		/**In case the progress bar didn't even appear, there is no need to wait for them to disappear**/
		if(presence == false) {
			System.out.println("Progress bar didn't even appeared");
			return;
		}

		/**This fluent wait waits for Progress bar to disappear **/
		presence = false;
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(getIOSDriver())
					.withTimeout(Duration.ofSeconds(timeout))
					.pollingEvery(Duration.ofMillis(500))
					.ignoring(NoSuchElementException.class);

			System.out.println("Waiting for progress bar to disappear");

			presence = wait.until(new Function<WebDriver, Boolean>()
			{
				public Boolean apply(WebDriver driver)
				{
					if(createElement(byVar1) == null) {
						System.out.println("Progress Bar no longer visible");
						return true;
					}
					else {
						System.out.println("Progress Bar still visible");
						return false;
					}
				}
			});
		}catch(Exception e)
		{
		}
		finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
	}

	public boolean isElementExists(WebElement element) {

		boolean isPresent = false;
		if(waitForFluent(element))
		{
			isPresent = true;
		}
		else
		{
			isPresent = false;
		}
		return isPresent;
	}

	public boolean validateElementExistance(WebElement element, String elementName)
	{
		if(isElementExist(element))
		{
			logPassed(elementName + " is found");
			return true;
		}
		else
		{
			logFailed(elementName + " is not found");
			return false;
		}
	}

	/**
	 * Returns the Calendar instance with the timezone that is sent.
	 * @param timeZone It can be:<br/>
	 * PST<br/>
	 * GST<br/>
	 * etc..
	 */
	public static Calendar getCalendarInstance(String timeZone)
	{
		Calendar calendar = null;
		try
		{
			if(timeZone!=null)
				calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		}catch(Exception ex) {
			//Ignored as null will be sent
		}
		return calendar;
	}

	/**
	 * This method gives the intermediate text.<br/>
	 * eg: cart(6) is given, this method returns 6.
	 * @param strText "cart(6)" in our example
	 * @param startChar '(' in our example
	 * @param endChar ')' in our example
	 * @return If nothing available, null will be sent.
	 */
	public String getNumbers(String strText) {
		return strText.replaceAll("[^\\d.]", "");
	}

	public String getIntermediateString(String strText, String startChar, String endChar)
	{
		try
		{
			int startIndex = strText.indexOf(startChar);
			int endIndex = strText.indexOf(endChar);
			String intermediateString = strText.substring(startIndex+1, endIndex);
			return intermediateString;
		}catch(Throwable t)
		{}
		return null;
	}

	public <T> List<T> removeDuplicatesInList(List<T> list)
	{
		List<T> updatedList = new ArrayList<T>();
		try {
			Set<T> set = new LinkedHashSet<T>();
			set.addAll(list);
			updatedList.addAll(set);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return updatedList;
	}

	public int getMonthValue(String monthInString)
	{
		for(int i=0;i<monthArray.length;i++)
		{
			if(monthArray[i].toLowerCase().contains(monthInString.toLowerCase()))
				return i;
		}
		return -1;
	}

	public <T> int getIndexOf(T [] strArray,T str)
	{
		int index = -1;
		for(int i=0;i<strArray.length;i++)
		{
			if(strArray[i].equals(str))
			{
				index = i;
				break;
			}
		}
		return index;
	}

	public String capitalize(String str)
	{
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	public static void logException(Throwable e){
		logException(e,false);
	}

	public static String logException(Throwable e, Boolean asWarning) throws SkipException {
		try {
			if(e instanceof SkipException){
				throw new SkipException(e.getMessage());
			}
			String exClassList = getExceptionClassList(e);
			String exceptionMessage = getExceptionMessage(e);
			String exceptionClassName = getExceptionClassName(e);
			if(exceptionClassName != null && exceptionClassName.equalsIgnoreCase("SkipException"))
				asWarning = true;
			if(asWarning)
				logWarning("Exception occured: " + exceptionClassName, exceptionMessage, "", exClassList);
			else
				logFailed("Exception occured: " + exceptionClassName, exceptionMessage, "", exClassList);
		} catch (Exception t) {
			logFailed("Exception occured in logging: ", "", "");
		}

		return null;
	}

	public static String getExceptionClassList(Throwable e)
	{
		String ourPackages[] = {"com.web","com.mobile","com.api","com.utilities","com.helper","com.tekion","com.jsonmapper","com.admin","com.bdc","com.datamigration","com.mobileweb"};

		List<String> exClassList = new ArrayList<String>();
		StackTraceElement[] stackTrace = e.getStackTrace();

		for (int i = stackTrace.length - 1; i >= 0; i--)
		{
			for(int j=0;j<ourPackages.length;j++)
			{
				if(stackTrace[i].getClassName().startsWith(ourPackages[j]))
				{
					exClassList.add(stackTrace[i].toString().substring(stackTrace[i].toString().lastIndexOf("(")));
					break;
				}
			}
		}

		return exClassList.toString();
	}

	public static String getExceptionClassName(Throwable e)
	{
		String exceptionClassNameWithPackage = e.getClass().getCanonicalName();
		String exceptionClassName = exceptionClassNameWithPackage.substring(exceptionClassNameWithPackage.lastIndexOf(".")+1);
		return exceptionClassName;
	}

	public static String getExceptionMessage(Throwable e)
	{
		String exceptionMessage = e.getMessage();
		if (exceptionMessage == null) {
			exceptionMessage = "Null Pointer";
		} else if (exceptionMessage.contains("no such element")) {
			exceptionMessage = exceptionMessage.substring(0, exceptionMessage.indexOf("(Session info"));
		}
		return exceptionMessage;
	}

	public static String getExceptionDetails(Throwable e)
	{
		String exceptionDetails = getExceptionMessage(e) + "<br/>" + getExceptionClassName(e) + "<br/>" +getExceptionClassList(e);
		return exceptionDetails;
	}

	/**
	 * Sometimes, list can be null or empty. In both cases, this method will return false. If not true
	 * @param list
	 * @return
	 */
	public <T> boolean isListEmpty(List<T> list)
	{
		try {
			if(list == null)
				return true;
			else if(list.size() == 0)
				return true;
			else
				return false;
		}catch(Exception ex)
		{
			return true;
		}
	}

	public String getCurrencySymbol(String currency)
	{
		try
		{
			if(currency.equalsIgnoreCase("USD"))
				return "$";
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return "$";
	}

	public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
		Set<T> keys = new HashSet<T>();
		for (Entry<T, E> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	public WebElement waitForElement(By byChildElement){
		return waitForElement(null,byChildElement);
	}

	public WebElement waitForElement(WebElement containerElement, By byChildElement){
		TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		presence = false;
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(getIOSDriver())
					.withTimeout(Duration.ofSeconds(10))
					.pollingEvery(Duration.ofMillis(200))
					.ignoring(NoSuchElementException.class);

			WebElement childElement = wait.until(new Function<WebDriver, WebElement>()
			{
				public WebElement apply(WebDriver driver)
				{
					if(containerElement != null)
						return containerElement.findElement(byChildElement);
					else
						return driver.findElement(byChildElement);
				}
			});
			return childElement;
		}catch(Exception e)
		{
		}
		finally {
			TLDriverFactory.getTLDriver().manage().timeouts().implicitlyWait(MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		return null;
	}

}
