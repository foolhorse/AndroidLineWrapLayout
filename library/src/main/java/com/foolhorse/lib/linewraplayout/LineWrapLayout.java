/*
 * Copyright 2014 MaChao
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foolhorse.lib.linewraplayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by ID_MARR on 2014/10/9.
 */
public class LineWrapLayout extends LinearLayout {

    public LineWrapLayout(Context context) {
        this(context, null);
    }

    public LineWrapLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineWrapLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthAtMost = MeasureSpec.getSize(widthMeasureSpec) - this.getPaddingRight() - this.getPaddingLeft();
        int heightAtMost = MeasureSpec.getSize(heightMeasureSpec) - this.getPaddingTop() - this.getPaddingBottom();

        int x = 0, y = 0;
        int widthContent = 0, heightContent = 0 ;
        int maxDepthInSingleLine = 0 ;
        int maxLengthInAllLine = 0 ;
        final int count = getChildCount();
        for (int i = 0; i < count; i = i + 1) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            LayoutParams childLp = (LayoutParams) child.getLayoutParams();
            child.measure(
                    getChildMeasureSpec(widthMeasureSpec, this.getPaddingLeft() + this.getPaddingRight(), childLp.width),
                    getChildMeasureSpec(heightMeasureSpec, this.getPaddingTop() + this.getPaddingBottom(), childLp.height)
            );

            if (getOrientation() == HORIZONTAL) {
                boolean isNewLine = x + child.getMeasuredWidth()+childLp.leftMargin+childLp.rightMargin > widthAtMost ;
                if (isNewLine) {
                    x = 0;
                    y = y + maxDepthInSingleLine; // in previous line
                    maxDepthInSingleLine = 0;   // in new line
                }

                x = x + child.getMeasuredWidth() + childLp.leftMargin +childLp.rightMargin ;
                if(x > maxLengthInAllLine){
                    maxLengthInAllLine = x ;
                }
                if (child.getMeasuredHeight() + childLp.topMargin + childLp.bottomMargin > maxDepthInSingleLine) {
                    maxDepthInSingleLine = child.getMeasuredHeight() + childLp.topMargin + childLp.bottomMargin;
                }
                if(i == count-1){ // the last in children
                    widthContent = maxLengthInAllLine ;
                    heightContent = y + maxDepthInSingleLine;
                }
            } else {

            }
        }
        if (getOrientation() == HORIZONTAL) {
            this.setMeasuredDimension(
                    resolveSize(widthContent + getPaddingLeft() + getPaddingRight(), widthMeasureSpec),
                    resolveSize(heightContent + getPaddingTop() + getPaddingBottom(), heightMeasureSpec)
            );
        }else{

        }
    }

    // TODO Gravity, Vertical, divider
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(!changed){
            return;
        }
        int x = 0, y = 0;
        int maxDepthInSingleLine = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i=i+1) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            LayoutParams childLp = (LayoutParams) child.getLayoutParams();
            int childLeft = 0, childRight = 0, childTop = 0, childBottom = 0;
            if (getOrientation() == HORIZONTAL) {
                boolean newLine = x + child.getMeasuredWidth()+childLp.leftMargin+childLp.rightMargin > r ;
                if (newLine) {
                    x = 0;
                    y = y + maxDepthInSingleLine;
                    maxDepthInSingleLine = 0;
                }
                childLeft = x + childLp.leftMargin;
                childRight = childLeft + child.getMeasuredWidth()  ;
                childTop = y + childLp.topMargin;
                childBottom = childTop + child.getMeasuredHeight() ;

                // x
                x = x + child.getMeasuredWidth() + childLp.leftMargin + childLp.rightMargin;
                // max in a line
                if (child.getMeasuredHeight() + childLp.topMargin + childLp.bottomMargin > maxDepthInSingleLine) {
                    maxDepthInSingleLine = child.getMeasuredHeight() + childLp.topMargin + childLp.bottomMargin;
                }
            } else {

            }
            child.layout(childLeft, childTop, childRight, childBottom);
        }
    }

}
