package cn.zjy.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public final class OneShotGallery extends Gallery
{
    public OneShotGallery(Context context)
    {
	super(context);
    }

    public OneShotGallery(Context context, AttributeSet attrs)
    {
	super(context, attrs);
    }

    public OneShotGallery(Context context, AttributeSet attrs, int defStyle)
    {
	super(context, attrs, defStyle);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
	int keyCode;
	if (isScrollingLeft(e1, e2))
	{
	    keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
	}
	else
	{
	    keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
	}
	onKeyDown(keyCode, null);
	return true;
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2)
    {
	return e2.getX() > e1.getX();
    }
}
