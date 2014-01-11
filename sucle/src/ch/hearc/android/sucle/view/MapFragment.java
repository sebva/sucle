package ch.hearc.android.sucle.view;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.controller.PostsManager;
import ch.hearc.android.sucle.model.Post;
import ch.hearc.android.sucle.view.TimelineFragment.OnPostSelectedListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment
{
	private OnPostSelectedListener	mCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.map_fragment, container, false);
		GoogleMap map = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		map.setMyLocationEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.setInfoWindowAdapter(new PostInfoWindowAdapter(getActivity()));
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker)
			{
				mCallback.onPostSelected(Integer.parseInt(marker.getTitle()), false);
			}
		});
		return view;
	}

	public void updatePosts()
	{
		GoogleMap map = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		Location location = PostsManager.getInstance().getLocation();
		if(location != null)
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
		Post[] posts = PostsManager.getInstance().getPosts();
		for (int i = 0; i < posts.length; ++i)
		{
			Post post = posts[i];
			map.addMarker(new MarkerOptions().position(post.getPosition()).title(Integer.toString(i)));
		}
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
