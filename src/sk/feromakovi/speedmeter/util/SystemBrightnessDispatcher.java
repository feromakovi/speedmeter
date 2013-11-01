package sk.feromakovi.speedmeter.util;

import android.content.ContentResolver;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class SystemBrightnessDispatcher {
	
	private ContentResolver mContentResolver;
	private int mBrightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
	private int mScreenBrightness = 255;
	
	public SystemBrightnessDispatcher(ContentResolver contenResolver){
		this.mContentResolver = contenResolver;
	}
	
	public void dispatchOnResume(){
		try {
			mBrightnessMode = Settings.System.getInt(this.mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
			mScreenBrightness = Settings.System.getInt(this.mContentResolver, Settings.System.SCREEN_BRIGHTNESS);
			updateSettings(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL, 255);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void dispatchOnPause(){
		updateSettings(mBrightnessMode, mScreenBrightness);
	}

	private void updateSettings(int brightnessMode, int brightnessValue){
		Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, brightnessMode);
		if(brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
			Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
	}
}
