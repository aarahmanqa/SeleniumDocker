package com.utilities;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class VerificationListener implements IInvokedMethodListener {
   
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
       
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result)
    {
        if (method.isTestMethod() && result.isSuccess())
        {
            try {
            	Tek_Properties.softAssertion.get().assertAll();
            	result.setStatus(ITestResult.SUCCESS);
            } catch (AssertionError e) {
                result.setThrowable(e);
                result.setStatus(ITestResult.FAILURE);       
            }
        }
    }
}
