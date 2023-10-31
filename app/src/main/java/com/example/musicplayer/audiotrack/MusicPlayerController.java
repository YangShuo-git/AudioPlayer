package com.example.musicplayer.audiotrack;

import android.media.AudioManager;
import android.os.Handler;

import com.example.musicplayer.opensles.SoundTrackController;

import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayerController implements SoundTrackController.OnCompletionListener {
    public static final int UPDATE_PLAY_VOICE_PROGRESS = 730;

    public MusicPlayer musicPlayer;

    private boolean isPlaying = false;

    private Timer mTimer;
    private TimerTask myTimerTask = null;
    private Handler mHander;

    private SoundTrackController.OnCompletionListener onCompletionListener;
    public void setOnCompletionListener(SoundTrackController.OnCompletionListener onCompletionListener){
        this.onCompletionListener = onCompletionListener;
        musicPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion() {
        isPlaying = false;
        mHander.sendMessage(mHander.obtainMessage(
                UPDATE_PLAY_VOICE_PROGRESS, getCurrentTimeMills(), isPlaying ? 1 : 0));
        onCompletionListener.onCompletion();
    }

    public int getCurrentTimeMills() {
        return musicPlayer.getCurrentTimeMills();
    }

    public MusicPlayerController() {
        try {
            if (musicPlayer == null) {
                musicPlayer = new MusicPlayer();
                musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
        } catch (Exception e) {
        }
    }

    public int getPlayerCurrentTime() {
        try {
            return musicPlayer.getCurrentTimeMills();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPlayerDuration() {
        try {
            if (null != musicPlayer) {
                return musicPlayer.getDuration();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public boolean setAudioDataSource(String path) {
        boolean result = musicPlayer.setDataSource(path);
        if (result) {
            musicPlayer.prepare();
        }
        return result;
    }

    public void start() {
        timerStop();
        isPlaying = true;
        // timelabelProgress.sendEmptyMessage(0);
        timerStart();
        try {
            if (musicPlayer != null) {
                musicPlayer.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        timerStop();
        try {
            if (musicPlayer != null) {
                musicPlayer.stop();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        isPlaying = false;
    }

    private void timerStop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (myTimerTask != null) {
            myTimerTask.cancel();
            myTimerTask = null;
        }
    }

    private void timerStart() {
        if (mTimer == null) {
            mTimer = new Timer();
            myTimerTask = new MusicTimerTask();
            mTimer.schedule(myTimerTask, 0, 1000);
        }
    }

    class MusicTimerTask extends TimerTask {
        @Override
        public void run() {
            int time = getPlayerCurrentTime();
            if (time != 0) {
                mHander.sendMessage(mHander.obtainMessage(
                        UPDATE_PLAY_VOICE_PROGRESS, time, isPlaying ? 1 : 0));
            }
        }
    }

    public MusicPlayer getMediaPlayer() {
        return musicPlayer;
    }

    public void setHandler(Handler mTimeHandler) {
        this.mHander = mTimeHandler;
    }

    public int getAccompanySampleRate() {
        try {
            if (musicPlayer != null) {
                return musicPlayer.getAccompanySampleRate();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
