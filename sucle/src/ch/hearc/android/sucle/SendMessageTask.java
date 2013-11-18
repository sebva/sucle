package ch.hearc.android.sucle;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class SendMessageTask extends AsyncTask<String, Void, Void>
{
	private String error = null;
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		//TODO Update GUI
		if(error != null)
			Log.i("error",error);
		else
			Log.i("success", "success"); 
	} 
	
	@Override
	protected Void doInBackground(String... params) {
		if(params.length != 5 && params.length != 6)
		{
			error = "Parameters length doesn't correspond ...";
			return null;
		}
		
		String token = params[0];
		String device_id = params[1];
		String message = params[2];
		String lat = params[3];
		String lon = params[4];
		String filepath = params.length == 6 ? params[5] : null;
		
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
			{
				Exception e = new Exception(WebServicesInfo.JSONKey.ERROR_MAP.get(jObject.getString(WebServicesInfo.JSONKey.ERROR_CODE)));
				error = e.getMessage();
				throw e;
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			error = e.getMessage();
		}
		return null;
	}
}
