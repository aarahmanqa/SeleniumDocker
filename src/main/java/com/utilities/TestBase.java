package com.utilities;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.awt.*;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class TestBase extends Tek_Properties {

    public static int beforeClassCounter = 0;
    public static int beforeTestCounter = 0;

    public static enum APPLICATION {
        CONSUMER, CDMS;
    }

    static {
        //System.setProperty(ATU_REPORTER_CONFIG,System.getProperty("user.dir")+"/atu.properties");
    }

    @BeforeSuite(alwaysRun = true)
    @Parameters({"Environment"})
    public void beforeSuite(@Optional() String propFile, ITestContext ctx) throws Throwable {
        if (environment == null)
            environment = "";
        if (System.getenv("Properties") != null) //Jenkins run
        {
            propertiesFile = System.getenv("Properties");
            propFile = "src/main/resources/" + propertiesFile;
            setProperties(propFile);
        } else {
            setProperties(propFile);
        }
        startReporting(ctx);
    }

    @BeforeClass(alwaysRun = true)
    public synchronized void a0_beforeClass() throws Throwable {
        String methodName = "BeforeClass_" + ++beforeClassCounter;
        createTestFolder("Before/BeforeClass/" + methodName);
    }

    @BeforeTest(alwaysRun = true)
    public synchronized void a0_beforeTest() throws Throwable {
        String methodName = "BeforeTest_" + ++beforeTestCounter;
        createTestFolder("Before/BeforeTest/" + methodName);
    }


    @BeforeMethod(alwaysRun = true)
    public synchronized void initializWebDriver(ITestContext ctx, ITestResult result) throws Throwable {
        File file = beforeMethodReporting(ctx, result);
        if (property_videoRecorder)
            Screen_Recorder.startRecording(result.getMethod().getMethodName(), file);
        //launchURL(getProperties().getProperty(PRIORITY_URL));
    }

    @AfterMethod(alwaysRun = true)
    public synchronized void testTearDown(ITestResult result, ITestContext ctx) throws Throwable {
        ////driver = TLDriverFactory.getTLDriver();
        /*if (CheckInDetails.cancelAppointment) {
            CancelAppointment objCancelAppointment = new CancelAppointment();
            CancelAppointmentRequest objCancelAppointmentRequest = new CancelAppointmentRequest();
            objCancelAppointmentRequest.setAppointmentID(CheckInDetails.appointmentId);
            //System.out.println( Common.objectToJSONString(objCancelAppointmentRequest));
            objCancelAppointment.callCancelAppointmentAPI(SessionDetails.getHeaddersForSeesion(), Common.objectToJSONString(objCancelAppointmentRequest));
            objLogOut = new LogOut();
            objLogOut.callTLogOutApi();
        }*/
        if (property_videoRecorder)
            Screen_Recorder.stopRecording();

        BaseFunctions.setNetworkLogsInReport();
        afterMethodReporting(result, ctx);
    }


    @AfterSuite(alwaysRun = true)
    public void LogFinish() throws Throwable {
        afterSuiteReporting();
    }

    public void goToCDMS() {

        //ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
        ////driver = TLDriverFactory.getTLDriver();
        if (!driver.getCurrentUrl().contains("cdms")) {
            //driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
            launchURL(APPLICATION.CDMS.toString());
        } else {
            System.out.println("You are already on CDMS");
        }
    }

    public void goToCONSUMER() {
        //driver = TLDriverFactory.getTLDriver();
        if (driver.getCurrentUrl().contains("cdms")) {
            //driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
            launchURL(APPLICATION.CONSUMER.toString());
        } else {
            System.out.println("You are already on CONSUMER");
        }
    }

    public void setMaximize() {
        driver = TLDriverFactory.getTLDriver();
        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        driver.manage().window().setPosition(new Point(0, 0));
        driver.manage().window().setSize(new Dimension(width, height));
    }

    String Application = null;

    public String launchURL(String application) {
        driver = TLDriverFactory.getTLDriver();
        setMaximize();
        Application = application;
        switch (application) {
            case "CDMS":
                url = getProperties().get(CDMS_URL).toString();
                break;
            case "CONSUMER":
                url = getProperties().get(CONSUMER_URL).toString();
                break;
            case "DMS":
                url = getProperties().get(DMS_URL).toString();
                break;
            default:
                url = application;
        }

        driver.get(url);
        driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        return url;
    }

    public void tearDown() {

        //driver= TLDriverFactory.getTLDriver();
        System.out.println("Tearing down thread "
                + Thread.currentThread().getId());
        if (null != driver) {
            driver.quit();
            driver = null;
        }
    }


    /*public void signOutSession() {
        try {
            //driver= TLDriverFactory.getTLDriver();
            // TODO Auto-generated method stub
            if (getProperties().getProperty(PRIORITY_URL).contains("CDMS")) {
                driver.navigate().to(getProperties().getProperty(CDMS_URL));
                header = new HeaderModule();
                header.signOut();

            } else if (getProperties().getProperty(PRIORITY_URL).contains("DMS")) {
                FavoriteAppsContainer favApp = new FavoriteAppsContainer();
                favApp.signOut();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void launchURL() {
        //driver= TLDriverFactory.getTLDriver();
        driver.get(getProperties().getProperty(CDMS_URL));
    }


}
