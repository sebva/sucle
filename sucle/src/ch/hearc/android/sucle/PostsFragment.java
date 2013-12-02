package ch.hearc.android.sucle;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PostsFragment extends Fragment implements TimelineFragment.OnPostSelectedListener
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_timeline, container, false);
		
/*
		if (view.findViewById(R.id.fragment_container) != null)
		{
			TimelineFragment timelineFragment = new TimelineFragment();

			timelineFragment.setCallback(this);

			// Add the fragment to the 'fragment_container' FrameLayout
			getFragmentManager().beginTransaction().add(R.id.fragment_container, timelineFragment).commit();
		}
		else */if (getFragmentManager().findFragmentById(R.id.timelineFragment) != null)
		{
			((TimelineFragment) getFragmentManager().findFragmentById(R.id.timelineFragment)).setCallback(this);
		}
		return view;
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
			transaction.hide((getFragmentManager()).findFragmentById(R.id.postListFragment));
			transaction.addToBackStack(null);

			transaction.commit();
		}
	}
}
