package pages;

import com.utilities.BaseFunctions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BaseFunctions {

    @FindBy(id = "username")
    private WebElement username;

    @FindBy(id = "password")
    private WebElement password;

    @FindBy(id = "loginButton")
    private WebElement loginButton;

    public void doLogin(String strUsername, String strPassword){
        enterText(username,strUsername,"Username");
        enterText(password,strPassword,"Password");
        click(loginButton,"Login Button");
    }
}
