package com.temenos.demo.prospect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

public class ValidateAddress {

	/**
	 * Takes a ',' separates list of name=value pairs, uses these to validate address. 
	 * @param in
	 * @return
	 */
	public String validate(String in) {
		GetMethod getMethod = null;
		try {
			// GET request to address resource
			HttpClient client = new HttpClient();
			client.getParams().setSoTimeout(60000);;
			String uri = "http://dawa.aws.dk/adresser";
			getMethod = new GetMethod(uri);
			List<NameValuePair> queryParams = new ArrayList<NameValuePair>();
			String params[] = in.split(",");
			for (int i = 0; i < params.length; i++) {
				if (params[i].contains("=")) {
					String[] nv = params[i].split("=");
					if (nv.length == 2) {
						NameValuePair nvpair = new NameValuePair(nv[0], nv[1]);
						queryParams.add(nvpair);
					}
				}
			}
			if (queryParams.size() == 0)
				return "OK";
			getMethod.setQueryString(queryParams.toArray(new NameValuePair[queryParams.size()]));
			System.out.println(getMethod.getURI().toString());
			client.executeMethod(getMethod);
			
	        // read the response
	        String str = getMethod.getResponseBodyAsString();
	        System.out.println(getMethod.getStatusLine());
	        return str;
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (getMethod != null) {
				getMethod.releaseConnection();
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(new ValidateAddress().validate("vejnavn=F�gangen,husnr=1,etage=1,d�r=13,postnr=4180"));
	}
 	
}
