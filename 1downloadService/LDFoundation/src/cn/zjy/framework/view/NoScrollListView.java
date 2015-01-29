package cn.zjy.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public final class NoScrollListView extends ListView
{
	public NoScrollListView(Context context) {
		super(context);
	}
	
	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST)); 
	}
}
