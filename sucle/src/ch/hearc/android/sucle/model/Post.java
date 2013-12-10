package ch.hearc.android.sucle.model;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import ch.hearc.android.sucle.Sucle;

import com.google.android.gms.maps.model.LatLng;

public class Post
{
	private User		user;
	private LatLng		position;
	private String		positionName;
	private Date		time;
	private Attachment	attachment;
	private String		message;

	public Post(User user, LatLng position, Date time, Attachment attachment, String message)
	{
		this.user = user;
		this.position = position;
		this.time = time;
		this.attachment = attachment;
		this.message = message;

		fetchPositionName();
	}

	private void fetchPositionName()
	{
		Geocoder geocoder = new Geocoder(Sucle.getAppContext(), Locale.getDefault());
		try
		{
			List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
			StringBuilder stringBuilder = new StringBuilder();
			Address address = addresses.get(0);
			for(int i = 0; i <= address.getMaxAddressLineIndex(); ++i)
			{
				stringBuilder.append(address.getAddressLine(i));
				if(i < address.getMaxAddressLineIndex())
					stringBuilder.append(", ");
			}
			positionName = stringBuilder.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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

	@Override
	public String toString()
	{
		return "(" + position.latitude + ";" + position.longitude + ") " + time + " " + user + " " + attachment + " " + message;
	}
}
