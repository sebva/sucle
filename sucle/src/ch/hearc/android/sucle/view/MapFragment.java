package ch.hearc.android.sucle.view;

import java.util.List;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment
{
	private OnPostSelectedListener	mCallback;
	private Circle					circle;

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

		addCircle();

		return view;
	}

	public void showMap()
	{
		Location location = PostsManager.getInstance().getLocation();
		if (location != null)
		{
			com.google.android.gms.maps.MapFragment mapFragment = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map));
			GoogleMap map = mapFragment.getMap();
			LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			float mapWidth = mapFragment.getView().getWidth() / metrics.scaledDensity;
			double zoom = getZoomForMetersWide(PostsManager.getInstance().getRadius() * 3, mapWidth, center.latitude);
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(center, (float) zoom));
		}
	}

	private void addCircle()
	{
		GoogleMap map = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		circle = map.addCircle(new CircleOptions().center(new LatLng(0, 0)).radius(0).strokeColor(getResources().getColor(R.color.circle_stroke_color))
				.fillColor(getResources().getColor(R.color.circle_fill_color)));
	}

	public void radiusUpdated()
	{
		Location location = PostsManager.getInstance().getLocation();
		if (location != null)
		{

			LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
			circle.setCenter(center);
			circle.setRadius(PostsManager.getInstance().getRadius());
			showMap();
		}
	}

	public void onPostsFetched()
	{
		GoogleMap map = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.clear();
		addCircle();
		radiusUpdated();
		List<Post> posts = PostsManager.getInstance().getPosts();
		for (int i = 0; i < posts.size(); ++i)
			map.addMarker(new MarkerOptions().position(posts.get(i).getPosition()).title(Integer.toString(i)));
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

	/**
	 * http://stackoverflow.com/a/21034310/2648956
	 */
	private static double getZoomForMetersWide(final double desiredMeters, final double mapWidth, final double latitude)
	{
		final int EQUATOR_LENGTH = 40075004;
		final double latitudinalAdjustment = Math.cos(Math.PI * latitude / 180.0);

		final double arg = EQUATOR_LENGTH * mapWidth * latitudinalAdjustment / (desiredMeters * 256.0);

		return Math.log(arg) / Math.log(2.0);
	}
}
