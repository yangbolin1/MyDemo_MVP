package com.ccdt.app.tv.mvpdemo.view.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonEx extends android.support.v7.widget.AppCompatButton {

	public ButtonEx(Context context) {
		super(context);
		init(context);
	}

	public ButtonEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ButtonEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {

	}
	float dx=0.01f;
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		if(isFocused())
		{
			float alp=getAlpha();
			alp=alp+dx;
			if(alp>=1)
			{
				alp=1;
				dx=-0.02f;
			}
			if(alp<=0.4)
			{
				alp=0.4f;
				dx=0.02f;
			}
			setAlpha(alp);
			postInvalidateDelayed(0);
		}
		else
		{
			setAlpha(1);
		}
	}
	
}
