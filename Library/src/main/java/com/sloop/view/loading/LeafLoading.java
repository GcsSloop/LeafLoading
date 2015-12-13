package com.sloop.view.loading;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;

import com.sloop.view.utils.UiUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 一个叶子飞旋的Loading核心类<br/>
 * 参考资料：http://blog.csdn.net/tianjian4592/article/details/44538605<br/>
 * Author: Sloop
 * Version: v1.0
 * Date: 2015/12/9
 * <ul type="disc">
 * <li><a href="http://www.sloop.icoc.cc"    target="_blank">作者网站</a>      <br/></li>
 * <li><a href="http://weibo.com/5459430586" target="_blank">作者微博</a>      <br/></li>
 * <li><a href="https://github.com/GcsSloop" target="_blank">作者GitHub</a>   <br/></li>
 * </ul>
 */
public class LeafLoading extends View {
    private static final String TAG = "SloopLeafLoading";

    private int mDefaultWidth = 300;                        //默认宽高
    private int mDefaultHeight = 60;

    private static final int COLOR_WHITE = 0xfffce094;      //淡白色
    private static final int COLOR_ORANGE = 0xffffa800;     //橘黄色

    private static final int MIDDLE_AMPLITUDE = 13;         //中等振幅大小
    private static final int AMPLITUDE_DISPARITY = 5;       //不同振幅之间的振幅差

    private static final int TOTAL_PROGRESS = 100;          //默认进度最大值
    private static final long LEAF_FLOAT_TIME = 3000;       //叶子飘动一个周期花费的时间
    private static final long LEAF_ROTATE_TIME = 2000;      //叶子旋转一个周期花费的时间

    private int mLeftMargin, mRightMargin;                  //用于控制进度条外边距

    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;        //中等振幅大小
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;  //振幅差

    private long mLeafFloatTime = LEAF_FLOAT_TIME;          //叶子飘动一个周期花费的时间
    private long mLeafRotateTime = LEAF_ROTATE_TIME;        //叶子旋转一个周期花费的时间

    private Paint mBitmapPaint, mWhitePaint, mOrangePaint;  //画笔
    private RectF mWhiteRectF, mOrangeRectF, mArcRectF;     //区域

    private int mCurrentProgress;                           //当前进度
    private int mProgressWidth;                             //当前进度条的宽度
    private int mCurrentProgressPosition;                   //当前所绘制部分进度条的位置
    private int mArcRadius;                                 //弧形半径

    private int mArcRightLocation;                          //arc的右上角x坐标，也是矩形x坐标的起始点
    private Resources mResources;                           //资源

    //叶子相关
    private int mAddTime;                                   //用于控制随机增加的时间，防止抱团
    private Bitmap mLeafBitmap;                             //叶子图片
    private List<Leaf> mLeafInfos;                          //叶子信息
    private LeafFactory mLeafFactory;                       //用于产生叶子信息
    private float mLeafBitmapWidth, mLeafBitmapHeight;      //叶子图片宽
    private float mLeafPaintWidth, mLeafPaintHeight;        //叶子绘制时宽高
    //外边框相关
    private Bitmap mOuterBitmap;
    private int mOuterWidth, mOuterHeight;
    private Rect mOuterSrcRect, mOuterDestRect;
    //风扇相关
    private Fan mFan;                                       //风扇对象
    private static final long FAN_SPEED = 6;                //风扇默认旋转速率级别(越大越快)
    private static final long FAN_SPEED_DISPARITY = 150;    //风扇相邻速率级别之间的差值
    private Bitmap mFanBitmap;
    private float mFanBitmapSideLength;                     //风扇图片边长
    private float mFanPaintSideLength;                      //风扇绘制时边长
    private long mFanSpeed = FAN_SPEED;                     //风扇旋转速率级别(1-10)


    public LeafLoading(Context context) {
        super(context);
        init((ContextThemeWrapper) context);
    }

    public LeafLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init((ContextThemeWrapper) context);
    }

    private void init(ContextThemeWrapper context) {
        initResources(context);
        initBitmap();
        initPaint();
        mLeafFactory = new LeafFactory();
        mLeafInfos = mLeafFactory.generateLeafs();
        mFan = new FanFactory().generateFan();
    }

    private void initResources(ContextThemeWrapper context) {
        mResources = getResources();
        mLeafFloatTime = LEAF_FLOAT_TIME;
        mLeafRotateTime = LEAF_ROTATE_TIME;
        mDefaultWidth = UiUtils.dip2px(context, mDefaultWidth);
        mDefaultHeight = UiUtils.dip2px(context, mDefaultHeight);
    }

    private void initBitmap() {

        mLeafBitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.leaf_xxhdpi)).getBitmap();
        mLeafBitmapWidth = mLeafBitmap.getWidth();
        mLeafBitmapHeight = mLeafBitmap.getHeight();

        mOuterBitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.outer_xxhdpi)).getBitmap();
        mOuterWidth = mOuterBitmap.getWidth();
        mOuterHeight = mOuterBitmap.getHeight();

        mFanBitmap = ((BitmapDrawable) mResources.getDrawable(R.drawable.fan_xxhdpi)).getBitmap();
        mFanBitmapSideLength = mFanBitmap.getWidth();
    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(COLOR_WHITE);

        mOrangePaint = new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(COLOR_ORANGE);
    }

    //确定View的大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams params = this.getLayoutParams();
        //获取宽高测量值
        int measureWidth = getMeasureWidth(widthMeasureSpec, params);
        int measureHeight = getMeasureHeight(heightMeasureSpec, params);
        //按比例重置宽高 按小一边的取值 (宽 : 高 = 300 : 60)
        measureWidth = measureWidth > measureHeight * 5 ? measureHeight * 5 : measureWidth;
        measureHeight = measureWidth / 5;
        //自定义宽高
        setMeasuredDimension(measureWidth, measureHeight);
    }

    //获取宽度测量值
    private int getMeasureWidth(int widthMeasureSpec, ViewGroup.LayoutParams params) {
        int measureWidth;
        if (params.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else if (params.width >= 0) {
            measureWidth = params.width;
        } else {
            measureWidth = mDefaultWidth;
        }
        return measureWidth;
    }

    //获取高度测量值
    private int getMeasureHeight(int heightMeasureSpec, ViewGroup.LayoutParams params) {
        int measureHeight;
        if (params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else if (params.height >= 0) {
            measureHeight = params.height;
        } else {
            measureHeight = mDefaultHeight;
        }
        return measureHeight;
    }

    //以确定视图大小 进行参数初始化(各个数值之间存在比例管理，详情请见说明文件)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int mTotalWidth = w > h * 5 ? h * 5 : w;
        int mTotalHeight = mTotalWidth / 5;

        mLeftMargin = mTotalHeight / 12;
        mRightMargin = mTotalHeight / 2;

        mProgressWidth = mTotalWidth - mLeftMargin - mRightMargin;
        mArcRadius = (mTotalHeight - 2 * mLeftMargin) / 2;

        mOuterSrcRect = new Rect(0, 0, mOuterWidth, mOuterHeight);
        mOuterDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);

        //风扇相关
        mFan.x = (float) mTotalWidth * 49 / 60;
        mFan.y = (float) mTotalHeight / 12;
        mFanPaintSideLength = (float) mTotalWidth / 6;
        //叶子相关
        mLeafPaintWidth = (float) mTotalHeight / 3;
        mLeafPaintHeight = (float) mTotalHeight / 6;

        mWhiteRectF = new RectF(mLeftMargin + mCurrentProgressPosition, mLeftMargin, mTotalWidth
                - mRightMargin, mTotalHeight - mLeftMargin);
        mOrangeRectF = new RectF(mLeftMargin + mArcRadius, mLeftMargin, mCurrentProgressPosition,
                mTotalHeight - mLeftMargin);

        mArcRectF = new RectF(mLeftMargin, mLeftMargin, mLeftMargin + 2 * mArcRadius,
                mTotalHeight - mLeftMargin);
        mArcRightLocation = mLeftMargin + mArcRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制进度条和叶子(之所以把叶子放进进度条中绘制，主要是层级原因。)
        drawProgressAndLeafs(canvas);
        canvas.drawBitmap(mOuterBitmap, mOuterSrcRect, mOuterDestRect, mBitmapPaint);
        drawFan(canvas);
        postInvalidate();
    }

    private void drawProgressAndLeafs(Canvas canvas) {
        if (mCurrentProgress >= TOTAL_PROGRESS) {
            mCurrentProgress = TOTAL_PROGRESS;
        }
        // mProgressWidth为进度条的宽度，根据当前进度算出进度条的位置
        mCurrentProgressPosition = mProgressWidth * mCurrentProgress / TOTAL_PROGRESS;

/*        Log.e(TAG, "当前进度值:" + mCurrentProgress + "\t||" + "当前进度位置:" + mCurrentProgressPosition +
                "\t||" + "圆弧半径:" + mArcRadius);*/

        //在进度在圆弧之内
        if (mCurrentProgressPosition < mArcRadius) {
            // 1.绘制white ARC，绘制orange ARC
            // 2.绘制白色矩形

            //1.绘制白色ARC
            canvas.drawArc(mArcRectF, 90, 180, false, mWhitePaint);
            //2.绘制白色矩形
            mWhiteRectF.left = mArcRightLocation;
            canvas.drawRect(mWhiteRectF, mWhitePaint);
            //3.绘制叶子
            drawLeafs(canvas);
            //4.绘制棕色ARC
            //单边角度
            int angle = (int) Math.toDegrees(Math.acos((mArcRadius - mCurrentProgressPosition) /
                    (float) mArcRadius));
            //起始位置
            int startAngle = 180 - angle;
            //扫过的角度
            int sweepAngle = 2 * angle;
            canvas.drawArc(mArcRectF, startAngle, sweepAngle, false, mOrangePaint);
        } else {
            // 1.绘制white RECT
            // 2.绘制Orange ARC
            // 3.绘制orange RECT
            // 这个层级进行绘制能让叶子感觉是融入棕色进度条中

            //1.绘制white RECT
            mWhiteRectF.left = mCurrentProgressPosition;
            canvas.drawRect(mWhiteRectF, mWhitePaint);
            //绘制叶子
            drawLeafs(canvas);
            //绘制orange ARC
            canvas.drawArc(mArcRectF, 90, 180, false, mOrangePaint);
            //绘制orange RECT
            mOrangeRectF.left = mArcRightLocation;
            mOrangeRectF.right = mCurrentProgressPosition;
            canvas.drawRect(mOrangeRectF, mOrangePaint);
        }

    }

    private void drawLeafs(Canvas canvas) {
        mLeafRotateTime = mLeafRotateTime <= 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mLeafInfos.size(); i++) {
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                //绘制叶子 -- 根据叶子的类型和当前时间
                getLeafLocation(leaf, currentTime);
//                Log.i(TAG, "left.x = " + leaf.x + "--leaf.y=" + leaf.y + "Type:" + leaf.type);
                //根据时间计算旋转角度
                canvas.save();
                //通过Matrix控制叶子的旋转
                Matrix matrix = new Matrix();
                //缩放
                float scaleX = mLeafPaintWidth / mLeafBitmapWidth;
                float scaleY = mLeafPaintHeight / mLeafBitmapHeight;
                matrix.postScale(scaleX, scaleY);
                //位移
                float transX = mLeftMargin + leaf.x;
                float transY = mLeftMargin + leaf.y;
                matrix.postTranslate(transX, transY);
                //通过时间关联旋转角度，则可以直接通过修改LEAF_ROTATE_TIME调节叶子旋转快慢
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime) /
                        (float) mLeafRotateTime;
                int angle = (int) (rotateFraction * 360);
                //根据叶子旋转方向确定叶子旋转的角度
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : -angle + leaf
                        .rotateAngle;
                matrix.postRotate(rotate, transX + mLeafBitmapWidth / 2, transY + mLeafBitmapHeight / 2);
                canvas.drawBitmap(mLeafBitmap, matrix, mBitmapPaint);
                canvas.restore();
            } else {
                continue;
            }
        }
    }

    private void getLeafLocation(Leaf leaf, long currentTime) {
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        long intervalTime = currentTime - leaf.startTime;

        if (intervalTime <= 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            leaf.startTime = System.currentTimeMillis() + new Random().nextInt((int)
                    mLeafFloatTime);
        }
        //   Log.e(TAG, "mLeafFloatTime = " + mLeafFloatTime + "--intervalTime=" + intervalTime);
        float fraction = (float) intervalTime / mLeafFloatTime;
        //  Log.i(TAG, "mLeafFloatTime = " + mLeafFloatTime + "--fraction=" + fraction);
        leaf.x = mProgressWidth - mProgressWidth * fraction;
        leaf.y = getLeafLocationY(leaf);
        // Log.e(TAG, "mProgressWidth = " + mProgressWidth + "--fraction=" + fraction);
    }

    private float getLeafLocationY(Leaf leaf) {
        // y= A sin(ωx+φ) + h 正弦函数
        float ω = (float) ((float) 2 * Math.PI / mProgressWidth);
        float A = mMiddleAmplitude;                 //默认中等振幅
        if (leaf.type == StartType.LITTLE) {        //小振幅 ＝ 中等振幅 － 振幅差
            A = mMiddleAmplitude - mAmplitudeDisparity;
        } else if (leaf.type == StartType.BIG) {    //大振幅 ＝ 中等振幅 + 振幅差
            A = mMiddleAmplitude + mAmplitudeDisparity;
        }
        float y = (float) (A * Math.sin(ω * leaf.x + leaf.φ) + mArcRadius * 2 / 3);
        return y;
    }

    private void drawFan(Canvas canvas) {
        long currentTime = System.currentTimeMillis();
        long mFanRotateTime = (10 - mFanSpeed) * FAN_SPEED_DISPARITY + 500;
//        Log.e(TAG, "mFanRotateTime:" + mFanRotateTime);
        canvas.save();
        //通过Matrix控制叶子的旋转
        Matrix matrix = new Matrix();
        float transX = mFan.x;
        float transY = mFan.y;
        float scale = mFanPaintSideLength / mFanBitmapSideLength;      //缩放比例
        matrix.postScale(scale, scale);                 //设置缩放
        matrix.postTranslate(transX, transY);           //设置位移

        //通过时间关联旋转角度，则可以直接通过修mFan.speed调节叶子旋转快慢
        float rotateFraction = ((currentTime - mFan.startTime) % mFanRotateTime) / (float) mFanRotateTime;
        int angle = (int) (rotateFraction * 360);
        //根据叶子旋转方向确定叶子旋转的角度
        int rotate = mFan.rotateDirection == 0 ? angle + mFan.rotateAngle : -angle + mFan.rotateAngle;

        matrix.postRotate(rotate, transX + mFanPaintSideLength / 2, transY + mFanPaintSideLength / 2);
        canvas.drawBitmap(mFanBitmap, matrix, mBitmapPaint);
        canvas.restore();
    }

    private class Fan {
        float x, y;             //左上角坐标
        int rotateAngle;        //旋转角度
        int rotateDirection;    //旋转方向(0-顺时针 1-逆时针)
        long startTime;         //起始时间(ms)
    }

    private class FanFactory {
        public Fan generateFan() {
            Fan fan = new Fan();
            fan.startTime = System.currentTimeMillis();
            fan.rotateDirection = -1;
            fan.rotateAngle = 0;
            return fan;
        }
    }

    private enum StartType {
        LITTLE, MIDDLE, BIG;
    }

    private class Leaf {
        float x, y;             //在绘制部分的位置
        StartType type;         //叶子飘动的振幅
        int rotateAngle;        //旋转角度
        int rotateDirection;    //旋转方向(0-顺时针 1-逆时针)
        long startTime;         //起始时间(ms)
        int φ;                  //初相位
    }

    private class LeafFactory {
        private static final int MAX_LEAFS = 8;
        Random random = new Random();

        //根据最大叶子数产生叶子
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        //根据传入叶子的数量产生叶子
        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }

        //生成一个叶子信息
        public Leaf generateLeaf() {
            Leaf leaf = new Leaf();
            //获取一个随机振幅类型
            int tempType = random.nextInt(3);
            StartType type = StartType.MIDDLE;
            switch (tempType) {
                case 0:
                    type = StartType.LITTLE;
                    break;
                case 1:
                    break;
                case 2:
                    type = StartType.BIG;
                    break;
            }
            leaf.type = type;
            leaf.φ = random.nextInt(20);    //添加初相位

            //叶子起始旋转角度
            leaf.rotateAngle = random.nextInt(360);
            //叶子旋转方向
            leaf.rotateDirection = random.nextInt(2);
            //为了产生交错感，让开始时间具有随机性
            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            mAddTime += random.nextInt((int) (mLeafFloatTime * 2));
            leaf.startTime = System.currentTimeMillis() + mAddTime;
            return leaf;
        }
    }

    /**
     * 设置中等振幅
     *
     * @param amplitude
     */
    public void setMiddleAmplitude(int amplitude) {
        this.mMiddleAmplitude = amplitude;
    }

    /**
     * 设置振幅差
     *
     * @param disparity
     */
    public void setMplitudeDisparity(int disparity) {
        this.mAmplitudeDisparity = disparity;
    }

    /**
     * 获取中等振幅
     */
    public int getMiddleAmplitude() {
        return mMiddleAmplitude;
    }

    /**
     * 获取振幅差
     */
    public int getMplitudeDisparity() {
        return mAmplitudeDisparity;
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        this.mCurrentProgress = progress;
        postInvalidate();
    }

    /**
     * 设置叶子飘完一个周期所花的时间
     *
     * @param time
     */
    public void setLeafFloatTime(long time) {
        this.mLeafFloatTime = time;
    }

    /**
     * 设置叶子旋转一周所花的时间
     *
     * @param time
     */
    public void setLeafRotateTime(long time) {
        this.mLeafRotateTime = time;
    }

    /**
     * 获取叶子飘完一个周期所花的时间
     */
    public long getLeafFloatTime() {
        mLeafFloatTime = mLeafFloatTime == 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        return mLeafFloatTime;
    }

    /**
     * 获取叶子旋转一周所花的时间
     */
    public long getLeafRotateTime() {
        mLeafRotateTime = mLeafRotateTime == 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        return mLeafRotateTime;
    }
}