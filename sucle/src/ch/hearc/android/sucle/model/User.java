package ch.hearc.android.sucle.model;

import java.util.Date;

import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;

public class User
{
	private int			socialId;
	private SocialType	socialType;
	private Date		registration;

	public User(int socialId, SocialType socialType, Date registration)
	{
		this.socialId = socialId;
		this.socialType = socialType;
		this.registration = registration;
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

	public void loadFullName(final TextView textview)
	{
		Request.newGraphPathRequest(null, Integer.toString(socialId), new Request.Callback() {

			@Override
			public void onCompleted(Response response)
			{
				if (response.getGraphObject() != null) textview.setText(response.getGraphObject().getProperty("name").toString());
			}
		}).executeAsync();
	}

}
