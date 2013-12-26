package ch.hearc.android.sucle.view;

import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.R.id;
import ch.hearc.android.sucle.R.layout;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

public class MediaPlayerFragment extends Fragment
{

	String		path	= null;

	MediaPlayer	mediaPlayer;
	VideoView	videoView;
	boolean		playing	= false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.e(MediaPlayerFragment.class.getSimpleName(), "oncreate");
		View view = inflater.inflate(R.layout.player_fragment, container, false);

		videoView = (VideoView) view.findViewById(R.id.videoView);
		// mediaPlayer = new MediaPlayer();

		return view;
	}

	public void play()
	{
		if (path != null)
		{
			playing = true;

			videoView.setVideoPath(path);
			videoView.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra)
				{
					return false;
				}
			});
			videoView.start();
		}
	}

	public void stop()
	{
		if (path != null)
		{
			if (playing)
			{
				videoView.stopPlayback();
				playing = false;
			}
		}
	}

	public void setPath(String path)
	{
		this.path = path;
	}

}
