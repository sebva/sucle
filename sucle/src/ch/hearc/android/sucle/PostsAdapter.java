package ch.hearc.android.sucle;

import java.util.Date;

import ch.hearc.android.sucle.model.Post;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PostsAdapter extends ArrayAdapter<Post>
{

	private final int	postsItemLayoutResource;

	public PostsAdapter(final Context context, final int postsItemLayoutResource)
	{
		super(context, 0);
		this.postsItemLayoutResource = postsItemLayoutResource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = getWorkingView(convertView);
		ViewHolder viewHolder = getViewHolder(view);
		Post post = getItem(position);

		long delta = ((new Date()).getTime() - post.getTime().getTime());
		viewHolder.username.setText(Integer.toString(post.getUser().getSocialId()));
		viewHolder.username.setText(post.getUser().getName());
		viewHolder.postContent.setText(post.getMessage());
		viewHolder.location.setText(post.getPositionName());
		viewHolder.postDate.setText(ago(delta));
		viewHolder.userImageView.setProfileId(Integer.toString(post.getUser().getSocialId()));
		if (post.getAttachment() != null)
		{
			switch (post.getAttachment().getAttachementType())
			{
				case Picture:
					Bitmap image = (Bitmap) post.getAttachment().getContent();
					viewHolder.attachmentImageView.setImageBitmap(image);
					break;
				case Video:
					viewHolder.attachmentImageView.setImageResource(android.R.drawable.ic_media_play);;
					break;
				case Sound:
					viewHolder.attachmentImageView.setImageResource(R.drawable.ic_sound);;
					break;
				default:
					break;
			}
			viewHolder.attachmentImageView.setVisibility(View.VISIBLE);
		}
		else
		{
			viewHolder.attachmentImageView.setVisibility(View.GONE);
		}

		return view;
	}

	private static String ago(long ago)
	{
		String agoString;
		if (ago < 1000)
		{
			agoString = ago + "ms ago";
		}
		else if (ago / 1000 < 60)
		{
			agoString = ago / 1000 + "sec ago";
		}
		else if (ago / 1000 / 60 < 60)
		{
			agoString = ago / 1000 / 60 + "min ago";
		}
		else if (ago / 1000 / 60 / 60 < 24)
		{
			agoString = ago / 1000 / 60 / 60 + "hours ago";
		}
		else
		{
			agoString = ago / 1000 / 60 / 60 / 24 + "days ago";
		}
		return agoString;
	}

	private View getWorkingView(View convertView)
	{
		// The workingView is basically just the convertView re-used if possible
		// or inflated new if not possible
		View workingView = null;

		if (convertView == null)
		{
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			workingView = inflater.inflate(postsItemLayoutResource, null);
		}
		else
		{
			workingView = convertView;
		}

		return workingView;
	}

	private ViewHolder getViewHolder(View workingView)
	{
		Object tag = workingView.getTag();
		ViewHolder viewHolder = null;

		if (null == tag || !(tag instanceof ViewHolder))
		{
			viewHolder = new ViewHolder();

			viewHolder.userImageView = (ProfilePictureView) workingView.findViewById(R.id.profilePictureView);
			viewHolder.username = (TextView) workingView.findViewById(R.id.username);
			viewHolder.postContent = (TextView) workingView.findViewById(R.id.postContent);
			viewHolder.location = (TextView) workingView.findViewById(R.id.location);
			viewHolder.postDate = (TextView) workingView.findViewById(R.id.postDate);
			viewHolder.attachmentImageView = (ImageView) workingView.findViewById(R.id.attachmentImageView);

			workingView.setTag(viewHolder);

		}
		else
		{
			viewHolder = (ViewHolder) tag;
		}

		return viewHolder;
	}

	/**
	 * ViewHolder allows us to avoid re-looking up view references Since views
	 * are recycled, these references will never change
	 */
	private static class ViewHolder
	{
		public ProfilePictureView	userImageView;
		public TextView				username;
		public TextView				postContent;
		public TextView				location;
		public TextView				postDate;
		public ImageView			attachmentImageView;
	}
}
