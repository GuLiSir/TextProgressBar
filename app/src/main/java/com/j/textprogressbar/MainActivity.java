package com.j.textprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.GuLiSir.progressbar.TextProgressbar;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextProgressbar textProgressbar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textProgressbar = findViewById(R.id.main_tpb);
        textView = findViewById(R.id.main_tv);
        textProgressbar.setProgressTextObtainAble(new TextProgressbar.ProgressTextObtainAble() {
            @Override
            public CharSequence obtainProgressText(float currentProgress) {
                String s = String.valueOf((int) (currentProgress * 100)) + "/100";
                return s;
            }
        });
        showText();
    }

    private static final String TAG = "MainActivity";

    float[] step = new float[]{0.3f, 0.6f, 0.9f, 1.0f, 0.5f, 0.2f, 0.8f, 0.0f, 0.5f};
    int cur = 0;

    public void start(View view) {
        Log.i(TAG, "start: ");
        textProgressbar.setProgress(step[cur], true, 1000);
        cur++;
        if (cur >= step.length) {
            cur = 0;
        }
        showText();
    }

    private void showText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("进度顺序:").append(Arrays.toString(step));
        stringBuilder.append("\n");
        stringBuilder.append("当前顺序:").append(cur).append("   值:").append(step[cur]);
        textView.setText(stringBuilder.toString());

    }

}
