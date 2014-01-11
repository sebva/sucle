package ch.hearc.android.sucle.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Attachment implements Serializable
{
	private transient Object	content;
	private AttachmentType		attachementType;
	private String				filePath;

	public Attachment(Object content, AttachmentType attachementType, String filePath)
	{
		this.content = content;
		this.attachementType = attachementType;
		this.filePath = filePath;

		downloadFile();
	}

	public Object getContent()
	{
		return content;
	}
	
	public String getFilePath()
	{
		return filePath;
	}

	public AttachmentType getAttachementType()
	{
		return attachementType;
	}

	private void downloadFile()
	{
		InputStream test_is;
		try
		{
			test_is = (InputStream) new URL(filePath).getContent();
			Bitmap test_b = BitmapFactory.decodeStream(test_is);
			test_is.close();
			content = test_b;
			return;
		}
		catch (MalformedURLException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// TODO:
		Object output = null;
		InputStream is = null;
		FileOutputStream fos = null;
		HttpURLConnection connection = null;
		try
		{
			connection = (HttpURLConnection) (new URL(filePath)).openConnection();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				is = connection.getInputStream();
				fos = new FileOutputStream("");// TODO
				int count;
				byte data[] = new byte[4096];
				while ((count = is.read()) != -1)
					fos.write(data, 0, count);
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (fos != null) fos.close();
				if (is != null) is.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			if (connection != null) connection.disconnect();
		}
	}

	@Override
	public String toString()
	{
		return attachementType + " " + filePath;
	}
}
