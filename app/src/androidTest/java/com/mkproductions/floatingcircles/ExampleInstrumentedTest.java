package com.mkproductions.floatingcircles;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private Context context;

    @Before
    public void setup() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        assertEquals("com.mkproductions.floatincircles", context.getPackageName());
    }
}