package com.dozen.world.ffmpeg.gl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.dozen.world.R

/**
 * Created by Hugo on 20-4-30.
 * Describe:
 *
 *
 *
 */
class PictureSurfaceView : GLSurfaceView {
    constructor(context: Context?) : super(context) {
        initData(context, null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initData(context, attrs)
    }

    private fun initData(context: Context?, attrs: AttributeSet?) {
        setEGLContextClientVersion(2)
        val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.show_dozen)
        val p = PictureRenderer.instance
        p.addPictureToShow(bitmap)
        setRenderer(p)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}