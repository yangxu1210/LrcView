package com.example.demo_lrcview;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {

	private MediaPlayer mPlayer;
	private boolean isPaused = false;

	@Override
	public void onCreate() {
		super.onCreate();
		mPlayer = new MediaPlayer();
	}

	private void play() {
		try {
			AssetFileDescriptor openFd = getAssets().openFd("music.mp3");
			mPlayer.reset();
			mPlayer.setDataSource(openFd.getFileDescriptor(),
					openFd.getStartOffset(), openFd.getLength());
			mPlayer.prepare();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		mPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer arg0) {
				mPlayer.start();
			}
		});
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}

	class MyBinder extends Binder {

		public void playMusic() {
			play();
		}

		// 获取音乐长度
		public int getDuration() {
			return mPlayer.getDuration();
		}

		// 获取当前音乐播放进度
		public int getCurrentPosition() {
			return mPlayer.getCurrentPosition();
		}

		public void setProgress(int progress) {
			mPlayer.seekTo(progress);
		}

	}
}
