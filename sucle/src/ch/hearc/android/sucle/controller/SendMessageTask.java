package ch.hearc.android.sucle.controller;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.Sucle;
import ch.hearc.android.sucle.WebServicesInfo;
import ch.hearc.android.sucle.R.string;
import ch.hearc.android.sucle.WebServicesInfo.JSONKey;
import android.os.AsyncTask;
import android.util.Log;

public class SendMessageTask extends AsyncTask<Object, Void, Void>
{
	private String error = null;
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		MessageNotification.cancel(Sucle.getAppContext());
		if(error != null)
			MessageNotification.basicNotification(Sucle.getAppContext(), error);
	} 
	
	@Override
	protected Void doInBackground(Object... params) {
		if(params.length != 5 && params.length != 6)
		{
			error = "Parameters length doesn't correspond ...";
			return null;
		}
		MessageNotification.basicNotification(Sucle.getAppContext(), Sucle.getAppContext().getResources().getString(R.string.sending_message), true);
		
		String token = (String) params[0];
		String device_id = (String) params[1];
		String message = (String) params[2];
		String lat = (String) params[3];
		String lon = (String) params[4];
		String parent = (String) params[5];
		String filepath = (String) (params.length == 7 ? params[6] : null);
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(WebServicesInfo.URL_SEND_MESSAGE);
		HttpResponse response = null;
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		if(filepath != null)
			builder.addPart("file", new FileBody(new File(filepath)));
		builder.addTextBody("token", token);
		builder.addTextBody("device_id", device_id);
		builder.addTextBody("message", message);
		builder.addTextBody("lat", lat);
		builder.addTextBody("lon", lon);
		if(!"-1".equals(parent))
			builder.addTextBody("parent", parent);
		try
		{  
			request.setEntity(builder.build());
			response = httpclient.execute(request);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			error = e.getMessage();
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
