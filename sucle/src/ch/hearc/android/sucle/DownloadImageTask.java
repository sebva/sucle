package ch.hearc.android.sucle;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import ch.hearc.android.sucle.model.Attachment;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
{
	private ImageView	imageView;
	private Attachment	attachment;

	public DownloadImageTask(ImageView imageView, Object attachment)
	{
		this.imageView = imageView;
		if (attachment != null) this.attachment = (Attachment) attachment;
	}

	protected Bitmap doInBackground(String... urls)
	{
		String urldisplay = urls[0];
		Bitmap mIcon11 = null;
		try
		{
			InputStream in = new java.net.URL(urldisplay).openStream();
			mIcon11 = BitmapFactory.decodeStream(in);
		}
		catch (Exception e)
		{
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		return mIcon11;
	}

	protected void onPostExecute(Bitmap result)
	{
		if (attachment != null) attachment.setContent(result);
		imageView.setImageBitmap(result);
	}
}
