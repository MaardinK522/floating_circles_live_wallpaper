package com.mkproductions.floatincircles.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.mkproductions.floatincircles.R;
import com.mkproductions.floatincircles.entities.Bob;

import java.util.Random;

public class FloatingCirclesWallpaperService extends WallpaperService {
    public static int WIDTH;
    public static int HEIGHT;
    public static int bobFactor = 50;
    private int frameCount = 0;
    private int bobCount;
    private float bobAlpha;
    private int bobColor;
    private boolean isRandomized;
    private int bobSpeed;
    private int shape;

    @Override
    public WallpaperService.Engine onCreateEngine() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE);
        bobCount = sharedPreferences.getInt(getString(R.string.bob_count), 0);
        bobAlpha = sharedPreferences.getInt(getString(R.string.bob_alpha), 100);
        bobSpeed = sharedPreferences.getInt(getString(R.string.bob_speed), 1);
        bobFactor = sharedPreferences.getInt(getString(R.string.bob_factor), 1);
        bobColor = sharedPreferences.getInt(getString(R.string.bob_color), Color.WHITE);
        isRandomized = sharedPreferences.getBoolean(getString(R.string.is_randomized), false);
        shape = sharedPreferences.getInt(getString(R.string.rendering_shape), R.id.main_activity_circle_shapes_radio_button);
        return new FloatingCirclesEngine();
    }

    public static float lerp(float start, float end, float factor) {
        return start + (end - start) * factor;
    }

    public static PointF getRandomPoint(int width, int height) {
        Random random = new Random();
        return new PointF(random.nextFloat() * (width - bobFactor) + bobFactor, random.nextFloat() * (height - bobFactor) + bobFactor);
    }

    private class FloatingCirclesEngine extends WallpaperService.Engine {
        private SurfaceHolder holder;
        private boolean visible;
        private final Handler handler;
        private final Paint paint;
        private final Bob[] bobs;

        public FloatingCirclesEngine() {
            handler = new Handler();
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            bobs = new Bob[bobCount];
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
            for (int a = 0; a < bobCount; a++)
                bobs[a] = new Bob(isRandomized ? getRandomColor() : bobColor, bobSpeed, WIDTH, HEIGHT);
        }

        private int getRandomColor() {
            Random random = new Random();
            float alpha = bobAlpha / 100;
            Log.d("Floating Circles", "Ball Alpha " + alpha);
            return Color.argb(alpha, random.nextFloat(), random.nextFloat(), random.nextFloat());
        }

        private final Runnable drawFloatingCircles = this::draw;

        private void draw() {
            if (this.visible) {
                Canvas canvas = holder.lockCanvas();
                paint.setColor(Color.argb(255, 0, 0, 0));
                canvas.drawRect(0, 0, WIDTH, HEIGHT, paint);
                for (Bob bob : bobs) {
                    if (shape == R.id.main_activity_circle_shapes_radio_button) bob.renderCircle(canvas, paint, bobFactor);
                    else if (shape == R.id.main_activity_round_rect_shapes_radio_button) bob.renderRoundedRect(canvas, paint, bobFactor);
                    else bob.renderRect(canvas, paint, bobFactor);
                    bob.update(frameCount, WIDTH, HEIGHT);
                }
                canvas.save();
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                handler.removeCallbacks(drawFloatingCircles);
                handler.postDelayed(drawFloatingCircles, 1000 / 60);
                frameCount++;
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) handler.post(drawFloatingCircles);
            else handler.removeCallbacks(drawFloatingCircles);
        }


        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            WIDTH = width;
            HEIGHT = height;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawFloatingCircles);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            for (Bob bob : bobs) {
                bob.setTarget(event.getX(), event.getY());
            }
        }
    }
}
