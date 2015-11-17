package org.slstudio.hsinchuiot.widget;

import org.slstudio.hsinchuiot.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class CurtainMenu extends RelativeLayout implements OnTouchListener{
	
	private Context mContext;
	
	private Scroller mScroller;
	
	
	private int downY = 0;
	private int moveY = 0;
	private int scrollY = 0;
	private int upY = 0;
	private int curtainHeigh = 0;
	private boolean isOpen = false;
	private boolean isMove = false;
	private ImageView img_curtain_rope;
	private RelativeLayout layout_menu;
	private int upDuration = 1000;
	private int downDuration = 500;
	
	public CurtainMenu(Context context) {
		super(context);
		init(context, null);
	}

	public CurtainMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(Context context,  AttributeSet attrs) {
		this.mContext = context;
		
		Interpolator interpolator = new BounceInterpolator();
		mScroller = new Scroller(context, interpolator);
		
		WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		
		this.setBackgroundColor(Color.argb(0, 0, 0, 0));
		
		//RelativeLayout curtainLayout = new RelativeLayout(context, attrs);
		//curtainLayout.setId(R.id.curtain_layout);
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.curtainmenu, null);  
        
		layout_menu =(RelativeLayout) view.findViewById(R.id.layout_curtain_menu);
		
		img_curtain_rope = (ImageView)view.findViewById(R.id.img_curtain_rope);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			
		addViewInternal(view, params);
		
		
		layout_menu.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				curtainHeigh  = layout_menu.getHeight();
				CurtainMenu.this.scrollTo(0, curtainHeigh);
			}
		});
		img_curtain_rope.setOnTouchListener(this);
	}
	
	
	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		layout_menu.addView(child, index, params);
	}
	
	@Override
	public void addView(View child, ViewGroup.LayoutParams params) {
		layout_menu.addView(child, params);
	}
	
	@Override
	public void addView(View child) {
		layout_menu.addView(child);
	}
	
	/**
	 * Used internally for adding view. Need because we override addView to
	 * pass-through to the Refreshable View
	 */
	protected final void addViewInternal(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
	}

	/**
	 * Used internally for adding view. Need because we override addView to
	 * pass-through to the Refreshable View
	 */
	protected final void addViewInternal(View child, ViewGroup.LayoutParams params) {
		super.addView(child, -1, params);
	}
	
	protected final void addViewInternal(View child){
		super.addView(child);
	}
	
	public void startMoveAnim(int startY, int dy, int duration) {
		isMove = true;
		mScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}
	
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
			isMove = true;
		} else {
			isMove = false;
		}
		super.computeScroll();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (!isMove) {
			int offViewY = 0;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downY = (int) event.getRawY();
				offViewY = downY - (int)event.getX();
				return true;
			case MotionEvent.ACTION_MOVE:
				moveY = (int) event.getRawY();
				scrollY = moveY - downY;
				if (scrollY < 0) {
					if(isOpen){
						if(Math.abs(scrollY) <= layout_menu.getBottom() - offViewY){
							scrollTo(0, -scrollY);
						}
					}
				} else {
					if(!isOpen){
						if (scrollY <= curtainHeigh) {
							scrollTo(0, curtainHeigh - scrollY);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				upY = (int) event.getRawY();
				if(Math.abs(upY - downY) < 10){
					onRopeClick();
					break;
				}
				if (downY > upY) {
					if(isOpen){
						if (Math.abs(scrollY) > curtainHeigh / 2) {
							startMoveAnim(this.getScrollY(),
									(curtainHeigh - this.getScrollY()), upDuration);
							isOpen = false;
						} else {
							startMoveAnim(this.getScrollY(), -this.getScrollY(),upDuration);
							isOpen = true;
						}
					}
				} else {
					if (scrollY > curtainHeigh / 2) {
						startMoveAnim(this.getScrollY(), -this.getScrollY(),upDuration);
						isOpen = true;
					} else {
						startMoveAnim(this.getScrollY(),(curtainHeigh - this.getScrollY()), upDuration);
						isOpen = false;
					}
				}
				break;
			default:
				break;
			}
		}
		return false;
	}
	
	public void onRopeClick(){
		if(isOpen){
			CurtainMenu.this.startMoveAnim(0, curtainHeigh, upDuration);
		}else{
			CurtainMenu.this.startMoveAnim(curtainHeigh,-curtainHeigh, downDuration);
		}
		isOpen = !isOpen;
	}
}

