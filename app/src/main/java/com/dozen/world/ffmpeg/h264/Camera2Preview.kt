package com.dozen.world.ffmpeg.h264

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.util.*


/**
 * Created by Hugo on 20-5-9.
 * Describe:
 *
 *
 *
 */
class Camera2Preview: TextureView {
    constructor(context: Context?) : super(context) {initCamera(context)}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {initCamera(context)}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {initCamera(context)}

    private fun initCamera(context: Context?){
        if (context != null) {
            this.mContext = context
        }
        keepScreenOn = true
        getDefaultCameraId()
    }

    companion object {
        private const val TAG = "Camera2Preview"
        private const val CAMERA_FONT = "0"
        private const val CAMERA_BACK = "1"
        private const val STATE_PREVIEW: Int = 0
        private const val STATE_RECORD: Int = 1
    }


    private lateinit var mBackgroundHandler: Handler
    private lateinit var mBackgroundThread: HandlerThread
    private var mCameraManager: CameraManager? = null
    private lateinit var mCameraDevice: CameraDevice
    private lateinit var mCameraCaptureSessions: CameraCaptureSession
    private lateinit var mPreviewRequestBuilder: CaptureRequest.Builder
    private lateinit var mImageReader: ImageReader
    private lateinit var mContext: Context

    private lateinit var mPreviewSize: Size

    private lateinit var mCameraId: String
    private lateinit var outputMediaFile: File


    private var textureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {

        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
            return false
        }

        override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
            //开启摄像头
            setupCamera()

        }

    }

    fun startVideo(filePath: String) {

        outputMediaFile = File(filePath)
        // Create the storage directory if it does not exist
        if (outputMediaFile.exists()) {
            outputMediaFile.delete()
            if (!outputMediaFile.createNewFile()) {
                Log.d(TAG, "failed to create directory")
                return
            }
        }

        startBackgroundThread()
        if (isAvailable) {
            setupCamera()
        } else {
            surfaceTextureListener = textureListener
        }
        toggleVideo()
    }


    fun stopVideo() {
        toggleVideo()
        closeCamera()
        stopBackgroundThread()
    }

    private fun stopBackgroundThread() {
        mBackgroundThread.quitSafely()
        try {
            mBackgroundThread.join()
        } catch (e:InterruptedException) {
            e.printStackTrace()
        }

    }

    private fun closeCamera() {
        closePreviewSession()
        mCameraDevice.close()
        mImageReader.close()
    }

    private fun closePreviewSession() {
        mCameraCaptureSessions.close()
    }

    /**
     * 开启摄像机线程
     */
    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread.start()
        mBackgroundHandler = Handler(mBackgroundThread.looper)

    }

    /**
     * 开启摄像头
     */
    private fun setupCamera() {
        Log.e(TAG, "setupCamera START")
        if (mCameraManager == null) {
            Log.e(TAG, "尚未得到CameraManager")
            return
        }
        try {
            //获取相机特征对象
            val characteristics = mCameraManager?.getCameraCharacteristics(mCameraId)!!
            //获取相机输出流配置
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            //获取预览输出尺寸
            mPreviewSize = getPreferredPreviewSize(
                map!!.getOutputSizes(Class.forName("android.graphics.SurfaceTexture")),
                width, height
            )
            transformImage(width.toFloat(), height.toFloat())

            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            setupImageReader()
            mCameraManager?.openCamera(mCameraId, stateCallback, null)
        } catch (e:CameraAccessException) {
            e.printStackTrace()
        }
        Log.e(TAG, "setupCamera END")
    }


    private fun getPreferredPreviewSize(mapSizes: Array<Size>, width: Int, height: Int): Size {
        Log.e(TAG, "getPreferredPreviewSize: surface width=$width,surface height=$height")
        val collectorSizes = ArrayList<Size>()

        mapSizes.forEach {
            if (width > height) {
                if (it.width > width &&
                    it.height > height
                ) {
                    collectorSizes.add(it)
                }
            } else {
                if (it.width > height &&
                    it.height > width
                ) {
                    collectorSizes.add(it)
                }
            }
        }

        if (collectorSizes.size > 0) {

            return Collections.min(collectorSizes) { lhs, rhs ->
                val i = when {
                    lhs.width * lhs.height - rhs.width * rhs.height > 0 -> 1
                    lhs.width * lhs.height - rhs.width * rhs.height < 0 -> -1
                    else -> 0
                }
                i
            }
        }
        Log.e(
            TAG,
            "getPreferredPreviewSize: best width=" + mapSizes[0].width + ",height=" + mapSizes[0].height
        )
        return mapSizes[0]
    }


    private fun transformImage(width:Float, height:Float) {
        val matrix = Matrix()
        val rotation = (mContext as Activity).windowManager.defaultDisplay.rotation
        val textureRectF = RectF(0f, 0f, width, height)
        val previewRectF = RectF(
            0f, 0f, mPreviewSize.width.toFloat(),
            mPreviewSize.width.toFloat()
        )
        val centerX = textureRectF.centerX()
        val centerY = textureRectF.centerY()
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            previewRectF.offset(
                centerX - previewRectF.centerX(),
                centerY - previewRectF.centerY()
            )
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL)
            val scale = (width / mPreviewSize.width).coerceAtLeast(height / mPreviewSize.height)
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
        }
        setTransform(matrix)
    }

    private var mState = STATE_PREVIEW
    private var mAvcEncoder: AvcEncoder?=null
    private var mFrameRate = 30

    private fun setupImageReader() {
        //2代表ImageReader中最多可以获取两帧图像流
        mImageReader = ImageReader.newInstance(
            mPreviewSize.width,
            mPreviewSize.height,
            ImageFormat.YV12,
            1
        )

        mImageReader.setOnImageAvailableListener({
            Log.e(TAG, "onImageAvailable: " + Thread.currentThread().name)
                //这里一定要调用reader.acquireNextImage()和img.close方法否则不会一直回掉了
            val img = it.acquireNextImage()
                when(mState) {
                    STATE_PREVIEW->{
                        Log.e(TAG, "mState: STATE_PREVIEW")
                        if (mAvcEncoder != null) {
                            mAvcEncoder?.stopThread()
                            mAvcEncoder = null
                            Toast.makeText(mContext, "停止录制视频成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                    STATE_RECORD->{
                        Log.e(TAG, "mState: STATE_RECORD")
                        val planes = img.planes
                        var dataYUV:ByteArray= ByteArray(0)
                        if (planes.size >= 3) {
                            val bufferY = planes[0].buffer
                            val bufferU = planes[1].buffer
                            val bufferV = planes[2].buffer
                            val lengthY = bufferY.remaining()
                            val lengthU = bufferU.remaining()
                            val lengthV = bufferV.remaining()
                            dataYUV = ByteArray(lengthY + lengthU + lengthV)
                            bufferY.get(dataYUV, 0, lengthY)
                            bufferU.get(dataYUV, lengthY, lengthU)
                            bufferV.get(dataYUV, lengthY + lengthU, lengthV)
                        }

                        if (mAvcEncoder == null) {
                            mAvcEncoder = AvcEncoder(
                                mPreviewSize.width,
                                mPreviewSize.height, mFrameRate,
                                outputMediaFile, false
                            )
                            mAvcEncoder?.startEncoderThread()
                            Toast.makeText(mContext, "开始录制视频成功", Toast.LENGTH_SHORT).show()
                        }
                        mAvcEncoder?.putYUVData(dataYUV)
                    }
                }
                img.close()
            }
        , mBackgroundHandler)
    }

    private fun getDefaultCameraId() {
        mCameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraList = mCameraManager?.cameraIdList!!
            cameraList.forEach { element ->
                if (TextUtils.equals(element, CAMERA_FONT)) {
                    mCameraId = element
                } else if (TextUtils.equals(element, CAMERA_BACK)) {
                    mCameraId = element
                }
            }
        } catch (e:CameraAccessException) {
            e.printStackTrace()
        }
    }

    private var stateCallback =  object : CameraDevice.StateCallback() {
        override fun onOpened(p0: CameraDevice) {
            Log.e(TAG, "onOpened...")
            mCameraDevice = p0
            createCameraPreview()
        }

        override fun onDisconnected(p0: CameraDevice) {
            mCameraDevice.close()
        }

        override fun onError(p0: CameraDevice, p1: Int) {
            mCameraDevice.close()
        }
    }


    /**
     * 创建预览界面
     */
    private fun createCameraPreview() {
        try {
            Log.e(TAG, "createCameraPreview")
            //获取当前TextureView的SurfaceTexture
            val texture = surfaceTexture
            //设置SurfaceTexture默认的缓存区大小，为 上面得到的预览的size大小
            texture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)
            val surface = Surface(texture)
            //创建CaptureRequest对象，并且声明类型为TEMPLATE_PREVIEW，可以看出是一个预览类型

            mPreviewRequestBuilder =
                mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

            //CaptureRequest.
            //设置请求的结果返回到到Surface上
            mPreviewRequestBuilder.addTarget(surface)

            mPreviewRequestBuilder.addTarget(mImageReader.surface)

            //创建CaptureSession对象
            mCameraDevice.createCaptureSession(
                listOf(surface, mImageReader.surface),
                object :CameraCaptureSession.StateCallback() {
                    override fun onConfigured(p0: CameraCaptureSession) {
                        //The camera is already closed
                        Log.e(TAG, "onConfigured: ")
                        // When the session is ready, we start displaying the preview.
                        mCameraCaptureSessions = p0
                        //更新预览
                        updatePreview()
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                        Toast.makeText(mContext, "Configuration change", Toast.LENGTH_SHORT).show()
                    }
                },
                null
            )
        } catch (e:CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 更新预览
     */
    private fun updatePreview() {
        Log.e(TAG, "updatePreview: ")
        //设置相机的控制模式为自动，方法具体含义点进去（auto-exposure, auto-white-balance, auto-focus）
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        try {
            //设置重复捕获图片信息
            mCameraCaptureSessions.setRepeatingRequest(
                mPreviewRequestBuilder.build(),
                null,
                mBackgroundHandler
            )
        } catch (e:CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun toggleVideo(): Boolean
    {
        return if (mState == STATE_PREVIEW) {
            mState = STATE_RECORD
            true
        } else {
            mState = STATE_PREVIEW
            false
        }
    }

}