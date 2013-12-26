package ch.hearc.android.sucle.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Fragment;
import android.app.ListFragment;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.Sucle;
import ch.hearc.android.sucle.controller.FetchMessagesTask.FetchMessagesListener;
import ch.hearc.android.sucle.controller.PostsAdapter;
import ch.hearc.android.sucle.controller.PostsManager;
import ch.hearc.android.sucle.model.Attachment;
import ch.hearc.android.sucle.model.AttachmentType;
import ch.hearc.android.sucle.model.Post;
import ch.hearc.android.sucle.model.SocialType;
import ch.hearc.android.sucle.model.User;

import com.google.android.gms.maps.model.LatLng;

public class TimelineFragment extends ListFragment implements FetchMessagesListener
{
	private OnPostSelectedListener	mCallback;
	private PostsManager postsManager;
	static public PostsAdapter postsAdapter; //TODO: better

	// The container Activity must implement this interface so the frag can
	// deliver messages
	public interface OnPostSelectedListener
	{
		/** Called by HeadlinesFragment when a list item is selected */
		public void onPostSelected(int position);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		postsAdapter = new PostsAdapter(this.getActivity(), R.layout.timeline_row_fragment);
		setListAdapter(postsAdapter);

		postsManager = new PostsManager(Sucle.getAppContext(), Integer.MAX_VALUE, 100, this);
		Location location = new Location(LocationManager.GPS_PROVIDER);
		location.setLatitude(45);
		location.setLongitude(6);
		postsManager.onLocationChanged(location);
		postsManager.getNearbyPosts();
		
		// Populate the list, through the adapter
//		for (final Post post : getPostsEntries())
//		{
//			postsAdapter.add(post);
//		}
	}

	@Override
	public void onPostsFetched()
	{
		Post[] posts = postsManager.getPost();
		if(posts != null)
			for (final Post post : posts)
			{
				postsAdapter.add(post);
			}
		else
			Log.i(TimelineFragment.class.getSimpleName(), "No post receive from server");
	}

	private List<Post> getPostsEntries()
	{

		// Let's setup some test data.
		// Normally this would come from some asynchronous fetch into a data
		// source
		// such as a sqlite database, or an HTTP request

		final List<Post> posts = new ArrayList<Post>();

		for (int i = 4; i < 50; i++)
		{
			posts.add(new Post(new User(i, SocialType.Facebook, new Date()), new LatLng(47.546, 6.954), new Date(), new Attachment(new Object(), AttachmentType.Picture, "path"), "Dummy message"));
		}

		return posts;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// When in two-pane layout, set the listview to highlight the selected
		// list item
		// (We do this during onStart because at the point the listview is
		// available.)
		if (getFragmentManager().findFragmentById(R.id.mainFragment) != null)
		{
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		// Notify the parent activity of selected item
		mCallback.onPostSelected(position);

		// Set the item as checked to be highlighted when in two-pane layout
		//getListView().setItemChecked(position, true);
	}

	public void setCallback(Fragment fragment)
	{
		try
		{
			mCallback = (OnPostSelectedListener) fragment;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(fragment.toString() + " must implement OnPostSelectedListener");
		}
	}
}
