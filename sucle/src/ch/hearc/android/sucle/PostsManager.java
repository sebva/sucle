package ch.hearc.android.sucle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import ch.hearc.android.sucle.FetchMessagesTask.FetchMessagesListener;
import ch.hearc.android.sucle.model.Post;

public class PostsManager
{
	
	private static final String TAG = "PostsManager";
	private Post[] posts;
	private double radius;
	private Location location;
	private int nbMessage;
	private FetchMessagesListener listener = null;
	private Context context;
	
	private static final String FILE = "posts";
	
	public PostsManager(Context context, double radius, int nbMessages)
	{
		this.context = context;
		this.radius = radius;
		this.location = null;
		this.nbMessage = nbMessages;
		posts = null;
	}
	
	public void restorePosts()
	{	
		try
		{
			FileInputStream fis = context.openFileInput(FILE);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);
			
			posts = (Post[]) ois.readObject();
			
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
			FileOutputStream fos = context.openFileOutput(FILE, Context.MODE_PRIVATE);
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
