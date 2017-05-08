package com.tanwuyu.ivrtym.danmutest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Random;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity {
    Button button;
    Button button2;
    Button button3;
    Button buttonAutoStart;
    Button buttonAutoEnd;
    DanMuView danmuContainer;

    Handler mHandler;

    boolean isAutoAdd;

    int[] colorArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                int danmuCount = new Random().nextInt(3);
                for (int i = 0; i < danmuCount; i++) {
                    DanMu danMu = new DanMu();
                    danMu.setDanMuColor(colorArray[new Random().nextInt(colorArray.length)]);
                    danMu.setBackgroundColor(colorArray[new Random().nextInt(colorArray.length)]);
                    danMu.setDanMuContent(getRandomString(new Random().nextInt(15)));
                    danmuContainer.addDanmu(danMu);
                }
            }
        };


        colorArray = new int[]{0x5400FF00,0x54FFFF00,0x54FF0000,0x6400FF66,0x6400FF66,0x648400FF,0x6400E1FF,0x64AAFF00,64606164};

        danmuContainer = (DanMuView) findViewById(R.id.danmu_container);
        button = (Button) findViewById(R.id.btn);
        button2 = (Button) findViewById(R.id.btn2);
        button3 = (Button) findViewById(R.id.btn3);
        buttonAutoStart = (Button) findViewById(R.id.btn_auto_start);
        buttonAutoEnd = (Button) findViewById(R.id.btn_auto_end);


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int danmuCount = new Random().nextInt(20);
                for (int i = 0; i < danmuCount; i++) {
                    DanMu danMu = new DanMu();
                    danMu.setDanMuColor(colorArray[new Random().nextInt(colorArray.length)]);
                    danMu.setBackgroundColor(colorArray[new Random().nextInt(colorArray.length)]);
                    danMu.setDanMuContent(getRandomString(danmuCount));
                    danmuContainer.addDanmu(danMu);
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 5; i++) {
                    DanMu danMu = new DanMu();
                    danMu.setDanMuColor(colorArray[new Random().nextInt(colorArray.length)]);
                    danMu.setBackgroundColor(colorArray[new Random().nextInt(colorArray.length)]);
                    danMu.setDanMuContent(getRandomString(40));
                    danmuContainer.addDanmu(danMu);
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 2; i++) {
                    DanMu danMu = new DanMu();
                    danMu.setDanMuColor(colorArray[new Random().nextInt(colorArray.length)]);
                    danMu.setBackgroundColor(colorArray[new Random().nextInt(colorArray.length)]);
                    danMu.setDanMuContent(getRandomString(40));
                    danmuContainer.addDanmu(danMu);
                }
            }
        });

        buttonAutoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAutoAdd = true;
                new AutoDanmuThread().start();
            }
        });
        buttonAutoEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAutoAdd = false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        isAutoAdd = false;
        super.onDestroy();
    }

    String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    class AutoDanmuThread extends Thread {
        @Override
        public void run() {
            while (isAutoAdd) {
                mHandler.sendEmptyMessage(0);
                try {
                    sleep(200L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
