package com.GuLiSir.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ProgressStepScrollBa extends View {
    public ProgressStepScrollBa(Context context) {
        super(context);
        init(null);
    }

    public ProgressStepScrollBa(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ProgressStepScrollBa(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public ProgressStepScrollBa(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    /**
     * 进度画笔
     */
    private Paint paintCircleNormal;
    /**
     * 边框画笔
     */
    private Paint paintBorder;
    private Paint paintText;

    private int circleRadius = 20;
    private int lineWidth = 20;
    /**
     * 总步数
     */
    private int totalStep = 10;

    private int progressBGColor = DEFAULT_PROGRESS_BG_COLOR;
    /**
     * 描边颜色
     */
    private int borderColor = DEFAULT_BORDER_COLOR;
    /**
     * 文字大小
     */
    private int textSize = DEFAULT_PROGRESS_TEXT_SIZE;
    //以下是定义默认值
    public static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    public static final int DEFAULT_CIRCLE_NORMAL_COLOR = Color.RED;
    public static final int DEFAULT_PROGRESS_TEXT_COLOR = Color.BLUE;
    public static final int DEFAULT_PROGRESS_TEXT_SIZE = 30;
    public static final int DEFAULT_PROGRESS_BG_COLOR = Color.WHITE;

    private void init(AttributeSet attrs) {

        int circleNormalColor = DEFAULT_CIRCLE_NORMAL_COLOR;
        int progressTextColor = DEFAULT_PROGRESS_TEXT_COLOR;
        if (attrs != null) {
            // TODO: 2019/11/11
//            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextProgressbar);
//            circleNormalColor = typedArray.getColor(R.styleable.TextProgressbar_progress_color, DEFAULT_CIRCLE_NORMAL_COLOR);
//            progressTextColor = typedArray.getColor(R.styleable.TextProgressbar_progress_text_color, DEFAULT_PROGRESS_TEXT_COLOR);
//            textSize = typedArray.getDimensionPixelSize(R.styleable.TextProgressbar_progress_text_size, DEFAULT_PROGRESS_TEXT_SIZE);
//            borderColor = typedArray.getColor(R.styleable.TextProgressbar_progress_border_color, DEFAULT_BORDER_COLOR);
//            progressBGColor = typedArray.getColor(R.styleable.TextProgressbar_progress_bg_color, DEFAULT_PROGRESS_BG_COLOR);
//            typedArray.recycle();
        }

        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
        paintBorder.setColor(borderColor);
        paintBorder.setStyle(Paint.Style.STROKE);


        paintCircleNormal = new Paint();
        paintCircleNormal.setAntiAlias(true);
        paintCircleNormal.setColor(circleNormalColor);
        paintCircleNormal.setStyle(Paint.Style.FILL);

        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(progressTextColor);
        paintText.setTextSize(textSize);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();

        float halfH = measuredHeight / 2.0f;
        float halfW = measuredWidth / 2.0f;
        canvas.drawLine(0, halfH, measuredWidth, halfH, paintBorder);

        int startX = 0;
        //绘制每一个圆
        for (int i = 0; i < totalStep; i++) {
            canvas.drawCircle(startX + circleRadius, halfH, circleRadius, paintCircleNormal);
            startX = (circleRadius * 2) + lineWidth;//下一个起始点

        }

    }

}
