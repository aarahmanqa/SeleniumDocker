package com.utilities;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class TLDriverFactory extends Tek_Properties {
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    private static ThreadLocal<File> tFile = new ThreadLocal<>();
    private static ThreadLocal<File> tFolder = new ThreadLocal<>();
    public static ThreadLocal<String> testcaseName = new ThreadLocal<String>();
    public static final String FIREFOX = "FIREFOX";
    public static final String CHROME = "CHROME";
    public static final String MOBILE = "MOBILE";
    public static final String MOBILE_DEVICE = "MOBILE_DEVICE";
    public static final String HEADLESS_MOB = "HEADLESS_MOB";
    public static final String CHROME_DOCKER = "CHROME_DOCKER";
    public static final String FIREFOX_DOCKER = "FIREFOX_DOCKER";
    public static IOSDriver<IOSElement> iDriver = null;
    public static MOBILE_APP mobile_app = MOBILE_APP.CDMS;
    public static final String platformVersions[] = {"11.4", "12.0", "12.1", "12.2", "12.4"};
    public static List<String> appInstalledPlatformVersions = new ArrayList<String>();
    public static List<String> appInstalledUDID = new ArrayList<String>();
    private static ThreadLocal<File> apiFolder = new ThreadLocal<>();
    public static ThreadLocal<File> downloadFolder = new ThreadLocal<File>();
    public static AppiumServiceBuilder serviceBuilder;
    static int portIncrement = 0;

    public static ThreadLocal<Boolean> relaunchFlag = new ThreadLocal<Boolean>();
    public static HashMap<Long, MobilePorts> mobilePortsMap = new HashMap<Long, MobilePorts>();

    public static ThreadLocal<AppiumDriverLocalService> server = new ThreadLocal<AppiumDriverLocalService>();
    public static ArrayList<AppiumDriverLocalService> serverList = new ArrayList<AppiumDriverLocalService>();
    /**
     * This is required to know that new driver object is created using new thread (in case of multi threading).
     * If the driver object is created with default thread, any phone id can be assigned. In case of multi threading, we should assign each device to each thread.
     * So, if driver object is called from methods like BeforeSuite, any device will be assigned. If its a new thread, first thread gets the first device, second- second device.
     **/
    public static boolean isBeforeMethodCalled = false;

    public static enum MOBILE_APP {
        CDMS,
        SALES
    }

    static {
        try {
            //	WebDriverManager.chromedriver().clearPreferences();
            WebDriverManager.chromedriver().setup();
            //WebDriverManager.chromedriver().version("76.0.3809.126").setup();
            //WebDriverManager.firefoxdriver().setup();
            relaunchFlag.set(true);
        }catch(Throwable t){}
    }

    public synchronized static void setReport(File testResultHtml) {
        //File f = new File(testResultHtml);
        tFile.set(testResultHtml);
    }

    public synchronized static File getReport() {
        return tFile.get();
    }

    public synchronized static void setScreenShotFolder(File testScreenShotFolder) {
        tFolder.set(testScreenShotFolder);
    }

    public synchronized static File getScreenShotFolder() {
        return tFolder.get();
    }

    public synchronized static void setTestcaseName(String tName) {
        //File f = new File(testResultHtml);
        testcaseName.set(tName);
    }

    public synchronized static void setAPIFolder(File apiFilesFolder) {
        apiFolder.set(apiFilesFolder);
    }

    public synchronized static File getAPIFolder() {
        return apiFolder.get();
    }

    public synchronized static void setDownloadFolder(File downloadFolder) {
        TLDriverFactory.downloadFolder.set(downloadFolder);
    }

    public synchronized static File getDownloadFolder() {
        return TLDriverFactory.downloadFolder.get();
    }

    public synchronized static String getTestcaseName() {
        String name = testcaseName.get();
        if (name == null)
            return "Common";
        else return name;
    }

    /**
     * @author
     * @createdDate
     * @modifiedBy
     * @modifiedDate 13-Dec-2019
     * @description Updated Download Default Directory
     */
    public synchronized static void setTLDriver(String browser) throws Throwable {
        setTLDriver(browser, false);
    }

    public synchronized static void setTLDriver(String browser, boolean isIncognito) throws Throwable {
        switch (browser.toUpperCase()) {
            case FIREFOX:
                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setCapability("download.prompt_for_download", "false");
                caps.setCapability("directory_upgrade", "true");
                caps.setCapability("plugins.plugins_disabled", "Chrome PDF Viewer");
                caps.setCapability("directory_upgrade", "true");
                caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);

                FirefoxOptions foptions = new FirefoxOptions();
                System.out.println("browser ###############   " + browser);
                foptions.addArguments("--disable-dev-shm-usage");
                foptions.addArguments("--enable-extensions");
                foptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                HashMap<String, Object> fireFoxperfs = new HashMap<String, Object>();
                String strDownloadFolders = TLDriverFactory.getDownloadFolder().getAbsolutePath();
                fireFoxperfs.put("download.default_directory", strDownloadFolders);
                fireFoxperfs.put("plugins.always_open_pdf_externally", true);
                foptions.addArguments("--no-sandbox");
                foptions.addArguments("--test-type");
                foptions.addArguments("--start-maximized");
                foptions.addArguments("--disable-notifications");
                foptions.addArguments("--disable-gpu");
                if (isIncognito)
                    foptions.addArguments("--incognito");
                foptions.setPageLoadStrategy(PageLoadStrategy.NONE);
                foptions.setCapability("cap", caps);
                foptions.setHeadless(false);
                FirefoxDriver ffDriver = new FirefoxDriver(foptions);
                tlDriver.set(ffDriver);
                break;

            case CHROME:
                DesiredCapabilities cap = new DesiredCapabilities();
                cap.setCapability("download.prompt_for_download", "false");
                cap.setCapability("directory_upgrade", "true");
                cap.setCapability("plugins.plugins_disabled", "Chrome PDF Viewer");
                cap.setCapability("directory_upgrade", "true");
                cap.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
                LoggingPreferences logPrefs = new LoggingPreferences();
                logPrefs.enable(LogType.PERFORMANCE, Level.ALL);

                ChromeOptions options = new ChromeOptions();
                System.out.println("browser ###############   " + browser);
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--enable-extensions");
                options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
                String strDownloadFolder = TLDriverFactory.getDownloadFolder().getAbsolutePath();
                chromePrefs.put("download.default_directory", strDownloadFolder);
                chromePrefs.put("plugins.always_open_pdf_externally", true);
                options.addArguments("--no-sandbox");
                options.addArguments("--test-type");
                options.addArguments("--start-maximized");
                options.setExperimentalOption("useAutomationExtension", false);
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-gpu");
                if (isIncognito)
                    options.addArguments("--incognito");
                options.setPageLoadStrategy(PageLoadStrategy.NONE);
                Map<String, Object> prefs = new HashMap<String, Object>();
                prefs.put("profile.default_content_setting_values.notifications", 2);

                options.setExperimentalOption("prefs", prefs);

                options.setCapability("cap", cap);
                options.setExperimentalOption("prefs", chromePrefs);
                options.setCapability("goog:loggingPrefs", logPrefs);
                ChromeDriver chromeDriver = new ChromeDriver(options);
                //tlDriver = ThreadLocal.withInitial(() -> chromeDriver);
                tlDriver.set(chromeDriver);
                break;

            case CHROME_DOCKER:
                ChromeOptions chromeDockerOptions = new ChromeOptions();
                System.setProperty("webdriver.chrome.driver","/app/bin/chromedriver");
                chromeDockerOptions.addArguments("--headless");
                chromeDockerOptions.addArguments("--no-sandbox");
                System.setProperty("webdriver.chrome.args", "--disable-logging");
                System.setProperty("webdriver.chrome.silentOutput", "true");
                chromeDockerOptions.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
                chromeDockerOptions.addArguments("disable-infobars"); // disabling infobars
                chromeDockerOptions.addArguments("--disable-extensions"); // disabling extensions
                chromeDockerOptions.addArguments("--disable-gpu"); // applicable to windows os only
                chromeDockerOptions.addArguments("window-size=1024,768"); // Bypass OS security model
                chromeDriver = new ChromeDriver(chromeDockerOptions);
                tlDriver.set(chromeDriver);
                break;

            case FIREFOX_DOCKER:
                FirefoxOptions firefoxDockerOptions = new FirefoxOptions();
                System.setProperty("webdriver.gecko.driver","/app/bin/firefoxdriver");
                firefoxDockerOptions.addArguments("--headless");
                firefoxDockerOptions.addArguments("--no-sandbox");
                System.setProperty("webdriver.gecko.args", "--disable-logging");
                System.setProperty("webdriver.gecko.silentOutput", "true");
                firefoxDockerOptions.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
                firefoxDockerOptions.addArguments("disable-infobars"); // disabling infobars
                firefoxDockerOptions.addArguments("--disable-extensions"); // disabling extensions
                firefoxDockerOptions.addArguments("--disable-gpu"); // applicable to windows os only
                firefoxDockerOptions.addArguments("window-size=1024,768"); // Bypass OS security model
                FirefoxDriver firefoxDriver = new FirefoxDriver(firefoxDockerOptions);
                tlDriver.set(firefoxDriver);
                break;

            case MOBILE:
                Map<String, String> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "Pixel 2");
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("disable-gpu");
                chromeOptions.addArguments("--disable-features=VizDisplayCompositor");
                chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);
                chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
                ChromeDriver mobdriver = new ChromeDriver(chromeOptions);
                tlDriver.set(mobdriver);
                break;
            case MOBILE_DEVICE:
                IOSDriver<IOSElement> iDriver = initializeIOSDriver();
                tlDriver.set(iDriver);
                new Mobile_BaseFunctions().handleAnyAlert();
                break;
            default:
                chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless");
                logPrefs = new LoggingPreferences();
                logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
                chromeOptions.setCapability("goog:loggingPrefs", logPrefs);
                chromeDriver = new ChromeDriver(chromeOptions);
                tlDriver.set(chromeDriver);
                //	tlDriver = ThreadLocal.withInitial(() -> driver);
                break;
        }
    }

    public synchronized static WebDriver getTLDriver() {
        return tlDriver.get();
    }

    public static boolean isPortAlreadyUsed(int appiumPort) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("/usr/local/bin/appium -p " + appiumPort);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String allLines = "";
            String singleLine = br.readLine();
            while (singleLine != null) {
                allLines += singleLine + " ";
                singleLine = br.readLine();
            }
            return allLines.contains("address already in use");
        } catch (Exception ex) {
        }
        return true;
    }

    /**
     * @param
     * @author ahamedabdulrahman
     * @createdDate 9 Dec 2019
     * @modifiedBy
     * @modifiedDate
     * @description To retry initializing the ios driver, this code is used. During retying, 'useNewWda' capability will be turned ON. Its usually made false. Otherwise, time taken between consequent test case would be huge.
     */
    public static IOSDriver<IOSElement> initializeIOSDriver() throws Exception {
        iDriver = null;
        for (int i = 0; i < 10; i++) {
            try {
                if (iDriver == null) {
                    iDriver = initIOSDriver();
                    relaunchFlag.set(false);
                } else
                    break;
            } catch (SessionNotCreatedException se) {
                se.printStackTrace();
                System.out.println("Relaunching IOSDriver... " + i);
                relaunchFlag.set(true);
            } catch (Throwable t) {
                t.printStackTrace();
                break;
            }
        }
        return iDriver;
    }

    public static void startAppiumServer(int port) {
        AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder();
        //serviceBuilder.withIPAddress(AppiumServiceBuilder.DEFAULT_LOCAL_IP_ADDRESS);
        // Use any port, in case the default 4723 is already taken (maybe by another Appium server)
        serviceBuilder.usingPort(port);
        // Tell serviceBuilder where node is installed. Or set this path in an environment variable named NODE_PATH
        serviceBuilder.usingDriverExecutable(new File("/usr/local/bin/node"));
        // Tell serviceBuilder where Appium is installed. Or set this path in an environment variable named APPIUM_PATH
        serviceBuilder.withAppiumJS(new File("/usr/local/bin/appium"));
        // The XCUITest driver requires that a path to the Carthage binary is in the PATH variable. I have this set for my shell, but the Java process does not see it. It can be inserted here.
        HashMap<String, String> environment = new HashMap();
        environment.put("PATH", "/usr/local/bin:" + System.getenv("PATH"));
        serviceBuilder.withEnvironment(environment);

        server.set(AppiumDriverLocalService.buildService(serviceBuilder));
        server.get().start();
        serverList.add(server.get());
    }

    public static IOSDriver<IOSElement> initIOSDriver() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        int wdaLocalPort = 8100 + portIncrement;
        int appiumPort = 4723 + portIncrement;
        HashSet<String> udidList = getDeviceID();
        if (udidList == null)
            throw new Exception("No device connected");
        String UDID = null;
        try {
            long id = Thread.currentThread().getId();
            if (mobilePortsMap.containsKey(id) == false) {
                UDID = (String) udidList.toArray()[portIncrement];
                MobilePorts mobilePorts = new MobilePorts();
                mobilePorts.UDID = UDID;
                mobilePorts.appiumPort = appiumPort;
                mobilePorts.wdaLocalPort = wdaLocalPort;
                mobilePortsMap.put(id, mobilePorts);
                //startAppiumServer(appiumPort);
                portIncrement++;
            } else {
                MobilePorts mobilePorts = mobilePortsMap.get(id);
                UDID = mobilePorts.UDID;
                wdaLocalPort = mobilePorts.wdaLocalPort;
                appiumPort = mobilePorts.appiumPort;
            }
        } catch (IndexOutOfBoundsException ie) {
            throw new Exception("Number of phones is less than thread count. Expected : " + udidList.size() + ". Actual : " + portIncrement);
        }
        System.out.println("Chosen device - " + UDID);
        capabilities.setCapability("noReset", true);
        if (CHOSEN_DEVICE == DEVICE.SIMULATOR) {
            capabilities.setCapability("platformVersion", UDID);
            if (appInstalledPlatformVersions.contains(UDID) == false) {
                capabilities.setCapability("noReset", false);
                capabilities.setCapability("fullReset", true);
                appInstalledPlatformVersions.add(UDID);
            }
        } else {
            capabilities.setCapability("udid", UDID);
            capabilities.setCapability("platformVersion", "12.1");

            //if the mobile build branch is null or empty string, it means no need to download ipa file
            if (Mobile_BaseFunctions.nvl(mobileBuildBranch, "").equals("") == false) {
                if (appInstalledUDID.contains(UDID) == false) {
                    if (appInstalledUDID.size() == 0) {
                        new S3Bucket().downloadAppFromS3(environment, mobileBuildBranch);
                        System.out.println("App downloaded path : " + Tek_Properties.appPath);
                    }
                    capabilities.setCapability("app", Tek_Properties.appPath);
                    capabilities.setCapability("noReset", false);
                    capabilities.setCapability("fullReset", true);
                    capabilities.setCapability("useNewWDA", true); //This will remove and install the WebDriverAgent.
                    appInstalledUDID.add(UDID);
                }
            }
        }

        if (relaunchFlag.get() == null || relaunchFlag.get()) {
            capabilities.setCapability("useNewWDA", true);
            capabilities.setCapability("--session-override", true); //https://stackoverflow.com/questions/32048031/org-openqa-selenium-sessionnotcreatedexception-a-new-session-could-not-be-creat
        }
        capabilities.setCapability("automationName", "XCUITest");
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("deviceName", "iPhone 7 Plus");
        capabilities.setCapability("wdaLocalPort", wdaLocalPort);
        capabilities.setCapability("newCommandTimeout", 0); //It specifies how much time appium needs to wait
        capabilities.setCapability("bundleId", getBundleId());
        capabilities.setCapability("waitForQuiescence", false);
        capabilities.setCapability("useJSONSource", true);
        capabilities.setCapability("realDeviceScreenshotter", "idevicescreenshot");
        capabilities.setCapability("showXcodeLog",true);
        try {
            ZonedDateTime zdtStartTime = ZonedDateTime.now();
            System.out.println("Launching IOSDriver...");
            iDriver = new IOSDriver<IOSElement>(new URL("http://0.0.0.0:" + appiumPort + "/wd/hub"), capabilities);
            //iDriver = new IOSDriver<IOSElement>(server.get().getUrl(), capabilities);
            ZonedDateTime zdtEndTime = ZonedDateTime.now();
            System.out.println("App launch time = " + calculateDuration(zdtStartTime, zdtEndTime));
            iDriver.manage().timeouts().implicitlyWait(Tek_Properties.MOBILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Throwable t) {
            throw t;
        }
        return iDriver;
    }

    public static String getBundleId() {
        switch (mobile_app) {
            case CDMS:
                return "com.tekion.cdmsmobileenterprise";
            default:
                return "com.tekion.sales";
        }
    }

    public static HashSet<String> getDeviceID() {
        HashSet<String> udidList = new HashSet<String>();
        if (CHOSEN_DEVICE == DEVICE.SIMULATOR) {
            udidList = new HashSet<String>(Arrays.asList(platformVersions));
            return udidList;
        } else {
            try {
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec("/usr/local/bin/idevice_id -l");
                BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String udid = br.readLine();
                while (udid != null) {
                    udidList.add(udid);
                    udid = br.readLine();
                }
                return udidList;
            } catch (Exception ex) {
            }
        }
        return null;
    }

    public static boolean isMobileDevice() {
        return (TLDriverFactory.getTLDriver() instanceof IOSDriver);
    }

    /**
     * @param
     * @author ahamedabdulrahman
     * @createdDate 26 Mar 2020
     * @modifiedBy
     * @modifiedDate
     * @description setTLDriver
     */
    public static void setTLDriver(WebDriver driver) {
        tlDriver.set(driver);
    }
}