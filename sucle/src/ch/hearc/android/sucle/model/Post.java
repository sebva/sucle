package ch.hearc.android.sucle.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

public class Post implements Serializable
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
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeObject(user);
		
		out.writeDouble(position.latitude);
		out.writeDouble(position.longitude);

		out.writeObject(time);
		out.writeObject(attachment);
		out.writeUTF(message);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		user = (User) in.readObject();
		
		position = new LatLng(in.readDouble(), in.readDouble());
		
		time = (Date) in.readObject();
		attachment = (Attachment) in.readObject();
		message = in.readUTF();
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
