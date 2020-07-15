package com.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DateUtil extends BaseFunctions {

	public static String get_Current_PST_Date_Time() throws ParseException {

		SimpleDateFormat dtf =  new SimpleDateFormat("yyyy-MMMM-dd hh:mm:ss a");

		Date localDate = new Date();
		String currentist_Time = dtf.format(localDate);

		System.out.println("Current IST Time: "+currentist_Time);
		String format = "yyyy-MMMM-dd hh:mm:ss a";
		SimpleDateFormat estFormatter = new SimpleDateFormat(format);
		estFormatter.setTimeZone(TimeZone.getTimeZone("IST"));
		Date date = estFormatter.parse(currentist_Time);

		SimpleDateFormat utcFormatter = new SimpleDateFormat(format);
		utcFormatter.setTimeZone(TimeZone.getTimeZone("PST"));

		String year_month_date = utcFormatter.format(date);
		System.out.println("Current PST Time: "+ year_month_date);
		return year_month_date;
	}

	public static String get_Current_PST_Date_Time_in_yyyy_mm_dd_format() throws ParseException {

		SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
		Date localDate = new Date();
		String currentist_Time = dtf.format(localDate);

		System.out.println("Current IST Time: "+currentist_Time);
		String format = "yyyy-MM-dd hh:mm:ss a";
		SimpleDateFormat estFormatter = new SimpleDateFormat(format);
		//		estFormatter.setTimeZone(TimeZone.getTimeZone("IST"));
		Date date = estFormatter.parse(currentist_Time);

		SimpleDateFormat utcFormatter = new SimpleDateFormat(format);
		utcFormatter.setTimeZone(TimeZone.getTimeZone("PST"));

		String year_month_date = utcFormatter.format(date);
		System.out.println("Current PST Time: "+ year_month_date);
		return year_month_date;
	}

	public static String get_Current_UST_to_PST_Date_Time_in_yyyy_mm_dd_format() throws ParseException {

		//		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		//		df.setTimeZone(TimeZone.getTimeZone("PST"));
		//		final String dateTimeString = df.format(new Date());
		//		System.out.println("PST: "+dateTimeString);

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
		LocalDateTime localDate = LocalDateTime.now();
		String currentist_Time = dtf.format(localDate);

		System.out.println("Current PST Time: "+currentist_Time);
		String format = "yyyy-MM-dd hh:mm:ss a";
		SimpleDateFormat estFormatter = new SimpleDateFormat(format);
		estFormatter.setTimeZone(TimeZone.getTimeZone("IST"));
		Date date = estFormatter.parse(currentist_Time);

		SimpleDateFormat utcFormatter = new SimpleDateFormat(format);
		utcFormatter.setTimeZone(TimeZone.getTimeZone("PST"));

		String year_month_date = utcFormatter.format(date);
		System.out.println("Current PST Time: "+ year_month_date);
		return year_month_date;
	}

	public static String get_DateAfterDaysFromCurrentDate_in_yyyy_mm_dd_HH_mm_ss_Formate(int days) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, days); // Adding  days

		return format.format(c.getTime());
	}

	/**
	 * This method adds number of days,month,year,hour,minutes and seconds to given DateAndTime returns the DateAndTime
	 * 
	 * @param date
	 * @param days
	 * @param month
	 * @param yeay
	 * @param hourin24
	 * @param mins
	 * @param secs
	 * @return
	 * @throws ParseException
	 */
	public static String get_DaysAfterGivenDate_in_yyyy_mm_dd_HH_mm_ss_Formate(String date,int days,int month,int year,int hourin24,int mins,int secs) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
		Calendar c = Calendar.getInstance();
		Date givenDate = format.parse(date);
		c.setTime(givenDate);
		c.add(Calendar.DATE, days); // Adding  days
		c.add(Calendar.MONTH, month);
		c.add(Calendar.YEAR, year);
		c.add(Calendar.HOUR_OF_DAY, hourin24);
		c.add(Calendar.MINUTE, mins);
		c.add(Calendar.SECOND, secs);
		return format.format(c.getTime());
	}

	public static void convert_UST_TO_PST(String date) {
		String dateInString = date;//"2016-08-16T15:23:01Z";

		Instant instant = Instant.parse(dateInString);

		System.out.println("Instant : " + instant);

		//get date time only
		LocalDateTime result = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));

		//get localdate
		System.out.println("LocalDate : " + result.toLocalDate());

		//get date time + timezone
		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("US/Alaska"));
		System.out.println(zonedDateTime);

		//get date time + timezone
		/*ZonedDateTime zonedDateTime2 = instant.atZone(ZoneId.of("Europe/Athens"));
		System.out.println(zonedDateTime2);*/
	}

	/**
	 * Converts the time from UTC timezone to default time zone which is set in properties file.<br/>
	 * @param dateInString eg: 2019-03-16T13:15:55Z
	 * @return ZonedDateTime
	 */
	public static ZonedDateTime convertApiUTCtoDefaultTimeZone(String dateInString)
	{
		/*
		 * To convert this ZonedDateTime to String, use this:
		 * ZonedDateTime objZonedDateTime = convertApiUTCtoAnotherTimeZone(dateInString);
		 * objZonedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
		 * 
		 * To get individual data, use this
		 * objZonedDateTime.getHour();
		 * objZonedDateTime.getMinute();
		 * objZonedDateTime.getSecond();
		 * objZonedDateTime.getDayOfMonth();
		 * objZonedDateTime.getMonthValue();
		 * objZonedDateTime.getYear();
		 */

		try {
			Instant instant = Instant.parse(dateInString);
			LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));        
			ZoneId toZoneId = ZoneId.of(Tek_Properties.TIME_ZONE);
			ZonedDateTime zonedDateTime = instant.atZone(toZoneId);
			return zonedDateTime;
		}catch(Exception ex)
		{
			return null;
		}
	}

	/**
	 * This method returns the current datetime from default timezone and subtracts it by value mentioned in param.<br/>
	 * This is useful for global serach
	 * @return
	 */
	public static String getDateTimeWithAddOrSub(int daysToAddOrSub)
	{
		ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneId.of(Tek_Properties.TIME_ZONE));
		zdt = zdt.plusDays(daysToAddOrSub);
		String str = zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		return str;
	}

	/**
	 * This method is used by MobileAppointmentsAll api. The current UTC date time with offset need to sent.
	 * @return
	 * Output will be like this:
	 * 2019-03-21T00:00:00-07:00
	 */
	public static String getUTCDateTimeForGetAPIRequest(ZonedDateTime ZonedDateTime)
	{
		ZoneOffset offset = ZoneId.of(Tek_Properties.TIME_ZONE).getRules().getOffset(ZonedDateTime.toInstant());
		String dateTime = ZonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		dateTime = dateTime + "T00:00:00"+offset.toString();
		return dateTime;
	}

	public static Long getEpochTimeFromCurrentTimeZone()
	{
		return DateUtil.convertZonedDateTimeToEpoch(DateUtil.getDateTimeFromCurrentTimeZone());
	}

	/**
	 * This method returns the current time of the time zone.
	 * @return ZonedDateTime instance
	 * output will be like this:
	 * 2019-03-21T00:34:12.467779-07:00[America/Los_Angeles]
	 */
	public static ZonedDateTime getDateTimeFromCurrentTimeZone()
	{
		ZonedDateTime zoneNow = ZonedDateTime.now(TimeZone.getTimeZone(Tek_Properties.TIME_ZONE).toZoneId());
		return zoneNow;
	}

	public static ZonedDateTime getDateTimeFromUTCTimeZone()
	{
		ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("UTC"));
		return zdt;
	}

	public static ZonedDateTime parseUIAppointmentDateTime(String appointmentDateTime)
	{
		try {
			appointmentDateTime = appointmentDateTime.replace("AM", "am");
			appointmentDateTime = appointmentDateTime.replace("PM", "pm");
			LocalDateTime objLocalDateTime = LocalDateTime.parse(appointmentDateTime, DateTimeFormatter.ofPattern("MMM dd, yyyy '|' h:mm a"));
			ZonedDateTime objZonedDateTime = objLocalDateTime.atZone(ZoneId.of(Tek_Properties.TIME_ZONE));
			return objZonedDateTime;
		}catch(Exception ex)
		{
			return null;
		}
	}

	/**
	 * Call this method like this:<br/>
	 * parseDateTimeToDefaultTimeZone("April 23, 2019 | 0:00 am","MMMM dd, yyyy '|' h:mm a")
	 * @param dateTime
	 * @param pattern
	 * @return
	 */
	public static ZonedDateTime parseDateTimeToDefaultTimeZone(String dateTime,String pattern)
	{
		try {
			dateTime = dateTime.replace("AM", "am");
			dateTime = dateTime.replace("PM", "pm");
			LocalDateTime objLocalDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
			ZonedDateTime objZonedDateTime = objLocalDateTime.atZone(ZoneId.of(Tek_Properties.TIME_ZONE));
			return objZonedDateTime;
		}catch(Exception ex)
		{
			return null;
		}
	}

	/**
	 * Call this method like this:<br/>
	 * parseDateTimeToDefaultTimeZone("April 23, 2019","MMMM dd, yyyy")
	 * @param dateTime
	 * @param pattern
	 * @return
	 */
	public static ZonedDateTime parseDateToDefaultTimeZone(String date,String pattern)
	{
		try {
			LocalDate objLocalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
			ZonedDateTime objZonedDateTime = objLocalDate.atTime(0, 0).atZone(ZoneId.of(Tek_Properties.TIME_ZONE));
			return objZonedDateTime;
		}catch(Exception ex)
		{
			return null;
		}
	}

	/**
	 * Call this method like this:<br/>
	 * parseDateTimeToUTCTimeZone("April 23, 2019 | 0:00 am","MMMM dd, yyyy '|' h:mm a")
	 * @param dateTime
	 * @param pattern
	 * @return
	 */
	public static ZonedDateTime parseDateTimeToUTCTimeZone(String dateTime,String pattern)
	{
		try {
			dateTime = dateTime.replace("AM", "am");
			dateTime = dateTime.replace("PM", "pm");
			LocalDateTime objLocalDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
			ZonedDateTime objZonedDateTime = objLocalDateTime.atZone(ZoneId.of(ZoneOffset.UTC.getId()));
			return objZonedDateTime;
		}catch(Exception ex)
		{
			return null;
		}
	}

	public static String formatZonedDateTime(ZonedDateTime objZonedDateTime,String pattern)
	{		
		String dateTime = null;
		try {
			dateTime = objZonedDateTime.format(DateTimeFormatter.ofPattern(pattern));
		}catch(Exception ex)
		{}
		return dateTime;
	}

	public static ZonedDateTime convertApiUTCtoUTCTimeZone(String dateTimeInAPI) {
		try {
			Instant instant = Instant.parse(dateTimeInAPI);
			LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));        
			ZoneId toZoneId = ZoneId.of("UTC");
			ZonedDateTime zonedDateTime = instant.atZone(toZoneId);
			return zonedDateTime;
		}catch(Exception ex)
		{
			return null;
		}
	}

	public static LocalDateTime convertCalendarToLocalDateTime(Calendar calendar)
	{
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DATE);
		int year = calendar.get(Calendar.YEAR);
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int sec = calendar.get(Calendar.SECOND);
		String am_pm = "";
		if(calendar.get(Calendar.AM_PM) == 0)
			am_pm = "am";
		else
			am_pm = "pm";
		LocalDateTime objLocalDateTime = LocalDateTime.parse(date + "-" + (month+1) + "-" + year + " | " + hour + ":" + minute + ":" + sec + " " + am_pm , DateTimeFormatter.ofPattern("d-M-yyyy | h:m:s a"));
		return objLocalDateTime;
	}

	public static ZonedDateTime tagLocalDateTimeToUTC(LocalDateTime ldt)
	{
		ZonedDateTime objZonedDateTime = ldt.atZone(ZoneId.of(ZoneOffset.UTC.getId()));
		return objZonedDateTime;
	}

	public static ZonedDateTime tagLocalDateTimeToDefaultTimeZone(LocalDateTime ldt)
	{
		ZonedDateTime objZonedDateTime = ldt.atZone(ZoneId.of(Tek_Properties.TIME_ZONE));
		return objZonedDateTime;
	}

	public static long convertZonedDateTimeToEpoch(ZonedDateTime zdt)
	{
		long epoch = 0;
		try {
			epoch =  Instant.from(zdt).toEpochMilli();
		}catch(Exception ex)
		{}
		return epoch;
	}

	public static ZonedDateTime convertEpochToZonedDateTime(Long epochMillis)
	{
		ZonedDateTime zdt = null;
		try
		{
			zdt = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.of(Tek_Properties.TIME_ZONE));
		}catch(Exception ex)
		{}
		return zdt;
	}

	/**
	 * Converts the ZonedDateTime to start of the day
	 * eg: zdt = 5-July-2019 02:30:00 pm
	 * will get changed to
	 * 5-July-2019 00:00:00 pm
	 * @param zdt
	 * @return
	 */
	public static ZonedDateTime convertToStartOfTheDay(ZonedDateTime zdt)
	{
		return zdt.toLocalDate().atStartOfDay(ZoneId.of("UTC"));
	}


	public static long calculateDifference(long time1,long time2,ChronoUnit chronounit)
	{
		try {
			ZonedDateTime zdt1 = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time1), ZoneId.of(Tek_Properties.TIME_ZONE));
			ZonedDateTime zdt2 = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time2), ZoneId.of(Tek_Properties.TIME_ZONE));
			return chronounit.between(zdt1, zdt2);
		}catch(Exception ex)
		{}
		return -1;
	}


	/**
	 * 
	 * @author kirankumar
	 * @throws ParseException 
	 * @createdDate 07-Nov-2019
	 * @modifiedBy   1572366934000
	 * @modifiedDate
	 * @description returns a set of Epoch Seconds(in UTC) from 2 days prior to the current date and to the required duration
	 */

	//	public static List<Long> getEpochDateRange(int Duration,ChronoUnit chronoUnit) throws ParseException {
	//
	//		Date today = Calendar.getInstance().getTime();
	//
	//		SimpleDateFormat simpledateformat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
	//
	//		String currentTime = simpledateformat.format(today);
	//
	//		List<Long> UTCDuration = new ArrayList<Long>();
	//
	//		Date date = simpledateformat.parse(currentTime);
	//
	//		Instant instant = date.toInstant();
	//		 	
	//		instant = date.toInstant().minus(Duration, chronoUnit);
	//		
	//		long epochStartTime = instant.getEpochSecond()*1000;
	//		
	//		logInfo("From Date","YY MM DD HH:mm:ss.SSS zzz format -"+date.toInstant().minus(Duration, chronoUnit),"EpochDate"+epochStartTime);
	//		
	//		UTCDuration.add(epochStartTime);
	//	
	//		instant = date.toInstant();
	//	
	//		long epochStopTime = instant.minus(2, chronoUnit).getEpochSecond() * 1000;
	//		
	//		logInfo("To Date","YY MM DD HH:mm:ss.SSS zzz format -"+instant.minus(2, chronoUnit).toString(),"EpochDate"+epochStopTime);
	//		
	//		UTCDuration.add(epochStopTime);
	//		
	//		return UTCDuration;
	//	}


	public static List<Long> getEpochDateRange(int Duration,ChronoUnit chronoUnit) throws ParseException {

		Date today = Calendar.getInstance().getTime();

		SimpleDateFormat simpledateformat = new SimpleDateFormat("MMM dd yyyy 00:00:00.000 zzz");

		String currentTime = simpledateformat.format(today);

		List<Long> UTCDuration = new ArrayList<Long>();

		Date date = simpledateformat.parse(currentTime);

		Instant instant = date.toInstant();

		instant = date.toInstant().minus(Duration, chronoUnit);

		long epochStartTime = instant.getEpochSecond()*1000;

		logInfo("From Date","YY MM DD HH:mm:ss.SSS zzz format -"+date.toInstant().minus(Duration, chronoUnit),"EpochDate"+epochStartTime);

		UTCDuration.add(epochStartTime);

		instant = date.toInstant();

		long epochStopTime = ((instant.minus(2, chronoUnit).getEpochSecond()+86399) * 1000);

		logInfo("To Date","YY MM DD HH:mm:ss.SSS zzz format -"+instant.minus(2, chronoUnit).toString(),"EpochDate"+epochStopTime);

		UTCDuration.add(epochStopTime);

		return UTCDuration;
	}

	/**
	 * 
	 * @author kirankumar
	 * @createdDate 14-Nov-2019
	 * @modifiedBy
	 * @modifiedDate
	 * @description returns a set of Epoch Seconds(in UTC) of the same day
	 */
	public static List<Long> getEpochDateRangeOfASameDay(int Duration,ChronoUnit chronoUnit) throws ParseException {

		Date today = Calendar.getInstance().getTime();

		SimpleDateFormat simpledateformat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");

		String currentTime = simpledateformat.format(today);

		List<Long> UTCDuration = new ArrayList<Long>();

		Date date = simpledateformat.parse(currentTime);

		Instant instant = date.toInstant();

		instant = date.toInstant().minus(Duration, chronoUnit);

		long epochStartTime = instant.getEpochSecond()*1000;

		logInfo("From Date","YY MM DD HH:mm:ss.SSS zzz format -"+date.toInstant().minus(Duration, chronoUnit),"EpochDate"+epochStartTime);

		UTCDuration.add(epochStartTime);

		/** Adding 10 Seconds extra for the Start Time **/

		UTCDuration.add(epochStartTime+10);

		return UTCDuration;
	}

	public static String getDateFromEpochTme(Long EpochTime) {

		EpochTime = EpochTime/1000;
		String str = new SimpleDateFormat("yyyy-MM-dd").format(EpochTime * 1000);
		return str;	
	}

	public Long getepochofparticulardate(String date) throws ParseException
	{
		//String dateString = "2020-01-02 08:00:00";
		String dateString = date;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date1 = df.parse(dateString);
		long time = date1.getTime();

		System.out.println(time);
		return time;
	}
	/**
	 * 
	 * @author kirankumar
	 * @createdDate 29-Jan-2020
	 * @modifiedBy Jatin
	 * @modifiedDate 16-April-2020
	 * @description getCurrentEpochTime
	 */
	public Long getCurrentEpochTime() throws ParseException {

		Date today = Calendar.getInstance().getTime();

		SimpleDateFormat simpledateformat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");

		String currentTime = simpledateformat.format(today);

		Date date = simpledateformat.parse(currentTime);

		Instant instant = date.toInstant();

		long epochStartTime = instant.getEpochSecond()*1000;
		return epochStartTime;
	}
	
	public String getCurrentDate() {

		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy ");
		dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));  
		Calendar cal = Calendar.getInstance();
		String currentdate = dateFormat.format(cal.getTime());
		return currentdate;
	}
	
	/**
	 * 
	* @author -kirankumar
	* @createdDate - 13-May-2020
	* @modifiedBy - 
	* @modifiedDate - 
	* @description - To get in this format -> May 13, 2020 5:11 AM
	 */
	public String getCurrentDateIn_Month_DDYYYY_TIME() {

		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy h:mm a");
		dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));  
		Calendar cal = Calendar.getInstance();
		String currentdate = dateFormat.format(cal.getTime());
		return currentdate;
	}
	
	
	public String getCurrentDateWithOutLeading0InDay() {

		DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy h:mm a");
		dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));  
		Calendar cal = Calendar.getInstance();
		String currentdate = dateFormat.format(cal.getTime());
		return currentdate;
	}
	
	
}
