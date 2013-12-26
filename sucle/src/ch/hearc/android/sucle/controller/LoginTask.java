package ch.hearc.android.sucle.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.Sucle;
import ch.hearc.android.sucle.WebServicesInfo;
import ch.hearc.android.sucle.R.string;
import ch.hearc.android.sucle.WebServicesInfo.JSONKey;

import android.os.AsyncTask;
import android.util.Log;

public class LoginTask extends AsyncTask<String, Void, Void>
{
	public interface LoginListener
	{
		public void onLogin();
	}
	
	private String error = null;
	private LoginListener listener;
	
	public LoginTask(LoginListener listener)
	{
		this.listener = listener;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if(error != null)
			MessageNotification.basicNotification(Sucle.getAppContext(), error);
		else
			listener.onLogin();
	} 
	
	@Override
	protected Void doInBackground(String... params) {
		if(params.length != 6)
		{
			error = "Parameters length doesn't correspond ...";
			return null;
		}
		String social_id = params[0];
		String type_social = params[1];
		String token = params[2];
		String device_id = params[3];
		String type_mobile = params[4];
		String os_version = params[5];
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(WebServicesInfo.URL_LOGIN);
		HttpResponse response = null;
		
		List<NameValuePair> values = new ArrayList<NameValuePair>(6);
		values.add(new BasicNameValuePair("social_id", social_id));
		values.add(new BasicNameValuePair("type_social", type_social));
		values.add(new BasicNameValuePair("token", token));
		values.add(new BasicNameValuePair("device_id", device_id));
		values.add(new BasicNameValuePair("type_mobile", type_mobile));
		values.add(new BasicNameValuePair("os_version", os_version));
		try
		{
			request.setEntity(new UrlEncodedFormEntity(values));
			response = httpclient.execute(request);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			error = Sucle.getAppContext().getResources().getString(R.string.error_internet_request);
			return null;
		}
		
		try 
		{
			JSONObject jObject = new JSONObject(WebServicesInfo.parseContent(response.getEntity().getContent()));
			if(jObject.getString(WebServicesInfo.JSONKey.STATUS).equals(WebServicesInfo.JSONKey.STATUS_NOT_VALID))
				error = WebServicesInfo.JSONKey.ERROR_MAP.get(Integer.valueOf(jObject.getString(WebServicesInfo.JSONKey.ERROR_CODE)));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

}
