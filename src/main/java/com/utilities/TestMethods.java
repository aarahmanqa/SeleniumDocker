package com.utilities;

import java.time.ZonedDateTime;

public class TestMethods 
{
	public String dealerName;
	public String xmlTestName;
	public String className;
	public String testMethodName;
	public String testMethodNameWithTimestamp;
	public String status = "Not Started";
	public ZonedDateTime startTime;
	public ZonedDateTime endTime;

	public String getStartTime()
	{
		if(startTime != null)
			return DateUtil.formatZonedDateTime(startTime, "MMM dd,yyyy HH:mm");
		else
			return "NA";
	}
	
	public String getEndTime()
	{
		if(endTime != null)
			return DateUtil.formatZonedDateTime(endTime, "MMM dd,yyyy HH:mm");
		else
			return "NA";
	}
	
	public String getKey() 
	{
		String key = xmlTestName+"/"+className+"/"+testMethodNameWithTimestamp;
		return key;
	}
	
	
}
