package com.tanwuyu.ivrtym.danmutest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by ivrty
 */

public class DanMuView extends FrameLayout {
    long danmuDuration;         //弹幕显示时间
    int danmuMargin;            //弹幕间隔距离
    int verticalLinesCount;     //总基本行数
    int preferentLinesCount;    //优先显示行数
    SparseArray<List<View>> verLineViewsListMap;

    public DanMuView(@NonNull Context context) {
        super(context);
        init();

    }

    public DanMuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DanMuView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


    }



    void init() {
        verticalLinesCount = 10;
        preferentLinesCount = 3;
        danmuMargin = 50;
        danmuDuration = 5000;
        verLineViewsListMap = new SparseArray<>();
        for (int i = 0; i < verticalLinesCount; i++) {
            verLineViewsListMap.put(i, new LinkedList<View>());
        }
    }

    public void addDanmu(DanMu danMu) {
        TextView textView = new TextView(getContext());
        textView.setText(danMu.getDanMuContent());
        /**
         * 设置弹幕样式
         */
        //弹幕文字颜色
        if (danMu.getDanMuColor()!=0){
            textView.setTextColor(danMu.getDanMuColor());
        }else {
            textView.setTextColor(Color.WHITE);
        }
        //弹幕背景颜色
        if (danMu.getBackgroundColor()!=0){
            textView.setBackgroundResource(R.drawable.shape_danmu_background);
            GradientDrawable backgroundDrawable = (GradientDrawable) textView.getBackground();
            backgroundDrawable.setColor(danMu.getBackgroundColor());
        }
        //弹幕文字大小
        if (danMu.getDanmuTextSize()!=0.0){
            textView.setTextSize(danMu.getDanmuTextSize());
        }



        //预测量,获得弹幕宽
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        int danmuWidth = textView.getMeasuredWidth();

        //设置弹幕添加位置为右侧,右边距 = - 弹幕宽度
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END);
        layoutParams.rightMargin = -textView.getMeasuredWidth();

        //计算弹幕垂直位置
        int insertLineKey = getInsertLineKey();
        if (insertLineKey != -1) {
            //基本行位置
            layoutParams.topMargin = getLineTop(insertLineKey);
            //设置tag,添加进Map
            textView.setTag(insertLineKey);
            verLineViewsListMap.get(insertLineKey).add(textView);
        } else {
            //随机位置
            int randomInsertTop = getRandomInsertTop();
            layoutParams.topMargin = randomInsertTop;
            //Log.e("RandowPosition", String.valueOf(randomInsertTop));
        }

        //添加弹幕
        textView.setLayoutParams(layoutParams);
        addView(textView);
        startTranslate(textView);

    }


    //判断指定行是否拥挤
    boolean isLineBusy(int lineNum) {
        List<View> lineViewsList = verLineViewsListMap.get(lineNum, new LinkedList<View>());
        if (lineViewsList.isEmpty()) {
            //行为空
            return false;
        } else {
            //判断当前行最后一个弹幕左移距离是否大于容器宽度,即弹幕已经从右侧完全滑进屏幕内
            View lastView = lineViewsList.get(lineViewsList.size() - 1);
            if (Math.abs(lastView.getTranslationX()) > lastView.getMeasuredWidth()) {
                return false;
            }

        }
        return true;
    }

    //获得优先空闲行
    List<Integer> getPreferentFreeLines(SparseArray<List<View>> sparseArray) {
        List<Integer> preferentFreeLines = new LinkedList<>();
        for (int i = 0; i < preferentLinesCount; i++) {
            if (!isLineBusy(i)) {
                preferentFreeLines.add(i);
            }
        }
        return preferentFreeLines;
    }


    //计算出当前插入行
    int getInsertLineKey() {
        int targetLine = -1;
        //判断优先行是否空闲,有责随机抽取一行
        List<Integer> preferentFreeLines = getPreferentFreeLines(verLineViewsListMap);
        if (!preferentFreeLines.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(preferentFreeLines.size());
            targetLine = preferentFreeLines.get(randomIndex);
        } else {
            //遍历非优先行,返回最近的空闲行
            for (int i = preferentLinesCount; i < verticalLinesCount; i++) {
                if (!isLineBusy(i)) {
                    targetLine = i;
                    break;
                }
            }
        }
        return targetLine;
    }

    //计算随机插入位置:top
    int getRandomInsertTop() {
        int containerInsideVerticalDistance = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int randomTopMin = containerInsideVerticalDistance / verticalLinesCount;
        int randomTopMax = containerInsideVerticalDistance - randomTopMin;
        Random random = new Random();
        int randomTop = random.nextInt(randomTopMax);
        return randomTop;
    }

    //计算行位置:top
    int getLineTop(int lineNum) {
        int lineHeight = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / verticalLinesCount;
        return lineNum * lineHeight;
    }

    //启动弹幕漂移动画
    void startTranslate(final View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 0 - getMeasuredWidth() - view.getMeasuredWidth());
        animator.setDuration(danmuDuration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //动画结束,移除弹幕,更新空闲位置
                if (view.getTag() != null) {
                    int lineKey = (int) view.getTag();
                    verLineViewsListMap.get(lineKey).remove(view);
                }
                DanMuView.this.removeView(view);
            }
        });

        animator.start();
    }

    /**
     * get and set
     *
     * @return
     */
    public int getVerticalLinesCount() {
        return verticalLinesCount;
    }

    public void setVerticalLinesCount(int verticalLinesCount) {
        this.verticalLinesCount = verticalLinesCount;
    }

    public int getPreferentLinesCount() {
        return preferentLinesCount;
    }

    public void setPreferentLinesCount(int preferentLinesCount) {
        this.preferentLinesCount = preferentLinesCount;
    }

    public int getDanmuMargin() {
        return danmuMargin;
    }

    public void setDanmuMargin(int danmuMargin) {
        this.danmuMargin = danmuMargin;
    }
}
