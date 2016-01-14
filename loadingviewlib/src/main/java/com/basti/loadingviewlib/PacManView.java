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
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

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
    private boolean isInit = false;

    private ValueAnimator degreeAnimator, beanXLoadingAnimator, pacmanXLoadingAnimator;//Loading状态时的动画
    private ValueAnimator beanCancelAnimator, pacmanCancelAnimator;//Cancel状态时的动画
    private ValueAnimator beanFinishAnimator;//Finish状态时的动画

    enum Status {
        BeforeLoading,Loading, Finish, Cancel,
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
        switch (mStatus){
            case Loading:

                degreeAnimator.cancel();
                beanXLoadingAnimator.cancel();
                pacmanXLoadingAnimator.cancel();

                degreeAnimator.start();
                beanXLoadingAnimator.start();
                pacmanXLoadingAnimator.start();
                break;
            case Finish:
                Toast.makeText(getContext().getApplicationContext(),"finish",Toast.LENGTH_SHORT).show();
                break;
            case Cancel:
                pacmanXLoadingAnimator.cancel();
                beanXLoadingAnimator.cancel();
                beanCancelAnimator.setFloatValues(beanX, width);
                beanCancelAnimator.setDuration((long) (3000 * ((float) width + 2 * pacmanRadius + space - beanX) / (width+2*beanRadius+2*pacmanRadius+space)));
                beanCancelAnimator.start();
                break;
        }



    }

    private void initCancelAnim() {
        if (beanCancelAnimator == null) {
            beanCancelAnimator = ValueAnimator.ofFloat(beanX, width);
            beanCancelAnimator.setDuration((long) (3000 * ((float) width + 2 * pacmanRadius + space - beanX) / (width+2*beanRadius+2*pacmanRadius+space)));
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
                    setVisibility(GONE);
                    beanCancelAnimator.cancel();
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
            Log.i("TAG","beanXLoadingAnimator");
            beanXLoadingAnimator = ValueAnimator.ofFloat(-beanRadius * 2, width + 2 * pacmanRadius + space);
            beanXLoadingAnimator.setDuration(3000);
            beanXLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
            beanXLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    beanX = (float) animation.getAnimatedValue();
                    Log.i("beanX1",beanX+"");
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
        float x = beanX+beanRadius;
        float y = getHeight() / 2;
        canvas.drawCircle(x, y, beanRadius, beanPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = measureDimension(dp2px(defaultWidth), widthMeasureSpec);
        height = measureDimension(dp2px(defaultHeight), heightMeasureSpec);
        setMeasuredDimension(width, height);

        pacmanRadius = height / 4;
        space = pacmanRadius / 2;
        beanRadius = pacmanRadius / 4;
/*        Log.i("pacmanRadius1", "" + pacmanRadius);
        Log.i("space1", "" + space);
        Log.i("beanRadius1", "" + beanRadius);*/

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
    }

    public void cancelLoading() {
        setStatus(Status.Cancel);
        initAnim();
    }

    public void setStatus(Status status) {
        mStatus = status;
    }
}
