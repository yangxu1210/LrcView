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
 * �Զ����ͼ��ʣ���������Ч��
 * 
 * @author Administrator
 * 
 */
public class LrcView extends TextView {

	private float width;// �����ͼ���
	private float height;// �����ͼ�߶�
	private Paint currentPaint;// ��ǰ���ʶ���
	private Paint notCurrentPaint;// �ǵ�ǰ���ʶ���
	private float textHeight = 35;// �ı����
	private float textSize;// ��ͨ�ı���С
	private float TEXT_SIZE; // �����ı���С
	private int index = 0;// list�����±�

	private List<LrcContent> mLrcList = new ArrayList<LrcContent>();

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		/**
		 * ��ȡ��Ļ�ֱ��ʣ�DisplayMetrics displayMetrics =
		 * getResources().getDisplayMetrics(); ��һ�ַ���(��Ҫ��activity)��DisplayMetrics
		 * dm = new DisplayMetrics();
		 * getWindowManager().getDefaultDisplay().getMetrics(dm)
		 */
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;
		float ratioWidth = (float) screenWidth / 480; // 480��800Ϊ���������ֱ���
		float ratioHeight = (float) screenHeight / 800; // ���㵱ǰ�ֱ����������ֱ��ʵı���

		float RATIO = Math.min(ratioWidth, ratioHeight); // ȥȡ��Сֵ

		textSize = Math.round(22 * RATIO); // round����������
		TEXT_SIZE = Math.round(26 * RATIO); // ���㵱ǰ�ֱ���Ӧ��ʾ�������С

		setFocusable(true); // ���ÿɶԽ�

		// ��������
		currentPaint = new Paint();
		currentPaint.setAntiAlias(true);// ���ÿ���ݡ�
		currentPaint.setTextAlign(Paint.Align.CENTER);// �����ı����䷽ʽ

		// �Ǹ�������
		notCurrentPaint = new Paint();
		notCurrentPaint.setAntiAlias(true);
		notCurrentPaint.setTextAlign(Paint.Align.CENTER);
	}

	/**
	 * ��ͼ���
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (canvas == null) {
			return;
		}

		currentPaint.setColor(Color.RED);
		notCurrentPaint.setColor(Color.BLACK);

		// ����
		currentPaint.setTextSize(TEXT_SIZE); // ���������С������ĵ�λ��px--����
		currentPaint.setTypeface(Typeface.SERIF); // ����������ʽ

		// ��ͨ
		notCurrentPaint.setTextSize(textSize);
		notCurrentPaint.setTypeface(Typeface.DEFAULT);

		try {
			setText("");
			canvas.drawText(mLrcList.get(index).getLrcStr(), width / 2,
					height / 2, currentPaint);

			float tempY = height / 2;
			// ��������֮ǰ�ľ���
			for (int i = index - 1; i >= 0; i--) {
				// ��������
				tempY = tempY - textHeight;
				canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY,
						notCurrentPaint);
			}
			tempY = height / 2;
			// ��������֮��ľ���
			for (int i = index + 1; i < mLrcList.size(); i++) {
				// ��������
				tempY = tempY + textHeight;
				canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY,
						notCurrentPaint);
			}
		} catch (Exception e) {
			setText("δ�ҵ�����ļ����Ͻ�ȥ���ذ�...");
		}
	}

	/**
	 * ��view��С�ı��ʱ����õķ���
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
