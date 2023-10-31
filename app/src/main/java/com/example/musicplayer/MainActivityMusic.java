package com.example.musicplayer;

import static com.example.musicplayer.service.MusicService.ACTION_OPT_MUSIC_VOLUME;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.musicplayer.service.MusicService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MainActivityMusic extends AppCompatActivity {
    private static final String TAG = "MainActivityMusic";
    private Button audioTrackPlayBtn;
    private Button audioTrackStopBtn;
    /** 要播放的文件路径 **/
    private static String playFilePath = "131.mp3";

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_music);
        checkPermission();

        CopyAssets(getApplicationContext(), playFilePath,
                getApplicationContext().getFilesDir().getAbsolutePath(), playFilePath);
        findView();
        bindListener();
    }

    private void findView() {
        audioTrackPlayBtn = (Button) findViewById(R.id.play_audiotrack_btn);
        audioTrackStopBtn = (Button) findViewById(R.id.stop_audiotrack_btn);
    }

    private void bindListener() {
        audioTrackPlayBtn.setOnClickListener(audioTrackPlayBtnListener);
        audioTrackStopBtn.setOnClickListener(audioTrackStopBtnListener);
    }

    View.OnClickListener audioTrackPlayBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click AudioTrack Play Btn");
            if (null == audioTrackPlayerController) {
                audioTrackPlayerController = new MusicPlayerController();
                audioTrackPlayerController.setHandler(handler);
                audioTrackPlayerController.setOnCompletionListener(new SoundTrackController.OnCompletionListener() {
                    @Override
                    public void onCompletion() {
                        // 发消息随后去执行结束PLAYER，否则会卡死；Toast也是在后面做
                        handler.sendMessage(handler.obtainMessage(
                                AUDIO_TRACK_PLAY_DONE, 1, 1));
                    }
                });
                String lplayFilePath = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + playFilePath;
                audioTrackPlayerController.setAudioDataSource(lplayFilePath);
                audioTrackPlayerController.start();
            }
        }
    };

    View.OnClickListener audioTrackStopBtnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click AudioTrack Stop Btn");
            // 普通AudioTrack的停止播放
            if (null != audioTrackPlayerController) {
                audioTrackPlayerController.stop();
                audioTrackPlayerController = null;
            }
        }
    };

    @Override
    public void onClick(View v) {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void optMusic(final String action) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(action));
    }

    private void play() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PLAY);
    }
    private void pause() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PAUSE);
    }
    public void resume( ) {
        optMusic(MusicService.ACTION_OPT_MUSIC_RESUME);
    }
    private void seekTo(int position) {
        Intent intent = new Intent(MusicService.ACTION_OPT_MUSIC_SEEK_TO);
        intent.putExtra(MusicService.PARAM_MUSIC_SEEK_TO,position);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    private void stop() {

    }
    public void speed(View view) {
    }
    public void pitch(View view) {
    }
    public void speedpitch(View view) {
    }
    public void normalspeedpitch(View view) {
    }

    /**
     　　*
     　　* @param myContext
     　　* @param ASSETS_NAME 要复制的文件名
     　　* @param savePath 要保存的路径
     　　* @param saveName 复制后的文件名
     　　*/
    public static void CopyAssets(Context myContext, String ASSETS_NAME,
                                  String savePath, String saveName) {
        String filename = savePath + File.separator + saveName;
        File dir = new File(savePath);
        // 如果目录不中存在，创建这个目录
        if (!dir.exists())
            dir.mkdir();
        try {
            if (!(new File(filename)).exists()) {
                InputStream is = myContext.getResources().getAssets()
                        .open(ASSETS_NAME);
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[7168];
                int count;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
