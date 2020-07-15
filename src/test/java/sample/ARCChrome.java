package sample;

import com.utilities.BaseFunctions;
import com.utilities.TLDriverFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;

public class ARCChrome extends BaseFunctions {

    @BeforeClass(alwaysRun = true)
    public void beforeClass(){
        getProperties().setProperty(TLDriverFactory.BROWSER,TLDriverFactory.CHROME);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(){
        launchURL("DMS");
        String strUsername = getProperties().getProperty("USERNAME");
        String strPassword = getProperties().getProperty("PASSWORD");
        new LoginPage().doLogin(strUsername,strPassword);
    }

    @Test(enabled = true)
    public void arcChromeDemo(){
        HomePage homePage = new HomePage();
        homePage.showDealership();
        homePage.showUserInfo();
    }
}
