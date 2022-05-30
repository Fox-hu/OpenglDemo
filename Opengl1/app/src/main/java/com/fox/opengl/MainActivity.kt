package com.fox.opengl

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private var glSurface: GLSurfaceView? = null
    private var rendererSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurface = GLSurfaceView(this)
        // Check if the system supports OpenGL ES 2.0.
        // Check if the system supports OpenGL ES 2.0.
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo: ConfigurationInfo = activityManager
            .deviceConfigurationInfo
        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        val supportsEs2 = (configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"))))

        rendererSet = if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurface?.setEGLContextClientVersion(2)

            // Assign our renderer.
            glSurface?.setRenderer(AirHockeyRenderer(this))
            true
        } else {
            /*
                 * This is where you could create an OpenGL ES 1.x compatible
                 * renderer if you wanted to support both ES 1 and ES 2. Since
                 * we're not doing anything, the app will crash if the device
                 * doesn't support OpenGL ES 2.0. If we publish on the market, we
                 * should also add the following to AndroidManifest.xml:
                 *
                 * <uses-feature android:glEsVersion="0x00020000"
                 * android:required="true" />
                 *
                 * This hides our app from those devices which don't support OpenGL
                 * ES 2.0.
                 */
            Toast.makeText(
                this, "This device does not support OpenGL ES 2.0.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        setContentView(glSurface)
    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            glSurface?.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            glSurface?.onResume()
        }
    }
}