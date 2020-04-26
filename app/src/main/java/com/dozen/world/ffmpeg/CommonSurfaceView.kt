package com.dozen.world.ffmpeg

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.Exception

/**
 * Created by Hugo on 20-4-26.
 * Describe:
 *
 *
 *
 */
class CommonSurfaceView: SurfaceView,SurfaceHolder.Callback,Runnable {

    private lateinit var mHolder:SurfaceHolder
    private lateinit var mCanvas: Canvas
    private lateinit var mThread: Thread

    private lateinit var mPaint: Paint


    private var isShow:Boolean=false


    constructor(context: Context?) : super(context){initThis()}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){initThis()}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){initThis()}

    private fun initThis(){

        mHolder=holder
        mHolder.addCallback(this)

        mPaint= Paint()
        mPaint.textSize=20f
        mPaint.color=Color.BLUE
        mPaint.isAntiAlias=true

        isFocusable=true
        isFocusableInTouchMode=true
        keepScreenOn=true
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        isShow=false
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        isShow=true
        mThread= Thread(this)
        mThread.start()
    }

    override fun run() {

        while (isShow){
            draw()
        }

    }

    private fun draw(){
        try {
            mCanvas=mHolder.lockCanvas()

            mCanvas.drawText("Hello SurfaceView",5f,30f,mPaint)

        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            if (isShow){
                mHolder.unlockCanvasAndPost(mCanvas)
            }
        }
    }
}