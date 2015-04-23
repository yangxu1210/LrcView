package com.example.demo_lrcview;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.demo_lrcview.MyService.MyBinder;
import com.example.view.LrcView;

public class MainActivity extends Activity {

	private LrcView mLrcView;
	private MyBinder mBinder;
	private int mDuration;

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinder = (MyBinder) service;
			mBinder.playMusic();
			mDuration = mBinder.getDuration();
			mSeekBar.setMax(mDuration);
			setLrcView();
		}
	};
	private SeekBar mSeekBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mLrcView = (LrcView) findViewById(R.id.lrcView);
		mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					mBinder.setProgress(progress);
				}
			}
		});
	}

	private void setLrcView() {
		try {
			InputStream is = getAssets().open("lyric.lrc");
			mLrcView.setLrcList(is); // 传入歌词文件流
			showLrc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 没100毫秒传入歌曲播放进度
	 */
	private void showLrc() {
		mLrcView.postDelayed(new Runnable() {

			@Override
			public void run() {
				int currentPosition = mBinder.getCurrentPosition();
				mLrcView.setIndex(currentPosition, mDuration);
				mLrcView.postDelayed(this, 100);
			}
		}, 100);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent service = new Intent(this, MyService.class);
		startService(service);

		bindService(service, conn, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();

		unbindService(conn);
	}

}
