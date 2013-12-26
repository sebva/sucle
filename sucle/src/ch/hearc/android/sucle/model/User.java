package ch.hearc.android.sucle.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.Sucle;

import com.facebook.Request;
import com.facebook.Response;
import com.google.android.gms.plus.PlusClient;

public class User implements Serializable
{
	private int			socialId;
	private SocialType	socialType;
	private Date		registration;
	private String		name;
	private PlusClient	client;
	private String		imageUrl;

	public User(int socialId, SocialType socialType, Date registration)
	{
		this.socialId = socialId;
		this.socialType = socialType;
		this.registration = registration;
		this.client = null;
		loadFullName();
	}

	public int getSocialId()
	{
		return socialId;
	}

	public SocialType getSocialType()
	{
		return socialType;
	}

	public Date getRegistration()
	{
		return registration;
	}

	public String getName()
	{
		return name;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	@Override
	public String toString()
	{
		return socialId + " " + socialType + " " + registration;
	}

	private void loadFullName()
	{
		switch (socialType)
		{
			case Facebook:
				Request.newGraphPathRequest(null, Integer.toString(socialId), new Request.Callback() {

					@Override
					public void onCompleted(Response response)
					{
						if (response.getGraphObject() != null) name = response.getGraphObject().getProperty("name").toString();
					}
				}).executeAndWait(); // In current thread, because this method
										// is already called by a second thread
				break;
			case GooglePlus:
				imageUrl = "https://plus.google.com/s2/photos/profile/" + Integer.toString(socialId);// ?sz=<your_desired_size>
				new Thread(new Runnable() {

					@Override
					public void run()
					{
						String url = "https://www.googleapis.com/plus/v1/people/" + Integer.toString(socialId) + "?fields=displayName&key="
								+ Sucle.getAppContext().getResources().getString(R.string.google_api_key);
						StringBuilder json = new StringBuilder();
						try
						{
							HttpClient httpclient = new DefaultHttpClient();
							HttpPost httpPost = new HttpPost(url);
							HttpResponse response = httpclient.execute(httpPost);
							HttpEntity entity = response.getEntity();
							InputStream is = entity.getContent();

							BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
							String line = null;
							while ((line = reader.readLine()) != null)
							{
								json.append(line);
							}
							is.close();

							Log.e("", json.toString());
							JSONObject jsonObject = new JSONObject(json.toString());
							name = jsonObject.getString("displayName");
						}
						catch (Exception e)
						{
							Log.e("", e.toString());
							name = Integer.toString(socialId);
						}
					}
				}).start();

				/*
				 * Log.e(TAG, "GooglePlus"); client = new
				 * PlusClient.Builder(Sucle.getAppContext(), new
				 * ConnectionCallbacks() {
				 * 
				 * @Override public void onDisconnected() { Log.e(TAG,
				 * "onDisconnected"); client = null; }
				 * 
				 * @Override public void onConnected(Bundle arg0) { Log.e(TAG,
				 * "onConnected"); client.loadPeople(new
				 * OnPeopleLoadedListener() {
				 * 
				 * @Override public void onPeopleLoaded(ConnectionResult arg0,
				 * PersonBuffer arg1, String arg2) { Log.e(TAG,
				 * "onPeopleLoaded"); Person person = arg1.get(0); name =
				 * person.getDisplayName(); imageUrl =
				 * person.getImage().getUrl(); client.disconnect(); } },
				 * Integer.toString(socialId)); } }, new
				 * OnConnectionFailedListener() {
				 * 
				 * @Override public void onConnectionFailed(ConnectionResult
				 * arg0) { Log.e(TAG, "onConnectionFailed" + arg0.getErrorCode()
				 * + arg0.toString()); name = Integer.toString(socialId); }
				 * }).setScopes(Scopes.PLUS_PROFILE).build(); client.connect();
				 */
				break;

			default:
				name = Integer.toString(socialId);
				break;
		}
	}

	private static final String	TAG	= User.class.getSimpleName();

}
