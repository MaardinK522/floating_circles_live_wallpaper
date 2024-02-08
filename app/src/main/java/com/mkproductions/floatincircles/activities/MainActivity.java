package com.mkproductions.floatincircles.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mkproductions.floatincircles.services.FloatingCirclesWallpaperService;
import com.mkproductions.floatincircles.R;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private MaterialButton mApplyButton;
    private TextInputEditText mBallCountTextInputEditText;
    private TextView mBrightnessTextView;
    private TextView mSpeedTextView;
    private TextView mSizeTextView;
    private SeekBar mSpeedSeekBar;
    private SeekBar mBrightnessSeekBar;
    private SeekBar mSizeSeekBar;
    private CardView mColorPicker;
    private CheckBox mRandomColorCheckBox;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefEditor;
    private RadioGroup mShapesRadioGroup;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = sharedPreferences.edit();

        AtomicInteger ballCount = new AtomicInteger(sharedPreferences.getInt(getString(R.string.bob_count), 0));
        AtomicInteger ballAlpha = new AtomicInteger(sharedPreferences.getInt(getString(R.string.bob_alpha), 0));
        AtomicInteger ballSpeed = new AtomicInteger(sharedPreferences.getInt(getString(R.string.bob_speed), 0));
        AtomicInteger ballSizeFactor = new AtomicInteger(sharedPreferences.getInt(getString(R.string.bob_factor), 0));
        AtomicInteger ballColor = new AtomicInteger(sharedPreferences.getInt(getString(R.string.bob_color), 0));
        AtomicBoolean areBallsRandomized = new AtomicBoolean(sharedPreferences.getBoolean(getString(R.string.is_randomized), false));
        AtomicInteger renderingShape = new AtomicInteger(sharedPreferences.getInt(getString(R.string.rendering_shape), R.id.main_activity_circle_shapes_radio_button));

        mColorPicker.setCardBackgroundColor(ballColor.get());
        mColorPicker.setOnClickListener(mColorPickerClickListener);

        mBallCountTextInputEditText.setText(" " + ballCount.get());

        mBrightnessTextView.setText("Brightness: " + ballAlpha + "%");
        mBrightnessSeekBar.setProgress(ballAlpha.get());

        mSpeedTextView.setText("Speed: " + ballSpeed + "%");
        mSpeedSeekBar.setProgress(ballSpeed.get());

        mSizeTextView.setText("Size: " + ballSizeFactor + "%");
        mSizeSeekBar.setProgress(ballSizeFactor.get());

        mBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ballAlpha.set(progress);
                prefEditor.putInt(getString(R.string.bob_alpha), ballAlpha.get()).apply();
                mBrightnessTextView.setText("Brightness: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ballSpeed.set(progress);
                prefEditor.putInt(getString(R.string.bob_speed), ballSpeed.get()).apply();
                mSpeedTextView.setText("Speed: " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ballSizeFactor.set(progress);
                prefEditor.putInt(getString(R.string.bob_factor), ballSizeFactor.get()).apply();
                mSizeTextView.setText("Size: " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mRandomColorCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mColorPicker.setActivated(!isChecked);
            if (isChecked) mColorPicker.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Please, uncheck the random colors.", Toast.LENGTH_SHORT).show());
            else mColorPicker.setOnClickListener(mColorPickerClickListener);
        });

        mShapesRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            renderingShape.set(checkedId);
            prefEditor.putInt(getString(R.string.rendering_shape), renderingShape.get()).apply();
            Log.d("MainActivity", "Shape: " + (renderingShape.get() == R.id.main_activity_circle_shapes_radio_button ? "Circle" : "Rectangle"));
            Log.d("MainActivity", "Id: " + (renderingShape.get() == R.id.main_activity_circle_shapes_radio_button ? R.id.main_activity_circle_shapes_radio_button : R.id.main_activity_rect_shapes_radio_button));
        });

        mBallCountTextInputEditText.setText(String.valueOf(ballCount.get()));
        Log.d("MainActivity", "Package name: " + this.getPackageName());
        mApplyButton.setOnClickListener(view -> {
            if (Integer.parseInt(String.valueOf(mBallCountTextInputEditText.getText())) <= 0) {
                Toast.makeText(this, "Please enter non-zero positive values of ball count", Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(String.valueOf(mBallCountTextInputEditText.getText())) <= 150) {
                ballCount.set(Integer.parseInt(String.valueOf(mBallCountTextInputEditText.getText())));

                areBallsRandomized.set(mRandomColorCheckBox.isChecked());

                if (mRandomColorCheckBox.isChecked()) {
                    ballColor.set(mColorPicker.getSolidColor());
                    prefEditor.putInt(getString(R.string.bob_color), mColorPicker.getSolidColor()).apply();
                }
                prefEditor.putInt(getString(R.string.bob_count), ballCount.get()).apply();
                prefEditor.putBoolean(getString(R.string.is_randomized), mRandomColorCheckBox.isChecked()).apply();

                Log.d("Ball count", String.valueOf(sharedPreferences.getInt(getString(R.string.bob_count), 10)));
                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, FloatingCirclesWallpaperService.class));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Rendering too much on screen may drain more battery. Please keep it under 150.", Toast.LENGTH_SHORT).show();
                mBallCountTextInputEditText.setText("150");
            }
        });
    }

    private void findViews() {
        mColorPicker = findViewById(R.id.main_activity_color_picker_button);
        mApplyButton = findViewById(R.id.main_activity_apply_wallpaper_button);
        mBrightnessTextView = findViewById(R.id.main_activity_brightness_textview);
        mSpeedTextView = findViewById(R.id.main_activity_speed_textview);
        mSizeTextView = findViewById(R.id.main_activity_size_textview);

        mBrightnessSeekBar = findViewById(R.id.main_activity_brightness_seekbar);
        mSpeedSeekBar = findViewById(R.id.main_activity_speed_seekbar);
        mSizeSeekBar = findViewById(R.id.main_activity_size_seekbar);
        mRandomColorCheckBox = findViewById(R.id.main_activity_random_colo_checkbox);

        mShapesRadioGroup = findViewById(R.id.main_activity_shapes_radio_group);

        mBallCountTextInputEditText = findViewById(R.id.main_activity_ball_count_textinputedittext);
    }

    View.OnClickListener mColorPickerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new ColorPickerDialog.Builder(MainActivity.this).setTitle("Select color").setPositiveButton("SELECT", (ColorEnvelopeListener) (envelope, fromUser) -> {
                prefEditor.putInt(getString(R.string.bob_color), envelope.getColor());
                mColorPicker.setCardBackgroundColor(envelope.getColor());
            }).setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss()).setBottomSpace(10).setOnDismissListener(null).show();
        }
    };
}