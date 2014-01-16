package ch.hearc.android.sucle.model;

import java.io.BufferedReader;
import java.io.FileOutputStream;
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.Sucle;

import com.facebook.Request;
import com.facebook.Response;
import com.google.android.gms.plus.PlusClient;

public class User implements Serializable
{
	private String		socialId;
	private SocialType	socialType;
	private Date		registration;
	private String		name;
	private PlusClient	client;
	private String		imageUrl;
	private Bitmap		image;

	public User(String socialId, String name, SocialType socialType, Date registration)
	{
		this.socialId = socialId;
		this.socialType = socialType;
		this.registration = registration;
		this.name = name;
		this.client = null;
		loadFullName();
	}

	public String getSocialId()
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
				Request.newGraphPathRequest(null, socialId, new Request.Callback() {

					@Override
					public void onCompleted(Response response)
					{
						if (response.getGraphObject() != null) name = response.getGraphObject().getProperty("name").toString();
					}
				}).executeAndWait(); // In current thread, because this method
										// is already called by a second thread
				break;
			case GooglePlus:
				imageUrl = "https://plus.google.com/s2/photos/profile/" + socialId;// ?sz=<your_desired_size>
				loadImage();
				break;

			default:
				name = socialId;
				break;
		}
	}

	private void loadImage()
	{
		String path = Sucle.getAppContext().getCacheDir().getPath() + "/";

		image = BitmapFactory.decodeFile(path + socialId);
		if (image == null)
		{
			// If no file, we load it
			try
			{
				InputStream in = new java.net.URL(imageUrl).openStream();
				image = BitmapFactory.decodeStream(in);

				FileOutputStream out = new FileOutputStream(path + socialId);
				image.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.close();
			}
			catch (Exception exception)
			{
				Log.e("Error", exception.getMessage());
			}
		}
	}

	public Bitmap getImage()
	{
		return image;
	}

	private static final String	TAG	= User.class.getSimpleName();
}
