package sk.feromakovi.speedmeter.view;

import sk.feromakovi.speedmeter.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference implements OnSeekBarChangeListener {

	private static final String KEY = "speed_view_size";

	private SeekBar mSeekBar;
	private TextView mStatusText;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPreference(context, attrs);
	}

	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPreference(context, attrs);
	}

	private void initPreference(Context context, AttributeSet attrs) {
		setPersistent(false);
	}

	@Override
	protected View onCreateDialogView() {
		LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View root =  mInflater.inflate(R.layout.preference_seek_bar, null, false);
		this.mSeekBar = (SeekBar) root.findViewById(R.id.Dialog_SeekBarPreference_seekbar);
		this.mStatusText = (TextView) root.findViewById(R.id.Dialog_SeekBarPreference_percentage);
		this.mSeekBar.setOnSeekBarChangeListener(this);
		return root;
	}
	
	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		SharedPreferences sharedPreferences = getSharedPreferences();
		int value = sharedPreferences.getInt(KEY, 100);
		this.mStatusText.setText(value + "%");
		this.mSeekBar.setProgress(value);
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
	        Editor editor = getEditor();
	        editor.putInt(KEY, this.mSeekBar.getProgress());
	        editor.commit();
	    }
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		this.mStatusText.setText(progress + "%");
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
}