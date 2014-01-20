package ch.hearc.android.sucle.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	private List<Post>				posts;
	private Post[]					comments;
	private int						radius;
	private Location				location;
	private int						nbMessage;
	private FetchMessagesListener	listenerMessage;
	private FetchCommentsListener	listenerComment;

	private static final int		BASE_RADIUS		= 50;
	private static final int		NUMBER_MESSAGES	= 100;
	private static final String		FILE			= "posts";

	private static PostsManager		instance		= null;

	public static PostsManager getInstance()
	{
		if (instance == null) instance = new PostsManager(BASE_RADIUS, NUMBER_MESSAGES, null, null);
		return instance;
	}

	private PostsManager(int radius, int nbMessages, FetchMessagesListener listenerM, FetchCommentsListener listenerC)
	{
		this.radius = radius;
		this.location = null;
		this.nbMessage = nbMessages;
		this.listenerMessage = listenerM;
		this.listenerComment = listenerC;
		posts = new ArrayList<Post>();
		comments = null;
	}

	public void restorePosts()
	{
		try
		{
			FileInputStream fis = Sucle.getAppContext().openFileInput(FILE);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);

			this.addNewPosts((Post[]) ois.readObject());
			Log.i(TAG, "Posts restored");

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
		Log.i(TAG, "Saving posts...");
		
		try
		{
			FileOutputStream fos = Sucle.getAppContext().openFileOutput(FILE, Context.MODE_PRIVATE);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);

			Post[] postsA = new Post[posts.size()];
			posts.toArray(postsA);
			oos.writeObject(postsA);

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

		Object[] params = new Object[4];
		params[0] = location;
		params[1] = radius;
		params[2] = nbMessage;

		List<Integer> postId = new ArrayList<Integer>();
		for(Post post :posts)
			postId.add(post.getId());
		params[3] = postId;
		new FetchMessagesTask(listenerMessage, this).execute(params);
	}

	public void getComments(int id) throws NullPointerException
	{
		if (listenerComment == null) return;
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

	public int getRadius()
	{
		return radius;
	}

	public int getNbMessages()
	{
		return nbMessage;
	}

	public void setRadius(int radius)
	{
		boolean smallerRadius = this.radius > radius;
		this.radius = radius;
		if (smallerRadius)
		{
			for (int i = 0; i < posts.size(); ++i)
			{
				if (posts.get(i).distanceToPoint(getLocation().getLatitude(), getLocation().getLongitude()) > radius)
				{
					posts.remove(i);
					i--;
				}
			}
			if(listenerMessage != null)
				listenerMessage.onPostsFetched();
		}
		else
		{
			try
			{
				getNearbyPosts();
			}
			catch (NullPointerException e)
			{
				// nothing todo
			}
		}
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

	public List<Post> getPosts()
	{
		return posts;
	}

	public Post[] getComments() throws NullPointerException
	{
		if (listenerComment == null) throw new NullPointerException("No listenerComment set");
		return comments;
	}

	public void addNewPosts(Post[] result)
	{
		if (result != null)
		{
			int postsQuantity = posts.size();
			for (int i = 0; i < result.length; ++i)
				if (!posts.contains(result[i]) && result[i].distanceToPoint(getLocation().getLatitude(), getLocation().getLongitude()) < radius) posts.add(result[i]);
			if (postsQuantity != posts.size())
			{
				Collections.sort(posts, new Comparator<Post>() {

					@Override
					public int compare(Post post1, Post post2)
					{
						double latitude = getLocation().getLatitude();
						double longitude = getLocation().getLongitude();
						return Float.compare(post1.distanceToPoint(latitude, longitude), post2.distanceToPoint(latitude, longitude));
					}

				});
				listenerMessage.onPostsFetched();
			}
		}
	}
}
