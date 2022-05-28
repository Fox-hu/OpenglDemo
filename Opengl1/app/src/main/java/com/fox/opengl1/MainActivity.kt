package com.fox.opengl1

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var glSurface: GLSurfaceView? = null
    private var rendererSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurface = GLSurfaceView(this)
        glSurface?.setRenderer(AirHockeyRenderer())
        rendererSet = true
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