package com.mkproductions.floatincircles.entities;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.mkproductions.floatincircles.services.FloatingCirclesWallpaperService;

import java.util.Random;

public class Bob {
    PointF pos;
    PointF target;
    int bobColor;
    int speed;

    public Bob(int bobColor, int speed, int width, int height) {
        this.bobColor = bobColor;
        this.speed = speed;
        this.pos = FloatingCirclesWallpaperService.getRandomPoint(width, height);
        this.target = FloatingCirclesWallpaperService.getRandomPoint(width, height);
    }

    public void renderCircle(Canvas canvas, Paint paint, int sizeFactor) {
        paint.setColor(this.bobColor);
        canvas.drawCircle(this.pos.x, this.pos.y, FloatingCirclesWallpaperService.bobFactor * ((float) sizeFactor / 100), paint);
    }

    public void renderRect(Canvas canvas, Paint paint, int sizeFactor) {
        float size = FloatingCirclesWallpaperService.bobFactor * ((float) sizeFactor / 100);
        size *= 2;
        paint.setColor(this.bobColor);
        canvas.drawRect(this.pos.x - (size / 2), this.pos.y - (size / 2), this.pos.x + size, this.pos.y + size, paint);
    }

    public void renderRoundedRect(Canvas canvas, Paint paint, int sizeFactor) {
        float size = FloatingCirclesWallpaperService.bobFactor * ((float) sizeFactor / 100);
        size *= 2;
        paint.setColor(this.bobColor);
        canvas.drawRoundRect(this.pos.x / 2, this.pos.y / 2, this.pos.x + size, this.pos.y + size, size * 0.25f, size * 0.25f, paint);
    }


    public void update(int frameCount, int width, int height) {
        int lowerBound = 50;
        int upperBound = 60;
        lowerBound += lowerBound * ((float) (speed / 100));
        upperBound += upperBound * ((float) (speed / 100));
        this.pos.x = FloatingCirclesWallpaperService.lerp(this.pos.x, this.target.x, 0.05f);
        this.pos.y = FloatingCirclesWallpaperService.lerp(this.pos.y, this.target.y, 0.05f);
        if (frameCount % (new Random().nextInt(upperBound - lowerBound) + lowerBound) == 0)
            this.target = FloatingCirclesWallpaperService.getRandomPoint(width, height);
    }

    public void setTarget(float x, float y) {
        this.target.x = x;
        this.target.y = y;
    }
}
