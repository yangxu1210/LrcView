package com.example.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

/**
 * 自定义绘图歌词，产生滚动效果
 * 
 * @author Administrator
 * 
 */
public class LrcView extends TextView {

	private float width;// 歌词视图宽度
	private float height;// 歌词视图高度
	private Paint currentPaint;// 当前画笔对象
	private Paint notCurrentPaint;// 非当前画笔对象
	private float textHeight = 35;// 文本宽度
	private float textSize;// 普通文本大小
	private float TEXT_SIZE; // 高亮文本大小
	private int index = 0;// list集合下表

	private List<LrcContent> mLrcList = new ArrayList<LrcContent>();

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		/**
		 * 获取屏幕分辨率：DisplayMetrics displayMetrics =
		 * getResources().getDisplayMetrics(); 另一种方法(需要有activity)：DisplayMetrics
		 * dm = new DisplayMetrics();
		 * getWindowManager().getDefaultDisplay().getMetrics(dm)
		 */
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;
		float ratioWidth = (float) screenWidth / 480; // 480、800为调试样机分辨率
		float ratioHeight = (float) screenHeight / 800; // 计算当前分辨率与样机分辨率的倍数

		float RATIO = Math.min(ratioWidth, ratioHeight); // 去取最小值

		textSize = Math.round(22 * RATIO); // round：四舍五入
		TEXT_SIZE = Math.round(26 * RATIO); // 计算当前分辨率应显示的字体大小

		setFocusable(true); // 设置可对焦

		// 高亮部分
		currentPaint = new Paint();
		currentPaint.setAntiAlias(true);// 设置抗锯齿。
		currentPaint.setTextAlign(Paint.Align.CENTER);// 设置文本对其方式

		// 非高亮部分
		notCurrentPaint = new Paint();
		notCurrentPaint.setAntiAlias(true);
		notCurrentPaint.setTextAlign(Paint.Align.CENTER);
	}

	/**
	 * 绘图歌词
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (canvas == null) {
			return;
		}

		currentPaint.setColor(Color.RED);
		notCurrentPaint.setColor(Color.BLACK);

		// 高亮
		currentPaint.setTextSize(TEXT_SIZE); // 设置字体大小，这里的单位是px--像素
		currentPaint.setTypeface(Typeface.SERIF); // 设置字体样式

		// 普通
		notCurrentPaint.setTextSize(textSize);
		notCurrentPaint.setTypeface(Typeface.DEFAULT);

		try {
			setText("");
			canvas.drawText(mLrcList.get(index).getLrcStr(), width / 2,
					height / 2, currentPaint);

			float tempY = height / 2;
			// 画出本句之前的句子
			for (int i = index - 1; i >= 0; i--) {
				// 向上推移
				tempY = tempY - textHeight;
				canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY,
						notCurrentPaint);
			}
			tempY = height / 2;
			// 画出本句之后的句子
			for (int i = index + 1; i < mLrcList.size(); i++) {
				// 往下推移
				tempY = tempY + textHeight;
				canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY,
						notCurrentPaint);
			}
		} catch (Exception e) {
			setText("未找到歌词文件，赶紧去下载吧...");
		}
	}

	/**
	 * 当view大小改变的时候调用的方法
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.width = w;
		this.height = h;
	}

	public void setLrcList(InputStream inputStream) {
		LrcProcess lrcProcess = new LrcProcess();
		lrcProcess.parseLrc(inputStream);
		mLrcList = lrcProcess.getLrcList();
	}

	public void setIndex(int currentPosition, int duration) {
		if (currentPosition < duration) {
			for (int i = 0; i < mLrcList.size(); i++) {
				if (i < mLrcList.size() - 1) {
					if (currentPosition < mLrcList.get(i).getLrcTime()
							&& i == 0) {
						index = i;
					}
					if (currentPosition > mLrcList.get(i).getLrcTime()
							&& currentPosition < mLrcList.get(i + 1)
									.getLrcTime()) {
						index = i;
					}
				}
				if (i == mLrcList.size() - 1
						&& currentPosition > mLrcList.get(i).getLrcTime()) {
					index = i;
				}
			}
		}
		invalidate();
	}

}
