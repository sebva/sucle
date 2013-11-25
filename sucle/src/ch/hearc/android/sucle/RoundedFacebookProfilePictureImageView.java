package ch.hearc.android.sucle;

import java.net.URISyntaxException;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;

import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;

public class RoundedFacebookProfilePictureImageView extends RoundedImageView
{
	public static final String	TAG			= RoundedFacebookProfilePictureImageView.class.getSimpleName();

	private ImageRequest		lastRequest;
	private String				profileId;
	private OnErrorListener		onErrorListener;
	private int					queryWidth	= 500;
	private int					queryHeight	= 500;

	public interface OnErrorListener
	{
		/**
		 * Called when a network or other error is encountered.
		 * 
		 * @param error
		 *            a FacebookException representing the error that was
		 *            encountered.
		 */
		void onError(FacebookException error);
	}

	public RoundedFacebookProfilePictureImageView(Context context)
	{
		super(context);
	}

	public RoundedFacebookProfilePictureImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public RoundedFacebookProfilePictureImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	private void sendImageRequest(boolean allowCachedResponse)
	{
		try
		{
			ImageRequest.Builder requestBuilder = new ImageRequest.Builder(getContext(), ImageRequest.getProfilePictureUrl(profileId, queryWidth, queryHeight));

			ImageRequest request = requestBuilder.setAllowCachedRedirects(allowCachedResponse).setCallerTag(this).setCallback(new ImageRequest.Callback() {
				@Override
				public void onCompleted(ImageResponse response)
				{
					processResponse(response);
				}
			}).build();

			// Make sure to cancel the old request before sending the new one to
			// prevent
			// accidental cancellation of the new request. This could happen if
			// the URL and
			// caller tag stayed the same.
			if (lastRequest != null)
			{
				ImageDownloader.cancelRequest(lastRequest);
			}
			lastRequest = request;

			ImageDownloader.downloadAsync(request);
		}
		catch (URISyntaxException e)
		{
			com.facebook.internal.Logger.log(LoggingBehavior.REQUESTS, Log.ERROR, TAG, e.toString());
		}
	}

	private void processResponse(ImageResponse response)
	{
		// First check if the response is for the right request. We may have:
		// 1. Sent a new request, thus super-ceding this one.
		// 2. Detached this view, in which case the response should be
		// discarded.
		if (response.getRequest() == lastRequest)
		{
			lastRequest = null;
			Bitmap responseImage = response.getBitmap();
			Exception error = response.getError();
			if (error != null)
			{
				OnErrorListener listener = onErrorListener;
				if (listener != null)
				{
					listener.onError(new FacebookException("Error in downloading profile picture for profileId: " + getProfileId(), error));
				}
				else
				{
					com.facebook.internal.Logger.log(LoggingBehavior.REQUESTS, Log.ERROR, TAG, error.toString());
				}
			}
			else if (responseImage != null)
			{
				setImageBitmap(responseImage);

				if (response.isCachedRedirect())
				{
					sendImageRequest(false);
				}
			}
		}
	}

	public String getProfileId()
	{
		return profileId;
	}

	public void setProfileId(String profileId)
	{
		this.profileId = profileId;
		sendImageRequest(true);
	}

	public int getQueryWidth()
	{
		return queryWidth;
	}

	public void setQueryWidth(int queryWidth)
	{
		this.queryWidth = queryWidth;
	}

	public int getQueryHeight()
	{
		return queryHeight;
	}

	public void setQueryHeight(int queryHeight)
	{
		this.queryHeight = queryHeight;
	}
}
