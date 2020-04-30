package com.dozen.world.ffmpeg.gl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 * Created by Hugo on 20-4-30.
 * Describe:
 *
 *
 *
 */
class TriangleSurfaceView : GLSurfaceView {

    constructor(context: Context?) : super(context) {
        initData(context, null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initData(context, attrs)
    }

    private fun initData(context: Context?, attrs: AttributeSet?) {
        setEGLContextClientVersion(2)
        setRenderer(TriangleRenderer.instance)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}