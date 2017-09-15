package ru.sonic.zabbix.pro.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class RangeGraph extends ImageView {
	//private String TAG = this.getClass().getSimpleName();
	private int thumb1X,thumb2X;
	private int thumb1Value, thumb2Value;
	private Paint paint = new Paint();
	private int selectedThumb;
	private onSeekBarChangeListener scl;

	public RangeGraph(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RangeGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RangeGraph(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getHeight() > 0)
			init();
	}

	private void init() {
		thumb1X = 10;
		invalidate();
	}

	public void setOnSeekBarChangeListener(onSeekBarChangeListener scl) {
		this.scl = scl;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//canvas.drawBitmap(thumb, thumb1X - thumbHalfWidth, thumbY, paint);
		if (selectedThumb!=0)
			canvas.drawLine(thumb1X, 0, thumb1X, getHeight(), paint);
		if (selectedThumb==2)
			canvas.drawLine(thumb2X, 0, thumb2X, getHeight(), paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int mx = (int) event.getX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			selectedThumb = 1;
			thumb1X = mx;
			break;
		case MotionEvent.ACTION_MOVE:
			selectedThumb = 2;
			thumb2X = mx;
			break;
		case MotionEvent.ACTION_UP:
			selectedThumb = 0;
			break;
		}

		if (thumb1X < 0)
			thumb1X = 0;

		if (thumb2X < 0)
			thumb2X = 0;

		if (thumb1X > getWidth()-72)
			thumb1X = getWidth()-72;
		if (thumb2X > getWidth()-72)
			thumb2X = getWidth()-72;
		
		if (thumb1X < 24)			
			thumb1X = 24;
		if (thumb2X < 24)
			thumb2X = 24;
		
		if (thumb2X < thumb1X)
			thumb2X = thumb1X;

		invalidate();
		if (scl != null && selectedThumb == 0) {
			calculateThumbValue();
			scl.onSeekBarValueChanged(thumb1Value, thumb2Value);
		}
		return true;
	}

	private void calculateThumbValue() {
		thumb1Value = (100 * (thumb1X)) / (getWidth()-96);
		thumb2Value = (100 * (thumb2X)) / (getWidth()-96);
	}
	
	/*
	private void printLog(String log) {
		Log.i(TAG, log);
	}
	*/

	public interface onSeekBarChangeListener {
		void onSeekBarValueChanged(int Thumb1Value, int Thumb2Value);
	}
}
