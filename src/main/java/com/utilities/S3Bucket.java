package com.utilities;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;
import java.time.ZonedDateTime;

public class S3Bucket
{
	// credentials object identifying user for authentication
	// user must have AWSConnector and AmazonS3FullAccess for
	// this example to work

	@SuppressWarnings({ "deprecation", "deprecation" })
	public void downloadAppFromS3(String env, String branchName) 
	{
		for(int counter = 0; counter < 5; counter ++) {
			try
			{
				AWSCredentials credentials = new BasicAWSCredentials("AKIARWHD4LUYUFF2YRNS", "6vgRtbgFfrUcn54/dawCHxCHtZqGGcLg9rQF2N9S");
				AmazonS3 s3client = AmazonS3ClientBuilder
						.standard()
						.withCredentials(new AWSStaticCredentialsProvider(credentials))
						.withRegion(Regions.US_WEST_1)
						.build();
				File appFolder = null;
				File appFile = null;

				if(env.equalsIgnoreCase("test"))
					env = "tst";				
				ZonedDateTime zdtStartTime = ZonedDateTime.now();
				switch(TLDriverFactory.mobile_app)
				{
				case SALES:

					appFolder = new File(Tek_Properties.currentRunResults + "/ARCMobile");
					if(! appFolder.exists())
					{
						appFolder.mkdirs();
						appFile = new File(appFolder.getAbsolutePath()+"/ARCMobile.ipa");
						System.out.println("Downloading ARCMobile.ipa...");				
						System.out.println("https://xyz-tekion-cdms-global.s3-us-west-1.amazonaws.com/cdms-mobile/2.0/ARCMobile/IpaBuilds/"+env+"/"+branchName+"/latest/ARCMobile.ipa");
						ObjectMetadata obj=s3client.getObject(new GetObjectRequest("xyz-tekion-cdms-global","cdms-mobile/2.0/ARCMobile/IpaBuilds/"+env+"/"+ branchName +"/latest/ARCMobile.ipa"),appFile);
						//System.out.println("https://s3-us-west-1.amazonaws.com/xyz-tekion-cdms-global/cdms-mobile/release/IpaBuilds/"+env+"/"+branchName+"/latest/ARCMobile.ipa");
						//ObjectMetadata obj=s3client.getObject(new GetObjectRequest("xyz-tekion-cdms-global","cdms-mobile/release/IpaBuilds/"+env+"/"+ branchName +"/latest/ARCMobile.ipa"),appFile);
						Tek_Properties.appPath = appFile.getAbsolutePath();
					}
					break;

				case CDMS:
					appFolder = new File(Tek_Properties.currentRunResults + "/cdms_app");
					if(! appFolder.exists())
					{
						appFolder.mkdirs();						
					}
					appFile = new File(appFolder.getAbsolutePath()+"/tekion_cdms_mobile.ipa");
					System.out.println("Downloading tekion_cdms_mobile.ipa...");
					System.out.println("https://s3-us-west-1.amazonaws.com//xyz-tekion-cdms-global/cdms-mobile/release/IpaBuilds/"+env+"/"+ branchName +"/latest/tekion_cdms_mobile.ipa");
					ObjectMetadata obj=s3client.getObject(new GetObjectRequest("xyz-tekion-cdms-global","cdms-mobile/release/IpaBuilds/"+env+"/"+ branchName +"/latest/tekion_cdms_mobile.ipa"),appFile);
					Tek_Properties.appPath = appFile.getAbsolutePath();
					break;
				}
				ZonedDateTime zdtEndTime = ZonedDateTime.now();
				System.out.println("Download completed in " + new Tek_Properties().calculateDuration(zdtStartTime, zdtEndTime));

				if(Tek_Properties.appPath == null)
				{
					System.out.println("Unable to download ipa file");
					BaseFunctions.logFailedWithOutScreenShot("Unable to download ipa file", "", "", "");
				}
				else {
					break;
				}
			}catch(SdkClientException sce) {
				//The download code will run again
			}
			catch(Throwable t)
			{
				t.printStackTrace();
				break;
			}
		}

	}

	@SuppressWarnings({ "deprecation", "deprecation" })
	public static void main(String[] args) {
		// credentials object identifying user for authentication
		// user must have AWSConnector and AmazonS3FullAccess for
		// this example to work
		AWSCredentials credentials = new BasicAWSCredentials("AKIARWHD4LUY7ZNPPLG7", "7fEl/43q3WSk+rCDMuDkpbbCTNqw0iwqdCAouBoI");
		//try {
		AmazonS3 s3client = AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.US_WEST_1)
				.build();
		File localFile = new File("/Users/ahamedabdulrahman/downloads/sales_app/Sales.ipa");
		ObjectMetadata obj=s3client.getObject(new GetObjectRequest("xyz-tekion-cdms-global","cdms-mobile/2.0/sales/IpaBuilds/stage/develop/latest/Sales.ipa"),localFile);
		System.out.println("Completed");
	}
}