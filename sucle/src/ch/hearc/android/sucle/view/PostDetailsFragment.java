package ch.hearc.android.sucle.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.model.Post;

public class PostDetailsFragment extends Fragment
{
	public final static String	ARG_POSITION	= "position";
	private int					currentPosition	= -1;
	private View				view;
	private ImageView			imageView;
	private VideoView			videoView;
	private MediaPlayer			mediaPlayer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null)
		{
			currentPosition = savedInstanceState.getInt(ARG_POSITION);
		}

		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.post_details_fragment, container, false);
		return view;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been
		// applied to the fragment at this point so we can safely call the
		// method
		// below that sets the article text.
		Bundle args = getArguments();
		if (args != null)
		{
			// Set article based on argument passed in
			updatePostView(args.getInt(ARG_POSITION));
		}
		else if (currentPosition != -1)
		{
			// Set article based on saved instance state defined during
			// onCreateView
			updatePostView(currentPosition);
		}
	}

	public void updatePostView(int position)
	{
		if (position == currentPosition) return;
		currentPosition = position;
		Post post = TimelineFragment.postsAdapter.getItem(position);
		TextView postContent = (TextView) view.findViewById(R.id.postContentDetails);

		postContent.setText(post.getMessage());
		if (imageView != null) imageView.setVisibility(View.GONE);
		if (videoView != null) videoView.setVisibility(View.GONE);
		if (mediaPlayer != null && mediaPlayer.isPlaying())
		{
			stopSound();
		}

		if (post.getAttachment() != null)
		{
			switch (post.getAttachment().getAttachementType())
			{
				case Picture:
					if (imageView == null)
					{
						ViewGroup layout = (ViewGroup) view;
						imageView = new ImageView(getActivity());
						layout.addView(imageView);
					}
					imageView.setImageBitmap((Bitmap) post.getAttachment().getContent());
					imageView.setVisibility(View.VISIBLE);
					break;
				case Video:
					if (videoView == null)
					{
						ViewGroup layout = (ViewGroup) view;
						videoView = new VideoView(getActivity());
						videoView.setOnTouchListener(new OnTouchListener() {
							
							@Override
							public boolean onTouch(View v, MotionEvent event)
							{
								if(videoView.isPlaying())
									videoView.pause();
								else
									videoView.start();
								return false;
							}
						});
						videoView.setOnPreparedListener(new OnPreparedListener() {
						    @Override
						    public void onPrepared(MediaPlayer mp) {
						        mp.setLooping(true);
						    }
						});
						layout.addView(videoView);
					}
					//playVideoForPath(post.getAttachment().getFilePath());
					//playVideoForPath("http://www.perezapp.ch/movie.mp4");
					//playVideo("http://www.perezapp.ch/movie.mp4");
					//playVideo(post.getAttachment().getFilePath());
					videoView.setVideoPath(post.getAttachment().getFilePath());
					videoView.start();
					videoView.setVisibility(View.VISIBLE);
					break;
				case Sound:
					mediaPlayer = MediaPlayer.create(getActivity(), Uri.parse(post.getAttachment().getFilePath()));
					mediaPlayer.start();
					break;
				default:
					break;
			}
		}
	}

	private void stopSound()
	{
		mediaPlayer.stop();
		mediaPlayer.release();
	}

	private void playVideoForPath(final String path)
	{
		if (!URLUtil.isNetworkUrl(path))
		{
			playVideo(path);
		}
		else
		{
			new Thread(new Runnable() {

				@Override
				public void run()
				{
					try
					{
						URL url = new URL(path);
						URLConnection cn = url.openConnection();
						cn.connect();
						InputStream stream = cn.getInputStream();
						if (stream == null) throw new RuntimeException("stream is null");
						File temp = File.createTempFile("mediaplayertmp", "dat");
						temp.deleteOnExit();
						String tempPath = temp.getAbsolutePath();
						FileOutputStream out = new FileOutputStream(temp);
						byte buf[] = new byte[128];
						do
						{
							int numread = stream.read(buf);
							if (numread <= 0) break;
							out.write(buf, 0, numread);
						} while (true);
						try
						{
							out.close();
							stream.close();
						}
						catch (IOException ex)
						{
							Log.e(TAG, "error: " + ex.getMessage(), ex);
						}
						playVideo(tempPath);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	private void playVideo(final String path)
	{
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run()
			{
				videoView.setVideoPath(path);
				videoView.start();
				videoView.requestFocus();
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Save the current article selection in case we need to recreate the
		// fragment
		outState.putInt(ARG_POSITION, currentPosition);
	}

	private static final String	TAG	= PostDetailsFragment.class.getSimpleName();
}
