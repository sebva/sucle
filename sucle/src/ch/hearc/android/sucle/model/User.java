package ch.hearc.android.sucle.model;

import java.util.Date;

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

}
