package ch.hearc.android.sucle.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.model.Post;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements TimelineFragment.OnPostSelectedListener
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.map_fragment, container, false);
		GoogleMap map = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setInfoWindowAdapter(new PostInfoWindowAdapter(getActivity()));
		return view;
	}

	public void updatePosts()
	{
		GoogleMap map = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		for (int i = 0; i < TimelineFragment.postsAdapter.getCount(); ++i)
		{
			Post post = TimelineFragment.postsAdapter.getItem(i);
			map.addMarker(new MarkerOptions().position(post.getPosition()).title(Integer.toString(i)));
		}
	}

	@Override
	public void onPostSelected(int position)
	{
		// TODO Auto-generated method stub

	}
}
