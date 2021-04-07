package com.leman.diyaobao.numpicker;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.leman.diyaobao.R;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * Author uidq1152
 * Date   2018-1-12 10:27
 */

public class NumPickView extends View {

    private static final String TAG = "NumPickView";
    private static final String DEF_TEXT_COLOR = "#FA6909";
    private static final String DEF_START_COLOR = "#ECECEC";
    /**
     * D0D1D2
     * 64666B
     * 4C4E53
     * 3A3D41
     */

    //高
    private int mHeight;
    //宽
    private int mWidth;
    //二分之一高
    private int middleHeight;
    //二分之一宽
    private int middleWidht;
    //单位高度
    private int mUnitHeight;
    //数据
    private List<String> mData = new ArrayList<>();
    //当前位置
    private int mCurrentPostion = 0;
    //偏移量
    private float pivot;
    //画笔
    private Paint mPaint;
    //字体的矩形
    private Rect mRect;
    //落点Y
    private float downY;
    //缩放扩大比例
    private float mScale;
    //滚轮状态
    private Status mStatus = Status.IDEL;
    //遮罩效果
    private LinearGradient mLg;
    //数值估值器
    private ValueAnimator mValueAnimator;
    //字体大小
    private int textSize;
    //字体大小差
    private int textStep;
    //显示个数
    private int mShowNum;
    //字体颜色
    private int mTextColor = Color.parseColor(DEF_TEXT_COLOR);
    //选择监听
    private OnSelectNumListener mListener;
    //颜色渐变计算器
    private ArgbEvaluator mArgvEvlauator;

    public NumPickView(Context context) {
        super(context);
    }

    public NumPickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumPickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NumPickView);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.NumPickView_totalNum:
                    int total = ta.getInteger(index, 24);
                    for (int j = 1; j < total; j++) {
                        mData.add(String.valueOf(j));
                    }
                    break;
                case R.styleable.NumPickView_showNum:
                    mShowNum = ta.getInteger(index, 6);
                    break;
                case R.styleable.NumPickView_textColor:
                    mTextColor = ta.getColor(index, mTextColor);
            }
        }
        ta.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mRect = new Rect();
        mArgvEvlauator = new ArgbEvaluator();
        mValueAnimator = new ValueAnimator();
        mValueAnimator.setDuration(300);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (abs(pivot) > mUnitHeight) {
                    return;
                }
                pivot = value;
                mScale = min(1, abs(pivot / mUnitHeight));
                invalidate();
            }
        });
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mStatus == Status.UP && pivot != 0) {
                    mCurrentPostion = clamp(mCurrentPostion );
                } else if (mStatus == Status.DOWN && pivot != 0) {
                    mCurrentPostion = clamp(mCurrentPostion);
                }
                invalidate();
                pivot = 0;
                mStatus = Status.IDEL;
                mScale = 0;
                if (mListener != null) {
                    mListener.onSelected(mCurrentPostion);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    //展示个数
    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        middleHeight = mHeight / 2;
        middleWidht = mWidth / 2;
        mUnitHeight = (mHeight - getPaddingTop() + getPaddingBottom()) / mShowNum;
        textSize = mUnitHeight / 2;
        textStep = mUnitHeight / 9;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画选中字体
        drawText(canvas, mData.get(mCurrentPostion), 0, 1);
        //画除中间外上下字体
        int num = mShowNum / 2;
        for (int i = 1; i <= num; i++) {
            drawText(canvas, mData.get(clamp(mCurrentPostion + i)), i, 1);
            drawText(canvas, mData.get(clamp(mCurrentPostion - i)), i, -1);
        }

    }

    /**
     * 选中当前数值
     *
     * @param num index
     */
    public void select(int num) {
        if (num < 0 || num >= mData.size()) {
            throw new IllegalArgumentException("The num must be in the range betwwen 0 and " + (mData.size() - 1));
        }
        mCurrentPostion = num;
        if (mListener != null) {
            mListener.onSelected(mCurrentPostion);
        }
        invalidate();
    }


    /**
     * @param canvas
     * @param text   要画的 String
     * @param level  选中为0级，每差一个 index 加一级
     * @param direct 以选中的为基准的方向，direct < 0 在上方，direct > 0 在下方
     */
    private void drawText(Canvas canvas, String text, int level, int direct) {
        mPaint.reset();
        mPaint.setShader(null);
        //字的位置渐变量
        float offset = direct * level * mUnitHeight;
        //字体的大小变化
        float step = (direct * mStatus.getValue() * mScale * textStep);

        if (level == 0) {
            //中间字体无论怎么样都是缩小的
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(textSize - abs(step));
            mPaint.getTextBounds(text, 0, text.length(), mRect);
            canvas.drawText(text, middleWidht - mRect.width() / 2, mHeight / 2 + mRect.height() / 2 + pivot, mPaint);
        } else {
            //其他字体根据上下和滑动方向关系放大或缩小, 颜色渐变
            int color = (int) mArgvEvlauator.evaluate(1 - abs(mRect.height() / 2 + offset + pivot) / middleHeight
                    , Color.parseColor(DEF_START_COLOR)
                    , mTextColor);
            mPaint.setColor(color);
            mPaint.setTextSize(textSize - textStep * level + step);
            mPaint.getTextBounds(text, 0, text.length(), mRect);
            canvas.drawText(text, middleWidht - mRect.width() / 2, middleHeight + mRect.height() / 2 + offset + pivot, mPaint);
        }

    }


    /**
     * distanceY > 0: 向下
     * distanceY < 0: 向上
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                mValueAnimator.cancel();
                break;
            case MotionEvent.ACTION_MOVE:
                pivot = event.getY() - downY;
                if (pivot > 0) {
                    //向下
                    mStatus = Status.DOWN;
                    if (abs(pivot) > mUnitHeight) {
                        mCurrentPostion = clamp(mCurrentPostion - 1);
                        downY = event.getY();
                        pivot = 0;
                    } else {
                        invalidate();
                    }
                } else {
                    //向上
                    mStatus = Status.UP;
                    if (abs(pivot) > mUnitHeight) {
                        mCurrentPostion = clamp(mCurrentPostion + 1);
                        downY = event.getY();
                        pivot = 0;
                    } else {
                        invalidate();
                    }
                }
                mScale = min(1, abs(pivot / mUnitHeight));
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                if (pivot == 0) {
                    //把点击事件统一为滑动事件处理，简化流程。
                    pivot = 0.00001f;
                }
                if (abs(pivot) > mUnitHeight / 2) {
                    //需要过渡
                    int rest = (int) abs(mUnitHeight / 2 - pivot);
                    if (mStatus == Status.UP) {
                        mValueAnimator.setFloatValues(pivot, -rest);
                    } else if (mStatus == Status.DOWN) {
                        //这里需要注意
                        mValueAnimator.setFloatValues(pivot, (int) pivot + rest + mUnitHeight / 2);
                    }
                } else {
                    //过渡失败，返回原数值，所以终点都是 0
                    if (mStatus == Status.UP) {
                        mValueAnimator.setFloatValues(pivot, 0);
                    } else if (mStatus == Status.DOWN) {
                        mValueAnimator.setFloatValues(pivot, 0);
                    }
                }
                if (mValueAnimator.getValues() == null || mValueAnimator.getValues().length == 0) {
                    return false;
                }
                mValueAnimator.start();
                break;
        }
        return true;
    }

    public int getCurrentPostion() {
        return mCurrentPostion;
    }

    /**
     * 保证 index 合法化
     *
     * @param p 下标
     * @return 合法后的下标
     */
    private int clamp(int p) {
        if (p > mData.size() - 1) {
            return p - mData.size();
        } else if (p < 0) {
            return mData.size() - abs(p);
        }
        return p;
    }

    /**
     * 设置滚轮监听
     *
     * @param listener 监听
     */
    public void setOnSelectNumListener(OnSelectNumListener listener) {
        this.mListener = listener;
    }

    /**
     * 滚轮状态
     */
    private enum Status {
        UP(1), DOWN(-1), IDEL(0);
        int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 监听接口
     */
    public interface OnSelectNumListener {
        void onSelected(int num);
    }


}
