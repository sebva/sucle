package ch.hearc.android.sucle;

import java.io.IOException;
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
	private int width;
	private int height;
	
	public DownloadImageTask(ImageView imageView, Attachment attachment, int width, int height)
	{
		this.imageView = imageView;
		this.attachment = attachment;
		this.width = width;
		this.height = height;
	}

	protected Bitmap doInBackground(String... urls)
	{
		String urldisplay = urls[0];
		Bitmap mIcon11 = null;
		InputStream in = null;
		try
		{
			in = new java.net.URL(urldisplay).openStream();
			
		    // First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeStream(in, null, options);

		    // Calculate inSampleSize
		    options.inSampleSize = Tools.calculateInSampleSize(options, width, height);

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    mIcon11 = BitmapFactory.decodeStream(in, null, options);
		}
		catch (Exception e)
		{
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(in != null)
					in.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return mIcon11;
	}

	protected void onPostExecute(Bitmap result)
	{
		if (attachment != null) attachment.setContent(result);
		imageView.setImageBitmap(result);
	}
}
