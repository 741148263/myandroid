package com.ehsure.selfprogressdemo;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    /**
     * 进度条
     */
    TextView tv_ma_progress;
    TextView tv_ma_text;
    Button btn;
    /**
     * 屏幕宽高
     */
    public static int screenWidth;
    /**
     * 总进度值
     */
    public int totalProgress = 100;
    /**
     * 起始进度值
     */
    public int beginProgress = 0;
    /**
     * 平均宽度
     */
    public int averageWidth = 1;
    /**
     * 因为求平均值的时候可能会除不尽  所以这里要求误差值
     */
    public int errorValue = 0;

    /**
     * 外边距magin   (dp)
     */
    public int marginSize = 30;

    /**
     * 屏幕密度
     */
    DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        tv_ma_progress = findViewById(R.id.tv_ma_progress);
        tv_ma_text = findViewById(R.id.tv_ma_text);
        btn = findViewById(R.id.btn);
        averageWidth = (screenWidth-convertPx(marginSize)*2)/totalProgress;
        errorValue = screenWidth-convertPx(marginSize)*2 - averageWidth*totalProgress;
    }

    public void click(View view) {
        try{
            beginProgress = 0;
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
                        if(beginProgress>=totalProgress){
                            //因为之前求平均值时会有偏差，所以进度最后要加上误差值
                            realShowWidth = realShowWidth+errorValue;
                        }
                        progressDto.setCurrentWidth(realShowWidth);
                        message.obj = progressDto;
                        message.what = 1;
                        progressHandler.sendMessage(message);

                        Message other = new Message();
                        other.obj = progressDto;
                        other.what = 2;
                        progressHandler.sendMessage(other);
                    }
                    Message over = new Message();
                    over.what = 3;
                    progressHandler.sendMessage(over);


                }
            }).start();
        } catch (Exception Err) {
            Log.e("错误", Err.getMessage());
            System.out.println(Err.getMessage() + "err --<");
        }

    }

    @SuppressLint("HandlerLeak")
    Handler progressHandler = new Handler(){
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ProgressDto progressDto = (ProgressDto) msg.obj;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(progressDto.getCurrentWidth(), convertPx(35));
                layoutParams.setMargins(convertPx(marginSize), convertPx(marginSize), convertPx(marginSize), convertPx(marginSize));
                if (progressDto.getCurrentProgress() <= 5) {
                    tv_ma_progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 4);
                } else if (progressDto.getCurrentProgress() <= 10) {
                    tv_ma_progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                } else {
                    tv_ma_progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }
                tv_ma_progress.setLayoutParams(layoutParams);
                Log.i("test", "~~~~~~~~当前宽度:" + progressDto.getCurrentWidth());
                tv_ma_progress.setText(progressDto.getCurrentProgress() + "%");
            } else if (msg.what == 2) {
                try{
                    String existsLog = tv_ma_text.getText().toString();
                    int endIndex = tv_ma_text.getText().toString().lastIndexOf("\n");
                    String maxLine = "";
                    if (endIndex != -1) {
                        maxLine = existsLog.substring(endIndex);
                    }
                    ProgressDto progressDto = (ProgressDto) msg.obj;
                    if (maxLine.equals("")) {
                        tv_ma_text.append("开始进行累计进度条" + "\n当前进度：" + progressDto.getCurrentProgress());
                    } else if (!maxLine.contains("当前进度")) {
                        tv_ma_text.append("\n开始进行累计进度条" + "\n当前进度：" + progressDto.getCurrentProgress());
                    } else {
                        tv_ma_text.setText(tv_ma_text.getText().toString().substring(0, endIndex) + "\n当前进度：" + progressDto.getCurrentProgress());
                    }
                } catch (Exception err) {
                    System.out.println("err --> " + err.getMessage());
                }

            } else {
                tv_ma_text.append("\n文件传输完毕！");
            }
        }
    };

    static class ProgressDto{
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
     */
    public int convertPx(int dp) {
        return dp * dm.densityDpi / 160;

    }


}
