package ch.hearc.android.sucle.view;

import android.app.ActionBar.OnNavigationListener;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.controller.FetchMessagesTask.FetchMessagesListener;
import ch.hearc.android.sucle.controller.PostsAdapter;
import ch.hearc.android.sucle.controller.PostsManager;
import ch.hearc.android.sucle.model.Post;

public class TimelineFragment extends ListFragment implements FetchMessagesListener
{
	private OnPostSelectedListener	mCallback;
	private PostsAdapter			postsAdapter;	// TODO: better

	// The container Activity must implement this interface so the frag can
	// deliver messages
	public interface OnPostSelectedListener
	{
		/**
		 * Called by HeadlinesFragment when a list item is selected
		 * 
		 * @param tabletOnly
		 */
		public void onPostSelected(int position, boolean tabletOnly);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		postsAdapter = new PostsAdapter(this.getActivity(), R.layout.timeline_row_fragment);
		setListAdapter(postsAdapter);

		PostsManager postsManager = PostsManager.getInstance();
		postsManager.setListenerMessage(this);

		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.radius_values, android.R.layout.simple_spinner_dropdown_item);
		getActivity().getActionBar().setListNavigationCallbacks(mSpinnerAdapter, new OnNavigationListener() {
			String[]	radius	= getResources().getStringArray(R.array.radius_values);

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId)
			{
				PostsManager.getInstance().setRadius(Integer.parseInt(radius[itemPosition]));
				return false;
			}
		});
	}

	@Override
	public void onPostsFetched()
	{
		Post[] posts = PostsManager.getInstance().getPosts();
		boolean noPostDisplay = postsAdapter.getCount() == 0;
		if (posts != null)
		{
			for (final Post post : posts)
			{
				if (postsAdapter.getPosition(post) == -1) postsAdapter.add(post);
			}
		}
		else
			Log.i(TimelineFragment.class.getSimpleName(), "No post receive from server");
		if (noPostDisplay && postsAdapter.getCount() > 0) mCallback.onPostSelected(0, true);
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
		mCallback.onPostSelected(position, false);

		// Set the item as checked to be highlighted when in two-pane layout
		// getListView().setItemChecked(position, true);
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
