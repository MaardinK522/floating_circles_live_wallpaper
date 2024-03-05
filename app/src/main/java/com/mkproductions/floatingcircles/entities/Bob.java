package com.mkproductions.floatingcircles.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.mkproductions.floatingcircles.services.FloatingCirclesWallpaperService;

import java.util.Random;

public class Bob {
    private final PointF pos;
    private final PointF vel;
    private final float velFactor;
    private final Color bobColor;
    private final float bobSize;

    public Bob(Color bobColor, float bobSize, int width, int height, float velFactor) {
        this.bobColor = bobColor;
        this.bobSize = bobSize;
        this.velFactor = velFactor;
        this.pos = FloatingCirclesWallpaperService.getRandomPoint((int) (width - bobSize), (int) (height - bobSize));
        Random random = new Random();
        this.vel = new PointF(random.nextFloat(), random.nextFloat());
    }

    public void renderCircle(Canvas canvas, Paint paint) {
        paint.setColor(this.bobColor.toArgb());
        canvas.drawCircle(this.pos.x, this.pos.y, this.bobSize / 2, paint);
    }

    public void renderRect(Canvas canvas, Paint paint) {
        paint.setColor(this.bobColor.toArgb());
        canvas.drawRect(this.pos.x, this.pos.y, this.pos.x + bobSize, this.pos.y + bobSize, paint);
    }

    public void renderRoundedRect(Canvas canvas, Paint paint) {
        paint.setColor(this.bobColor.toArgb());
        canvas.drawRoundRect(this.pos.x, this.pos.y, this.pos.x + this.bobSize, this.pos.y + this.bobSize, this.bobSize * 0.25f, this.bobSize * 0.25f, paint);
    }

    public void update(int width, int height) {
//        this.pos.x += FloatingCirclesWallpaperService.lerp(this.lastPos.x, this.target.x, 0.01f);
//        this.pos.y += FloatingCirclesWallpaperService.lerp(this.lastPos.y, this.target.y, 0.01f);
//        if (this.pos.x == this.target.x && this.pos.y == this.target.y) {
//            this.target = FloatingCirclesWallpaperService.getRandomPoint(width, height);
//        }
        if (this.pos.x + this.bobSize >= width || this.pos.x <= 0) this.vel.x *= -1;
        if (this.pos.y + this.bobSize >= height || this.pos.y <= 0) this.vel.y *= -1;
        this.pos.x += this.vel.x * this.velFactor;
        this.pos.y += this.vel.y * this.velFactor;
    }
}
