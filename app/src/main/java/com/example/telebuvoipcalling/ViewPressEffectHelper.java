package com.example.telebuvoipcalling;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 *
 * View Press Effect Helper
 * - do some simple press effect like iOS
 *
 * Copyright (c) 2014 @author extralam @ HongKong (http://ah-lam.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Simple Usage:
 * ImageView img = (ImageView) findViewById(R.id.img);
 * ViewPressEffectHelper.attach(img)
 *
 *
 *
 */
public class ViewPressEffectHelper {

    /**
     * Attach the View which you want have a touch event
     * @param view - any view
     */
    public static void attach(View view){
        view.setOnTouchListener(new ASetOnTouchListener(view));
    }

    private static class ASetOnTouchListener implements View.OnTouchListener{

        final float ZERO_ALPHA = 1.0f;
        final float HALF_ALPHA = 0.8f;
        final int FIXED_DURATION = 100;
        float alphaOrginally = 1.0f;

        public ASetOnTouchListener(View v){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                alphaOrginally = v.getAlpha();
            }
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                        final AlphaAnimation animation = new AlphaAnimation(ZERO_ALPHA, HALF_ALPHA);
                        animation.setDuration(FIXED_DURATION);
                        animation.setFillAfter(true);
                        v.startAnimation(animation);
                    } else{
                        v.animate().setDuration(FIXED_DURATION).alpha(HALF_ALPHA);
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:{
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                        final AlphaAnimation animation = new AlphaAnimation(HALF_ALPHA, ZERO_ALPHA);
                        animation.setDuration(FIXED_DURATION);
                        animation.setFillAfter(true);
                        v.startAnimation(animation);
                    } else {
                        v.animate().setDuration(100).alpha(alphaOrginally);
                    }
                }
                break;
            }
            return false;
        }

    }
}


