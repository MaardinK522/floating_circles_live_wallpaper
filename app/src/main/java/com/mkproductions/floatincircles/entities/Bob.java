package com.mkproductions.floatincircles.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.mkproductions.floatincircles.services.FloatingCirclesWallpaperService;

import java.util.Random;

public class Bob {
    PointF pos;
    PointF target;
    int bobColor;

    public Bob(int bobColor, int width, int height) {
        this.bobColor = bobColor;
        this.pos = FloatingCirclesWallpaperService.getRandomPoint(width, height);
        this.target = FloatingCirclesWallpaperService.getRandomPoint(width, height);
    }

    public void show(Canvas canvas, Paint paint) {
        paint.setColor(this.bobColor);
        canvas.drawCircle(this.pos.x, this.pos.y, FloatingCirclesWallpaperService.bobSize, paint);
    }

    public void update(int frameCount, int width, int height) {
        int lowerBound = 40;
        int upperBound = 60;
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
