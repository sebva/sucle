package ch.hearc.android.sucle;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ch.hearc.android.sucle.model.Attachment;
import ch.hearc.android.sucle.model.AttachmentType;
import ch.hearc.android.sucle.model.Post;
import ch.hearc.android.sucle.model.SocialType;
import ch.hearc.android.sucle.model.User;

public class TimelineFragment extends ListFragment
{
	OnPostSelectedListener	mCallback;

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

		PostsAdapter postsAdapter = new PostsAdapter(this.getActivity(), R.layout.fragment_post_list);
		setListAdapter(postsAdapter);

		// Populate the list, through the adapter
		for (final Post post : getPostsEntries())
		{
			postsAdapter.add(post);
		}
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
			posts.add(new Post(new User(i, SocialType.Facebook, new Date()), new LatLng(47.546, 6.954), new Date(12315465), new Attachment(new Object(), AttachmentType.Picture, "path")));
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
		if (getFragmentManager().findFragmentById(R.id.postListFragment) != null)
		{
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.

		Log.e("hello", getArguments().toString());
		
		/*try
		{
			mCallback = (OnPostSelectedListener) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement OnPostSelectedListener");
		}*/

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		// Notify the parent activity of selected item
		mCallback.onPostSelected(position);

		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);
	}
}
