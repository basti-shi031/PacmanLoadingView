package com.basti.loadingviewlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by SHIBW-PC on 2016/1/14.
 */
public class PacManView extends View {

    private Paint pacmanPaint, beanPaint;
    private int pacmanColor, beanColor;
    private int defaultHeight = 45;
    private int defaultWidth = 45;
    private float degree;
    private int width, height;
    private float pacmanRadius;//pacman半径
    private float beanX, pacmanX;//x轴坐标
    private float beanRadius;//bean半径
    private float space;//bean和pacman的举例
    private Status mStatus = Status.BeforeLoading;//当前状态
    private float alpha;

    private ValueAnimator degreeAnimator, beanXLoadingAnimator, pacmanXLoadingAnimator;//Loading状态时的动画
    private ValueAnimator beanCancelAnimator;//Cancel状态时的动画
    private ValueAnimator pacmanFinishAnimator;//Finish状态时的动画
    private ValueAnimator alphaAnimator;//消失时的alpha动画

    enum Status {
        BeforeLoading, Loading, Finish, Cancel,
    }

    public PacManView(Context context) {
        this(context, null);
    }

    public PacManView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PacManView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化属性
        initAttrs(context, attrs);
    }

    private void initAnim() {

        initLoadingAnim();
        initCancelAnim();
        initFinishAnim();
        initAlphtAnim();
        switch (mStatus) {
            case BeforeLoading:
                setVisibility(GONE);
                break;
            case Loading:
                showLoading();
                break;
            case Finish:
                showFinish();
                break;
            case Cancel:
                showCancel();
                break;
        }


    }

    private void showCancel() {
        pacmanXLoadingAnimator.cancel();
        beanXLoadingAnimator.cancel();
        float speed = (width + 2 * beanRadius + space + 2 * pacmanRadius) / (3000);
        beanCancelAnimator.setFloatValues(beanX, beanX + speed * 400);
        beanCancelAnimator.setDuration(400);
        alphaAnimator.setDuration(400);
        beanCancelAnimator.start();
        alphaAnimator.start();
    }

    private void showFinish() {
        beanXLoadingAnimator.cancel();
        pacmanXLoadingAnimator.cancel();

        pacmanFinishAnimator.setFloatValues(pacmanX, beanX);
        pacmanFinishAnimator.setDuration((long) (3000 * (space + pacmanRadius) / (width + 2 * beanRadius + 2 * pacmanRadius + space)) * 5);
        alphaAnimator.setDuration((long) (3000 * (space + pacmanRadius) / (width + 2 * beanRadius + 2 * pacmanRadius + space)) * 5);
        pacmanFinishAnimator.start();
        alphaAnimator.start();
    }

    private void showLoading() {
        degreeAnimator.cancel();
        beanXLoadingAnimator.cancel();
        pacmanXLoadingAnimator.cancel();
        setAlpha(1);

        degreeAnimator.start();
        beanXLoadingAnimator.start();
        pacmanXLoadingAnimator.start();
    }

    private void initAlphtAnim() {

        if (alphaAnimator == null) {
            alphaAnimator = ValueAnimator.ofFloat(1, 0);
            alphaAnimator.setRepeatCount(0);
            alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    alpha = (float) animation.getAnimatedValue();
                    setAlpha(alpha);
                }
            });
            alphaAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(INVISIBLE);
                    alphaAnimator.cancel();
                }
            });
        }

    }

    private void initFinishAnim() {

        if (pacmanFinishAnimator == null) {
            pacmanFinishAnimator = ValueAnimator.ofFloat(0, 0);
            pacmanFinishAnimator.setRepeatCount(0);
            pacmanFinishAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    pacmanX = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            pacmanFinishAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    pacmanFinishAnimator.cancel();
                }
            });
        }

    }

    private void initCancelAnim() {
        if (beanCancelAnimator == null) {
            beanCancelAnimator = ValueAnimator.ofFloat(beanX, width);
            beanCancelAnimator.setDuration((long) (3000 * ((float) width + 2 * pacmanRadius + space - beanX) / (width + 2 * beanRadius + 2 * pacmanRadius + space)));
            beanCancelAnimator.setRepeatCount(0);
            beanCancelAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    beanX = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            beanCancelAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    beanCancelAnimator.cancel();
                    degreeAnimator.cancel();
                }
            });
            beanCancelAnimator.setInterpolator(new LinearInterpolator());
        }
    }

    private void initLoadingAnim() {
        //pacman张嘴的动画
        if (degreeAnimator == null) {
            degreeAnimator = ValueAnimator.ofFloat(0, 45, 0);
            degreeAnimator.setDuration(750);
            degreeAnimator.setRepeatCount(ValueAnimator.INFINITE);
            degreeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    degree = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
        }

        //bean移动动画
        if (beanXLoadingAnimator == null) {
            beanXLoadingAnimator = ValueAnimator.ofFloat(-beanRadius * 2, width + 2 * pacmanRadius + space);
            beanXLoadingAnimator.setDuration(3000);
            beanXLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
            beanXLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    beanX = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            beanXLoadingAnimator.setInterpolator(new LinearInterpolator());
        }

        //pacman移动动画
        if (pacmanXLoadingAnimator == null) {
            pacmanXLoadingAnimator = ValueAnimator.ofFloat(-2 * pacmanRadius - space - beanRadius * 2, width);
            pacmanXLoadingAnimator.setDuration(3000);
            pacmanXLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
            pacmanXLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    pacmanX = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            pacmanXLoadingAnimator.setInterpolator(new LinearInterpolator());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //画pacman
        RectF rectf = new RectF(pacmanX, pacmanRadius, pacmanX + pacmanRadius * 2, pacmanRadius * 3);
        canvas.drawArc(rectf, degree, 360 - 2 * degree, true, pacmanPaint);

        //画bean
        //bean的x坐标
        float x = beanX + beanRadius;
        float y = getHeight() / 2;
        canvas.drawCircle(x, y, beanRadius, beanPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = measureDimension(dp2px(defaultWidth), widthMeasureSpec);
        height = measureDimension(dp2px(defaultHeight), heightMeasureSpec);
        setMeasuredDimension(width, height);


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        pacmanRadius = height / 4;
        space = pacmanRadius / 2;
        beanRadius = pacmanRadius / 4;

        initAnim();
    }

    private int measureDimension(int defaultSize, int measureSpec) {

        int result = defaultSize;

        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, size);
        }

        return result;
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PacManView);
        pacmanColor = ta.getColor(R.styleable.PacManView_pacman_color, Color.WHITE);
        beanColor = ta.getColor(R.styleable.PacManView_bean_color, Color.WHITE);
        ta.recycle();

        pacmanPaint = new Paint();
        pacmanPaint.setAntiAlias(true);
        pacmanPaint.setStyle(Paint.Style.FILL);
        pacmanPaint.setColor(pacmanColor);

        beanPaint = new Paint();
        beanPaint.setAntiAlias(true);
        beanPaint.setStyle(Paint.Style.FILL);
        beanPaint.setColor(pacmanColor);
    }

    private int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }

    public void startLoading() {
        setStatus(Status.Loading);
        setVisibility(VISIBLE);
        initAnim();
    }

    public void finishLoading() {
        setStatus(Status.Finish);
        initAnim();
    }

    public void cancelLoading() {
        setStatus(Status.Cancel);
        initAnim();
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAllAnimator();
    }

    private void cancelAllAnimator() {
        degreeAnimator.cancel();
        beanXLoadingAnimator.cancel();
        pacmanXLoadingAnimator.cancel();
        beanCancelAnimator.cancel();
        pacmanFinishAnimator.cancel();
        alphaAnimator.cancel();
    }
}
