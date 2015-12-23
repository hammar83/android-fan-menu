/*
 * Copyright (C) 2015 Fredrik Hammarström
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.hammarstrom.fanmenu;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RemoteViews;

/**
 * A custom {@link ViewGroup} that can be used to create a fan menu.
 *
 */
@RemoteViews.RemoteView
public class FanMenu extends ViewGroup {

    private static final int MIDDLE = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;

    private int mMenuItemCount;
    private int mWidth;
    private int mHeight;
    private int mPadding;
    private int mMenuPosition;
    private boolean mMenuOpen = false;
    private boolean mShowMenuButtonAnimation;

    private View mMenuButton;
    private PointF circleCenter;

    public FanMenu(Context context) {
        this(context, null);
    }

    public FanMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FanMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FanMenu, 0, 0);

        try {
            mMenuPosition = a.getInteger(R.styleable.FanMenu_menu_position, RIGHT);
            mShowMenuButtonAnimation = a.getBoolean(R.styleable.FanMenu_default_button_animation, true);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        mMenuItemCount = getChildCount() - 1;
        mMenuButton = getChildAt(0);
        mMenuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenuItems();
            }
        });

        circleCenter = new PointF(0, 0);
    }

    /**
     * Toggle visibility of menu items
     */
    private void toggleMenuItems() {
        if (!mMenuOpen) {
            if(mShowMenuButtonAnimation) {
                mMenuButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.menu_button_rotate_open));
            }

            for(int i = 1; i < getChildCount(); i++) {
                final View item = getChildAt(i);
                showItemsAnimation(item);
            }

        } else {
            if(mShowMenuButtonAnimation) {
                mMenuButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.menu_button_rotate_close));
            }

            for(int i = 1; i < getChildCount(); i++) {
                final View item = getChildAt(i);
                hideItemsAnimation(item);
            }
        }

        mMenuOpen = !mMenuOpen;
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int sizeFactor = mMenuItemCount > 3 ? mMenuItemCount : 4;
        mPadding = getPaddingBottom() + getPaddingTop() + getPaddingRight() + getPaddingLeft();

        measureChildWithMargins(mMenuButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
        width = (mMenuButton.getMeasuredWidth() * sizeFactor);
        height = (mMenuButton.getMeasuredHeight() * sizeFactor);

        for(int i = 1; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        setMeasuredDimension(
                resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec)
        );
    }

    /**
     * Position all children within this layout.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View firstChild = getChildAt(0);
        int radius = Math.min( (getMeasuredWidth() - mPadding) - 50, (getMeasuredHeight() - mPadding) - 50) / 2;
        double step = (360 / getChildCount()) / 2;
        double angleDegrees = 0;

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        FanMenu.LayoutParams lp = (FanMenu.LayoutParams) firstChild.getLayoutParams();
        if(mMenuPosition == RIGHT) {
            firstChild.layout(
                    mWidth - (firstChild.getMeasuredWidth() + lp.rightMargin),
                    mHeight - (firstChild.getMeasuredHeight() + lp.bottomMargin),
                    mWidth - lp.rightMargin,
                    mHeight - lp.bottomMargin
            );

            angleDegrees = step - 45;
            circleCenter.x = firstChild.getLeft();
            circleCenter.y = firstChild.getTop();

        } else if(mMenuPosition == MIDDLE) {
            firstChild.layout(
                    (mWidth / 2) - (firstChild.getMeasuredWidth() / 2),
                    mHeight - (firstChild.getMeasuredHeight() + lp.bottomMargin),
                    (mWidth / 2) + (firstChild.getMeasuredWidth() / 2),
                    mHeight - lp.bottomMargin
            );

            radius -= 35;
            angleDegrees = step;
            circleCenter.x = firstChild.getLeft() + (firstChild.getMeasuredWidth() / 2);
            circleCenter.y = firstChild.getTop() + (firstChild.getMeasuredHeight() / 2);

        } else if(mMenuPosition == LEFT) {
            firstChild.layout(
                    lp.leftMargin,
                    mHeight - (firstChild.getMeasuredHeight() + lp.bottomMargin),
                    firstChild.getMeasuredWidth() + lp.leftMargin,
                    mHeight - lp.bottomMargin
            );

            angleDegrees = step + 45;
            circleCenter.x = firstChild.getRight();
            circleCenter.y = firstChild.getTop();
        }

        // Place menu items
        for (int i = 1; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            PointF childPosition = getChildCenterPosition(angleDegrees, radius);
            child.layout(
                    (int) childPosition.x - (child.getMeasuredWidth() / 2),
                    (int) childPosition.y - (child.getMeasuredHeight() / 2),
                    (int) childPosition.x + (child.getMeasuredWidth() / 2),
                    (int) childPosition.y + (child.getMeasuredHeight() / 2)
            );
            angleDegrees += step;
        }
    }

    /**
     * Calculate child position
     *
     * @param angleDegrees
     * @param radius
     * @return
     */
    private PointF getChildCenterPosition(double angleDegrees, int radius) {
        PointF position = new PointF(0, 0);
        position.x = (int) (circleCenter.x - Math.cos((angleDegrees * Math.PI) / 180.0f ) * radius);
        position.y = (int) (circleCenter.y - Math.sin( (angleDegrees * Math.PI) / 180.0f ) * radius);
        return position;
    }

    /**
     * Animation to be used when showing menu items
     *
     * @param item
     */
    private void showItemsAnimation(final View item) {
        item.setVisibility(VISIBLE);

        float fromX = mMenuButton.getX() - item.getX();
        float toX = 0f;
        float fromY = mMenuButton.getY() - item.getY();
        float toY = 0f;

        Animation translate = new TranslateAnimation(fromX, toX, fromY, toY);
        translate.setDuration(300);
        translate.setInterpolator(new OvershootInterpolator());
        translate.setFillAfter(true);
        translate.setFillEnabled(true);

        Animation alpha = new AlphaAnimation(0.0f, 1.0f);
        alpha.setDuration(300);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(translate);
        animationSet.addAnimation(alpha);
        item.startAnimation(animationSet);
    }

    /**
     * Animation to be used when hiding menu items
     *
     * @param item
     */
    private void hideItemsAnimation(final View item) {

        float toX = mMenuButton.getX() - item.getX();
        float fromX = 0f;
        float toY = mMenuButton.getY() - item.getY();
        float fromY = 0f;

        Animation translate = new TranslateAnimation(fromX, toX, fromY, toY);
        translate.setDuration(300);
        translate.setInterpolator(new AccelerateInterpolator());
        translate.setFillAfter(true);
        translate.setFillEnabled(true);

        Animation alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setDuration(250);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(translate);
        animationSet.addAnimation(alpha);
        item.startAnimation(animationSet);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                item.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FanMenu.LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new FanMenu.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new FanMenu.LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof FanMenu.LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
