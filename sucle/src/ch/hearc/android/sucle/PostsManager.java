package ch.hearc.android.sucle;

import ch.hearc.android.sucle.model.Post;
import android.location.Location;

public class PostsManager {
	
	public interface FetchMessagesListener
	{
		public void onPostsFetched(Post[] posts);
	}

	private double radius;
	private Location location;
	private int nbMessage;
	private FetchMessagesListener listener = null;
	
	public PostsManager(double radius, int nbMessages)
	{
		this.radius = radius;
		this.location = null;
		this.nbMessage = nbMessages;
	}
	
	public void getNearbyPosts()
	{
		if(listener == null)
			return;
		
		Object[] params = new Object[3];
		params[0] = location;
		params[1] = radius;
		params[2] = nbMessage;
		
		new FetchMessagesTask(listener).execute(params);
	}
	
	public void onLocationChanged(Location location)
	{
		this.location = location;
	}
	
	public double getRadius()
	{
		return radius;
	}
	
	public int getNbMessages()
	{
		return nbMessage;
	}
	
	public void setRadius(double radius)
	{
		this.radius = radius;
	}
	
	public void setNbMessage(int nbMessage)
	{
		this.nbMessage = nbMessage;
	}

	public void setListener(FetchMessagesListener listener)
	{
		this.listener = listener;
	}
}
