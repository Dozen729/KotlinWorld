package com.dozen.world.ffmpeg

import android.graphics.*
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Created by Hugo on 20-4-26.
 * Describe:
 *
 *
 *
 */
class CameraShowTest {
    private lateinit var mCamera: Camera
    private var open = false
    private var surfaceView: SurfaceView? = null
    private lateinit var imageView:ImageView
    private var textureView: TextureView? = null

    //ImageView show
    private lateinit var mBAOS:ByteArrayOutputStream
    private lateinit var mImageBytes:ByteArray
    private lateinit var mBitmap: Bitmap
    private lateinit var mPreviewSize:Camera.Size

    companion object{
        val instance:CameraShowTest by lazy(mode=LazyThreadSafetyMode.SYNCHRONIZED){
            CameraShowTest()
        }
    }

    //通过SurfaceView预览相机
    fun openSurface(sv:SurfaceView){
        if (!open) {
            mCamera = Camera.open()
            open = true
        }
        surfaceView=sv
        surfaceView?.visibility = View.VISIBLE
        surfaceView?.holder!!.addCallback(surfaceCallBack)
        surfaceView?.keepScreenOn = true

    }

    //通过TextureView预览相机
    fun openTexture(tv:TextureView){
        if (!open) {
            mCamera = Camera.open()
            open = true
        }
        textureView=tv
        textureView?.visibility = View.VISIBLE
        textureView?.surfaceTextureListener = textureCallBack
        textureView?.invalidate()


    }

    //获取相机NV21的数据并显示
    fun showNV21Data(iv:ImageView){
        imageView=iv
        if (open){
            val p=mCamera.parameters
            p.previewFormat=ImageFormat.NV21
            mCamera.parameters=p
            mCamera.setPreviewCallback(cameraPCallback)
        }
    }

    private val surfaceCallBack: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        }

        override fun surfaceDestroyed(p0: SurfaceHolder?) {
            closeCamera()
        }

        override fun surfaceCreated(p0: SurfaceHolder?) {
            try {
                mCamera.setPreviewDisplay(p0)
                mCamera.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private val textureCallBack = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
            closeCamera()
            return false
        }

        override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
            try {
                mCamera.setPreviewTexture(p0)
                mCamera.startPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val cameraPCallback = Camera.PreviewCallback { p0, p1 ->

        if(open){
            mPreviewSize=mCamera.parameters.previewSize

            val yuvimage= YuvImage(p0, ImageFormat.NV21,mPreviewSize.width,mPreviewSize.height,null)

            mBAOS= ByteArrayOutputStream();
            yuvimage.compressToJpeg(Rect(0,0,mPreviewSize.width,mPreviewSize.height),100,mBAOS)
            mImageBytes=mBAOS.toByteArray()

            val options= BitmapFactory.Options()
            options.inPreferredConfig= Bitmap.Config.RGB_565

            mBitmap= BitmapFactory.decodeByteArray(mImageBytes,0,mImageBytes.size,options)
            imageView.setImageBitmap(mBitmap)
        }
    }

    fun closeCamera(){
        if (open){
            open = false
            mCamera.setPreviewCallback(null)
            mCamera.release()
            textureView?.visibility = View.GONE
            surfaceView?.visibility = View.GONE
        }
    }
}