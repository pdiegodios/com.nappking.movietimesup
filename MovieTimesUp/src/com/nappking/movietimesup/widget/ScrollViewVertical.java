package com.nappking.movietimesup.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class ScrollViewVertical extends ScrollView{
	private boolean mPaging = true;
	
	public ScrollViewVertical(Context context) {
	    super(context);
	}

	public boolean onTouchEvent(MotionEvent evt) {
	    if (evt.getAction() == MotionEvent.ACTION_UP)
	        if (mPaging){
	            centralizeContent();
	            return true;
	        }

	    return super.onTouchEvent(evt);
	}

	private void centralizeContent() {
	    int currentY = getScrollY() + getHeight() / 2;
	    ViewGroup content = (ViewGroup) getChildAt(0);
	    for (int i = 0; i < content.getChildCount(); i++) {
	        View child = content.getChildAt(i);
	        if (child.getTop() < currentY && child.getBottom() > currentY) {
	            smoothScrollTo(0, child.getTop());
	            break;
	        }
	    }
	}
	
}
