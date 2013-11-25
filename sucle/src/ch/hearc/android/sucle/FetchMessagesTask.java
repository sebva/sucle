package ch.hearc.android.sucle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import ch.hearc.android.sucle.PostsManager.FetchMessagesListener;
import ch.hearc.android.sucle.model.Attachment;
import ch.hearc.android.sucle.model.AttachmentType;
import ch.hearc.android.sucle.model.Post;
import ch.hearc.android.sucle.model.SocialType;
import ch.hearc.android.sucle.model.User;

import com.google.android.gms.maps.model.LatLng;

public class FetchMessagesTask extends AsyncTask<Object, Void, Post[]>
{
	private String error = null;
	private FetchMessagesListener listener;
	
	public FetchMessagesTask(FetchMessagesListener listener)
	{
		this.listener = listener;
	}

	@Override
	protected void onPostExecute(Post[] result) {
		super.onPostExecute(result);
		if(result == null)
			Log.i("posts", "There was a little problem during the process ...");
		else if(error != null)
			Log.i("error", error);
		else
		{
			Log.i("posts", result.toString());
			listener.onPostsFetched(result);
		}
	}
	
	@Override
	protected Post[] doInBackground(Object... params) {
		if(params.length != 3)
		{
			error = "Parameters length doesn't correspond ...";
			return null;
		}
		
		Location location = (Location)params[0];
		double radius = (Double)params[1];
		int nbMessage = (Integer)params[2];
		
		HttpClient httpclient = new DefaultHttpClient();
		String getParameter = "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() +"&r" + radius + "&nb=" + nbMessage;
		HttpGet request = new HttpGet(WebServicesInfo.URL_GET_MESSAGE + getParameter);

		HttpResponse response = null;
		try
		{
			response = httpclient.execute(request);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			error = e.getMessage();
			response = null;
		}

		if(response == null)
			return null;
		
		try 
		{
			JSONObject jObject = new JSONObject(WebServicesInfo.parseContent(response.getEntity().getContent()));

			if(jObject.getString(WebServicesInfo.JSONKey.STATUS).equals(WebServicesInfo.JSONKey.STATUS_VALID))
			{
				JSONArray jArray = jObject.getJSONArray(WebServicesInfo.JSONKey.MESSAGES);

				List<Post> posts = new ArrayList<Post>();
				
				for(int i = 0; i < jArray.length(); ++i)
				{
					JSONObject object = jArray.getJSONObject(i);
					SocialType socialType = SocialType.Undefined;
					String socialTypestr = jObject.getString(WebServicesInfo.JSONKey.USER_TYPE);
					
					if(socialTypestr == WebServicesInfo.JSONKey.USER_TYPE_FACEBOOK)
						socialType = SocialType.Facebook;
					else if(socialTypestr == WebServicesInfo.JSONKey.USER_TYPE_GOOGLEPLUS)
						socialType = SocialType.GooglePlus;
					
					User user = new User(object.getInt(WebServicesInfo.JSONKey.USER_SOCIAL_ID), socialType, new Date(object.getString(WebServicesInfo.JSONKey.USER_INSCRIPTION)));
					
					Attachment attachment = null;
					AttachmentType attachmentType = AttachmentType.Undefined;
					String filePath = jObject.getString(WebServicesInfo.JSONKey.MESSAGE_FILE);
					String mime = jObject.getString(WebServicesInfo.JSONKey.MESSAGE_MIME);
					
					if(WebServicesInfo.MIME_AUDIO.contains(mime))
						attachmentType = AttachmentType.Sound;
					else if(WebServicesInfo.MIME_IMAGE.contains(mime))
						attachmentType = AttachmentType.Picture;
					else if(WebServicesInfo.MIME_VIDEO.contains(mime))
						attachmentType = AttachmentType.Video;

					if(attachmentType != AttachmentType.Undefined)
						attachment = new Attachment(null , attachmentType, filePath);
					
					
					posts.add(new Post(user, new LatLng(object.getDouble(WebServicesInfo.JSONKey.MESSAGE_LAT), object.getDouble(WebServicesInfo.JSONKey.MESSAGE_LONG)), new Date(object.getString(WebServicesInfo.JSONKey.MESSAGE_DATETIME)), attachment, object.getString(WebServicesInfo.JSONKey.MESSAGE_MESSAGE)));
				
					Post[] out = new Post[posts.size()];
					return posts.toArray(out);
				}
			}
			else
			{
				Exception e = new Exception(WebServicesInfo.JSONKey.ERROR_MAP.get(jObject.getString(WebServicesInfo.JSONKey.ERROR_CODE)));
				error = e.getMessage();
				throw e;
			}
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null; 
	}

}
