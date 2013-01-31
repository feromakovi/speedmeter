package sk.feromakovi.speedmeter.view;

import sk.feromakovi.speedmeter.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class SpeedTextView extends TextView{
	
	public enum DisplayMode{ Normal, Mirror, MirrorUpside } 
	
	private DisplayMode mDisplayMode = DisplayMode.Normal;
	
	public SpeedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public SpeedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}
	public SpeedTextView(Context context) {
		super(context);
	}
	
	public void init(AttributeSet attrs){
		if(isInEditMode())
			return;
		TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.sk_feromakovi_speedmeter_view_SpeedTextView);
		String font = array.getString(R.styleable.sk_feromakovi_speedmeter_view_SpeedTextView_font);
		if(font != null){
			Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + font + ".ttf");
			setTypeface(typeFace);
        }        
        int displayMode = array.getInt(R.styleable.sk_feromakovi_speedmeter_view_SpeedTextView_displayMode, 0);
        if(displayMode == 0)
        	mDisplayMode = DisplayMode.Normal;
        else if(displayMode == 1)
        	mDisplayMode = DisplayMode.Mirror;
        else if(displayMode == 2)
        	mDisplayMode = DisplayMode.MirrorUpside;
        array.recycle();
	}
	
	public void setFont(String font){
		Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + font + ".ttf");
        setTypeface(typeFace);
        invalidate();
	}
	
	public void setDisplayMode(DisplayMode mode){
		this.mDisplayMode = mode;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		switch(mDisplayMode){
		case Mirror:
			canvas.translate(getWidth(), 0);
	        canvas.scale(-1, 1);
	        super.onDraw(canvas);
			return;
		case MirrorUpside:
			canvas.translate(getWidth(), 0);
	        canvas.scale(-1, 1);
			canvas.save();
	        float py = this.getHeight()/2.0f;
	        float px = this.getWidth()/2.0f;
	        canvas.rotate(180, px, py);
	        super.onDraw(canvas);
	        canvas.restore();
			return;
		default:
			super.onDraw(canvas);
		}		
	}
}
