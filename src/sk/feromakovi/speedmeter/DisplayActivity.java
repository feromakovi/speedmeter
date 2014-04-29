package sk.feromakovi.speedmeter;

import sk.feromakovi.speedmeter.util.SystemBrightnessDispatcher;
import sk.feromakovi.speedmeter.util.SystemUiHider;
import sk.feromakovi.speedmeter.view.SpeedTextView;
import sk.feromakovi.speedmeter.view.SpeedTextView.DisplayMode;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class DisplayActivity extends Activity implements LocationListener, OnCheckedChangeListener {
	
	private static final String TAG = "SpeedMeter - " + DisplayActivity.class.getSimpleName();
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	/**
	 * The instance of the {@link SpeedTextView} for this activity. This customized
	 * TextView is used for displaying actual device speed. It offer more diplay modes.
	 */
	private SpeedTextView mSpeedDisplay;
	
	private LocationManager mLocationManager;
	
	private SystemBrightnessDispatcher mBrightnessDispather = null;
	
	private boolean isMetrics = true;
	
	private int mDisplaySize;
	
	private ToggleButton mSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.display_activity_layout);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		mSpeedDisplay = (SpeedTextView) findViewById(R.id.speed_display);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, mSpeedDisplay,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
							
							//in this version should be native ActionBar accesible
							//condition to handle visibiliti of ActionBar
							if(visible) 
								getActionBar().show();
							else 
								getActionBar().hide();
							
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		mSpeedDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		mSwitch = (ToggleButton) findViewById(R.id.control_button);
		mSwitch.setOnTouchListener(mDelayHideTouchListener);
		mSwitch.setOnCheckedChangeListener(this);
		
		this.mDisplaySize = getDisplaySize();
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mBrightnessDispather = new SystemBrightnessDispatcher(getContentResolver());
	}
	
	private int getDisplaySize(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return Math.min(metrics.heightPixels, metrics.widthPixels);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	@Override
	protected void onPause() {
		if(mLocationManager != null)
			mLocationManager.removeUpdates(this);
		if(mBrightnessDispather != null)
			mBrightnessDispather.dispatchOnPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		onRefreshSettings();
		super.onResume();
		if(mSwitch != null && mSwitch.isChecked() && mLocationManager != null && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
		if(mBrightnessDispather != null)
			mBrightnessDispather.dispatchOnResume();
	}

	private static final int RESET_SPEED_MAX_LIMIT = 4 * 1000;

	private Handler mUpdatesHandler = new Handler();
	private Runnable mUpdatesRunnable = new Runnable(){
		@Override
		public void run(){
			mSpeedDisplay.setText("0");
		}
	};
	
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		if(isChecked){
			if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				Log.d(TAG, "requesting location updates");
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
			}else
				mSwitch.setChecked(false);
		}else{
			mLocationManager.removeUpdates(this);
		}
	}	

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged");
		mUpdatesHandler.removeCallbacks(mUpdatesRunnable);
		if(location != null && location.hasSpeed()){
			double locSpeed = transformSpeed(location.getSpeed());
			mSpeedDisplay.setText(String.format("%.0f", new Object[]{locSpeed}));
			mUpdatesHandler.postDelayed(mUpdatesRunnable, RESET_SPEED_MAX_LIMIT);
		}		
	}
	
	private double transformSpeed(double actualSpeed){
		double kmh = actualSpeed * 3.6;
		if(isMetrics)
			return kmh;
		return (kmh / 1.609);
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	public void onSettingsButtonClick(View v){
		startActivity(SettingsActivity.getIntent(this));
	}
	
	private void onRefreshSettings(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.isMetrics = prefs.getString("units_system", "metrics").equalsIgnoreCase("metrics");
		boolean reverse = prefs.getBoolean("reverse_color", false);
		int cText = prefs.getInt("color_text", Color.WHITE);
		int cBackground = prefs.getInt("color_background", Color.BLACK);
		mSpeedDisplay.setTextColor((!reverse) ? cText : cBackground);
		mSpeedDisplay.setBackgroundColor((!reverse) ? cBackground : cText);
		long percentage = Math.round(this.mDisplaySize * 1.3 * prefs.getInt("speed_view_size", 100) / 100);
		mSpeedDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, percentage);
		DisplayMode displayMode = DisplayMode.resolve(Integer.parseInt(prefs.getString("display_mode", "0")));
		this.mSpeedDisplay.setDisplayMode(displayMode);
		String font = prefs.getString("font", "digital.ttf");
		this.mSpeedDisplay.setFont(font);
	}
}
