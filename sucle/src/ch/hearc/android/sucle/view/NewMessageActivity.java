package ch.hearc.android.sucle.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.controller.SendMessageTask;

public class NewMessageActivity extends Activity
{
	
	private static final int RESULT_LOAD_MEDIA = 0;
	private Location mLocation;
	private int mParentMessageId;
	private String mToken;
	private String mDeviceId;
	private String mFilePath = null;
	private EditText mMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_message);
		// Show the Up button in the action bar.
		setupActionBar();
		
		setTitle(R.string.new_message);
		mMessage = (EditText) findViewById(R.id.message_text);
		
		mLocation = getIntent().getParcelableExtra("location");
		mToken = getIntent().getStringExtra("token");
		mDeviceId = getIntent().getStringExtra("deviceId");
		mParentMessageId = getIntent().getIntExtra("parent", -1);
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mMessage, InputMethodManager.SHOW_IMPLICIT);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar()
	{
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_message, menu);
		if(mParentMessageId != -1)
			menu.findItem(R.id.action_attach_file).setVisible(false);
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
        }
	}

	private void postMessage()
	{
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
