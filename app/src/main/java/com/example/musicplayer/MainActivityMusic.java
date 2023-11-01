package com.example.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.audiotrack.MusicPlayerController;
import com.example.musicplayer.opensles.SoundTrackController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MainActivityMusic extends AppCompatActivity {
    private static final String TAG = "MainActivityMusic";
    private Button audioTrackPlayBtn;
    private Button audioTrackStopBtn;

    /** 要播放的文件路径 **/
    private static String playFilePath = "CornfieldChase.mp3";

    private MusicPlayerController audioTrackPlayerController;
    private SoundTrackController openSLPlayerController;

    public static final int UPDATE_PLAY_VOICE_PROGRESS = 730;
    public static final int AUDIO_TRACK_PLAY_DONE = 740;
    public static final int OPENSL_ES_PLAY_DONE = 750;

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

        /** 将文件复制到当前程序的路径 **/
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
            if (audioTrackPlayerController == null) {
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

    View.OnClickListener openSLESPlayBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click OpenSL ES Play Btn");
            if (null == openSLPlayerController) {
                // OpenSL ES初始化播放器
                openSLPlayerController = new SoundTrackController();
                openSLPlayerController.setOnCompletionListener(new SoundTrackController.OnCompletionListener() {
                    @Override
                    public void onCompletion() {
                        // 发消息随后去执行结束PLAYER，否则会卡死；Toast也是在后面做
                        handler.sendMessage(handler.obtainMessage(
                                OPENSL_ES_PLAY_DONE, 1, 1));
                    }
                });
                String lplayFilePath = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + playFilePath;
                openSLPlayerController.setAudioDataSource(lplayFilePath, 0.2f);
                // OpenSL ES进行播放
                openSLPlayerController.play();
            }
        }
    };

    View.OnClickListener openSLESStopBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click OpenSL ES Stop Btn");
            if (null != openSLPlayerController) {
                openSLPlayerController.stop();
                openSLPlayerController = null;
            }
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            // 计算当前时间
            // ?msg.arg2是有没有在播放?
//            int _time = Math.max(msg.arg1, 0) / 1000;
//            int total_time = Math.max(msg.arg2, 0) / 1000;
//            float ratio = (float) _time / (float) total_time;
            if (msg.what == AUDIO_TRACK_PLAY_DONE) {
                Log.i(TAG, "AUDIO_TRACK_PLAY_DONE");
                // 普通AudioTrack的停止播放
                if (null != audioTrackPlayerController) {
                    audioTrackPlayerController.stop();
                    audioTrackPlayerController = null;
                }
                Toast.makeText(MainActivityMusic.this, "AUDIO TRACK 播放完成", Toast.LENGTH_SHORT).show();
            } else if (msg.what == OPENSL_ES_PLAY_DONE) {
                Log.i(TAG, "OPENSL_ES_PLAY_DONE");
                if (null != openSLPlayerController) {
                    openSLPlayerController.stop();
                    openSLPlayerController = null;
                }
                Toast.makeText(MainActivityMusic.this, "OPENSL ES 播放完成", Toast.LENGTH_SHORT).show();
            } else if (msg.what == UPDATE_PLAY_VOICE_PROGRESS){
                Log.i(TAG, "Play Progress : " + msg.arg1 / 1000 + " seconds, playing " + msg.arg2);
            }
        }
    };

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
