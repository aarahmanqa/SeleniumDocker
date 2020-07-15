package com.utilities;

import org.openqa.selenium.WebDriverException;

public class CustomException extends WebDriverException {

    String message;
    public CustomException(String exceptionMessage)
    {
        message = exceptionMessage;
    }

    public String getMessage()
    {
        return message;
    }
}