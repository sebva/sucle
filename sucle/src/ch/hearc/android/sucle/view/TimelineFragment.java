package ch.hearc.android.sucle.view;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.controller.PostsAdapter;
import ch.hearc.android.sucle.controller.PostsManager;

public class TimelineFragment extends ListFragment
{
	private OnPostSelectedListener	mCallback;
	private PostsAdapter			postsAdapter;
	private int						position	= -1;

	public interface OnPostSelectedListener
	{
		public void onPostSelected(int position, boolean tabletOnly);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		postsAdapter = new PostsAdapter(this.getActivity(), R.layout.timeline_row_fragment, PostsManager.getInstance().getPosts());
		setListAdapter(postsAdapter);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if (getFragmentManager().findFragmentById(R.id.mainFragment) != null)
		{
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	public void onPostsFetched()
	{
		if(position == -1)
			mCallback.onPostSelected(0, true);
		postsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		this.position = position;
		
		// Notify the parent activity of selected item
		mCallback.onPostSelected(position, false);

		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);
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
