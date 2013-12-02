package ch.hearc.android.sucle;

import ch.hearc.android.sucle.FetchMessagesTask.FetchMessagesListener;
import ch.hearc.android.sucle.model.Post;
import android.location.Location;

public class PostsManager {
	
	private Post[] posts;
	private double radius;
	private Location location;
	private int nbMessage;
	private FetchMessagesListener listener = null;
	
	public PostsManager(double radius, int nbMessages)
	{
		this.radius = radius;
		this.location = null;
		this.nbMessage = nbMessages;
		posts = null;
	}
	
	public void getNearbyPosts()
	{
		if(listener == null)
			return;
		
		Object[] params = new Object[3];
		params[0] = location;
		params[1] = radius;
		params[2] = nbMessage;
		
		new FetchMessagesTask(null, this).execute(params);
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
	
	public void setPosts(Post[] posts)
	{
		this.posts = posts;
	}
	
	public Post[] getPost()
	{
		return posts;
	}
}
