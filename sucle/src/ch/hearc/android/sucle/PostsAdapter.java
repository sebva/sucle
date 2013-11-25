package ch.hearc.android.sucle;

import java.util.Date;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.hearc.android.sucle.model.Post;

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
		post.getUser().loadFullName(viewHolder.username);
		viewHolder.postContent.setText("Dummy message");// TODO: message
		viewHolder.location.setText(post.getPosition().latitude + ", " + post.getPosition().longitude);
		viewHolder.postDate.setText(delta / 1000 + "sec ago");
		viewHolder.userImageView.setProfileId(Integer.toString(post.getUser().getSocialId()));
		// viewHolder.attachmentImageView;
		// viewHolder.locationImageView;

		return view;
	}

	private View getWorkingView(View convertView)
	{
		// The workingView is basically just the convertView re-used if possible
		// or inflated new if not possible
		View workingView = null;

//		if (convertView == null)
//		{
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			workingView = inflater.inflate(postsItemLayoutResource, null);
//		}
//		else
//		{
//			workingView = convertView;
//		}

		return workingView;
	}

	private ViewHolder getViewHolder(View workingView)
	{
		Object tag = workingView.getTag();
		ViewHolder viewHolder = null;

		if (null == tag || !(tag instanceof ViewHolder))
		{
			viewHolder = new ViewHolder();

			viewHolder.userImageView = (RoundedFacebookProfilePictureImageView) workingView.findViewById(R.id.userImageView);
			viewHolder.username = (TextView) workingView.findViewById(R.id.username);
			viewHolder.postContent = (TextView) workingView.findViewById(R.id.postContent);
			viewHolder.locationImageView = (ImageView) workingView.findViewById(R.id.locationImageView);
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
		public RoundedFacebookProfilePictureImageView	userImageView;
		public TextView									username;
		public TextView									postContent;
		public ImageView								locationImageView;
		public TextView									location;
		public TextView									postDate;
		public ImageView								attachmentImageView;
	}
}
