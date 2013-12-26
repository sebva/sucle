package ch.hearc.android.sucle.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.hearc.android.sucle.R;

public class MainFragment extends Fragment implements TimelineFragment.OnPostSelectedListener
{
	private boolean	mapDisplayed;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mapDisplayed = false;
		View view = inflater.inflate(R.layout.main_fragment, container, false);

		hide(R.id.mapFragment);

		/*
		 * if (view.findViewById(R.id.fragment_container) != null) {
		 * TimelineFragment timelineFragment = new TimelineFragment();
		 * 
		 * timelineFragment.setCallback(this);
		 * 
		 * // Add the fragment to the 'fragment_container' FrameLayout
		 * getFragmentManager().beginTransaction().add(R.id.fragment_container,
		 * timelineFragment).commit(); } else
		 */if (getFragmentManager().findFragmentById(R.id.timelineFragment) != null)
		{
			((TimelineFragment) getFragmentManager().findFragmentById(R.id.timelineFragment)).setCallback(this);
		}
		return view;
	}

	public void changeToMap()
	{
		if (mapDisplayed) return;
		mapDisplayed = true;
		hide(R.id.timelineFragment);
		show(R.id.mapFragment);
		((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).updatePosts();
	}

	public void changeToList()
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
	public void onPostSelected(int position)
	{
		PostDetailsFragment postDetailsFragment = (PostDetailsFragment) getFragmentManager().findFragmentById(R.id.postDetailsFragment);

		if (postDetailsFragment != null)
		{
			postDetailsFragment.updatePostView(position);
		}
		else
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
