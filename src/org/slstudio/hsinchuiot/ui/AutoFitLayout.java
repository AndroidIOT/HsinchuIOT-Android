package org.slstudio.hsinchuiot.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class AutoFitLayout extends ViewGroup{
	private final static int VIEW_MARGIN = 2;
	
	public AutoFitLayout(Context context){
		super(context);
	}
	
	public AutoFitLayout(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public AutoFitLayout(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		for (int index = 0; index < getChildCount(); index++){
			final View child = getChildAt(index);
			// measure
			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	
	@Override
	protected void onLayout(boolean arg0, int l, int t, int r, int b) {
		//Log.d(TAG, "changed = " + arg0 + " left = " + arg1 + " top = " + arg2+ " right = " + arg3 + " botom = " + arg4);
		final int count = getChildCount();
		int row = 0;// which row lay you view relative to parent
		int lengthX = l; // right position of child relative to parent
		int lengthY = t; // bottom position of child relative to parent
		for (int i = 0; i < count; i++) {

			final View child = this.getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			lengthX += width + VIEW_MARGIN;
			lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height + t;
			
			// if it can't drawing on a same line , skip to next line
			if (lengthX > r) {
				lengthX = width + VIEW_MARGIN + l;
				row++;
				lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height + t;
			}

			child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
		}

	}

}
