package ch.hearc.android.sucle.model;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

public class Post
{
	private User		user;
	private LatLng		position;
	private Date		time;
	private Attachment	attachment;
	private String message;
	
	public Post(User user, LatLng position, Date time, Attachment attachment, String message)
	{
		this.user = user;
		this.position = position;
		this.time = time;
		this.attachment = attachment;
		this.message = message;
	}

	public User getUser()
	{
		return user;
	}

	public LatLng getPosition()
	{
		return position;
	}

	public Date getTime()
	{
		return time;
	}

	public Attachment getAttachment()
	{
		return attachment;
	}
	
	public String getMessage()
	{
		return message;
	}

	@Override
	public String toString() {
		return "(" + position.latitude + ";" + position.longitude + ") " + time + " " + user + " " + attachment + " " + message; 
	}
}
