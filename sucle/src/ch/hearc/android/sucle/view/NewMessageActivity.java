package ch.hearc.android.sucle.view;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.controller.SendMessageTask;

public class NewMessageActivity extends Activity
{
	private static final int MAX_CHARS = 160;
	private static final int RESULT_LOAD_MEDIA = 0;
	private static final int RESULT_CAPTURE_MEDIA = 1;
	private Location mLocation;
	private int mParentMessageId;
	private String mToken;
	private String mDeviceId;
	private String mFilePath = null;
	private EditText mMessage;
	private TextView mCounterTextView;
	private TextView mGeocodeTextView;
	private ImageView mImageView;
	private String mCurrentPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_message);
		// Show the Up button in the action bar.
		setupActionBar();
		
		setTitle(R.string.new_message);
		mMessage = (EditText) findViewById(R.id.message_text);
		mGeocodeTextView = (TextView) findViewById(R.id.geocodeTextView);
		mImageView = (ImageView) findViewById(R.id.imageView);
		
		mLocation = getIntent().getParcelableExtra("location");
		mToken = getIntent().getStringExtra("token");
		mDeviceId = getIntent().getStringExtra("deviceId");
		mParentMessageId = getIntent().getIntExtra("parent", -1);
		
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(5);
		mGeocodeTextView.setText(getResources().getString(R.string.sent_from) + " " + 
				format.format(mLocation.getLatitude()) + ", " + format.format(mLocation.getLongitude()));
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mMessage, InputMethodManager.SHOW_IMPLICIT);
		
		new AsyncTask<Double, Void, String>()
		{

			@Override
			protected String doInBackground(Double... params)
			{
				try
				{
					Address address = new Geocoder(NewMessageActivity.this).getFromLocation(params[0], params[1], 1).get(0);
					int nbLines = address.getMaxAddressLineIndex();
					if(nbLines > 0)
					{
						String[] str = new String[nbLines];
						for(int i = 0; i < nbLines; i++)
							str[i] = address.getAddressLine(i);
						return TextUtils.join(", ", str);
					}
					else
					{
						this.cancel(false);
						return null;
					}
				}
				catch (IOException e)
				{
					Log.w("Geocoder", "Unable to compute reverse geocode");
					this.cancel(false);
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(String result)
			{
				mGeocodeTextView.setText(getResources().getString(R.string.sent_from) + " " + result);
			}
		}.execute(mLocation.getLatitude(), mLocation.getLongitude());
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar()
	{
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_message, menu);
		if(mParentMessageId != -1)
			menu.findItem(R.id.action_attach_file).setVisible(false);
		
		mCounterTextView = (TextView) menu.findItem(R.id.char_counter).getActionView();
		mCounterTextView.setText(String.valueOf(MAX_CHARS));
		mMessage.addTextChangedListener(new TextWatcher()
		{
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				mCounterTextView.setText(String.valueOf(MAX_CHARS - mMessage.getText().length()));
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent i;
		switch (item.getItemId())
		{
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_post:
				postMessage();
				break;
			case R.id.action_attach_photo:
				i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_MEDIA);
				break;
			case R.id.action_attach_video:
				i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_MEDIA);
				break;
			case R.id.action_attach_audio:
				i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_MEDIA);
				break;
			case R.id.action_take_photo:
				i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				File photoFile = null;
				try
				{
					photoFile = createImageFile();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
					return false;
				}
				// Continue only if the File was successfully created
				if (photoFile != null)
				{
					i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
					startActivityForResult(i, RESULT_CAPTURE_MEDIA);
				}
				break;
			case R.id.action_capture_video:
				i = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
				File videoFile = null;
				try
				{
					videoFile = createVideoFile();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
					return false;
				}
				// Continue only if the File was successfully created
				if (videoFile != null)
				{
					i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
					startActivityForResult(i, RESULT_CAPTURE_MEDIA);
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_MEDIA && resultCode == RESULT_OK && null != data)
		{
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mFilePath = cursor.getString(columnIndex);
            cursor.close();
            
            Uri uri = data.getData();
            List<String> pathSegments = uri.getPathSegments();
			if(pathSegments.contains("audio"))
				mImageView.setImageResource(R.drawable.ic_sound);
			else if(pathSegments.contains("video"))
				mImageView.setImageResource(android.R.drawable.ic_media_play);
			else
				mImageView.setImageURI(uri);
        }
		else if (requestCode == RESULT_CAPTURE_MEDIA && resultCode == RESULT_OK)
		{
            mFilePath = mCurrentPath;
			if(mFilePath.endsWith(".jpg"))
				mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPath));
			else
	        	mImageView.setImageResource(android.R.drawable.ic_media_play);
        }
	}

	private File createImageFile() throws IOException
	{
	    // Create an image file name
	    String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPath = image.getAbsolutePath();
	    return image;
	}
	
	private File createVideoFile() throws IOException
	{
	    // Create an image file name
	    String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
	    String videoFileName = "MOVIE_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_MOVIES);
	    File video = File.createTempFile(
	        videoFileName,  /* prefix */
	        ".mp4",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPath = video.getAbsolutePath();
	    return video;
	}
	
	private void postMessage()
	{
		if(mMessage.getText().length() <= 0)
			return;
		
		new SendMessageTask().execute(
				mToken,
				mDeviceId,
				mMessage.getText().toString(),
				Double.toString(mLocation.getLatitude()),
				Double.toString(mLocation.getLongitude()),
				Integer.toString(mParentMessageId),
				mFilePath);
		finish();
	}

}
