package com.mkproductions.floatingcircles.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.mkproductions.floatingcircles.R;
import com.mkproductions.floatingcircles.entities.Bob;

import java.util.Objects;
import java.util.Random;

public class FloatingCirclesWallpaperService extends WallpaperService {
    public static int WIDTH;
    public static int HEIGHT;

    @Override
    public WallpaperService.Engine onCreateEngine() {
        return new FloatingCirclesEngine();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    public static float lerp(float start, float end, float factor) {
        return start + (end - start) * factor;
    }

    public static PointF getRandomPoint(int width, int height) {
        Random random = new Random();
        return new PointF(random.nextFloat() * width, random.nextFloat() * height);
    }

    private class FloatingCirclesEngine extends WallpaperService.Engine {
        private SurfaceHolder holder;
        private boolean visible;
        private Handler handler;
        private Paint paint;
        private Bob[] bobs;
        private float bobAlpha;
        public int bobFactor = 50;
        private int bobCount;
        private int bobSpeed;
        private int bobColor;
        private boolean isRandomized;
        private String shape = "";

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE);
            bobCount = sharedPreferences.getInt(getString(R.string.bob_count), 0);
            bobSpeed = sharedPreferences.getInt(getString(R.string.bob_speed), 0);
            bobColor = sharedPreferences.getInt(getString(R.string.bob_color), Color.WHITE);
            isRandomized = sharedPreferences.getBoolean(getString(R.string.is_randomized), false);
            bobAlpha = sharedPreferences.getInt(getString(R.string.bob_alpha), 0);
            bobFactor = sharedPreferences.getInt(getString(R.string.bob_factor), 0);
            int renderingShape = sharedPreferences.getInt(getString(R.string.rendering_shape), R.id.main_activity_circle_shapes_radio_button);

            handler = new Handler();
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            bobs = new Bob[bobCount];

            if (renderingShape == R.id.main_activity_circle_shapes_radio_button) {
                this.shape = "C";
            } else if (renderingShape == R.id.main_activity_rect_shapes_radio_button) {
                this.shape = "R";
            } else if (renderingShape == R.id.main_activity_round_rect_shapes_radio_button) {
                this.shape = "RR";
            }

            this.holder = surfaceHolder;
            Log.d("FloatingService", "Bob speed: " + bobSpeed);
            for (int x = 0; x < bobCount; x++) {
                bobs[x] = new Bob(isRandomized ? getRandomColor() : Color.valueOf(bobColor), bobFactor, WIDTH, HEIGHT, (float) bobSpeed / 100);
            }
        }

        private Color getRandomColor() {
            Random random = new Random();
            float alpha = bobAlpha / 100;
//            Log.d("Floating Circles", "Ball Alpha " + alpha);
            float red = random.nextFloat();
            float green = random.nextFloat();
            float blue = random.nextFloat();
//            Log.d("Floating Circles", "Color(alpha: " + alpha + ", red: " + red + ", green: " + green + ", blue: " + blue + ")");
            return Color.valueOf(red, green, blue, alpha);
        }

        private final Runnable drawFloatingCircles = this::draw;

        private void draw() {
            if (this.visible) {
                Canvas canvas = holder.lockCanvas();
                paint.setColor(Color.argb(255, 0, 0, 0));
                canvas.drawRect(0, 0, WIDTH, HEIGHT, paint);
                for (Bob bob : bobs) {
                    if (Objects.equals(this.shape, "C")) bob.renderCircle(canvas, paint);
                    else if (Objects.equals(this.shape, "RR")) bob.renderRoundedRect(canvas, paint);
                    else bob.renderRect(canvas, paint);
                    bob.update(WIDTH, HEIGHT);
                }
                canvas.save();
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                handler.removeCallbacks(drawFloatingCircles);
                handler.postDelayed(drawFloatingCircles, 1000 / 60);
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
            for (int x = 0; x < bobCount; x++) {
                bobs[x] = new Bob(isRandomized ? getRandomColor() : Color.valueOf(bobColor), bobFactor, WIDTH, HEIGHT, bobSpeed);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawFloatingCircles);
        }
    }
}
