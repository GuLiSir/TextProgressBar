package com.GuLiSir.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * 支持进度过渡的进度条
 */
public class TextProgressbar extends View {

    private static final String TAG = "TextProgressbar";


    public TextProgressbar(Context context) {
        super(context);
        init(null);
        Log.w(TAG, "TextProgressbar: 1");
    }

    public TextProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        Log.w(TAG, "TextProgressbar: 2");
    }

    public TextProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        Log.w(TAG, "TextProgressbar: 3");
    }

    public TextProgressbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
        Log.w(TAG, "TextProgressbar: 4");
    }

    //以下是定义默认值
    public static final int DEFAULT_BORDER_RADIUS = 20;
    public static final int DEFAULT_BORDER_SIZE = 3;
    public static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    public static final int DEFAULT_PROGRESS_COLOR = Color.RED;
    public static final int DEFAULT_PROGRESS_TEXT_COLOR = Color.BLUE;
    public static final int DEFAULT_PROGRESS_TEXT_SIZE = 30;
    public static final int DEFAULT_PROGRESS_BG_COLOR = Color.WHITE;

    /**
     * 动画时长,单位ms
     */
    private long duration;
    /**
     * 当前控件状态
     */
    private Status status = Status.END;
    /**
     * 当前的显示的进度点
     */
    private float progressCur = 0;
    /**
     * 动画开始的进度点
     */
    private float progressStart = 0;
    /**
     * 动画结束的进度点
     */
    private float progressEnd = 0.0f;

    /**
     * 动画开始的时间点
     */
    private long currentThreadTimeMillis;

    /**
     * 进度画笔
     */
    private Paint paintProgress;
    /**
     * 边框画笔
     */
    private Paint paintBorder;
    private Paint paintText;
    /**
     * 边框填充画笔
     */
    private Paint paintBGFill;

    /**
     * 圆角大小
     */
    private int borderRadius = DEFAULT_BORDER_RADIUS;
    /**
     * 描边宽度
     */
    private int borderSize = DEFAULT_BORDER_SIZE;
    /**
     * 描边颜色
     */
    private int borderColor = DEFAULT_BORDER_COLOR;
    /**
     * 文字大小
     */
    private int textSize = DEFAULT_PROGRESS_TEXT_SIZE;
    /**
     * 控件填充背景,
     * ***不能纯透明,否则计算不出重叠层***
     */
    private int progressBGColor = DEFAULT_PROGRESS_BG_COLOR;


    private ProgressTextObtainAble progressTextObtainAble;

    public void setProgressTextObtainAble(ProgressTextObtainAble progressTextObtainAble) {
        this.progressTextObtainAble = progressTextObtainAble;
    }

    private void init(AttributeSet attrs) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        int progressColor = DEFAULT_PROGRESS_COLOR;
        int progressTextColor = DEFAULT_PROGRESS_TEXT_COLOR;
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextProgressbar);
            progressColor = typedArray.getColor(R.styleable.TextProgressbar_progress_color, DEFAULT_PROGRESS_COLOR);
            progressTextColor = typedArray.getColor(R.styleable.TextProgressbar_progress_text_color, DEFAULT_PROGRESS_TEXT_COLOR);
            textSize = typedArray.getDimensionPixelSize(R.styleable.TextProgressbar_progress_text_size, DEFAULT_PROGRESS_TEXT_SIZE);
            borderRadius = typedArray.getDimensionPixelSize(R.styleable.TextProgressbar_progress_border_radius, DEFAULT_BORDER_RADIUS);
            borderSize = typedArray.getDimensionPixelSize(R.styleable.TextProgressbar_progress_border_width, DEFAULT_BORDER_SIZE);
            borderColor = typedArray.getColor(R.styleable.TextProgressbar_progress_border_color, DEFAULT_BORDER_COLOR);
            progressBGColor = typedArray.getColor(R.styleable.TextProgressbar_progress_bg_color, DEFAULT_PROGRESS_BG_COLOR);
            typedArray.recycle();
        }

        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
        paintBorder.setColor(borderColor);
        paintBorder.setStrokeWidth(borderSize);
        paintBorder.setStyle(Paint.Style.STROKE);

        paintBGFill = new Paint();
        paintBGFill.setAntiAlias(true);
        paintBGFill.setColor(progressBGColor);
        paintBGFill.setStyle(Paint.Style.FILL);

        paintProgress = new Paint();
        paintProgress.setAntiAlias(true);
        paintProgress.setColor(progressColor);

        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(progressTextColor);
        paintText.setTextSize(textSize);


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        rectFBorder.left = borderSize;
        rectFBorder.top = borderSize;
        rectFBorder.right = measuredWidth - borderSize;
        rectFBorder.bottom = measuredHeight - borderSize;


        rectFProgress.left = borderSize;
        rectFProgress.top = borderSize;
        rectFProgress.right = 0;
        rectFProgress.bottom = measuredHeight - borderSize;

    }

    /**
     * 进度
     */
    private final RectF rectFProgress = new RectF();
    /**
     * 边框
     */
    private final RectF rectFBorder = new RectF();
    private final PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    /**
     * 差值器,在开始的地方快然后慢
     */
    private final DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        if (status == Status.RUNNING) {
            long l = System.currentTimeMillis() - currentThreadTimeMillis;
            float percent = (float) l / (float) duration;//当前动画进度
            percent = Math.min(1.0f, percent);//处理进度溢出
            percent = decelerateInterpolator.getInterpolation(percent);//差值器平滑效果
            if (percent == 1.0f) {
                //动画播放结束
                status = Status.END;
                progressCur = progressEnd;
            } else {
                //当前的要显示的进度
                progressCur = progressStart + (progressEnd - progressStart) * percent;
            }
        } else {
            //结束状态,当前进度直接等于末尾进度
            progressCur = progressEnd;
        }
//        Log.i(TAG, "onDraw: progressCur:" + progressCur);


        canvas.drawRoundRect(rectFBorder, borderRadius, borderRadius, paintBGFill);//绘制透明边框填充

        paintProgress.setXfermode(porterDuffXfermode);//重叠模式,在上层填充里面显示
        rectFProgress.right = measuredWidth * progressCur;

        canvas.drawRect(rectFProgress, paintProgress);//绘制进度

        canvas.drawRoundRect(rectFBorder, borderRadius, borderRadius, paintBorder);//绘制边框


        if (progressTextObtainAble != null) {
            // 绘制居中文字
            CharSequence charSequence = progressTextObtainAble.obtainProgressText(progressCur);
            if (!TextUtils.isEmpty(charSequence)) {
                //需要显示文字
                // 将坐标原点移到控件中心
                canvas.translate(measuredWidth / 2.0f, measuredHeight / 2.0f);
                // 文字宽
                float textWidth = paintText.measureText(charSequence, 0, charSequence.length());
                // 文字baseline在y轴方向的位置
                float baseLineY = Math.abs(paintText.ascent() + paintText.descent()) / 2;
                canvas.drawText(charSequence, 0, charSequence.length(), -textWidth / 2, baseLineY, paintText);
                canvas.restore();
            }
        }

        if (status == Status.RUNNING) {
            invalidate();
        }
    }

    /**
     * 解释见 {@link #setProgress(float, boolean, long)}
     */
    public void setProgress(float progress) {
        setProgress(progress, false, 0);
    }

    /**
     * 设置显示的进度
     *
     * @param progress      要显示的进度0.0f-1.0f
     * @param playAnimation 是否要播放过渡动画
     * @param duration      播放动画的时长,单位:ms
     */
    public void setProgress(float progress, boolean playAnimation, long duration) {
        this.duration = duration;
        this.progressStart = progressCur;
        if (playAnimation) {
            //播放动画
            status = Status.RUNNING;
            currentThreadTimeMillis = System.currentTimeMillis();
        } else {
            //不播放动画,直接将当前值设置为目标值
            progressCur = progress;
            status = Status.END;
        }
        this.progressEnd = progress;
        invalidate();
    }

    private enum Status {
        /**
         * 执行动画中
         */
        RUNNING,
        /**
         * 静止显示状态
         */
        END
    }

    public interface ProgressTextObtainAble {
        CharSequence obtainProgressText(float currentProgress);
    }

}
