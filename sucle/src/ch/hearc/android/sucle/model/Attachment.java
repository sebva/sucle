package ch.hearc.android.sucle.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Attachment implements Serializable
{
	private transient Object content;
	private AttachmentType	 attachementType;
	private String			 filePath;

	public Attachment(Object content, AttachmentType attachementType, String filePath)
	{
		this.content = content;
		this.attachementType = attachementType;
		this.filePath = filePath;
	}

	public Object getContent()
	{
		return content;
	}

	public AttachmentType getAttachementType()
	{
		return attachementType;
	}

	private Object downloadFile(String url)
	{
		Object output = null;
		InputStream is = null;
		FileOutputStream fos = null;
		HttpURLConnection connection = null;
		try 
		{
			connection = (HttpURLConnection) (new URL(url)).openConnection();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				is = connection.getInputStream();
				fos = new FileOutputStream("");//TODO
				int count;
				byte data[] = new byte[4096];
				while((count = is.read()) != -1)
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
				if(fos != null)
					fos.close();
				if(is != null)
					is.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}

			if(connection != null)
				connection.disconnect();
		}
		
		return output;
	}
	
	@Override
	public String toString() {
		return attachementType + " " + filePath;
	}
}
