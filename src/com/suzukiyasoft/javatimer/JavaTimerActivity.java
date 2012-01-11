package com.suzukiyasoft.javatimer;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class JavaTimerActivity extends Activity implements OnClickListener,
		View.OnTouchListener {

	private TextView mStatusView;
	private TextView mTextView;
	private Button mStartBtn;
	private Button mStopBtn;
	private Button mResetBtn;

	private Timer mTimer;
	private float mLaptime = 0.0f;
	private int setMinutes = 1;
	private Boolean CountdownToggle = true;
	
	private MediaPlayer mplayer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// 初期化表示
		mStatusView = (TextView) findViewById(R.id.textViewStatus);
		mTextView = (TextView) findViewById(R.id.textView1);
		mStartBtn = (Button) findViewById(R.id.startButton);
		mStopBtn = (Button) findViewById(R.id.stopButton);
		mResetBtn = (Button) findViewById(R.id.resetButton);
		
		
		mStartBtn.setOnClickListener(this);
		mStopBtn.setOnClickListener(this);
		mResetBtn.setOnClickListener(this);
		
		
		mStatusView.setOnTouchListener(this);
		mTextView.setOnTouchListener(this);

		mStopBtn.setEnabled(false);
		mTextView.setText(FtoMinutesSeconds(mLaptime, CountdownToggle));
		
		mplayer = MediaPlayer.create(this, R.raw.v5);
	}

	// ボタンを押した時の処理
	@Override
	public void onClick(View v) {

		final Handler mHandler = new Handler();
		Button btn = (Button) v;

		switch (btn.getId()) {
		case R.id.startButton:
			mStartBtn.setEnabled(false);
			mStopBtn.setEnabled(true);
			if (mTimer == null) {
				mTimer = new Timer(true);
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						// mHandlerを通じてUI Threadへ処理をキューイング
						mHandler.post(new Runnable() {
							public void run() {
								// 実行間隔分を加算処理
								mLaptime += 0.1d;

								// 現在のLapTimeを表示
								mTextView.setText(FtoMinutesSeconds(mLaptime,
										CountdownToggle));
								if (mLaptime > setMinutes * 60) {
									mplayer.start();
									cancel();
								}
							}
						});
					}
				}, 100, 100);
			}
			break;

		case R.id.stopButton:
			mStartBtn.setEnabled(true);
			mStopBtn.setEnabled(false);
			if (mTimer != null) {
				mTimer.cancel();
				mTimer = null;
			}
			break;

		case R.id.resetButton:
			mLaptime = 0.0f;
			mTextView.setText(FtoMinutesSeconds(mLaptime, CountdownToggle));
			break;

		default:
			break;

		}

	}

	// テキストをタッチした時の処理
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		CountdownToggle = CountdownToggle ? false : true;
		mTextView.setText(FtoMinutesSeconds(mLaptime, CountdownToggle));
		String label = CountdownToggle ? "CountDown" : "CountUp";
		mStatusView.setText(label);
		return false;
	}

	//FloatからCountdownToggleによって分秒のStringsを返す
	private String FtoMinutesSeconds(float f, Boolean CountdownToggle) {

		int totaltime = CountdownToggle ? setMinutes * 60 - (int) f : (int) f;
		int Minutes = totaltime / 60;
		int Seconds = totaltime % 60;

		return String.format("%1$02d:%2$02d", Minutes, Seconds);
	}
	
	//回転によるActivityのリセットを抑止　＊AndroidManifestに追記　android:configChanges="orientation"
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  //Log.v("TEST", "onConfigrationChanged was called!!");
	}
	
	protected void onDestroy() {
		super.onDestroy();
		mplayer.release();
	}
}