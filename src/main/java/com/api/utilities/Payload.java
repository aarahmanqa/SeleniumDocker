package com.api.utilities;

import io.restassured.response.Response;

import java.util.HashMap;

public class Payload {
	public String baseUrl;
	public String reqrelateiveURl;
	public HashMap<String,String> reqparameter = new HashMap<String, String>();
	public HashMap<String,String> reqHeaders = new HashMap<String, String>();
	public String reqmethod;
	public int reqStatus;
	public String reqbody;
	public String resHeaders;
	public String resStatus;
	public int resStatusCode;
	public String resStrbody;
	public String access_token;
	public Response response;

	public static final String GET="GET";
	public static final String PUT="PUT";
	public static final String POST="POST";
	public static final String DELETE="DELETE";
}
