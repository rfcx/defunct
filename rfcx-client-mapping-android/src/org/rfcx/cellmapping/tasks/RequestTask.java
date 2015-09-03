package org.rfcx.cellmapping.tasks;

/**
* @copyright Urucas
* @license   Copyright (C) 2013. All rights reserved
* @version   Release: 1.0.0
* @link       http://urucas.com
* @developers Bruno Alassia, Pamela Prosperi
*/

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class RequestTask extends AsyncTask<String, String, String> {

	protected RequestTaskHandler rsh;
	protected JSONRequestTaskHandler jrsh;
	private ArrayList<NameValuePair> headers;
	private ArrayList<NameValuePair> params;
	
	public RequestTask(RequestTaskHandler _rsh) {
		super();
		rsh = _rsh;
	}
	
	public RequestTask(JSONRequestTaskHandler _jrsh) {
		super();
		jrsh = _jrsh;
	}

	public RequestTask() {
		super();
	}
	
	public RequestTask addHeader(String name, String value) {
		if (null == headers) {
			headers = new ArrayList<NameValuePair>();
		}
		headers.add(new BasicNameValuePair(name, value));
		return this;
	}
	
	public RequestTask addParam(String name, String value) {
		if (null == params) {
			params = new ArrayList<NameValuePair>();
		}
		params.add(new BasicNameValuePair(name, value));
		return this;
	}
	
	public ArrayList<NameValuePair> getHeaders() {
		if (null == headers) {
			headers = new ArrayList<NameValuePair>();
		}
		return headers;
	}
	
	public ArrayList<NameValuePair> getParams() {
		if (null == params) {
			params = new ArrayList<NameValuePair>();
		}
		return params;
	}

	@Override
	protected String doInBackground(String... uri) {
		
		HttpClient httpclient = new DefaultHttpClient();
		
		// add request params
		StringBuffer url = new StringBuffer(uri[0])
			.append("?");
		//	.append(URLEncodedUtils.format(getParams(), "UTF-8"));

		//HttpGet _get = new HttpGet(url.toString());

        HttpPost _post = new HttpPost(url.toString());

        Log.i("url---->", url.toString());

        try {
            _post.setEntity(new UrlEncodedFormEntity(getParams()));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // add request headers
		ArrayList<NameValuePair> _headers = getHeaders();
		for (NameValuePair pair : _headers) {
			_post.addHeader(pair.getName(), pair.getValue());
		}
		
		HttpResponse response;
		String responseString = null;
		// execute
		try {
			response = httpclient.execute(_post);
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				responseString = out.toString();
				
				if(isCancelled()) {
					rsh.onError("Task cancel");
				}
				
			} else{
				//Closes the connection.
				response.getEntity().getContent().close();
                Log.i("error", statusLine.getReasonPhrase());
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			//rsh.onError(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			//rsh.onError(e.getMessage());		
		}
		return responseString;
	}

	@Override
	protected void onPostExecute(String result) {
		Log.i("api result", result);
		super.onPostExecute(result);
		rsh.onSuccess(result);
	}
}
