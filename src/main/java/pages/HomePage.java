package pages;

import com.utilities.BaseFunctions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class HomePage extends BaseFunctions {

    @FindBy(xpath = "(//div[contains(@class,'selectedDealer')])[last()]")
    private WebElement selectedDealer;

    @FindBy(xpath = "(//div[contains(@class,'avatar')])[last()]")
    private WebElement avatar;

    @FindAll(@FindBy(xpath = "//div[contains(@class,'profile_userInfoPopoverWrapper')]"))
    private List<WebElement> userInfoPopover;

    public void showDealership(){
        String strDealerName = selectedDealer.getText();
        logInfo("Dealer Name = " + strDealerName);
    }

    public void showUserInfo(){
        click(avatar,"Profile Picture - Avatar");
        String popoverInfo = "";
        for(int i=0; i<userInfoPopover.size(); i++){
            popoverInfo += userInfoPopover.get(i).getText();
        }
        logInfo("Pop over Info = " + popoverInfo);

    }
}
