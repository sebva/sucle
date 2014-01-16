package ch.hearc.android.sucle.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import ch.hearc.android.sucle.Sucle;

import com.google.android.gms.maps.model.LatLng;

public class Post implements Serializable
{
	private int			id;
	private int			parent;
	private User		user;
	private LatLng		position;
	private String		positionName;
	private Date		time;
	private Attachment	attachment;
	private String		message;

	public Post(int id, int parent, User user, LatLng position, Date time, Attachment attachment, String message)
	{
		this.id = id;
		this.parent = parent;
		this.user = user;
		this.position = position;
		this.time = time;
		this.attachment = attachment;
		this.message = message;

		fetchPositionName();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeObject(id);
		out.writeObject(user);

		out.writeDouble(position.latitude);
		out.writeDouble(position.longitude);

		out.writeObject(time);
		out.writeObject(attachment);
		out.writeUTF(message);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		id = (Integer) in.readObject();
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

	public String getPositionName()
	{
		return positionName;
	}

	public int getId()
	{
		return id;
	}

	public int getParent()
	{
		return parent;
	}

	@Override
	public String toString()
	{
		return "(" + position.latitude + ";" + position.longitude + ") " + time + " " + user + " " + attachment + " " + message;
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	@Override
	public boolean equals(Object o)
	{
		return id == ((Post) o).id;
	}

	public float distanceToPoint(double latitude, double longitude)
	{
		float[] results = new float[3];
		Location.distanceBetween(position.latitude, position.longitude, latitude, longitude, results);
		return results[0];
	}

	private void fetchPositionName()
	{
		Geocoder geocoder = new Geocoder(Sucle.getAppContext(), Locale.getDefault());
		try
		{
			List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
			StringBuilder stringBuilder = new StringBuilder();
			Address address = addresses.get(0);
			for (int i = 0; i <= address.getMaxAddressLineIndex(); ++i)
			{
				stringBuilder.append(address.getAddressLine(i));
				if (i < address.getMaxAddressLineIndex()) stringBuilder.append(", ");
			}
			positionName = stringBuilder.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
