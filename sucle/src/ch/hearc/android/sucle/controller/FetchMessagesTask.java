package ch.hearc.android.sucle.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.Sucle;
import ch.hearc.android.sucle.WebServicesInfo;
import ch.hearc.android.sucle.model.Attachment;
import ch.hearc.android.sucle.model.AttachmentType;
import ch.hearc.android.sucle.model.Post;
import ch.hearc.android.sucle.model.SocialType;
import ch.hearc.android.sucle.model.User;

import com.google.android.gms.maps.model.LatLng;

public class FetchMessagesTask extends AsyncTask<Object, Void, Post[]>
{
	public interface FetchMessagesListener
	{
		public void onPostsFetched();
	}

	private String			error;
	private PostsManager	postsManager;

	public FetchMessagesTask(FetchMessagesListener listener, PostsManager postsManager)
	{
		this.error = null;
		this.postsManager = postsManager;
	}

	@Override
	protected void onPostExecute(Post[] result)
	{
		super.onPostExecute(result);
		if (error != null)
			MessageNotification.basicNotification(Sucle.getAppContext(), error);
		else
		{
			postsManager.addNewPosts(result);
		}
	}

	@Override
	protected Post[] doInBackground(Object... params)
	{
		if (params.length != 3)
		{
			error = "Parameters length doesn't correspond ...";
			return null;
		}

		Location location = (Location) params[0];
		int radius = (Integer) params[1];
		int nbMessage = (Integer) params[2];

		HttpClient httpclient = new DefaultHttpClient();
		String getParameter = "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&r=" + radius + "&nb=" + nbMessage;
		HttpGet request = new HttpGet(WebServicesInfo.URL_GET_MESSAGE + getParameter);

		HttpResponse response = null;
		try
		{
			response = httpclient.execute(request);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			error = Sucle.getAppContext().getResources().getString(R.string.error_internet_request);
			response = null;
		}

		if (response == null) return null;

		try
		{
			JSONObject jObject = new JSONObject(WebServicesInfo.parseContent(response.getEntity().getContent()));

			if (jObject.getString(WebServicesInfo.JSONKey.STATUS).equals(WebServicesInfo.JSONKey.STATUS_VALID))
			{
				JSONArray jArray = jObject.getJSONArray(WebServicesInfo.JSONKey.MESSAGES);

				List<Post> posts = new ArrayList<Post>();

				for (int i = 0; i < jArray.length(); ++i)
				{
					JSONObject object = jArray.getJSONObject(i);
					SocialType socialType = SocialType.Undefined;

					JSONObject userObject = object.getJSONObject(WebServicesInfo.JSONKey.MESSAGE_USER);
					String socialTypestr = userObject.getString(WebServicesInfo.JSONKey.USER_TYPE);

					if (socialTypestr.equals(WebServicesInfo.JSONKey.USER_TYPE_FACEBOOK))
						socialType = SocialType.Facebook;
					else if (socialTypestr.equals(WebServicesInfo.JSONKey.USER_TYPE_GOOGLEPLUS)) socialType = SocialType.GooglePlus;

					User user = new User(userObject.getString(WebServicesInfo.JSONKey.USER_SOCIAL_ID), socialType, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(userObject
							.getString(WebServicesInfo.JSONKey.USER_INSCRIPTION)));

					Attachment attachment = null;
					AttachmentType attachmentType = AttachmentType.Undefined;
					String filePath = object.getString(WebServicesInfo.JSONKey.MESSAGE_FILE);
					String mime = object.getString(WebServicesInfo.JSONKey.MESSAGE_MIME);
					String id = object.getString(WebServicesInfo.JSONKey.MESSAGE_ID);

					if (WebServicesInfo.MIME_AUDIO.contains(mime))
						attachmentType = AttachmentType.Sound;
					else if (WebServicesInfo.MIME_IMAGE.contains(mime))
						attachmentType = AttachmentType.Picture;
					else if (WebServicesInfo.MIME_VIDEO.contains(mime)) attachmentType = AttachmentType.Video;

					if (attachmentType != AttachmentType.Undefined) attachment = new Attachment(null, attachmentType, filePath);

					posts.add(new Post(Integer.valueOf(id), user, new LatLng(Double.valueOf(object.getString(WebServicesInfo.JSONKey.MESSAGE_LAT)), Double.valueOf(object
							.getString(WebServicesInfo.JSONKey.MESSAGE_LONG))), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(object
							.getString(WebServicesInfo.JSONKey.MESSAGE_DATETIME)), attachment, object.getString(WebServicesInfo.JSONKey.MESSAGE_MESSAGE)));
				}

				Post[] out = new Post[posts.size()];
				return posts.toArray(out);
			}
			else
			{
				error = WebServicesInfo.JSONKey.ERROR_MAP.get(Integer.valueOf(jObject.getString(WebServicesInfo.JSONKey.ERROR_CODE)));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

}
