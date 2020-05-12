package com.dozen.world.ffmpeg.h264


import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ArrayBlockingQueue

/**
 * Created by Hugo on 20-5-9.
 * Describe:
 *
 *
 *
 */
class AvcEncoder(
    var width: Int,
    var height: Int,
    var frameRate: Int,
    var outFile: File,
    var isCamera: Boolean
) {
    private val TAG = "AvcEncoder"
    private var TIMEOUT_USEC: Long = 10000
    private var mYuvQueueSize: Int = 10

    public var mYuvQueue = ArrayBlockingQueue<ByteArray>(mYuvQueueSize)

    private lateinit var mMediaCodec: MediaCodec
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mFrameRate: Int = 0

    private lateinit var mOutFile: File
    //true--Camera的预览数据编码
    // false--Camera2的预览数据编码
    private var mIsCamera: Boolean = false

    lateinit var mConfigByte: ByteArray

    init {
        mIsCamera = isCamera
        mWidth = width
        mHeight = height
        mFrameRate = frameRate
        mOutFile = outFile

        var mediaFormat =
            MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
        )
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 5)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mMediaCodec.start()
        createFile()
    }

    private lateinit var outputStream: BufferedOutputStream
    private fun createFile() {
        try {
            outputStream = BufferedOutputStream(FileOutputStream(mOutFile))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun stopEncoder() {
        if (mMediaCodec != null) {
            mMediaCodec.stop()
            mMediaCodec.release()
//            mMediaCodec = null
        }
    }

    fun putYUVData(buffer: ByteArray) {
        if (mYuvQueue.size >= 10) {
            mYuvQueue.poll()
        }
        mYuvQueue.add(buffer)
    }

    fun stopThread() {
        if (!isRunning) return
        isRunning = false
        try {
            stopEncoder()
            if (outputStream != null) {
                outputStream.flush()
                outputStream.close()
//                outputStream = null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    var isRunning: Boolean = false
    fun startEncoderThread() {

        Thread(Runnable {
            isRunning = true
            var input: ByteArray? = null
            var pts: Long = 0
            var generateIndex: Long = 0

            while (isRunning) {
                if (mYuvQueue.size > 0) {
                    input = mYuvQueue.poll()!!
                    input = if (mIsCamera) {//Camera  NV21
                        //NV12数据所需空间为如下，所以建立如下缓冲区
                        //y=W*hu=W*H/4v=W*H/4,so total add is W*H*3/2 (1 + 1/4 + 1/4 = 3/2)
                        val yuv420sp = ByteArray(mWidth * mHeight * 3 / 2)
                        NV21ToNV12(input, yuv420sp, mWidth, mHeight)
                        yuv420sp
                    } else {//Camera 2
                        val yuv420sp = ByteArray(mWidth * mHeight * 3 / 2)
                        YV12toNV12(input, yuv420sp, mWidth, mHeight)
                        yuv420sp
                    }
                }

                if (input != null) {
                    val inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1)
                    if (inputBufferIndex >= 0) {
                        pts = computePresentationTime(generateIndex)
                        val inputBuffer = mMediaCodec.getInputBuffer(inputBufferIndex)!!
                        inputBuffer.clear()
                        inputBuffer.put(input)
                        mMediaCodec.queueInputBuffer(inputBufferIndex, 0, input.size, pts, 0)
                        generateIndex += 1
                    }

                    val bufferInfo = MediaCodec.BufferInfo()
                    var outputBufferIndex = mMediaCodec.dequeueOutputBuffer(
                        bufferInfo,
                        TIMEOUT_USEC
                    )
                    while (outputBufferIndex >= 0) {
                        val outputBuffer = mMediaCodec.getOutputBuffer(outputBufferIndex)!!
                        val outData = ByteArray(bufferInfo.size)
                        outputBuffer.get(outData)
                        when {
                            bufferInfo.flags == 2 -> {
                                mConfigByte = ByteArray(bufferInfo.size)
                                mConfigByte = outData
                            }
                            bufferInfo.flags == 1 -> {
                                val keyframe = ByteArray(bufferInfo.size + mConfigByte.size)
                                System.arraycopy(mConfigByte, 0, keyframe, 0, mConfigByte.size)
                                System.arraycopy(outData, 0, keyframe, mConfigByte.size, outData.size)
                                try {
                                    outputStream.write(keyframe, 0, keyframe.size)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                            else -> try {
                                outputStream.write(outData, 0, outData.size)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        mMediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                        outputBufferIndex = mMediaCodec.dequeueOutputBuffer(
                            bufferInfo,
                            TIMEOUT_USEC
                        )
                    }
                } else {
                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
         }).start()

//        val encoderThread = Thread(Runnable {
//
//        })
//        encoderThread.start()
    }

    private fun NV21ToNV12(nv21: ByteArray, nv12: ByteArray, width: Int, height: Int) {
        val frameSize = width * height
        System.arraycopy(nv21, 0, nv12, 0, frameSize)
        for (i in 0 until frameSize) nv12[i] = nv21[i]
        for (i in 0 until frameSize / 2 step 2) nv12[frameSize + i - 1] = nv21[i + frameSize]
        for (i in 0 until frameSize / 2 step 2) nv12[frameSize + i] = nv21[i + frameSize - 1]
    }

    private fun YV12toNV12(yv12bytes: ByteArray, nv12bytes: ByteArray, width: Int, height: Int) {

        val nLenY = width * height
        val nLenU = nLenY / 4


        System.arraycopy(yv12bytes, 0, nv12bytes, 0, width * height)
        for (i in 0 until nLenU) {
            nv12bytes[nLenY + 2 * i] = yv12bytes[nLenY + i]
            nv12bytes[nLenY + 2 * i + 1] = yv12bytes[nLenY + nLenU + i]
        }
    }

    /**
     * Generates the presentation time for frame N, in microseconds.
     */
    private fun computePresentationTime(frameIndex: Long) = 132 + frameIndex * 1000000 / mFrameRate

}