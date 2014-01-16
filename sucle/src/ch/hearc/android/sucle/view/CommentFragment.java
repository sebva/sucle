package ch.hearc.android.sucle.view;

import java.util.Date;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.hearc.android.sucle.DownloadImageTask;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.controller.PostsAdapter;
import ch.hearc.android.sucle.model.Post;

public class CommentFragment extends Fragment
{
	private View	view;
	private Post	post;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.timeline_row_fragment, container, false);
		return view;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		ProfilePictureView userImageViewFB = (ProfilePictureView) view.findViewById(R.id.profilePictureViewFB);
		RoundedImageView userImageViewGP = (RoundedImageView) view.findViewById(R.id.profilePictureViewGP);
		TextView username = (TextView) view.findViewById(R.id.username);
		TextView postContent = (TextView) view.findViewById(R.id.postContent);
		TextView location = (TextView) view.findViewById(R.id.location);
		TextView postDate = (TextView) view.findViewById(R.id.postDate);

		long delta = ((new Date()).getTime() - post.getTime().getTime());
		username.setText(post.getUser().getSocialId());
		username.setText(post.getUser().getName());
		postContent.setText(post.getMessage());
		location.setText(post.getPositionName());
		postDate.setText(PostsAdapter.ago(delta));

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
				new DownloadImageTask(userImageViewGP, null).execute(post.getUser().getImageUrl());
				break;

			default:
				break;
		}
	}

	public void setPost(Post post)
	{
		this.post = post;
	}
}
