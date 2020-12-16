package com.example.serviceboundmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.serviceboundmusic.MyService.MyBinder;

public class MainActivity extends AppCompatActivity {

    private MyService myService;
    private boolean isBound = false;
    private ServiceConnection connection;
    private boolean x, r = false;
    private SeekBar seekBarVolume, seekBarTime;
    private ImageView imgVolume;
    private TextView tvTime, tvDuration;
    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        tvTime = findViewById(R.id.tvTime);
        tvDuration = findViewById(R.id.tvDuration);
        seekBarTime = findViewById(R.id.seekBarTime);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        final ImageButton btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        final ImageButton btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        final ImageButton btnNext = (ImageButton) findViewById(R.id.btnNext);
        final ImageButton btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        // Khởi tạo ServiceConnection
        imgVolume = findViewById(R.id.imgVolume);
        connection = new ServiceConnection() {

            // Phương thức này được hệ thống gọi khi kết nối tới service bị lỗi
            @Override
            public void onServiceDisconnected(ComponentName name) {

                isBound = false;
            }

            // Phương thức này được hệ thống gọi khi kết nối tới service thành công
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MyBinder binder = (MyBinder) service;
                myService = binder.getService();// lấy đối tượng MyService
                duration = binder.getDuration();
                tvDuration.setText(millisecondsToString(duration));
                seekBarTime.setMax(duration);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isBound) {
                            if(x == true) {
                                try {
                                    final double current = binder.getCurrentPosition();
                                    final String elapsedTime = millisecondsToString((int) current);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tvTime.setText(elapsedTime);
                                            seekBarTime.setProgress((int) current, true);
                                            if ((tvTime.getText().equals(tvDuration.getText()))&& r==false) {
                                                btnPlay.setBackgroundResource(R.drawable.play1);
                                                x= false;
                                            }
                                        }
                                    });

                                    Thread.sleep(1000);
                                }catch (InterruptedException e) {}
                            }
                        }
                    }
                }).start();
                isBound = true;
            }
        };
        final Intent intent =
                new Intent(MainActivity.this,
                        MyService.class);
        bindService(intent, connection,
                Context.BIND_AUTO_CREATE);
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myService.setSong(R.raw.la_phamdinhthaingan);
//            }
//        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x == false){
                    if(isBound) {
                        // bật nhạc
                        myService.start();
                    }
                    x = true;
                    btnPlay.setBackgroundResource(R.drawable.stop1);
                }
                else {
                    btnPlay.setBackgroundResource(R.drawable.play1);
                    x = false;
                    if(isBound){
                        // tạm dừng nhạc
                       myService.pause();
                    }
                }
            }
        });
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (r == false){
                    if (isBound) {
                        myService.setRepeat(true);
                    }
                    r = true;
                    btnRepeat.setImageResource(R.drawable.repeatonce);
                    Toast.makeText(MainActivity.this,
                            "Lặp lại bài hát hiện tại", Toast.LENGTH_SHORT).show();
                }
                else {
                    r = false;
                    myService.setRepeat(false);
                    btnRepeat.setImageResource(R.drawable.repeat);
                    Toast.makeText(MainActivity.this,
                            "Bình thường", Toast.LENGTH_SHORT).show();
                }

            }
        });

        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                float volume = progress / 100f;
                myService.setVolume(volume,volume);
                if (volume == 0)
                    imgVolume.setImageResource(R.drawable.baseline_music_off_white_24);
                else imgVolume.setImageResource(R.drawable.baseline_music_note_white_24);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if(isFromUser) {
                    myService.setSeekTo(progress);
                    seekBar.setProgress(progress);
                    tvTime.setText(String.valueOf(millisecondsToString(progress)));
                    }
                }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    public String millisecondsToString(int time) {
        String elapsedTime = "";
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        elapsedTime = minutes+":";
        if(seconds < 10) {
            elapsedTime += "0";
        }
        elapsedTime += seconds;

        return  elapsedTime;
    }
    }