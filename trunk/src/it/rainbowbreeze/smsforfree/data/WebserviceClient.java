package it.rainbowbreeze.smsforfree.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class WebserviceClient
{
	//---------- Ctors
	public WebserviceClient()
	{ }

	//---------- Private fields

	
	
	
	//---------- Public properties



	
	//---------- Public methods

	/**
	 * Send to the webservice a GET request
	 * 
	 * @param url
	 *            the Url of the webservice
	 *            
	 * @return the string returned from the webservice
	 *
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String requestGet(String url)
		throws ClientProtocolException, IOException
	{
		// reference here:
		// http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/

		HttpGet httpGet;
		String result;

		result = "";

		// prepare a request object
		httpGet = new HttpGet(url);

		// Get hold of the response entity
		result = executeResponse(httpGet);

		return result;
	}


	/**
	 * Send to the webservice a POST request
	 * 
	 * @param url the Url of the webservice
	 * @param parameters the data to pass in post via parameters
	 * @param headers the data to pass in post via headers
	 *
	 * @return the string returned from the webservice
	 * 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String requestPost(
			String url,
			HashMap<String, String> headers,
			HashMap<String, String> parameters
		)
		throws ClientProtocolException, IOException
	{
		// reference here:
		// http://www.androidsnippets.org/snippets/36/
		// another reference, but with different method, here
		// http://www.anddev.org/doing_http_post_with_android-t492.html

		//prepare the post client
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

		//create new list of values to pass as post data
		if (headers != null) {
			Iterator<String> it = headers.keySet().iterator();
			String k, v;
			while (it.hasNext()) {
				k = it.next();
				v = headers.get(k);
				httpPost.setHeader(k, v);
			}
		}

		//create new list of values to pass as post data
		if (parameters != null) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			Iterator<String> it = parameters.keySet().iterator();
			String k, v;
			while (it.hasNext()) {
				k = it.next();
				v = parameters.get(k);
				nameValuePairs.add(new BasicNameValuePair(k, v));
			}

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}

		//execute the request
		String result;

		// Execute HTTP Post Request
		result = executeResponse(httpPost);

		return result;
		
		//update=2 login ok!
	}
	
	
	public String requestPostQuick(
			String urlAddress,
			HashMap<String, String> postValues
		)
		throws MalformedURLException, IOException
	{
		//http://blog.dahanne.net/2009/08/16/how-to-access-http-resources-from-android/
		
		StringBuilder data = new StringBuilder();
		
		// Construct data
		if (postValues != null)
		{
			Iterator<String> it = postValues.keySet().iterator();
			String k, v;
			while (it.hasNext()) {
				k = it.next();
				v = postValues.get(k);
				
				data.append(URLEncoder.encode(k, HTTP.UTF_8));
				data.append("=");
 				//data.append(URLEncoder.encode(v, HTTP.UTF_8));
 				data.append(v);
				if (it.hasNext()) data.append("&");
			}
		}		

		// Send data
		URL url = new URL(urlAddress);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		//conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
//		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
//		conn.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");
		conn.setRequestProperty("Content-type", "text/plain");
		
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data.toString());
		wr.flush();
		
		// Get the response
		String result = convertStreamToString(conn.getInputStream());
		//Field summary: http://java.sun.com/j2se/1.4.2/docs/api/java/net/HttpURLConnection.html
		int response = conn.getResponseCode();
		wr.close();
		
		return result;
		
//		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		
//		String line;
//		while ((line = rd.readLine()) != null)
//		{
//			// Process line...
//			
//		}
//		rd.close(); 		
	}





	//---------- Private methods

	/**
	 * Execute the GET or POST request
	 * 
	 */
	protected String executeResponse(HttpUriRequest httpRequest)
		throws ClientProtocolException, IOException
	{
		HttpClient httpClient;
		HttpResponse response;
		String result;

		// create the client
		httpClient = new DefaultHttpClient();

		result = "";
	
		response = httpClient.execute(httpRequest);
		// Log.i("XXXX",response.getStatusLine().toString());

		// Get hold of the response entity
		HttpEntity entity = response.getEntity();

		//TODO
		// If the response does not enclose an entity, there is no need
		// to worry about connection release
		if (entity != null) {
			InputStream instream = entity.getContent();
			result = convertStreamToString(instream);
		}

		return result;
	}

	
	/**
	 * Convert the response stream of an http request to a string
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	protected static String convertStreamToString(InputStream is)
		throws IOException
	{
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		line = reader.readLine();
		while (line != null) {
			sb.append(line);
			line = reader.readLine();
			if (line != null) sb.append("\n");
		}
		is.close();

		return sb.toString();
	}

}
