package ch.hearc.android.sucle;

import android.location.Location;

public class PostsManager {

	private double radius;
	private Location location;
	private int nbMessage;
	
	public PostsManager(double radius, int nbMessages)
	{
		this.radius = radius;
		this.location = null;
		this.nbMessage = nbMessages;
	}
	
	public void getNearbyPosts()
	{
		Object[] params = new Object[3];
		params[0] = location;
		params[1] = radius;
		params[2] = nbMessage;
		
		new FetchMessagesTask().execute(params);
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
}
