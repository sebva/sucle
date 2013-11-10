package ch.hearc.android.sucle;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * An activity representing a list of Posts. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link PostDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PostListFragment} and the item details (if present) is a
 * {@link PostDetailFragment}.
 * <p>
 * This activity also implements the required {@link PostListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class PostsListFragment extends Fragment implements PostListFragment.Callbacks
{

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean	mTwoPane;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.activity_post_list, container, false);
		if (view.findViewById(R.id.post_detail_container) != null)
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((PostListFragment) getFragmentManager().findFragmentById(R.id.post_list)).setActivateOnItemClick(true);
		}
		return view;
	}
	
	/**
	 * Callback method from {@link PostListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id)
	{
		if (mTwoPane)
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(PostDetailFragment.ARG_ITEM_ID, id);
			PostDetailFragment fragment = new PostDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction().replace(R.id.post_detail, fragment).commit();
		}
		else
		{
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(getActivity(), PostDetailActivity.class);
			detailIntent.putExtra(PostDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
