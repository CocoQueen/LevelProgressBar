package com.example.coco.drawprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * Created by coco on 2018/2/14.
 */

public class LevelProgressBar extends ProgressBar {
    private final int EMPTY_MESSAGE = 1;

    //xml中的自定义属性
    private int levelTextChooseColor;
    private int levelTextUnChooseColor;
    private int levelTextSize;
    private int progressStartColor;
    private int progressEndColor;
    private int progressBgColor;
    private int progressHeight;

    //代码中需要设置的属性
    private int levels;
    private String[] levelTexts;
    private int currentLevel;
    private int animInterval;//动画时间间隔
    private int animMaxTime;//动画最大时长
    private int targetProgress;

    private Paint paint;
    private int mTotalWidth;
    int textHeight;
    private ValueAnimator animator;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int progress = getProgress();
            //小于目标值时增加进度，大于目标值时减小进度
            if (progress < targetProgress) {
                setProgress(++progress);
                handler.sendEmptyMessageDelayed(EMPTY_MESSAGE, animInterval);
            } else if (progress > targetProgress) {
                setProgress(--progress);
                handler.sendEmptyMessageDelayed(EMPTY_MESSAGE, animInterval);
            } else {
                handler.removeMessages(EMPTY_MESSAGE);
            }

        }
    };

    public LevelProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyleAttributes(attrs);//获得xml中设置的属性值
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(levelTextSize);
        paint.setColor(levelTextUnChooseColor);
    }

    private void obtainStyleAttributes(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.LevelProgressBar);
        levelTextUnChooseColor = array.getColor(R.styleable.LevelProgressBar_levelTextUnChooseColor, 0x000000);
        levelTextChooseColor = array.getColor(R.styleable.LevelProgressBar_levelTextChooseColor, 0x333333);
        levelTextSize = (int) array.getDimension(R.styleable.LevelProgressBar_levelTextSize, dpTopx(15));
        progressStartColor = array.getColor(R.styleable.LevelProgressBar_progressStartColor, 0xCCFFCC);
        progressEndColor = array.getColor(R.styleable.LevelProgressBar_progressEndColor, 0x00FF00);
        progressBgColor = array.getColor(R.styleable.LevelProgressBar_progressBgColor, 0x000000);
        progressHeight = (int) array.getDimension(R.styleable.LevelProgressBar_progressHeight, dpTopx(20));
//        array.recycle();
    }

    private int dpTopx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void setLevel(int level) {
        this.levels = level;
    }

    public void setLevelTexts(String[] texts) {
        levelTexts = texts;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
        this.targetProgress = (int) (level * 1f / levels * getMax());
    }

    public void setAnimInterval(int animInterval) {
        this.animInterval = animInterval;
        handler.sendEmptyMessage(EMPTY_MESSAGE);
    }

    public void setAnimMaxTime(int animMaxTime) {
        this.animMaxTime = animMaxTime;
        animator = ValueAnimator.ofInt(getProgress(), targetProgress);
        animator.setDuration(animMaxTime * Math.abs(getProgress() - targetProgress / getMax()));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = getProgress();
                if (progress < targetProgress || progress > targetProgress) {
                    setProgress((Integer) animation.getAnimatedValue());
                }

            }
        });
        animator.start();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode != MeasureSpec.EXACTLY) {
            textHeight = (int) (paint.descent() - paint.ascent());
            height = getPaddingTop() + getPaddingBottom() + textHeight + progressHeight + dpTopx(10);
        }
        setMeasuredDimension(width, height);
        mTotalWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        for (int i = 0; i < levels; i++) {
            int textWidth = (int) paint.measureText(levelTexts[i]);
            paint.setColor(levelTextUnChooseColor);
            paint.setTextSize(levelTextSize);

            if (getProgress() == targetProgress && currentLevel >= 1 && currentLevel <= levels && i == currentLevel - 1) {
                paint.setColor(levelTextChooseColor);
            }
            canvas.drawText(levelTexts[i], mTotalWidth / levels * (i + 1) - textWidth, textHeight, paint);
        }
        int lineY = textHeight + progressHeight / 2 + dpTopx(10);

        paint.setColor(progressBgColor);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(progressHeight);
        canvas.drawLine(0 + progressHeight / 2, lineY, mTotalWidth - progressHeight / 2, lineY, paint);

        int reachedPartEnd = (int) (getProgress() * 1.0f / getMax() * mTotalWidth);
        if (reachedPartEnd > 0) {
            paint.setStrokeCap(Paint.Cap.ROUND);

            Shader shader = new LinearGradient(0, lineY, getWidth(), lineY, progressStartColor, progressEndColor, Shader.TileMode.REPEAT);
            paint.setShader(shader);
            int accurentEnd = reachedPartEnd - progressHeight / 2;
            int accurentStart = 0 + progressHeight / 2;
            if (accurentEnd > accurentStart) {
                canvas.drawLine(accurentStart, lineY, accurentEnd, lineY, paint);
            } else {
                canvas.drawLine(accurentStart, lineY, accurentStart, lineY, paint);
            }
            paint.setShader(null);
        }
        canvas.restore();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }
}
