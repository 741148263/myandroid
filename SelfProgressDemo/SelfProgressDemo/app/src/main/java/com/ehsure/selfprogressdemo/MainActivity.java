package com.ehsure.selfprogressdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tv_ma_progress;
    public static int screenWidth;
    public static int screenHeight;
    public int totalProgress = 100;
    public int beginProgress = 0;
    public int averageWidth = 1;

    /**
     * 外边距magin   (dp)
     */
    public int marginSize = 30;


    /**
     * 获取屏幕密度
     */
    public int densityDPI;
    DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        tv_ma_progress = findViewById(R.id.tv_ma_progress);
        averageWidth = screenWidth/totalProgress;
        Log.i("test","~~~~~~~~平均宽度:"+averageWidth);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (beginProgress<totalProgress){
                    beginProgress+=1;
                   try{
                       Thread.sleep(100);
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                    Message message = new Message();
                    ProgressDto progressDto = new ProgressDto();
                    progressDto.setCurrentProgress(beginProgress);
                    int realShowWidth = averageWidth * beginProgress;
                    if(progressDto.getCurrentProgress()==totalProgress){
                        realShowWidth = screenWidth;//因为之前求平均值时会有偏差，所以进度最后要设置为屏幕宽度的值
                    }
                    //实际宽度要减去左边距和右边距的值
                    realShowWidth = realShowWidth-convertPx(marginSize)*2;
                    progressDto.setCurrentWidth(realShowWidth);
                    message.obj = progressDto;
                    message.what = 1;
                    progressHandler.sendMessage(message);
                }


            }
        }).start();
    }

    Handler progressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    ProgressDto progressDto = (ProgressDto) msg.obj;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(progressDto.getCurrentWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
//                    tv_ma_progress.getLayoutParams().width = progressDto.getCurrentWidth();
                    layoutParams.setMargins(convertPx(marginSize), convertPx(marginSize), convertPx(marginSize), convertPx(marginSize));
                    tv_ma_progress.setLayoutParams(layoutParams);
                    Log.i("test","~~~~~~~~当前宽度:"+progressDto.getCurrentWidth());
                    tv_ma_progress.setText("+"+progressDto.getCurrentProgress());
                    break;
            }
        }
    };

    class ProgressDto{
        private int currentProgress;
        private int currentWidth;

        public int getCurrentProgress() {
            return currentProgress;
        }

        public void setCurrentProgress(int currentProgress) {
            this.currentProgress = currentProgress;
        }

        public int getCurrentWidth() {
            return currentWidth;
        }

        public void setCurrentWidth(int currentWidth) {
            this.currentWidth = currentWidth;
        }
    }


    /**
     * 根据dp转换成px
     * px = dp*ppi/160
     * dp = px / (ppi / 160)
     * px = sp*ppi/160
     *
     * @return
     */
    public int convertPx(int dp) {
        return dp * dm.densityDpi / 160;

    }



}
