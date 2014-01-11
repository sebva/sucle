package ch.hearc.android.sucle.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import ch.hearc.android.sucle.Sucle;
import ch.hearc.android.sucle.controller.FetchCommentsTask.FetchCommentsListener;
import ch.hearc.android.sucle.controller.FetchMessagesTask.FetchMessagesListener;
import ch.hearc.android.sucle.model.Post;

public class PostsManager
{

	private static final String		TAG				= "PostsManager";
	private Set<Post>				posts;
	private Post[]					comments;
	private double					radius;
	private Location				location;
	private int						nbMessage;
	private FetchMessagesListener	listenerMessage;
	private FetchCommentsListener	listenerComment;

	private static final double		BASE_RADIUS		= 1000;
	private static final int		NUMBER_MESSAGES	= 100;
	private static final String		FILE			= "posts";

	private static PostsManager		instance		= null;

	public static PostsManager getInstance()
	{
		if (instance == null) instance = new PostsManager(BASE_RADIUS, NUMBER_MESSAGES, null, null);
		return instance;
	}

	private PostsManager(double radius, int nbMessages, FetchMessagesListener listenerM, FetchCommentsListener listenerC)
	{
		this.radius = radius;
		this.location = null;
		this.nbMessage = nbMessages;
		this.listenerMessage = listenerM;
		this.listenerComment = listenerC;
		posts = new HashSet<Post>();
		comments = null;
	}

	public void restorePosts()
	{
		try
		{
			FileInputStream fis = Sucle.getAppContext().openFileInput(FILE);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);

			posts = new HashSet<Post>();
			posts.addAll(Arrays.asList((Post[]) ois.readObject()));

			ois.close();
			bis.close();
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			Log.i(TAG, "No messages were restored (FileNotFoundException)");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void savePosts()
	{
		try
		{
			FileOutputStream fos = Sucle.getAppContext().openFileOutput(FILE, Context.MODE_PRIVATE);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);

			oos.writeObject(posts);

			oos.close();
			bos.close();
			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void getNearbyPosts() throws NullPointerException
	{
		if (listenerMessage == null || location == null) throw new NullPointerException("No listenerMessage set or no location");

		Object[] params = new Object[3];
		params[0] = location;
		params[1] = radius;
		params[2] = nbMessage;

		new FetchMessagesTask(listenerMessage, this).execute(params);
	}

	public void getComments(int id) throws NullPointerException
	{
		if (listenerComment == null) throw new NullPointerException("No listenerComment set");
		Object[] params = new Object[1];
		params[0] = id;

		new FetchCommentsTask(listenerComment, this).execute(params);
	}

	public void onLocationChanged(Location location)
	{
		this.location = location;
		try
		{
			getNearbyPosts();
		}
		catch (NullPointerException e)
		{
			// nothing todo
		}
	}

	public Location getLocation()
	{
		return location;
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

	public void setListenerMessage(FetchMessagesListener listener)
	{
		this.listenerMessage = listener;
		try
		{
			getNearbyPosts();
		}
		catch (NullPointerException e)
		{
			// nothing todo
		}
	}

	public void setListenerComment(FetchCommentsListener listener)
	{
		this.listenerComment = listener;
	}

	public void setComments(Post[] comments)
	{
		this.comments = comments;
	}

	public Post[] getPosts()
	{
		Post[] posts = new Post[this.posts.size()];
		return this.posts.toArray(posts);
	}

	public Post[] getComments()
	{
		return comments;
	}

	public void addNewPosts(Post[] result)
	{
		posts.addAll(Arrays.asList(result));		
	}
}
