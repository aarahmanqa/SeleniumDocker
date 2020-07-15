package com.api.utilities;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.utilities.BaseFunctions;
import com.utilities.Mobile_BaseFunctions;
import com.utilities.TLDriverFactory;
import com.utilities.Tek_Properties;
import io.restassured.RestAssured;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class Common extends BaseFunctions {

    String slotDateTime;
    String slotTime;
    String date1;
    String day1;
    int hour1;
    int min1;
    int pstTime;
    String chosenTime1;
    static int exceptionIndex = 1;
    public static int counter = 0;

    public static ThreadLocal<Payload> payLoadObj = new ThreadLocal<Payload>();

    public static Payload getPayloadObj() {
        return payLoadObj.get();
    }

    public static void setPayloadObj(Payload payload) {
        payLoadObj.set(payload);
    }

    /**
     * isIndApiValidation - this is a flag that describes whether this is individual api validation.
     * In case of Individual api validation, the status code validation will not be done.
     * ie. Usually, if the status code came as value other than 200, it will be marked as failure.
     * If  isIndApiValidation flag is turned ON, this status code validation won't be done.
     */
    public static ThreadLocal<Boolean> isIndApiValidation = new ThreadLocal<Boolean>();

    public static Boolean getIsIndApiValidation() {
        Boolean status = false;
        try {
            status = isIndApiValidation.get();
            if (status == null)
                status = false;
        } catch (Throwable t) {
        }
        return status;
    }

    public static void setIsIndApiValidation(Boolean status) {
        isIndApiValidation.set(status);
    }

    /**
     * showInReport would be null by default.
     * So, if it is true or null, then api results will be shown in report.
     * if it is false, api results won't be shown.
     */
    public static ThreadLocal<Boolean> apiShowInReport = new ThreadLocal<Boolean>();

    public static Boolean getApiShowInReport() {
        return apiShowInReport.get() == null ? true : apiShowInReport.get();
    }

    public static void setApiShowInReport(Boolean b) {
        apiShowInReport.set(b);
    }

    public void convert_UST_to_PST() {
        slotTime = slotDateTime.substring(11, 19);
        System.out.println(slotTime);
        date1 = slotDateTime.substring(0, 10);
        ;
        day1 = slotDateTime.substring(8, 10);
		/*if(day1.substring(0,1).equals("0")){
			day1 = day1.substring(1);
		}*/

        hour1 = Integer.parseInt(slotTime.split(":")[0]);
        min1 = Integer.parseInt(slotTime.split(":")[1]);

        pstTime = hour1 - 8;
        String pstTime_string = String.format("%02d", Integer.parseInt(pstTime + ""));
        String min1_string = String.format("%02d", Integer.parseInt(min1 + ""));

        chosenTime1 = pstTime_string + ":" + min1_string;
        System.out.println("Expected Slot: " + chosenTime1);
    }


    private static final String SCENARIO_OBJECT = "scenario.obj";

    //
    public static <T> String objectToJSONString(T object) {
        String json = "";
        ObjectMapper mapperObj = new ObjectMapper();
        mapperObj.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {
            // get object as a json string
            json = mapperObj.writeValueAsString(object);
        } catch (Throwable e) {
            logException(e, true);
            e.printStackTrace();
        }

        //		System.out.println("Request Body "+ json);
        return json;

    }

    public static <T> T jsonStringToObject(String resStrbody, Class<T> obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, getIsIndApiValidation());
        try {
            return mapper.readValue(new StringReader(resStrbody), obj);
        } catch (Throwable e) {
            logException(e, true);
        }
        return null;
    }

    public static <T> Object jsonFileToObject(String fileName, Class<T> obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, getIsIndApiValidation());

        try {
            File file = new File(ResourceHelper.getResourcePath(fileName));
            return mapper.readValue(file, obj);
        } catch (Throwable e) {
            logException(e, true);
        }
        return null;
    }

    public static String readFile(String filename) {
        String result = "";
        try {
            File file = new File("/Users/naveen/git/apiautomation/src/test/java/framework/runner/stepdefinition/file.json");
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static String getCustomerType(int index) {

        if (index == 0)
            return "PERSONAL";
        else if (index == 1)
            return "BUSINESS";

        return "CustomerType for the (" + index + ") is not avilabel";
    }

    public static void writeDataIntoReport(Payload payload) {

        try {
            if (getApiShowInReport()) {
                String apiName = payload.getClass().getName().substring(
                        payload.getClass().getName().lastIndexOf(".") + 1,
                        payload.getClass().getName().length());

                int statusCode = payload.resStatusCode;

                String reqData = "Method : " + payload.reqmethod + "\n" +
                        "URL : " + RestAssured.baseURI + payload.reqrelateiveURl + "\n"
                        + "Parameters : " + payload.reqparameter + "\n"
                        + "Req Body : " + getPrettyJsonString(payload.reqbody) + "\n\n"
                        + "Req Headers : " + payload.reqHeaders + "\n\n"
                        + "cURL : \n" + createCurl(payload);


                String reqLink = "";
                apiName = apiName.replace(" ", "_");
                counter++;

                File apiFilePath = TLDriverFactory.getAPIFolder();
                if (reqData != null && !reqData.isEmpty()) {
                    String requestFileName = apiName + counter + "_request.txt";
                    String requestFilePathWithName = apiFilePath + "/" + requestFileName;

                    File requestFile = new File(requestFilePathWithName);
                    FileUtils.writeStringToFile(requestFile, reqData, "UTF-8");
                    String reqName = "Request";
                    if (payload.reqmethod != null)
                        reqName = payload.reqmethod + " " + reqName;
                    reqLink = getLink(requestFileName, reqName);
                }

                if (payload.resStrbody != null) {
                    String responseFileName = apiName + counter + "_response.txt";
                    String responseFilePathWithName = apiFilePath + "/" + responseFileName;
                    File responseFile = new File(responseFilePathWithName);
                    FileUtils.writeStringToFile(responseFile, payload.resStrbody, "UTF-8");
                    logInfoWithOutScreenShot("API - Get Response data", apiName, reqLink, getLink(responseFileName, " Response"));

                } else {
                    logFailedWithOutScreenShot(apiName + " API", reqLink, "Response data", "Response data is not fetched.");
                }

                //In individual api validations, the status codes are validated separately. So no need of this generic validation.
                //As ON and OFF switch, we have made isIndApiValidation boolean.
                //isIndApiValidation - false = Perform status code validation here (not individual api test case)
                //isIndApiValidation - true = Do not perform status code validation here. Individual api test case.
                if (getIsIndApiValidation() == false) {
                    if (statusCode == 200)
                        setAPIDataInReport(apiName + " - Passed");
                    else if (statusCode == 400 && IgnoreApiStatus.status400.contains(apiName))
                        logWarningWithOutScreenShot("API - Get Response data", apiName, reqLink, "Status Code: " + statusCode);
                    else {
                        //logFailedWithOutScreenShot("API - Get Response data", apiName, reqLink,"Status Code: "+statusCode);
                        setAPIDataInReport(apiName + " - Failed");
                    }
                } else {
                    setAPIDataInReport(apiName + " - Passed");
                }
            }
        } catch (Throwable e) {
            logException(e);
        }
    }

    /**
     * @param logAs                - The valid values are : passed, failed, info, warning, message.
     * @param testDescription      - Shown as Test Description in the report.
     * @param inputString          - Shown as input data in the report.
     * @param expectedResultHeader - This will be shown in the report as the link's name.
     * @param expectedResultData   - This will be made as a separate file.
     * @param actualResultHeader   - This will be shown in the report as the link's name.
     * @param actualResultData     - This will be made as a separate file.
     *                             This method can be used to write large contents in a separate file and add that file's link in the report.
     *                             Sample calls:
     *                             Common.writeDataIntoReport("info","Print api response link content", "", "", "", "Link Contents", text);
     *                             Common.writeDataIntoReport("passed","Print api response link content", "", "Expected Contents", expectedData, "Link Contents", text);
     * @author ahamedabdulrahman
     * @createdDate 28 Jan 2020
     * @modifiedBy
     * @modifiedDate
     * @description writeDataIntoReport
     */
    public static void writeDataIntoReport(String logAs, String testDescription, String inputString, String expectedResultHeader, String expectedResultData, String actualResultHeader, String actualResultData, boolean screenshotRequired) {
        try {

            expectedResultHeader = nvl(expectedResultHeader, "").replaceAll(" ", "_");
            actualResultHeader = nvl(actualResultHeader, "").replaceAll(" ", "_");
            File apiFilePath = TLDriverFactory.getAPIFolder();
            String reqLink = "", resLink = "";
            if (expectedResultData != null && !expectedResultData.isEmpty()) {
                String requestFileName = expectedResultHeader + counter + "_request.txt";
                String requestFilePathWithName = apiFilePath + "/" + requestFileName;

                File requestFile = new File(requestFilePathWithName);
                FileUtils.writeStringToFile(requestFile, expectedResultData, "UTF-8");
                reqLink = getLink(requestFileName, expectedResultHeader);
            }

            if (actualResultData != null && !actualResultData.isEmpty()) {
                String responseFileName = actualResultHeader + counter + "_response.txt";
                String responseFilePathWithName = apiFilePath + "/" + responseFileName;

                File responseFile = new File(responseFilePathWithName);
                FileUtils.writeStringToFile(responseFile, actualResultData, "UTF-8");
                resLink = getLink(responseFileName, actualResultHeader);
            }
            counter++;
            new Tek_Properties().writeToHtml(testDescription, inputString, reqLink, resLink, logAs,screenshotRequired);

        } catch (Throwable t) {
            logException(t);
        }
    }

    /**
     * @param
     * @author ahamedabdulrahman
     * @createdDate 29 Jan 2020
     * @modifiedBy
     * @modifiedDate
     * @description writeDataIntoReport
     */
    public static void writeDownloadedDataIntoReport(String logAs, String testDescription, String inputString, String expectedResultHeader, String expectedResultFileName, String actualResultHeader, String actualResultFileName) {
        try {

            expectedResultHeader = nvl(expectedResultHeader, "").replaceAll(" ", "_");
            actualResultHeader = nvl(actualResultHeader, "").replaceAll(" ", "_");
            String reqLink = "", resLink = "";
            if (expectedResultFileName != null) {
                reqLink = getDownloadLink(expectedResultHeader, expectedResultFileName);
            }

            if (actualResultFileName != null) {
                resLink = getDownloadLink(actualResultHeader, actualResultFileName);
            }
            counter++;
            new Tek_Properties().writeToHtml(testDescription, inputString, reqLink, resLink, logAs);

        } catch (Throwable t) {
            logException(t);
        }
    }

    public static String getDownloadLink(String fileDesc, String fileName) {
        try {
            return "<u><font color='blue'><a target=_blank href=downloads/" + fileName + ">" + fileDesc + "</a></font>";
        } catch (Throwable t) {
            logException(t);
        }
        return null;
    }

    public static void writeExceptionIntoReport(String exceptionMessage) {
        try {
            String exceptionName = "Exception_" + exceptionIndex;
            exceptionIndex++;
            File file = new File("API_Files/" + TLDriverFactory.getTestcaseName() + "/" + exceptionName + ".txt");
            if (!file.exists()) {
                file.mkdir();
            }
            FileUtils.writeStringToFile(file, exceptionMessage, "UTF-8");
            logFailed("Exception Thrown", getLink(file.getAbsolutePath(), "Exception Stack Trace"));
        } catch (Exception e) {
            if (TLDriverFactory.isMobileDevice())
                Mobile_BaseFunctions.logException(e);
            else
                logException(e);
        }
    }

    public static String getLink(String source, String sourceName) {
        return "<u><font color='blue'><a target=_blank href=API_Files/" + source + ">" + sourceName + "</a></font>";
    }

    /**
     * List 1 should contains List2 or vice versa
     */
    public static boolean sortListAndCompare(List<String> list1, List<String> list2, String desc) {

        try {

            Collections.sort(list1);
            Collections.sort(list2);

            System.out.println(list1);
            System.out.println(list2);
            ArrayList<String> nonMatchedList = new ArrayList<String>();
            for (int i = 0; i < list1.size(); i++) {
                boolean match = false;
                for (int j = 0; j < list2.size(); j++) {
                    if (list1.get(i).trim().contains(list2.get(j).replace("...", "").trim()) ||
                            list2.get(j).trim().contains(list1.get(i).replace("...", "").trim())) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    nonMatchedList.add(list1.get(i));
                }
            }
            if (nonMatchedList.isEmpty()) {
                logPassed("Compare Lists of " + desc, "" + list1, "" + list2);
            } else {
                logFailed("Compare Lists of " + desc, "" + list1, "" + list2);
            }

        } catch (Exception e) {
            logException(e);
        }

        return false;
    }


    /**
     * List 1 should contains List2 or vice versa
     */
    public static boolean compareLists(List<String> list1, List<String> list2, String desc) {

        try {
            System.out.println(list1);
            System.out.println(list2);
            ArrayList<String> nonMatchedList = new ArrayList<String>();
            for (int i = 0; i < list1.size(); i++) {
                System.out.println(list1.get(i).trim());
                System.out.println(list2.get(i).trim());
                System.out.println(list1.get(i).trim().contains(list2.get(i).trim()));
                System.out.println(list2.get(i).trim().contains(list1.get(i).trim()));
                if (!list1.get(i).trim().contains(list2.get(i).trim()) ||
                        !list2.get(i).trim().contains(list1.get(i).trim())) {
                    nonMatchedList.add(list1.get(i));
                }
            }
            if (nonMatchedList.isEmpty()) {
                logPassed("Compare Lists of " + desc, "" + list1, "" + list2);
            } else {
                logFailed("Compare Lists of " + desc, "" + list1, "" + list2);
                logFailed("Non matched list", nonMatchedList + "");
            }

        } catch (Exception e) {
            logException(e);
        }

        return false;
    }

    public static String getROStatus(float stsCode) {

        int statusCode = (int) stsCode;
        String status = null;
        System.out.println(stsCode);
        switch (statusCode) {
            case 0:
                status = "";
                break;
            case 1:
                status = "";
                break;
            case 2:
                status = "";
                break;
            case 200:
                status = "Unassigned";
                break;
        }
        return status;
    }

    public static String getTransportType(int code) {

        String status = null;
        switch (code) {
            case 0:
                status = "Self";
                break;
            case 1:
                status = "Wait";
                break;
            case 2:
                status = "Shuttle";
                break;
            case 3:
                status = "Loaner";
                break;
            case 4:
                status = "";
                break;
            case 5:
                status = "";
                break;
            case 6:
                status = "Valet";
                break;
        }
        return status;
    }

    public static String getCurrentDateTime(String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime ldt = LocalDateTime.now(ZoneId.of("UTC-7"));
        //	LocalDateTime ldt = LocalDateTime.now()
        System.out.println(df.format(ldt));
        return df.format(ldt);
    }

    public static String getUsername(String tech) {

        String username = null;
        // System.out.println(tech.toUpperCase().replaceAll(" ", ""));
        switch (tech.toUpperCase()) {

            case "AUTOMATION":
                username = getProperties().getProperty("USERNAME_TECHNICIAN");
                break;
            case "TECHNICIAN001":
                username = getProperties().getProperty("USERNAME_TECHNICIAN2");
                break;
            case "SANYAM AGRAWAL - TECH":
                username = getProperties().getProperty("USERNAME_TECHNICIAN3");
                break;
            case "TOM SULLIVAN":
                username = getProperties().getProperty("USERNAME_TECHNICIAN4");
                break;
            case "STEVEN B":
                username = getProperties().getProperty("USERNAME_TECHNICIAN5");
                break;
            case "TECHNICIAN":
                username = getProperties().getProperty("USERNAME_TECHNICIAN6");
                break;

        }
        return username;
    }

    public static String getPassword(String tech) {

        String password = null;
        switch (tech.toUpperCase()) {

            case "AUTOMATION":
                password = getProperties().getProperty("PASSWORD_TECHNICIAN");
                break;
            case "TECHNICIAN001":
                password = getProperties().getProperty("PASSWORD_TECHNICIAN2");
                break;
            case "SANYAM AGRAWAL - TECH":
                password = getProperties().getProperty("PASSWORD_TECHNICIAN3");
                break;
            case "TOM SULLIVAN":
                password = getProperties().getProperty("PASSWORD_TECHNICIAN4");
                break;
            case "STEVEN B":
                password = getProperties().getProperty("PASSWORD_TECHNICIAN5");
                break;
            case "TECHNICIAN":
                password = getProperties().getProperty("PASSWORD_TECHNICIAN6");
                break;

        }

        return password;
    }

    public static int getARandomNumber(int max) {
        return ThreadLocalRandom.current().nextInt(0, max);
    }

    public static void compareAndLog(Object C1, Object C2, String description) {
        if (C1 == null && C2 == null)
            logPassedWithOutScreenShot(description + " is null", "", "", "");
        else if (C1 == null || C2 == null)
            logFailedWithOutScreenShot(description + " is not matching", "", nvl(C1, "null").toString(), nvl(C2, "null").toString());
        else if (C1.toString().equalsIgnoreCase(C2.toString()))
            logPassedWithOutScreenShot(description + " is matching", "", C1.toString(), C2.toString());
        else
            logFailedWithOutScreenShot(description + " is not matching", "", C1.toString(), C2.toString());
    }

    public static void validateAdditionalProperties(Map<String, Object> map, String desc) {
        if (map.keySet().size() == 0)
            logPassedWithOutScreenShot("No unknown properties found in " + desc, "", "", "");
        else
            logFailedWithOutScreenShot("Unknown properties found in " + desc, "", "", map.keySet().toString());
    }

    public static void validateStatusCode(HashMap<String, JsonNode> map) {
        Payload payload = getPayloadObj();
        String expectedStatusCode = map.get("response").get("statusCode").asText();
        String actualStatusCode = payload.resStatusCode + "";
        Common.compareAndLog(expectedStatusCode, actualStatusCode, "Status Code");
    }

    public static String getPrettyJsonString(String json) {
        try {
            if (json != null && !json.isEmpty()) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonParser jp = new JsonParser();

                JsonElement je = jp.parse(json);
                return gson.toJson(je);
            }
        } catch (Exception e) {
            logWarning(e.getMessage());
        }
        return json;
    }

    public static String createCurl(Payload payload) {
        String text = "";
        try {
            text += "curl -X " + payload.reqmethod + " \\\n";

            for (String key : payload.reqHeaders.keySet()) {
                text += "-H '" + key + ": " + payload.reqHeaders.get(key) + "' \\\n";
            }


            if (payload.reqbody != null)
                text += "-d '" + getPrettyJsonString(payload.reqbody) + "' \\\n";

            //URL
            text += "'" + payload.baseUrl + payload.reqrelateiveURl;

            String parms = "";
            Iterator<String> iterator = payload.reqparameter.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                parms += key + "=" + payload.reqparameter.get(key);
                if (iterator.hasNext())
                    parms += "&";

                try {
                    parms = encode(parms);
                } catch (Throwable t) {
                }
            }

            if (parms.equals("") == false)
                text += "?" + parms;

            text += "' ";
        } catch (Throwable t) {
        }
        return text;
    }

    public static String encode(String parms) {
        try {
            parms = parms.replace(":", "%3A");
            return parms;
        } catch (Throwable t) {
        }
        return "";
    }

    /**
     * @param
     * @author ahamedabdulrahman
     * @createdDate 7 Apr 2020
     * @modifiedBy
     * @modifiedDate
     * @description savePageSource
     */
    public static void getPageSourceInReport() {
        try {
            String pageSource = TLDriverFactory.getTLDriver().getPageSource();
            writeDataIntoReport("info", "Page Source", "", "", "", "PageSourceFile", pageSource,true);
        } catch (Throwable t) {
            logException(t);
        }
    }

	/*public static void writeDataIntoReport(String responseString, String apiName)
	{ 
		writeDataIntoReport(null,null,responseString,apiName); 
	}

	public static void writeDataIntoReport(String reqMethod, String requestString,String responseString, String apiName) {

		try { 
			String reqLink = ""; apiName = apiName.replace(" ", "_"); counter++;


			File apiFilePath = TLDriverFactory.getAPIFolder(); if (requestString != null && !requestString.isEmpty()) { String requestFileName = apiName + counter + "_request.txt"; String requestFilePathWithName = apiFilePath + "/" +requestFileName;

			File requestFile = new File(requestFilePathWithName);
			FileUtils.writeStringToFile(requestFile, requestString, "UTF-8"); String
			reqName = "Request"; if (reqMethod != null) reqName = reqMethod + " " + reqName; reqLink = getLink(requestFileName, reqName); }

			if(responseString!=null) { String responseFileName = apiName+counter+"_response.txt"; 
			String responseFilePathWithName = apiFilePath + "/" + responseFileName; 
			File responseFile = new File(responseFilePathWithName); 
			FileUtils.writeStringToFile(responseFile, responseString,"UTF-8"); 
			logInfoWithOutScreenShot("API - Get Response data", apiName, reqLink, getLink(responseFileName, " Response"));

			} else { 
				logFailedWithOutScreenShot(apiName + " API", reqLink,
						"Response data", "Response data is not fetched."); 
			} 
		}

		catch(Throwable e) { 
			logException(e); 
		} 
	}*/


}
