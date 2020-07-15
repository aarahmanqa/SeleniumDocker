package com.utilities;


import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions.VideoQuality;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.xml.XmlSuite.ParallelMode;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;


public class Mobile_TestBase extends Tek_Properties {

	public static String PATH_CA_CarGroup = "src/resources/java/Credentials/CA_CarGroup.properties";
	public static boolean finalResult = false;
	public static String getTexts = null;
	private static final String ATU_REPORTER_CONFIG="atu.reporter.config";
	protected WebDriverWait wait;
	public static String webBrowser;
	public static int beforeClassCounter = 0;
	public static int beforeTestCounter = 0;

//	ATUTestRecorder recorder;

	static {
		System.setProperty(ATU_REPORTER_CONFIG,System.getProperty("user.dir")+"/atu_mobile.properties");
	}

	public Mobile_TestBase() {
		driver = TLDriverFactory.getTLDriver();
		PageFactory.initElements(driver, this);
	}


	@BeforeSuite(alwaysRun=true)
	@Parameters({"Environment","device","appFilePath","buildBranch"})
	public synchronized void m01_readEnvProperties(String propFile,String device,String appFilePath,String buildBranch, ITestContext ctx) throws Throwable
	{
		//Configure to run in jenkins		
		if(System.getenv("Properties") != null) //Jenkins run
		{					
			mobileBuildBranch = System.getenv("Branch");
			propertiesFile = System.getenv("Properties");
			
			propFile = "src/main/resources/"+propertiesFile;	
			setProperties(propFile);
		}
		else
		{
			setProperties(propFile);			
			if(environment == null)
				environment = "";
			mobileBuildBranch = buildBranch;
		}

		environment = getProperties().getProperty("env");
		System.out.println("Chosen property file = " + propFile);
		System.out.println("Environment = " + environment);
		System.out.println("Branch = " + mobileBuildBranch);
		//Set device
		if(device == null)
			CHOSEN_DEVICE = DEVICE.REAL_DEVICE;
		if(device.equalsIgnoreCase("real"))
			CHOSEN_DEVICE = DEVICE.REAL_DEVICE;
		else
			CHOSEN_DEVICE = DEVICE.SIMULATOR;
		
		//App file path
		appPath = appFilePath;
		
		//Initialize Parameter property file:
		zdtSuiteStartTime = ZonedDateTime.now();
		webBrowser = getProperties().getProperty(BROWSER);
		getProperties().setProperty(BROWSER, TLDriverFactory.MOBILE_DEVICE);
		System.out.println(getProperties().getProperty(BROWSER));
		startReporting(ctx);
		changeThreadCount(ctx);	
	}

	public void changeThreadCount(ITestContext ctx) throws Exception
	{
		int threadCount = 0;
		if(CHOSEN_DEVICE == DEVICE.SIMULATOR)
		{
			threadCount = TLDriverFactory.platformVersions.length;
		}
		else
		{
			Set<String> devices = TLDriverFactory.getDeviceID();
			threadCount = devices.size();
			System.out.println("Connected Device(s) :");
			System.out.println(devices.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", "\n"));
			if(devices.size() == 0)
			{
				throw new  Exception("No device connected");
			}
		}

		if(threadCount > 1)
		{
			ctx.getSuite().getXmlSuite().setParallel(ParallelMode.METHODS);
			ctx.getSuite().getXmlSuite().setThreadCount(threadCount);
		}
	}	

	@BeforeTest(alwaysRun=true)
	public synchronized void callRequiredAPIs() throws Throwable
	{
		String methodName = "BeforeTest_" + ++beforeTestCounter;
		createTestFolder("Before/BeforeTest/" + methodName);

		//getBasicData();

	}

	@BeforeClass(alwaysRun=true)
	public synchronized void a0_beforeClass() throws Throwable
	{
		String methodName = "BeforeClass_" + ++beforeClassCounter;		
		createTestFolder("Before/BeforeClass/" + methodName);
	}

	@BeforeMethod(alwaysRun=true)
	public synchronized void initializWebDriver(Method method, ITestContext ctx, ITestResult result) throws Throwable
	{
		String methodName = result.getMethod().getMethodName();
		createTestFolder("Before/BeforeMethod/" + methodName);		
		beforeMethodReporting(ctx, result);
		//startRecorder();
	}

	@AfterMethod(alwaysRun=true)
	public synchronized void testTearDown(ITestResult result, Method method, ITestContext ctx) throws Throwable
	{
		afterMethodReporting(result, ctx);
		//stopRecorder();
	}

	@AfterSuite(alwaysRun=true)
	public synchronized void LogFinish( ) throws Throwable 
	{
		afterSuiteReporting();
	}

	public void startRecorder()
	{
		IOSStartScreenRecordingOptions options = new IOSStartScreenRecordingOptions();
		options.withVideoQuality(VideoQuality.HIGH);
		options.withTimeLimit(java.time.Duration.ofMinutes(10));
		getIOSDriver().startRecordingScreen(options);
	}

	public void stopRecorder() throws Exception{
		DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH-mm-ss");
		Date date = new Date();
		FileUtils.writeByteArrayToFile(new File("RecordedVideos/Record_"+dateFormat.format(date)+".mp4"), Base64.decodeBase64(getIOSDriver().stopRecordingScreen()));
	}

	public IOSDriver<IOSElement> getIOSDriver() 
	{
		return (IOSDriver<IOSElement>) TLDriverFactory.getTLDriver();
	}
}
