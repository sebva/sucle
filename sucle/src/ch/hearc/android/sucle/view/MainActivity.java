package ch.hearc.android.sucle.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import ch.hearc.android.sucle.R;
import ch.hearc.android.sucle.Tools;
import ch.hearc.android.sucle.controller.LoginTask;
import ch.hearc.android.sucle.controller.PostsManager;
import ch.hearc.android.sucle.model.SocialType;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.plus.PlusClient;

public class MainActivity extends Activity implements PlusClient.OnAccessRevokedListener, OnClickListener,
		LoginTask.LoginListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener
{
	private boolean					isResumed					= false;
	private boolean					onConnectionView			= true;
	private static final int		SOCIAL_CONNECTION_FRAGMENT	= 0;
	private static final int		MAIN_FRAGMENT				= 1;
	private static final int		FRAGMENT_COUNT				= MAIN_FRAGMENT + 1;

	private Fragment[]				fragments					= new Fragment[FRAGMENT_COUNT];

	private UiLifecycleHelper		uiHelper;
	private Session.StatusCallback	callback					= new Session.StatusCallback() {
																	@Override
																	public void call(Session session, SessionState state, Exception exception)
																	{
																		onSessionStateChange(session, state, exception);
																	}
																};

	private static final int		REQUEST_CODE				= 0;
	protected static final int		REQUEST_CODE_TOKEN			= 0;
	private PlusClient				mPlusClient;
	private ConnectionResult		mConnectionResult;
	private boolean					askedForGoogle				= false;
	private LocationRequest mLocationRequest;
	private LocationClient mLocationClient;
	private Location currentLocation = null;
	private String mToken = null;
	private String mDeviceId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mPlusClient = new PlusClient.Builder(this, this, this).setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity").setScopes(Scopes.PLUS_LOGIN).build();

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		FragmentManager fm = getFragmentManager();
		fragments[SOCIAL_CONNECTION_FRAGMENT] = fm.findFragmentById(R.id.socialConnectionFragment);
		fragments[MAIN_FRAGMENT] = fm.findFragmentById(R.id.mainFragment);
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++)
		{
			transaction.hide(fragments[i]);
		}
		transaction.commit();
		
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(10000);

        ConnectionCallbacks connectionCallbacks = new GooglePlayServicesClient.ConnectionCallbacks()
		{	
			@Override
			public void onDisconnected()
			{
			}
			
			@Override
			public void onConnected(Bundle connectionHint)
			{
				mLocationClient.requestLocationUpdates(mLocationRequest, MainActivity.this);
			}
		};
        mLocationClient = new LocationClient(this, connectionCallbacks, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (!onConnectionView)
		{
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.main, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle presses on the action bar items
		switch (item.getItemId())
		{
			case R.id.action_map:
				MainFragment mainFragment = (MainFragment) fragments[MAIN_FRAGMENT];
				if (mainFragment.isMapDisplayed())
					mainFragment.changeToList();
				else
					mainFragment.changeToMap();
				return true;
			case R.id.action_new_message:
				if(currentLocation == null)
				{
					Toast.makeText(this, R.string.not_yet_located, Toast.LENGTH_SHORT).show();
					return true;
				}
				Intent intent = new Intent(this, NewMessageActivity.class);
				intent.putExtra("location", currentLocation);
				intent.putExtra("deviceId", mDeviceId);
				intent.putExtra("token", mToken);
				//intent.putExtra("parent", parent);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		mPlusClient.connect();
		mLocationClient.connect();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		mPlusClient.disconnect();
		mLocationClient.disconnect();
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.google_sign_in_button)
		{
			askedForGoogle = true;
			int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			if (available == ConnectionResult.SUCCESS)
			{
				mPlusClient.connect();
			}
			else
				Toast.makeText(this, "Please update Google Play Services", Toast.LENGTH_LONG).show();
		}
	}

	private void showFragment(int fragmentIndex, boolean addToBackStack)
	{
		onConnectionView = fragmentIndex == SOCIAL_CONNECTION_FRAGMENT;
		invalidateOptionsMenu();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++)
		{
			if (i == fragmentIndex)
			{
				transaction.show(fragments[i]);
			}
			else
			{
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack)
		{
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	@Override
	public void onAccessRevoked(ConnectionResult result)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		mConnectionResult = result;
		if (askedForGoogle) try
		{
			mConnectionResult.startResolutionForResult(this, REQUEST_CODE);
		}
		catch (SendIntentException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint)
	{
		getGoogleToken();
	}

	@Override
	public void onDisconnected()
	{
		// TODO Auto-generated method stub

	}

	private void getGoogleToken()
	{
		Log.d(TAG, "getGoogleToken");
		new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params)
			{
				try
				{
					return GoogleAuthUtil.getToken(MainActivity.this, params[0], "audience:server:client_id:1022385909919-sqdskn0j7sugi80hn9osi496mv7cfs52.apps.googleusercontent.com");
				}
				catch (UserRecoverableAuthException e)
				{
					startActivityForResult(e.getIntent(), REQUEST_CODE_TOKEN);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(String token)
			{
				Log.d(TAG, "Server token = " + token);
				Log.d(TAG, "User id = " + mPlusClient.getCurrentPerson().getId());
				loginToSucle(mPlusClient.getCurrentPerson().getId(), token, SocialType.GooglePlus);
			};
		}.execute(mPlusClient.getAccountName());
	}

	@Override
	public void onLogin()
	{
		showFragment(MAIN_FRAGMENT, false);
	}

	private void onSessionStateChange(final Session session, SessionState state, Exception exception)
	{
		// Only make changes if the activity is visible
		if (isResumed)
		{
			FragmentManager manager = getFragmentManager();
			// Get the number of entries in the back stack
			int backStackSize = manager.getBackStackEntryCount();
			// Clear the back stack
			for (int i = 0; i < backStackSize; i++)
			{
				manager.popBackStack();
			}
			if (state.isOpened())
			{
				// If the session state is open:
				// Show the authenticated fragment

				Log.d(FACEBOOK_LOG, "Access Token" + session.getAccessToken());
				Request.newMeRequest(session, new Request.GraphUserCallback() {

					@Override
					public void onCompleted(GraphUser user, Response response)
					{
						if (user != null)
						{
							Log.d(FACEBOOK_LOG, "User ID " + user.getId());
							Log.d(FACEBOOK_LOG, "User Name " + user.getFirstName() + " " + user.getLastName());
							/*
							 * Toast.makeText(MainActivity.this, "Welcome : " +
							 * user.getFirstName() + " " + user.getLastName() +
							 * "\nYour User ID IS : " + user.getId(),
							 * Toast.LENGTH_SHORT) .show();
							 */
							loginToSucle(user.getId(), session.getAccessToken(), SocialType.Facebook);
						}
					}
				}).executeAsync();
			}
			else if (state.isClosed())
			{
				// If the session state is closed:
				// Show the login fragment
				showFragment(SOCIAL_CONNECTION_FRAGMENT, false);
			}
		}
	}

	private void loginToSucle(String id, String token, SocialType type)
	{
		mToken = token;
		String[] params = new String[6];
		params[0] = id;
		params[1] = type.toString();
		params[2] = token;
		mDeviceId = Tools.sha512(Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID));
		params[3] = mDeviceId;
		params[4] = "Android";
		params[5] = Build.VERSION.CODENAME;
		new LoginTask(MainActivity.this).execute(params);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		Session session = Session.getActiveSession();

		if (session != null && session.isOpened())
		{
			// if the session is already open,
			// try to show the selection fragment
			showFragment(MAIN_FRAGMENT, false);
		}
		else
		{
			// otherwise present the splash screen
			// and ask the person to login.
			showFragment(SOCIAL_CONNECTION_FRAGMENT, false);
		}

		uiHelper.onResume();
		isResumed = true;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE)
		{
			if (resultCode == RESULT_OK && !mPlusClient.isConnected() && !mPlusClient.isConnecting())
			{
				// This time, connect should succeed.
				mPlusClient.connect();
			}
		}
		else if (requestCode == REQUEST_CODE_TOKEN)
			getGoogleToken();
		else
			uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private static final String	FACEBOOK_LOG	= "Log : Facebook";
	private static final String	TAG				= "Log : " + MainActivity.class.getSimpleName();

	@Override
	public void onLocationChanged(Location location)
	{
		currentLocation = location;
		Log.e(TAG, "New location " + location.toString());
		PostsManager.getInstance().onLocationChanged(location);
	}
}
