package ch.hearc.android.sucle.model;

import java.sql.Date;
import com.google.android.gms.maps.model.LatLng;

public class Post
{
	private User		user;
	private LatLng		position;
	private Date		time;
	private Attachment	attachment;

	public Post(User user, LatLng position, Date time, Attachment attachment)
	{
		this.user = user;
		this.position = position;
		this.time = time;
		this.attachment = attachment;
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

}
