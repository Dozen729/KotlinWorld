package com.dozen.world.ffmpeg.h264

import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import com.dozen.world.impl.CommonCallback
import java.io.*
import java.nio.ByteBuffer


/**
 * Created by Hugo on 20-5-15.
 * Describe:
 *
 *
 *
 */
class PlayMedia {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mSurfaceView: SurfaceView
    private lateinit var mSurfaceHolder: SurfaceHolder
    private lateinit var mCodec: MediaCodec
    private var mStopFlag = false
    private lateinit var mInputStream: DataInputStream
    private var mUseSPSandPPS = false
    private lateinit var mCommonCallback: CommonCallback

    /**
     * 获取需要解码的文件流
     */
    fun setFileInputStream(filePath: String, surfaceView: SurfaceView) {
        mSurfaceView = surfaceView
        try {
            val file = File(filePath)
            mInputStream = DataInputStream(FileInputStream(file))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            try {
                mInputStream.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        }
    }

    fun initCallback(callback: CommonCallback) {
        mCommonCallback = callback
    }

    /**
     * 初始化解码器
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    fun initMediaCodec() {
        mSurfaceHolder = mSurfaceView.holder

        mSurfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

            override fun surfaceDestroyed(p0: SurfaceHolder?) {}

            override fun surfaceCreated(p0: SurfaceHolder?) {
                try {
                    //创建编码器
                    mCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                //初始化编码器
                val mediaFormat = MediaFormat.createVideoFormat(
                    MediaFormat.MIMETYPE_VIDEO_AVC,
                    p0?.surfaceFrame?.width()!!,
                    p0.surfaceFrame.height()
                )
                /*h264常见的帧头数据为：
                00 00 00 01 67    (SPS)
                00 00 00 01 68    (PPS)
                00 00 00 01 65    (IDR帧)
                00 00 00 01 61    (P帧)*/

                //获取H264文件中的pps和sps数据
                if (mUseSPSandPPS) {
                    val headerSPS = arrayListOf(
                        0,
                        0,
                        0,
                        1,
                        67,
                        66,
                        0,
                        42,
                        149.toByte(),
                        168.toByte(),
                        30,
                        0,
                        137.toByte(),
                        249.toByte(),
                        102,
                        224.toByte(),
                        32,
                        32,
                        32,
                        64
                    )
                    val headerPPS = arrayListOf(
                        0,
                        0,
                        0,
                        1,
                        68,
                        206.toByte(),
                        60,
                        128.toByte(),
                        0,
                        0,
                        0,
                        1,
                        6,
                        229.toByte(),
                        1,
                        151.toByte(),
                        128.toByte()
                    )
                    mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(headerSPS.toByteArray()))
                    mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(headerPPS.toByteArray()))
                }

                //设置帧率
                mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 40)
                mCodec.configure(mediaFormat, p0.surface, null, 0)

                startDecodingThread()
            }
        })
    }

    /**
     * 开启解码器并开启读取文件的线程
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun startDecodingThread() {
        mCodec.start()

        Thread(Runnable {
            //获取一组输入缓存区
            val inputBuffers = mCodec.inputBuffers
            //解码后的数据，包含每一个buffer的元数据信息
            val info = MediaCodec.BufferInfo()
            val startMs = System.currentTimeMillis()
            val timeoutUs: Long = 10000
            //用于检测文件头
            val maker0 = arrayListOf<Byte>(0, 0, 0, 1)

            val dummyFrame = arrayListOf<Byte>(0x00, 0x00, 0x01, 0x20)
            //返回可用的字节数组
            var streamBuffer: ByteArray = getBytes(mInputStream)

            var bytesCNT: Int
            while (!mStopFlag) {
                //得到可用字节数组长度
                bytesCNT = streamBuffer.size

                if (bytesCNT == 0) {
                    streamBuffer = dummyFrame.toByteArray()
                }
                var startIndex = 0
                //定义记录剩余字节的变量
                val remaining = bytesCNT
                while (true) {
                    //当剩余的字节=0或者开始的读取的字节下标大于可用的字节数时  不在继续读取
                    if (remaining == 0 || startIndex >= remaining) {
                        break
                    }
                    //寻找帧头部
                    var nextFrameStart =
                        mKMPMatch(maker0.toByteArray(), streamBuffer, startIndex + 2, remaining)
                    //找不到头部返回-1
                    if (nextFrameStart == -1) {
                        nextFrameStart = remaining
                    }
                    //得到可用的缓存区
                    val inputIndex = mCodec.dequeueInputBuffer(timeoutUs)
                    //有可用缓存区
                    if (inputIndex >= 0) {
                        val byteBuffer = inputBuffers[inputIndex]
                        byteBuffer.clear()
                        //将可用的字节数组，传入缓冲区
                        byteBuffer.put(streamBuffer, startIndex, nextFrameStart - startIndex)
                        //把数据传递给解码器
                        mCodec.queueInputBuffer(inputIndex, 0, nextFrameStart - startIndex, 0, 0)
                        //指定下一帧的位置
                        startIndex = nextFrameStart
                    } else {
                        continue
                    }

                    val outputIndex = mCodec.dequeueOutputBuffer(info, timeoutUs)
                    if (outputIndex >= 0) {
                        //帧控制是不在这种情况下工作，因为没有PTS H264是可用的
                        while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                            try {
                                Thread.sleep(100)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                        val doRender = (info.size != 0)
                        //对outputBuffer的处理完后，调用这个函数把buffer重新返回给codec类。
                        mCodec.releaseOutputBuffer(outputIndex, doRender)
                    }
                }
                mStopFlag = true
                endHandler.sendEmptyMessage(0)
                Log.d(TAG, "over")
            }
        }).start()
    }

    private val endHandler = Handler {
        mCommonCallback.success()
        return@Handler false
    }

    /**
     * 获得可用的字节数组
     * @param isf
     * @return
     * @throws IOException
     */
    private fun getBytes(isf: InputStream): ByteArray {

        var size = 1024
        var buf: ByteArray
        if (isf is ByteArrayInputStream) {
            //返回可用的剩余字节
            size = isf.available()
            //创建一个对应可用相应字节的字节数组
            buf = ByteArray(size)
            //读取这个文件并保存读取的长度
        } else {
            buf = ByteArray(size)
            try {
                val bos = ByteArrayOutputStream()
                isf.use { it.copyTo(bos) }
                //将这个流转换成字节数组
                buf = bos.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return buf
    }


    /**
     *  查找帧头部的位置
     * @param pattern    文件头字节数组
     * @param bytes      可用的字节数组
     * @param start       开始读取的下标
     * @param remain       可用的字节数量
     * @return
     */
    private fun mKMPMatch(pattern: ByteArray, bytes: ByteArray, start: Int, remain: Int): Int {
        try {
            Thread.sleep(30)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val lsp = computeLspTable(pattern)

        var j = 0  // Number of chars matched in pattern
        for (i in start until remain) {

            while (j > 0 && bytes[i] != pattern[j]) {
                // Fall back in the pattern
                j = lsp[j - 1]  // Strictly decreasing
            }
            if (bytes[i] == pattern[j]) {
                // Next char matched, increment position
                j++
                if (j == pattern.size)
                    return i - (j - 1)
            }
        }

        return -1  // Not found
    }

    // 0 1 2 0
    private fun computeLspTable(pattern: ByteArray): IntArray {
        val lsp = IntArray(pattern.size)
        lsp[0] = 0  // Base case
        for (i in 1 until pattern.size - 1) {
            // Start by assuming we're extending the previous LSP
            var j = lsp[i - 1]
            while (j > 0 && pattern[i] != pattern[j])
                j = lsp[j - 1]
            if (pattern[i] == pattern[j])
                j++
            lsp[i] = j
        }
        return lsp
    }

}