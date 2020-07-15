package com.utilities;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.*;
import org.testng.asserts.SoftAssert;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Tek_Properties {

    protected WebDriver driver;
    public static ThreadLocal<Properties> properties = new ThreadLocal<Properties>();
    public static Properties reportProperties;
    public static String MONEY_SYMBOL = "$";
    public static String TIME_ZONE = "America/Los_Angeles";
    public static String environment;
    public static String mobileBuildBranch;
    public static String propertiesFile;
    public ZonedDateTime zdtStart = null;
    public static DecimalFormat format = new DecimalFormat("#,###,###,##0.00");
    public static String currentRunResults;
    public static String appPath = null;
    public static ThreadLocal<Integer> reportCounter = new ThreadLocal<Integer>();
    public static ThreadLocal<Integer> failedCounter = new ThreadLocal<Integer>();
    public static int maxFailedCount = 10;
    public static ThreadLocal<String> methodName = new ThreadLocal<>();

    public static boolean property_takeScreenshot;
    public static boolean property_continueExecutionAfterStepFailed;
    public static boolean property_showBrowserLog;
    public static boolean property_headlessExecution;
    public static boolean property_screenshot_on_step;
    public static boolean property_videoRecorder;
    public static boolean property_showNetworkLog;

    protected WebDriverWait wait;
    public static String currentRunResultFolder;
    public static ZonedDateTime zdtSuiteStartTime;
    public static ZonedDateTime zdtSuiteEndTime;
    public long startTime;
    public long endTime;
    public File testResultHtml;
    public File testScreenShotFolder;
    public File apiFolder;
    public File downloadFolder;
    public static HashMap<String, String> testAndRunTime = new HashMap<String, String>();
    public static HashMap<String, String> testAndStartTime = new HashMap<String, String>();
    public static HashMap<String, String> testAndEndTime = new HashMap<String, String>();
    // public static Properties properties;
    public static TreeMap<String, TestMethods> testMap = new TreeMap<String, TestMethods>();
    public static TreeMap<String, TestMethods> testMap2 = new TreeMap<>();

    public static final String LOG_PATH = "src/main/resources/log/log4j.properties";
    String url;
    public static final String BROWSER = "BROWSER";
    public static final String PRIORITY_URL = "PRIORITY_URL";
    public static final String Environment = "Environment";
    public static final String CDMS_URL = "CDMS_URL";
    public static final String CONSUMER_URL = "CONSUMER_URL";
    public static final String DMS_URL = "DMS_URL";
    //	public static final String ATU_REPORTER_CONFIG = "atu.reporter.config";

    public static final String EMPTY_STRING = "";
    static LocalDateTime previousLocalDateTime = LocalDateTime.now();

    DesiredCapabilities capabilities;
    static ITestResult result;
    static ITestNGMethod method;
    static ITestContext ctx;
    boolean isAfterSuiteCalled = false;
    static String browserName;
    static String browserVersion;
    static String phoneModel;
    static String phoneID;

    static Set<String> allTestCases = new HashSet<String>();
    static Set<String> passedTestCases = new HashSet<String>();
    static Set<String> failedTestCases = new HashSet<String>();
    static Set<String> skippedTestCases = new HashSet<String>();

    static int invCount = 0;
    static TestMethods testMethods;
    static String key;

    //	static boolean testFailure = false;

    List<String> passedMethodsWithIC = new LinkedList<>();
    List<String> failedMethodsWithIC = new LinkedList<>();
    List<String> skippedMethodsWithIC = new LinkedList<>();

    Set<String> passedTestCasesWithIC = new HashSet<String>();
    Set<String> failedTestCasesWithIC = new HashSet<String>();
    Set<String> skippedTestCasesWithIC = new HashSet<String>();


    public static Properties backupProperties;
    static ThreadLocal<ZonedDateTime> lastZdt = new ThreadLocal<ZonedDateTime>();
    public static ThreadLocal<SoftAssert> softAssertion = new ThreadLocal<SoftAssert>();
    public static ThreadLocal<TestMethods> testMethod = new ThreadLocal<TestMethods>();

    public static ThreadLocal<HashMap<String, NetworkLog>> mapApiRequests = new ThreadLocal<HashMap<String, NetworkLog>>();

    public static int MOBILE_TIMEOUT_SECONDS = 5;
    public static int TIMEOUT_SECONDS = 30;

    public Tek_Properties() {
        try {
            driver = TLDriverFactory.getTLDriver();
            if (driver == null)
                TLDriverFactory.setTLDriver(TLDriverFactory.HEADLESS_MOB);
            driver = TLDriverFactory.getTLDriver();
            PageFactory.initElements(driver, this);
        }catch(Throwable t){}
    }

    public enum DEVICE {
        REAL_DEVICE,
        SIMULATOR
    }

    public static DEVICE CHOSEN_DEVICE = DEVICE.REAL_DEVICE;

    public synchronized static void setProperties(String strFile) {
        /** Storing the properties file path in strProp so that if at all properties.get() returns null, this file would be used.
         In case of test cases other than Consumer Scheudling prod, only one property file is used.
         So this step is completely benign.
         In case of Consumer Scheduling test cases, the property file will be assigned in before class for each thread. */
        try {
            if (strFile == null) {
                return;
            }
            Properties prop = new Properties();
            FileInputStream f = new FileInputStream(strFile);
            prop.load(f);
            properties.set(prop);
            try {
                String[] split = strFile.split("/");
                environment = split[split.length - 2].toUpperCase();
                getProperties().setProperty("env", environment);
            } catch (Throwable t) {
            }
            //In case of DMS, DSE lite URL needs to be copied to DMS
            if (getProperties() != null && getProperties().getProperty("PRIORITY_URL").equals("DMS")) {
                String dseLiteUrl = getProperties().getProperty("DSE_LITE_API_URL");
                getProperties().setProperty("API_URL", dseLiteUrl);
            }
            backupProperties = prop;
            if (f != null)
                f.close();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public synchronized static void setProperties(Properties prop) {
        properties.set(prop);
    }

    public synchronized static Properties getProperties() {
        if (properties.get() == null) {
            setProperties(backupProperties);
        }
        return properties.get();
    }

    public synchronized static void incrementReportCounter() {
        Integer i = reportCounter.get();
        if (i == null)
            i = 1;
        reportCounter.set(i + 1);
    }

    public synchronized static Integer getReportCounter() {
        Integer n = reportCounter.get();
        if (n == null)
            return 1;
        else
            return n;
    }

    static {
        try {
            reportProperties = new Properties();
            FileInputStream f = new FileInputStream("framework.properties");
            reportProperties.load(f);
            property_screenshot_on_step = getBoolean(reportProperties.get("SCREENSHOT_ON_STEP"));
            property_takeScreenshot = getBoolean(reportProperties.get("tekion.reports.takescreenshot"));
            property_continueExecutionAfterStepFailed = getBoolean(
                    reportProperties.get("tekion.reports.continueExecutionAfterStepFailed"));
            property_showBrowserLog = getBoolean(reportProperties.get("tekion.reports.showBrowserLog"));
            property_videoRecorder = getBoolean(reportProperties.get("VIDEO_RECORDER"));
            property_showNetworkLog = getBoolean(reportProperties.get("tekion.reports.showNetworkLog"));
            if (f != null)
                f.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static boolean getBoolean(Object obj) {
        String s = (String) obj;
        return s.trim().equalsIgnoreCase("true");
    }

    public void startReporting(ITestContext ctx) throws Exception {

        if (softAssertion.get() == null)
            softAssertion.set(new SoftAssert());

        TestRunner runner = (TestRunner) ctx;
        runner.getSuite().addListener(new VerificationListener());

        long threadId = Thread.currentThread().getId();
        zdtSuiteStartTime = ZonedDateTime.now();
        LocalDateTime lt = LocalDateTime.now();
        String runResultFolder = lt.toString().replace(":", "_");
        currentRunResultFolder = runResultFolder.replace(".", "_");
        String cwd = System.getProperty("user.dir");
        currentRunResults = cwd + "/results/" + currentRunResultFolder;
        File resultDir = new File(currentRunResults);
        File suiteCss = new File(cwd + "/suite.css");
        File testReportCss = new File(cwd + "/test_report.css");
        File tableChangeJs = new File(cwd + "/tableChange.js");
        File collpasibleJs = new File(cwd + "/collapsible.js");
        File imgDir = new File(cwd + "/img");
        if (!resultDir.exists()) {
            resultDir.mkdir();
            FileUtils.copyFileToDirectory(testReportCss, resultDir);
            FileUtils.copyFileToDirectory(suiteCss, resultDir);
            FileUtils.copyFileToDirectory(tableChangeJs, resultDir);
            FileUtils.copyDirectory(imgDir, resultDir);
            FileUtils.copyFileToDirectory(collpasibleJs, resultDir);
        }

        //Initializing a folder to have report of before methods.
        createTestFolder("Before/BeforeSuite/Before");

        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        for (ITestNGMethod method : ctx.getAllTestMethods()) {
            if (method.getInvocationCount() > 1) {
                for (int i = 0; i < method.getInvocationCount(); i++) {
                    createTestMethods(method, zonedDateTime);
                }
            } else {
                createTestMethods(method, zonedDateTime);
            }

        }
        writeSuiteData();
    }

    public void createTestMethods(ITestNGMethod method, ZonedDateTime zonedDateTime) throws Exception {
        zonedDateTime.plusSeconds(2);
        String timestamp = DateUtil.formatZonedDateTime(zonedDateTime, "hh_mm_ss_SSS");
        String key = getKey(method) + "$" + timestamp;
        TestMethods testMethod = createTestMethods(method, timestamp);
        testMap.put(key, testMethod);
        testMap2.put(key, testMethod);
    }


    /**
     * @author paramagurusubbiah
     * @createdDate 10-Dec-2019
     * @modifiedBy
     * @modifiedDate
     * @description createTestFolder
     */
    public File createTestFolder(String key) {

        File testResultFolder = new File(currentRunResults + "/" + key);
        if (!testResultFolder.exists()) {
            testResultFolder.mkdirs();
            testResultHtml = new File(testResultFolder + "/index.html");
            String[] split = key.split("/");
            String methodName = split[split.length - 1];
            methodName = methodName.split("\\$")[0];
            //writeFileHeader(testResultHtml, TestBase.method.getMethodName());
            writeFileHeader(testResultHtml, methodName);
        }
        TLDriverFactory.setReport(testResultHtml);

        testScreenShotFolder = new File(currentRunResults + "/" + key + "/screenshots");
        if (!testScreenShotFolder.exists()) {
            testScreenShotFolder.mkdirs();
        }
        TLDriverFactory.setScreenShotFolder(testScreenShotFolder);

        apiFolder = new File(currentRunResults + "/" + key + "/API_Files");
        if (!apiFolder.exists()) {
            apiFolder.mkdirs();
        }
        TLDriverFactory.setAPIFolder(apiFolder);

        downloadFolder = new File(currentRunResults + "/" + key + "/downloads");
        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs();
        }
        TLDriverFactory.setDownloadFolder(downloadFolder);
        return testResultFolder;
    }

    public synchronized File beforeMethodReporting(ITestContext ctx, ITestResult result) throws Throwable {
        File file = null;
        long threadId = Thread.currentThread().getId();
        System.out.println("Thread: " + threadId);

        softAssertion.set(new SoftAssert());

        TestBase.result = result;
        TestBase.method = result.getMethod();
        TestBase.ctx = ctx;

        try {
            reportCounter.set(1);
            failedCounter.set(0);
            testExecutionStatus.set(false);

            String browser = TLDriverFactory.HEADLESS_MOB;
            if (getProperties() != null)
                browser = getProperties().getProperty(BROWSER);
            String key = null;
            while (key == null) {
                for (String testcase : testMap2.keySet()) {
                    if (testcase.contains(result.getMethod().getMethodName())) {
                        key = testcase;
                        break;
                    }
                }

                if (key != null)
                    break;
                else {
                    ZonedDateTime zonedDateTime = DateUtil.getDateTimeFromCurrentTimeZone();
                    zonedDateTime = zonedDateTime.plusDays(1);
                    createTestMethods(result.getMethod(), zonedDateTime);
                }
            }

            testMap2.remove(key);
            file = createTestFolder(key);

            System.out.println("Running." + key);
            TLDriverFactory.setTestcaseName(key);
            if (browser != null) {
                TLDriverFactory.setTLDriver(browser);
                driver = TLDriverFactory.getTLDriver();
                //Updating the status of test case step is kept after we launch the TLDriverFactory.getTLDriver(). If launching the driver faces issue, the below code won't run.
                TestBase.key = key;
                testMethod.set(testMap.get(key));
                testMethod.get().startTime = ZonedDateTime.now();
                testMethod.get().status = "Running";

                if (driver != null) {
                    wait = new WebDriverWait(driver, 10);
                    driver = TLDriverFactory.getTLDriver();
                    Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
                    if (browser.equalsIgnoreCase("Mobile_Device")) {
                        phoneModel = (String) caps.getCapability("deviceName");
                        phoneID = TLDriverFactory.getDeviceID().toString();
                    } else {
                        browserName = caps.getBrowserName();
                        browserVersion = caps.getVersion();
                    }
                } else {
                    //If mobile case runs, stop execution if driver initialization fails.
                    if (browser.equals("MOBILE_DEVICE")) {
                        testMethod.get().status = "Failed";
                        writeSuiteData();
                        Mobile_BaseFunctions.logFailed("Unable to launch WebDriver");
                        Assert.assertTrue(false, "Unable to launch Webdriver");
                    }
                }
            }
        } catch (AssertionError we) {
            throw we;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        writeSuiteData();
        return file;
    }


    public synchronized void afterMethodReporting(ITestResult result, ITestContext ctx)
            throws IOException, InterruptedException {

        // Taking the last instance of these variables, so that they can be called from
        // AfterSuite.
        TestBase.result = result;
        TestBase.method = result.getMethod();
        TestBase.ctx = ctx;

        // Set the end time.
        try {
            String key = TLDriverFactory.getTestcaseName();

            TestMethods testMethod = testMap.get(key);
            if (testMethod == null)
                throw new Exception("Test method is null");
            if (testExecutionStatus.get()) {
                testMethod.status = "Passed";
            } else {
                testMethod.status = "Failed";
            }
            testMethod.dealerName = getProperties().getProperty("dealer");
            setTestResultInReport();
            testMethod.endTime = ZonedDateTime.now();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TLDriverFactory.getTLDriver() != null)
            TLDriverFactory.getTLDriver().quit();

        writeSuiteData();
    }

    public void afterSuiteReporting() throws IOException, InterruptedException {
        zdtSuiteEndTime = ZonedDateTime.now();
        isAfterSuiteCalled = true;
        //afterMethodReporting(result, ctx);
        writeSuiteData();

        /*if (TLDriverFactory.serverList.size() > 0) {
            for (AppiumDriverLocalService appiumServer : TLDriverFactory.serverList) {
                appiumServer.stop();
            }
        }*/
        try {
            File resultDir = new File(currentRunResults);
            String cwd = System.getProperty("user.dir");
            String recentRunDirName = cwd + "/results/recentrun";
            File recentRunDir = new File(recentRunDirName);
            if (!recentRunDir.exists()) {
                recentRunDir.mkdir();
            }
            BaseFunctions.delete(recentRunDir);
            String recentRunResults = cwd + "/results/recentrun/results";
            File recentResults = new File(recentRunResults);
            FileUtils.copyDirectory(resultDir, recentResults);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // extent.flush();
    }

    public void writeFileHeader(File testResultHtml, String testName) {
        try {
            //	testName = testName.substring(testName.lastIndexOf("/") + 1);
            FileWriter fileWriter = new FileWriter(testResultHtml.getAbsolutePath(), false);
            fileWriter.write("<html>");
            fileWriter.write("<head>");

            // fileWriter.write("<meta http-equiv=\"refresh\" content=\"5\">");
            fileWriter.append("<script type=\"text/javascript\" src=\"../../../tableChange.js\"></script>");
            fileWriter.append("<script type=\"text/javascript\" src=\"../../../collapsible.js\"></script>");
            fileWriter.append("<link rel=stylesheet type=text/css href=../../../test_report.css>");
            fileWriter.append("</head>");
            // fileWriter.append("<body style=\"font-family:'Verdana'\">");
            fileWriter.write("<title>" + testName + "</title>");
            fileWriter.write("<h1><center> " + testName + " </center></h1>");
            fileWriter.write("<h3>Test description : </h3>");
            fileWriter.write("<div id='testDescription'>");
            fileWriter.write("<table id='testDescription'>");
            fileWriter.write("</table id='testDescription'>");
            fileWriter.write("</div id='testDescription'>");
            fileWriter.write("<h3>Pre-requisite/Test Data:</h3>");
            fileWriter.write("<table id='testData'></table id='testData'>");
            fileWriter.write("<br>");

            setApiDetails(fileWriter);

            fileWriter.write("<h3>Test Result Summary:</h3>");
            fileWriter.write("<div id='resultData'></div id='resultData'>");
            fileWriter.write("<br>");
            fileWriter.write("<h3>Test Steps:</h3>");
            fileWriter.write("<input onClick=allButtonClick() type=checkbox checked id=AllCB />All (exluding Browser Logs) &emsp; <input onClick=buttonClick() type=checkbox id=InfoCB />Info &emsp; <input type=checkbox onClick=buttonClick() id=PassedCB />Passed &emsp; <input type=checkbox onClick=buttonClick() id=FailedCB />Failed &emsp; <input type=checkbox onClick=buttonClick() id=WarningCB />Warning &emsp; <input type=checkbox onClick=buttonClick() id=MessageCB />Message &emsp; <input type=checkbox onClick=browserLogButtonClick() id=BrowserLogsCB />Browser Logs &emsp;<br/><br/> ");
            fileWriter.write("<table id='each_test_report' style=\"text-align:left;\" style=width:100%><center>");
            fileWriter.write("<tr id=header><th><center>Sl.No.<center></th>");
            fileWriter.write("<th><center>Step Description<center></th>");
            fileWriter.write("<th><center>Input Value<center></th>");
            fileWriter.write("<th><center>Expected Result</center></th>");
            fileWriter.write("<th><center>Actual Result</center></th>");
            fileWriter.write("<th><center>Status</center></th>");
            fileWriter.write("<th><center>Duration</center></th>");
            fileWriter.write("<th><center>Screenshot</center></th></tr>");
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param fileWriter
     * @author paramagurusubbiah
     * @createdDate 18-Oct-2019
     * @modifiedBy
     * @modifiedDate
     * @description setApiDetails
     */
    private void setApiDetails(FileWriter fileWriter) {

        try {
            fileWriter.write(
                    "<style>" +
                            ".collapsible{font-size: 15px; border-radius: 5px;font-weight:bold; font-family: Times New Roman;background-color: #a5c5d5;}"
                            + ".active, .collapsible:hover{background-color: #95c5d5;}"
                            + "#divMsg {padding: 18px; display: none; overflow: hidden; background-color: #f1f1f1;}" +
                            "</style>" +
                            "<button onClick=\"showHideDiv('divMsg')\" type=\"button\" class=\"collapsible\" id=\"collapsible\" >API Details:</button>" +
                            "<div id=\"divMsg\" style=\"display:none;\">" +
                            "<div id='apiData'></div id='apiData'>" +
                            "</div>");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeSuiteData() {
        File suiteHtml = new File(currentRunResults + "/suite.html");
        FileWriter fileWriter = null;

        allTestCases.add(TestBase.key);

        int testsCount = 0;

        int passedTestsCount = 0;
        int failedTestsCount = 0;
        int skippedTestsCount = 0;

        testsCount = testMap.keySet().size();
        for (String key : testMap.keySet()) {
            if (testMap.get(key).status.equalsIgnoreCase("passed"))
                passedTestsCount++;
            else if (testMap.get(key).status.equalsIgnoreCase("failed"))
                failedTestsCount++;
            else if (testMap.get(key).status.equalsIgnoreCase("skipped"))
                skippedTestsCount++;
        }


        int notStartedTestsCount = testsCount - (passedTestsCount + failedTestsCount + skippedTestsCount);
        try {
            fileWriter = new FileWriter(suiteHtml.getAbsolutePath(), false);
            fileWriter.append("<html>");
            fileWriter.append("<head>");
            fileWriter.append("<link rel=stylesheet type=text/css href=suite.css>");
            fileWriter.append("<script type=\"text/javascript\" src=\"tableChange.js\"></script>");
            fileWriter.append("</head>");
            fileWriter.append(showLogo("left"));
            fileWriter.append("<br>");
            fileWriter.append("<br>");
            fileWriter.append("<h1 style= \"text-align:center; color:teal;\"> Tekion Automation Report </h1>");
            fileWriter.append("<br>");
            fileWriter.append("<br>");

            appendExecutionDetailsAsLine(fileWriter);

            //	pieChart(fileWriter, testsCount, passedTestsCount, failedTestsCount, notStartedTestsCount);

            fileWriter.append("<table id=test_count>");
            fileWriter.append("<tr><center><th>No Of TC's</th>");
            fileWriter.append("<th>Not Started</th>");
            fileWriter.append("<th>Passed</th>");
            fileWriter.append("<th>Failed</th>");
            fileWriter.append("<th>Skipped</th>");
            fileWriter.append("<th style=\"text-align:center;\">Start Time</th>");
            fileWriter.append("<th style=\"text-align:center;\">End Time</th>");
            fileWriter.append("<th style=\"text-align:center;\">Duration</th></center></tr>");

            fileWriter.append("<tr><td style=\"text-align:center;\">" + testsCount + "</td>");
            fileWriter.append("<td style=\"text-align:center;\">" + notStartedTestsCount + "</td>");
            fileWriter.append("<td style=\"text-align:center;\">" + passedTestsCount + "</td>");
            fileWriter.append("<td style=\"text-align:center;\">" + failedTestsCount + "</td>");
            fileWriter.append("<td style=\"text-align:center;\">" + skippedTestsCount + "</td>");

            if (isAfterSuiteCalled) {
                String suiteDuration = calculateDuration(zdtSuiteStartTime, zdtSuiteEndTime);
                String startTime = DateUtil.formatZonedDateTime(zdtSuiteStartTime, "MMM dd,yyyy HH:mm");
                String endTime = DateUtil.formatZonedDateTime(zdtSuiteEndTime, "MMM dd,yyyy HH:mm");
                fileWriter.append("<td style=\"text-align:center;\">" + startTime + "</td>");
                fileWriter.append("<td style=\"text-align:center;\">" + endTime + "</td>");
                fileWriter.append("<td style=\"text-align:center;\">" + suiteDuration + "</td></tr></table>");
            } else {
                ZonedDateTime currentZdt = ZonedDateTime.now();
                String suiteDuration = calculateDuration(zdtSuiteStartTime, currentZdt);
                String startTime = DateUtil.formatZonedDateTime(zdtSuiteStartTime, "MMM dd,yyyy HH:mm");
                fileWriter.append("<td style=\"text-align:center;\">" + startTime + "</td>");
                fileWriter.append("<td style=\"text-align:center;\">NA</td>");
                fileWriter.append("<td style=\"text-align:center;\">" + suiteDuration + "</td></tr></table>");
            }


            fileWriter.append("<b style=color:black;> Environment:  </b>");
            fileWriter.append("<b style=color:teal;> " + environment.toUpperCase() + " </b>");
            fileWriter.append("<br>");
            fileWriter.append("<br>");

            fileWriter.append("<div id=xmlTestNames>");
            TreeSet<String> dealerNames = new TreeSet<String>();
            for (TestMethods testMethods : testMap.values()) {
                if (testMethods.dealerName != null)
                    dealerNames.add(testMethods.dealerName);
            }
            fileWriter.append("<input type=checkbox onClick=xmlTestNameClick() checked id=All />All &emsp;");
            for (String dealerName : dealerNames)
                fileWriter.append("<input type=checkbox onClick=xmlTestNameClick() id=" + dealerName + " />"
                        + dealerName + " &emsp;");
            fileWriter.append("</div id=xmlTestNames>");

            fileWriter.append("<table id=test_status align=left>");
            fileWriter.append("<tr><center><th>Sno</th>");
            fileWriter.append("<th style=\"text-align:center;\">Dealer</th>");
            fileWriter.append("<th style=\"text-align:center;\">Test Name</th>");
            fileWriter.append("<th style=\"text-align:center;\">Test Case</th>");
            fileWriter.append("<th style=\"text-align:center;\">Start Time</th>");
            fileWriter.append("<th style=\"text-align:center;\">End Time</th>");
            fileWriter.append("<th style=\"text-align:center;\">Run Time</th>");
            fileWriter.append("<th style=\"text-align:center;\">Status</th></center></tr>");

            //			fileWriter.append("<div style=\"position:fixed;bottom:0;right:0;left:0;width:100%;text-align:center;font-size:12px;"
            //					+ "border-top:1px solid #ccc; margin:25px; padding-top:15px; width:calc(100% - 50px);"
            //					+ "\">Designed By Tekion Automation Team</div>");

            int index = 0;
            TreeSet<String> allKeys = new TreeSet<String>(testMap.keySet());
            for (String key : allKeys) {
                TestMethods testMethods = testMap.get(key);
                String time = calculateDuration(testMethods.startTime, testMethods.endTime);
                String statusWithFont = testMethods.status;
                if (testMethods.status.equalsIgnoreCase("Passed"))
                    statusWithFont = "<font color=green>Passed</font>";
                else if (testMethods.status.equalsIgnoreCase("Failed"))
                    statusWithFont = "<font color=red>Failed</font>";
                else if (testMethods.status.equalsIgnoreCase("Running"))
                    statusWithFont = "<font color=blue>Running</font>";
                else if (testMethods.status.equalsIgnoreCase("Skipped"))
                    statusWithFont = "<font color=orange>Skipped</font>";
                fileWriter.append("<tr><td>" + (++index) + "</td>" + "<td>" + testMethods.dealerName + "</td>" + "<td>"
                        + testMethods.xmlTestName + "</td>" + "<td>" + testMethods.testMethodName + "</td>" + "<td>"
                        + testMethods.getStartTime() + "</td>" + "<td>" + testMethods.getEndTime() + "</td>" + "<td><center>"
                        + time + "</center></td>" + "<td><a href=" + testMethods.getKey() + "/index.html>" + statusWithFont
                        + "</a></td></tr>");
            }

            fileWriter.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private CharSequence showLogo(String position) {
        return "<img src=Tekion.gif style=float:" + position + ";>";
    }

    private void pieChart(FileWriter fw, int testsCount, int passedTestsCount, int failedTestsCount, int notStartedTestsCount) {
        try {

            fw.append("<h1></h1>\n" +
                    "\n" +
                    "<div id=\"piechart\" style=\"position:absolute;top:120px;right:40;\"></div>\n" +
                    "\n" +
                    "<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                    "\n" +
                    "<script type=\"text/javascript\">\n" +
                    "// Load google charts\n" +
                    "google.charts.load('current', {'packages':['corechart']});\n" +
                    "google.charts.setOnLoadCallback(drawChart);\n" +
                    "\n" +
                    "// Draw the chart and set the chart values\n" +
                    "function drawChart() {\n" +
                    "  var data = google.visualization.arrayToDataTable([\n" +
                    "  ['Task', 'Hours per Day'],\n" +
                    "  ['Not Started', " + notStartedTestsCount + "],\n" + //blue
                    "  ['Failed', " + failedTestsCount + "],\n" + //red
                    "  ['Running', " + (testsCount - (passedTestsCount + failedTestsCount + notStartedTestsCount)) + "],\n" + //orange
                    "  ['Passed', " + passedTestsCount + "],\n" + //green
                    "  ['', ],\n" + //purple
                    "]);\n" +
                    "\n" +
                    "  // Optional; add a title and set the width and height of the chart\n" +
                    "  var options = {'title':'', 'width':550, 'height':400};\n" +
                    "\n" +
                    "  // Display the chart inside the <div> element with id=\"piechart\"\n" +
                    "  var chart = new google.visualization.PieChart(document.getElementById('piechart'));\n" +
                    "  chart.draw(data, options);\n" +
                    "}\n" +
                    "</script>");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void appendExecutionDetailsAsLine(FileWriter fileWriter) {
        try {

            if (getProperties() == null)
                return;

            if (getProperties().getProperty(BROWSER).equalsIgnoreCase("Headless_Mob")) {

                fileWriter.append("<b style=color:teal;> Scenario Type: </b>");
                fileWriter.append("<b style=color:black;> API Execution | </b>");

                if (ctx.getSuite().getParallel().equalsIgnoreCase("methods")) {
                    fileWriter.append("<b style=color:teal;> Execution Mode: </b>");
                    fileWriter.append("<b style=color:black;> Parallel</b>");
                } else {
                    fileWriter.append("<b style=color:teal;> Execution Mode: </b>");
                    fileWriter.append("<b style=color:black;> Normal </b>");
                }

                fileWriter.append("</br>");
                fileWriter.append("</br>");

            } else {
                if (getProperties().getProperty(BROWSER).equalsIgnoreCase("Mobile_Device")) {
                    fileWriter.append("<b style=color:teal;>" + "Phone Model: </b>");
                    fileWriter.append("<b style=black;>" + phoneModel + " | </b>");
                    fileWriter.append("<b style=color:teal;>" + "Phone UDID: </b>");
                    fileWriter.append("<b style=black;>" + phoneID + " | </b>");
                } else {
                    fileWriter.append("<b style=color:teal;>" + "Browser Name: </b>");
                    fileWriter.append("<b style=color:black;>" + browserName+ " | </b>");
                    fileWriter.append("<b style=color:teal;> Browser Version: </b>");
                    fileWriter.append("<b style=color:black;>" + browserVersion + " | </b>");
                }

                if (getProperties().getProperty(BROWSER).equalsIgnoreCase("Headless_Mob")) {
                    fileWriter.append("<b style=color:teal;> Browser Mode: </b>");
                    fileWriter.append("<b style=color:black;> Headless | </b>");
                } else {
                    fileWriter.append("<b style=color:teal;> Browser Mode: </b>");
                    fileWriter.append("<b style=color:black;> Normal |  </b>");
                }

                if (ctx != null) {
                    if (ctx.getSuite().getParallel().equalsIgnoreCase("methods")) {
                        fileWriter.append("<b style=color:teal;> Execution Mode: </b>");
                        fileWriter.append("<b style=color:black;> Parallel | </b>");
                    } else {
                        fileWriter.append("<b style=color:teal;> Execution Mode: </b>");
                        fileWriter.append("<b style=color:black;> Normal | </b>");
                    }
                } else {
                    //	System.out.println("No methods are enabled for execution");
                }

                fileWriter.append("</br>");
                fileWriter.append("</br>");

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void appendExecutionDetailsAsTable(FileWriter fileWriter) {
        try {
            fileWriter.append("<table id=test_count>");

            if (getProperties().getProperty(BROWSER).equalsIgnoreCase("Mobile_Device")) {
                fileWriter.append("<tr><td>" + "Phone Model" + "</td>");
                fileWriter.append("<td>" + "Phone UDID" + "</td>");
            } else {
                fileWriter.append("<tr><td>" + "Browser Name" + "</td>");
                fileWriter.append("<td>" + "Browser Version" + "</td>");
            }

            fileWriter.append("<th>Browser Mode</th>");
            fileWriter.append("<th>Execution Mode</th></tr>");

            if (getProperties().getProperty(BROWSER).equalsIgnoreCase("Mobile_Device")) {
                fileWriter.append("<tr><td>" + phoneModel + "</td>");
                fileWriter.append("<td>" + phoneID + "</td>");
            } else {
                fileWriter.append("<tr><td>" + browserName + "</td>");
                fileWriter.append("<td>" + browserVersion + "</td>");
            }

            if (property_headlessExecution)
                fileWriter.append("<td> Headless </td>");
            else
                fileWriter.append("<td> Normal </td>");

            if (ctx.getSuite().getParallel().equalsIgnoreCase("methods"))
                fileWriter.append("<td> Parallel </td>");
            else
                fileWriter.append("<td> Normal </td>");

            fileWriter.append("</tr></table>");

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public String createTestMethodsName(ITestNGMethod testngMethod) {
        try {
            return testngMethod.getXmlTest().getName().replace(" ", "_") + "/"
                    + testngMethod.getTestClass().getName()
                    .substring(testngMethod.getTestClass().getName().lastIndexOf(".") + 1)
                    + "/" + testngMethod.getMethodName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public TestMethods createTestMethods(ITestNGMethod testngMethod, String timestamp) {
        try {
            testMethods = new TestMethods();
            String xmlTestName = testngMethod.getXmlTest().getName();
            testMethods.xmlTestName = xmlTestName.replace(" ", "_");
            String className = testngMethod.getTestClass().getName();
            className = className.substring(className.lastIndexOf(".") + 1);
            testMethods.className = className;
            String testMethodName = testngMethod.getMethodName();
            testMethods.testMethodName = testMethodName;
            testMethods.testMethodNameWithTimestamp = testMethodName + "$" + timestamp;
            if (getProperties() != null) {
                testMethods.dealerName = getProperties().getProperty("dealer");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return testMethods;
    }

    public String getKey(ITestNGMethod testngMethod) {
        String xmlTestName = testngMethod.getXmlTest().getName().replace(" ", "_");
        String className = testngMethod.getTestClass().getName();
        className = className.substring(className.lastIndexOf(".") + 1);
        String testMethodName = testngMethod.getMethodName();
        String key = xmlTestName + "/" + className + "/" + testMethodName;
        int count = testngMethod.getCurrentInvocationCount();
        return key;
    }

    public static String calculateDuration(ZonedDateTime zdtTime1, ZonedDateTime zdtTime2) {
        if (zdtTime1 == null || zdtTime2 == null)
            return "NA";
        String suiteDuration = "";
        try {
            long hours = ChronoUnit.HOURS.between(zdtTime1, zdtTime2);
            if (hours > 0)
                suiteDuration += hours + " h ";
            long minutes = ChronoUnit.MINUTES.between(zdtTime1, zdtTime2);
            minutes = minutes % 60;
            if (minutes > 0)
                suiteDuration += minutes + " m ";

            long seconds = ChronoUnit.SECONDS.between(zdtTime1, zdtTime2);
            seconds = seconds % 60;
            if (seconds > 0)
                suiteDuration += seconds + " s";
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return suiteDuration;
    }

    public static void setTestDescriptionInHtml(String description) {
        String htmlFileName = TLDriverFactory.getReport().getAbsolutePath();
        setDataInHtml(description, htmlFileName, "testDescription");
    }

    public static void setTestDataInHtml(String key) {
        String htmlFileName = TLDriverFactory.getReport().getAbsolutePath();
        String htmlData = convertMapToHtmlTable(key);
        setDataInHtml(htmlData, htmlFileName, "testData");
    }

    public static void setTestDataInHtml(String key, String value) {
        String htmlFileName = TLDriverFactory.getReport().getAbsolutePath();
        String htmlData = convertMapToHtmlTable(key, value);
        setDataInHtml(htmlData, htmlFileName, "testData");
    }

    public static void setTestDataInHtml(HashMap<String, String> map) throws Throwable {
        String htmlFileName = TLDriverFactory.getReport().getAbsolutePath();
        String htmlData = convertMapToHtmlTable(map);
        setDataInHtml(htmlData, htmlFileName, "testData");
    }


    public static void setTestDataInHtml(Hashtable<String, String> map) throws Throwable {
        String htmlFileName = currentRunResults + "/" + TLDriverFactory.getTestcaseName() + "/index.html";
        String htmlData = convertHashtableToHtmlTable(map);
        setDataInHtml(htmlData, htmlFileName, "testData");
    }

    public static void setDataInHtml(String htmlData, String htmlFileName, String id) {
        try {
            File htmlFile = new File(htmlFileName);
            FileInputStream fis = new FileInputStream(htmlFile);
            BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
            String str = "";
            String line = bf.readLine();
            while (str != null) {
                line += str;
                str = bf.readLine();
            }
            int lastIndex = line.indexOf("</table id='" + id + "'>");

            htmlData = htmlData.replaceAll("\n", "<br/>");

            line = new StringBuffer(line).insert(lastIndex, htmlData).toString();

            fis.close();

            FileWriter fileWriter = new FileWriter(htmlFile);
            fileWriter.append(line);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	/*public void setDataInHtml(String htmlData, String htmlFileName, String id)
	{
		try {
			File htmlFile = new File(htmlFileName);

			FileInputStream fis = new FileInputStream(htmlFile);
			BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			line = bf.readLine();

			int startIndex = line.indexOf("<div id='"+id+"'>");
			int lastIndex = line.indexOf("</div id='"+id+"'>");

			if(startIndex == -1 || lastIndex == -1) //If not found, add it at the last
				line += htmlData + "</div id='"+id+"'>";
			else
				line = new StringBuffer(line).replace(startIndex, lastIndex, htmlData).toString();
			fis.close();

			FileWriter fileWriter = new FileWriter(htmlFile);
			fileWriter.append(line);
			fileWriter.close();
		}
		catch(Throwable e) {
			logException(e);
		}

	}*/

    public static String convertMapToHtmlTable(HashMap<String, String> map) {
        String htmlData = "";
        for (String key : map.keySet())
            htmlData += "<tr><th align=\"left\">" + key + "</th><td>:</td><td>" + map.get(key) + "</td></tr>";
        return htmlData;
    }

    public static String convertHashtableToHtmlTable(Hashtable<String, String> map) {
        String htmlData = "";
        for (String key : map.keySet())
            htmlData += "<tr><th align=\"left\">" + key + "</th><td>:</td><td>" + map.get(key) + "</td></tr>";
        return htmlData;
    }


    public static String convertMapToHtmlTable(String key) {
        String htmlData = "<tr style=\"height: 15px;\"></tr>"
                + "<tr><th colspan=\"2\"><u>" + key + "</u></th></tr>";
        return htmlData;
    }

    public static String convertMapToHtmlTable(String key, String value) {
        String htmlData = "<tr><th align=\"left\">" + key + "</th><td>:</td><td>" + value + "</td></tr>";
        return htmlData;
    }

    public void writeToHtml(String StepDesc, String inputValue, String Expected, String Actual, String status) {

        //		if ( property_screenshot_on_step==true  || getProperties().getProperty("SCREENSHOT_ON_STEP").equals("true") || status.equalsIgnoreCase("failed") )
        if (property_screenshot_on_step == true || status.equalsIgnoreCase("failed"))
            writeToHtml(StepDesc, inputValue, Expected, Actual, status, true);
        else
            writeToHtml(StepDesc, inputValue, Expected, Actual, status, false);
    }

    public static ThreadLocal<Boolean> testExecutionStatus = new ThreadLocal<>();

    public synchronized void writeToHtml(String StepDesc, String inputValue, String Expected, String Actual, String status, boolean screenshotRequired) {
        new Tek_Properties().checkThreadPauseRequest();
        String statusImg = null;
        String screenshotName = null;
        File f = TLDriverFactory.getReport();
        FileWriter fileWriter = null;
        if (screenshotRequired && property_takeScreenshot)
            screenshotName = getScreenShot();

        int reportCounterValue = -1;
        try {
            reportCounterValue = reportCounter.get();
        } catch (Exception e) {
        }

        if (reportCounterValue == 1)
            testExecutionStatus.set(true);

        if (status.equalsIgnoreCase("passed")) {
            statusImg = "../../../logpass.png";
        } else if (status.equalsIgnoreCase("failed")) {
            statusImg = "../../../logfail.png";
            testExecutionStatus.set(false);
            failedCounter.set(failedCounter.get() == null ? 1 : failedCounter.get() + 1);
        } else if (status.equalsIgnoreCase("info")) {
            statusImg = "../../../loginfo.png";
        } else if (status.equalsIgnoreCase("warning")) {
            statusImg = "../../../logwarning.png";
        } else if (status.equalsIgnoreCase("message")) {
            statusImg = "../../../loginfo.png";
        } else {
            statusImg = "../../../loginfo.png";
            status += " style=\"display: none;\"";
        }

        try {
            fileWriter = new FileWriter(f.getAbsolutePath(), true);
            if (screenshotRequired && property_takeScreenshot && screenshotName != null) {

                fileWriter.append("<tr id=" + status + ">"
                        + "<td>" + getReportCounter() + "</td>"
                        + "<td>" + StepDesc + "</td>"
                        + "<td>" + inputValue + "</td>"
                        + "<td>" + Expected + "</td>"
                        + "<td>" + Actual + "</td>"
                        + "<td><img src=" + statusImg + " style=width:25px></td>"
                        + "<td>" + currentTime() + "</td>"
                        + "<td><a target=_blank href=./screenshots/" + screenshotName + ">"
                        + "<img src=./screenshots/" + screenshotName + " width = \"100\" height = \"70\" rel=\"noopener noreferrer\">"
                        + "</a></td>"
                        + "</tr>");
            } else {
                fileWriter.append("<tr id=" + status + ">"
                        + "<td>" + getReportCounter() + "</td>"
                        + "<td>" + StepDesc + "</td>"
                        + "<td>" + inputValue + "</td>"
                        + "<td>" + Expected + "</td>"
                        + "<td>" + Actual + "</td>"
                        + "<td><img src=" + statusImg + " style=width:25px></td>"
                        + "<td>" + currentTime() + "</td>"
                        + "<td></td>"
                        + "</tr>");
            }

            incrementReportCounter();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //This code is kept at the bottom so that we will log the final result in report and then close the execution. Otherwise, the last failed step won't appear in report.
        if (status.equalsIgnoreCase("failed")) {
            int failedCounterValue = failedCounter.get() == null ? 0 : failedCounter.get();
            if (property_continueExecutionAfterStepFailed && failedCounterValue < maxFailedCount)
                softAssertion.get().assertEquals(true, false);
            else if (property_continueExecutionAfterStepFailed && failedCounterValue >= maxFailedCount)
                Assert.assertEquals(true, false);
            else
                Assert.assertEquals(true, false);
        }

    }

    public static String currentTime() {
        if (lastZdt.get() == null)
            lastZdt.set(ZonedDateTime.now());
        ZonedDateTime thisZdt = ZonedDateTime.now();
        String result = null;
        long secs = ChronoUnit.SECONDS.between(lastZdt.get(), thisZdt);
        if (secs > 0)
            result = secs + " secs";
        else {
            long millisecs = ChronoUnit.MILLIS.between(lastZdt.get(), thisZdt);
            result = millisecs + " millis";
        }

        lastZdt.set(thisZdt);
        return result;
    }

    public static String getScreenShot() {
        String screenshotName = null;
        try {

            WebDriver driver = TLDriverFactory.getTLDriver();
            if (driver == null)
                return null;
            File f = TLDriverFactory.getScreenShotFolder();
            String screenshotPath = f.getAbsolutePath();

            Calendar cal = new GregorianCalendar();
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            int sec = cal.get(Calendar.SECOND);
            int min = cal.get(Calendar.MINUTE);
            int date = cal.get(Calendar.DATE);
            int day = cal.get(Calendar.HOUR_OF_DAY);
            String screenshot = null;
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            screenshotName = year + "_" + date + "_" + (month + 1) + "_" + day + "_" + min + "_" + sec
                    + ".png";

            screenshot = screenshotPath + "/" + screenshotName;
            FileUtils.copyFile(scrFile, new File(screenshot));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenshotName;

    }


    /**
     * @author paramagurusubbiah
     * @createdDate 10-Oct-2019
     * @modifiedBy rakesh, ahamed
     * @modifiedDate 6-Dec-2019
     * @description setAPIDataInReport
     * The htmlFileName is changed from
     * currentRunResults + "/" + TLDriverFactory.getTestCaseName() + "/index.html";
     * to
     * TLDriverFactory.getReport().getAbsolutePath();
     */
    public static void setAPIDataInReport(String description) {

        String htmlFileName = TLDriverFactory.getReport().getAbsolutePath();

        if (description.contains("Passed"))
            appendTestDataInReport(description, htmlFileName, "apiData", true);
        else
            appendTestDataInReport(description, htmlFileName, "apiData", false);
    }

    public static void setTestResultInReport() {
        if (testExecutionStatus.get())
            setTestResultInReport("Test case executed successfully");
        else
            setTestResultInReport("The test case execution got failed", false);
    }

    public static void setTestResultInReport(String description) {
        setTestResultInReport(description, true);
    }

    /**
     * @author paramagurusubbiah
     * @createdDate 10-Oct-2019
     * @modifiedBy
     * @modifiedDate
     * @description setTestDataInReport
     */
    public static void setTestResultInReport(String description, boolean isPassed) {
        String htmlFileName = TLDriverFactory.getReport().getAbsolutePath();
        appendTestDataInReport(description, htmlFileName, "resultData", isPassed);
    }

    /**
     * @author paramagurusubbiah
     * @createdDate 10-Oct-2019
     * @modifiedBy
     * @modifiedDate
     * @description setTestDataInHtmlAsString
     */
    public static void appendTestDataInReport(String htmlData, String htmlFileName, String id, boolean isPassed) {
        try {
            File htmlFile = new File(htmlFileName);

            FileInputStream fis = new FileInputStream(htmlFile);
            BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
            String str = "";
            String line = bf.readLine();
            while (str != null) {
                line += str;
                str = bf.readLine();
            }

            if (!line.contains(htmlData)) {

                if (isPassed)
                    htmlData = "<font color=\"green\">" + htmlData + "</font>";
                else
                    htmlData = "<font color=\"red\">" + htmlData + "</font>";

                String codeLine = line.substring(line.indexOf("<div id='" + id + "'>"),
                        line.indexOf("</div id='" + id + "'>") + ("</div id='" + id + "'>").length());

                // int startIndex = apiLine.indexOf("<div id='"+id+"'>");
                int lastIndex = codeLine.indexOf("</div id='" + id + "'>");

                String codeLine2 = new StringBuffer(codeLine).replace(lastIndex,
                        lastIndex + ("</div id='" + id + "'>").length(), htmlData + "</br></div id='" + id + "'>")
                        .toString();
                line = line.replace(codeLine, codeLine2);

                FileWriter fileWriter = new FileWriter(htmlFile);
                fileWriter.append(line);
                fileWriter.close();
            }

            bf.close();
            fis.close();

        } catch (Throwable e) {
        }

    }

    public static String nvl(String ifThisIsNull, String replaceThis) {
        return ifThisIsNull == null ? replaceThis : ifThisIsNull;
    }

    public static long requestingThread = 0;
    public static ZonedDateTime requestTime = null;
    public static int threadJoinTime = 10;

    public void pauseOtherThreads() {
        pauseOtherThreads(10);
    }

    /**
     * @param joinTime - in seconds
     * @author ahamedabdulrahman
     * @createdDate 9 Mar 2020
     * @modifiedBy
     * @modifiedDate
     * @description Wherever, there is a need to hold the execution of other threads for some time, this method can be used. I will hold the execution for mentioned seconds.
     */
    public void pauseOtherThreads(int joinTime) {
        checkThreadPauseRequest();
        requestingThread = Thread.currentThread().getId();
        requestTime = DateUtil.getDateTimeFromCurrentTimeZone();
        threadJoinTime = joinTime;
    }

    /**
     * @param
     * @author ahamedabdulrahman
     * @createdDate 13 Mar 2020
     * @modifiedBy
     * @modifiedDate
     * @description resumeOtherThreads
     */
    public void resumeOtherThreads() {
        requestingThread = 0;
    }

    /**
     * @param
     * @author ahamedabdulrahman
     * @createdDate 9 Mar 2020
     * @modifiedBy
     * @modifiedDate
     * @description This thread is called by every BaseFunction methods that uses driver object.
     */
    public void checkThreadPauseRequest() {
        try {
            for (int counter = 0; counter < 3; counter++) {
                //No thread is requesting pause, so this thread can continue its execution.
                if (requestingThread == 0)
                    return;

                //A thread has requested for pause but that was long time ago.
                ZonedDateTime currentTime = DateUtil.getDateTimeFromCurrentTimeZone();
                long timeDifference = threadJoinTime - ChronoUnit.SECONDS.between(requestTime, currentTime);
                if (timeDifference <= 0) {
                    resumeOtherThreads();
                    return;
                }

                //A thread has requested for pause recently. So, this need to join.
                //By the time, it completes the join, another thread could have asked for pause, so having a loop.
                if (Thread.currentThread().getId() != requestingThread) {
                    Thread.currentThread().join(timeDifference * 1000);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * @param
     * @author ahamedabdulrahman
     * @createdDate 4 May 2020
     * @modifiedBy
     * @modifiedDate
     * @description This method sets the label for the test methods. In case of Data Provider test cases, all the test cases share same name. If we want to differentiate them, we shall just use these labels
     */
    public void setTestMethodLabel(String label) {
        try {
            testMethod.get().testMethodName = testMethod.get().testMethodName + "_" + label;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /*
    public void getBasicData() {
        try {
            if (getProperties().get("PRIORITY_URL").toString().equalsIgnoreCase("CDMS")) {
                //new LoginModule().doLogin(username, password);
                new LoginModule().loginAsServiceAdvisorToApiForMobile();
                new FixedOperationModule().getProductFeatures();
                new DealerModule().getProductFeatures();
                DealerResponse objDealerResponse = ProductFeatureKeysAndValues.objDealerResponse;
                String currency = objDealerResponse.getData().getCurrency();
                if (currency.equals("USD"))
                    MONEY_SYMBOL = "$";
                else //As of now, $ is used
                    MONEY_SYMBOL = "$";
                TIME_ZONE = objDealerResponse.getData().getTimeZone();

            } else {
                //new DmsLoginModule().doLogin(username, password);
                new DmsLoginModule().loginAsServiceAdvisorToApiForMobile();
                try {
                    DealerConfigResponse dealerConfigResponse = new DealerConfigModule().callDealerConfigApi();
                    String currency = dealerConfigResponse.getData().getCurrency();
                    if (currency.equals("USD"))
                        MONEY_SYMBOL = "$";
                    else //As of now, $ is used
                        MONEY_SYMBOL = "$";
                    TIME_ZONE = dealerConfigResponse.getData().getTimeZone();
                } catch (Exception ex) {
                    MONEY_SYMBOL = "$";
                    TIME_ZONE = "America/Los_Angeles";
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }*/
}