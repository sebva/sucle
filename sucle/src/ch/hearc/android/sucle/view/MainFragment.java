package ch.hearc.android.sucle.view;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.controller.FetchMessagesTask.FetchMessagesListener;
import ch.hearc.android.sucle.controller.PostsManager;

public class MainFragment extends Fragment implements TimelineFragment.OnPostSelectedListener, FetchMessagesListener
{
	private boolean	mapDisplayed;
	private boolean	realStart;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mapDisplayed = false;
		realStart = false;
		View view = inflater.inflate(R.layout.main_fragment, container, false);

		hide(R.id.mapFragment);

		if (getFragmentManager().findFragmentById(R.id.timelineFragment) != null)
		{
			((TimelineFragment) getFragmentManager().findFragmentById(R.id.timelineFragment)).setCallback(this);
		}
		if (getFragmentManager().findFragmentById(R.id.mapFragment) != null)
		{
			((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).setCallback(this);
		}

		setActionBarListNavigation();

		return view;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		PostsManager.getInstance().setListenerMessage(this);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (realStart)
			getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		else
			realStart = true;
		try
		{
			PostsManager.getInstance().getNearbyPosts();
		}
		catch (Exception e)
		{
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public void onPostsFetched()
	{
		((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).onPostsFetched();
		((TimelineFragment) getFragmentManager().findFragmentById(R.id.timelineFragment)).onPostsFetched();
	}

	private void setActionBarListNavigation()
	{
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(getActivity().getActionBar().getThemedContext(), R.array.radius_tilte, android.R.layout.simple_spinner_dropdown_item);
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setListNavigationCallbacks(mSpinnerAdapter, new OnNavigationListener() {
			int[]	radius	= getResources().getIntArray(R.array.radius_values);

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId)
			{
				PostsManager.getInstance().setRadius(radius[itemPosition]);
				((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).radiusUpdated();
				return false;
			}
		});
		// getActivity().getActionBar().setSelectedNavigationItem(2);
	}

	public void changeToMap()
	{
		if (mapDisplayed) return;
		mapDisplayed = true;
		hide(R.id.timelineFragment);
		show(R.id.mapFragment);
		((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).showMap(true);
	}

	public void changeToTimeline()
	{
		if (!mapDisplayed) return;
		mapDisplayed = false;
		hide(R.id.mapFragment);
		show(R.id.timelineFragment);
	}

	public boolean isMapDisplayed()
	{
		return mapDisplayed;
	}

	private void hide(int fragmentId)
	{
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		Fragment fragment = getFragmentManager().findFragmentById(fragmentId);
		ft.hide(fragment);
		ft.commit();
	}

	private void show(int fragmentId)
	{
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		Fragment fragment = getFragmentManager().findFragmentById(fragmentId);
		ft.show(fragment);
		ft.commit();
	}

	@Override
	public void onPostSelected(int position, boolean tabletOnly)
	{
		PostDetailsFragment postDetailsFragment = (PostDetailsFragment) getFragmentManager().findFragmentById(R.id.postDetailsFragment);

		if (postDetailsFragment != null)
		{
			postDetailsFragment.updatePostView(position);
		}
		else if (!tabletOnly)
		{
			PostDetailsFragment newFragment = new PostDetailsFragment();
			Bundle args = new Bundle();
			args.putInt(PostDetailsFragment.ARG_POSITION, position);
			newFragment.setArguments(args);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();

			transaction.add(R.id.activity_fragment_container, newFragment);
			transaction.hide((getFragmentManager()).findFragmentById(R.id.mainFragment));
			transaction.addToBackStack(null);

			transaction.commit();
		}
	}
}
