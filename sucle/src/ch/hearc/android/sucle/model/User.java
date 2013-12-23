package ch.hearc.android.sucle.model;

import java.io.Serializable;
import java.util.Date;

import com.facebook.Request;
import com.facebook.Response;

public class User implements Serializable
{
	private int			socialId;
	private SocialType	socialType;
	private Date		registration;
	private String		name;

	public User(int socialId, SocialType socialType, Date registration)
	{
		this.socialId = socialId;
		this.socialType = socialType;
		this.registration = registration;
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

	@Override
	public String toString()
	{
		return socialId + " " + socialType + " " + registration;
	}

	private void loadFullName()
	{
		Request.newGraphPathRequest(null, Integer.toString(socialId), new Request.Callback() {

			@Override
			public void onCompleted(Response response)
			{
				if (response.getGraphObject() != null) name = response.getGraphObject().getProperty("name").toString();
			}
		}).executeAndWait();
	}

}
