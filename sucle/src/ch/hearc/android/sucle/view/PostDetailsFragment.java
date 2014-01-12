package ch.hearc.android.sucle.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import ch.hearc.android.sucle.DownloadImageTask;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.controller.FetchCommentsTask.FetchCommentsListener;
import ch.hearc.android.sucle.controller.PostsManager;
import ch.hearc.android.sucle.model.Post;

public class PostDetailsFragment extends Fragment implements FetchCommentsListener
{
	public final static String	ARG_POSITION	= "position";
	private int					currentPosition	= -1;
	private View				view;
	private ImageView			imageView;
	private VideoView			videoView;
	private MediaPlayer			mediaPlayer;
	private Post				post;

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

		PostsManager.getInstance().setListenerComment(this);

		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.post_details_fragment, container, false);

		Button addCommentButton = (Button) view.findViewById(R.id.addComment);
		addCommentButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0)
			{
				Intent intent = new Intent(getActivity(), NewMessageActivity.class);
				intent.putExtra("location", PostsManager.getInstance().getLocation());
				intent.putExtra("deviceId", MainActivity.mDeviceId);
				intent.putExtra("token", MainActivity.mToken);
				intent.putExtra("parent", post.getParent());
				startActivity(intent);
			}
		});

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

	@Override
	public void onStop()
	{
		super.onStop();
		stopSound();
	}

	public void updatePostView(int position)
	{
		if (position == currentPosition) return;
		currentPosition = position;

		((LinearLayout) (view.findViewById(R.id.commentsFragment))).removeAllViews();

		post = PostsManager.getInstance().getPosts().get(position);

		PostsManager.getInstance().getComments(post.getId());

		TextView postContent = (TextView) view.findViewById(R.id.postContentDetails);
		TextView profileName = (TextView) view.findViewById(R.id.profileName);

		ProfilePictureView userImageViewFB = (ProfilePictureView) view.findViewById(R.id.profilePictureViewFB);
		RoundedImageView userImageViewGP = (RoundedImageView) view.findViewById(R.id.profilePictureViewGP);

		switch (post.getUser().getSocialType())
		{
			case Facebook:
				userImageViewFB.setVisibility(View.VISIBLE);
				userImageViewGP.setVisibility(View.GONE);
				userImageViewFB.setProfileId(post.getUser().getSocialId());
				break;
			case GooglePlus:
				userImageViewFB.setVisibility(View.GONE);
				userImageViewGP.setVisibility(View.VISIBLE);
				new DownloadImageTask(userImageViewGP).execute(post.getUser().getImageUrl());
				break;

			default:
				break;
		}

		profileName.setText(post.getUser().getName());
		postContent.setText(post.getMessage());

		if (imageView != null) imageView.setVisibility(View.GONE);
		if (videoView != null) videoView.setVisibility(View.GONE);
		stopSound();

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
						// videoView.setMediaController(new
						// MediaController(getActivity()));
						videoView.requestFocus();
						videoView.start();
						videoView.setOnTouchListener(new OnTouchListener() {

							@Override
							public boolean onTouch(View v, MotionEvent event)
							{
								if (videoView.isPlaying())
									videoView.pause();
								else
									videoView.start();
								return false;
							}
						});
						videoView.setOnPreparedListener(new OnPreparedListener() {
							@Override
							public void onPrepared(MediaPlayer mp)
							{
								mp.setLooping(true);
								videoView.start();
								Log.e(TAG, "ready");
							}
						});
						layout.addView(videoView);
					}

					playVideoForPath(post.getAttachment().getFilePath());

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
		try
		{
			if (mediaPlayer != null && mediaPlayer.isPlaying())
			{
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		}
		catch (Exception e)
		{
			// nothing to do
		}
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
						out.close();
						stream.close();
						playVideo(tempPath);
					}
					catch (IOException e)
					{
						Log.e(TAG, e.getMessage());
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
				// videoView.start();
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

	@Override
	public void onCommentsFetched()
	{
		Post[] comments = PostsManager.getInstance().getComments();
		for (int i = 0; i < comments.length; ++i)
		{
			if (comments[i].getParent() != post.getId()) continue;
			CommentFragment commentFragment = new CommentFragment();
			commentFragment.setPost(comments[i]);
			getFragmentManager().beginTransaction().add(R.id.commentsFragment, commentFragment, "comment" + comments[i].getId()).commit();
		}
	}
}
