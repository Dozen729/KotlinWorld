package com.dozen.world.ffmpeg.mp3toaac

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import com.dozen.world.impl.CommonCallback
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Created by Hugo on 20-5-6.
 * Describe:
 *
 *
 *
 */
class AudioDecodeRunnable : Runnable {
    private lateinit var extractor: MediaExtractor
    private var audioTrack = 0
    private lateinit var mListener: CommonCallback
    private lateinit var mPcmFilePath: String

    companion object {
        private const val TAG = "AudioDecodeRunnable"
        private const val TIMEOUT_USEC: Long = 0
    }

    fun initAudioDecode(
        extractor: MediaExtractor,
        trackIndex: Int,
        savePath: String,
        listener: CommonCallback
    ) {
        this.extractor = extractor
        audioTrack = trackIndex
        mListener = listener
        mPcmFilePath = savePath
    }


    override fun run() {
        try {
            val format = extractor.getTrackFormat(audioTrack)
            //初始化音频解码器
            val audioCodec =
                MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME)!!)
            audioCodec.configure(format, null, null, 0)

            audioCodec.start()//启动MediaCodec，等待传入数据

            val inputBuffers =
                audioCodec.inputBuffers//MediaCodec在此ByteBuffer[]中获取输入数据
            var outputBuffers =
                audioCodec.outputBuffers//MediaCodec将解码后的数据放到此ByteBuffer[]中 我们可以直接在这里面得到PCM数据
            val decodeBufferInfo = MediaCodec.BufferInfo()//用于描述解码得到的byte[]数据的相关信息
            val inputInfo = MediaCodec.BufferInfo()
            var codeOver = false
            val inputDone = false//整体输入结束标记

            val fos = FileOutputStream(mPcmFilePath)

            while (!codeOver) {
                if (!inputDone) {
                    for (i in inputBuffers.indices) {
                        //将数据传入之后，再去输出端取出数据
                        val inputIndex = audioCodec.dequeueInputBuffer(TIMEOUT_USEC)
                        if (inputIndex >= 0) {
                            //从分离器拿出输入，写入解码器
                            val inputBuffer: ByteBuffer =
                                inputBuffers[inputIndex]//拿到inputBuffer，新的API中好像可以直接拿到
                            val sampleSize = extractor.readSampleData(
                                inputBuffer,
                                0
                            )//将MediaExtractor读取数据到inputBuffer
                            if (sampleSize < 0) {//表示所有数据已经读取完毕
                                audioCodec.queueInputBuffer(
                                    inputIndex,
                                    0,
                                    0,
                                    0L,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                )
                            } else {
                                inputInfo.offset = 0
                                inputInfo.size = sampleSize
                                inputInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME
                                inputInfo.presentationTimeUs = extractor.sampleTime

                                Log.e(TAG, "往解码器写入数据，当前时间戳：" + inputInfo.presentationTimeUs)
                                //通知MediaCodec解码刚刚传入的数据
                                audioCodec.queueInputBuffer(
                                    inputIndex,
                                    inputInfo.offset,
                                    sampleSize,
                                    inputInfo.presentationTimeUs,
                                    0
                                )
                                extractor.advance()
                            }
                        }
                    }
                }

                var decodeOutputDone = false
                var chunkPCM: ByteArray
                while (!decodeOutputDone) {
                    val outputIndex = audioCodec.dequeueOutputBuffer(decodeBufferInfo, TIMEOUT_USEC)
                    if (outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        //没有可用的解码器
                        decodeOutputDone = true
                    } else if (outputIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        outputBuffers = audioCodec.outputBuffers
                    } else if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        val newFormat = audioCodec.outputFormat
                    } else if (outputIndex < 0) {

                    } else {
                        val outputBuffer: ByteBuffer
                        if (Build.VERSION.SDK_INT >= 21) {
                            outputBuffer = audioCodec.getOutputBuffer(outputIndex)!!
                        } else {
                            outputBuffer = outputBuffers[outputIndex]
                        }

                        chunkPCM = ByteArray(decodeBufferInfo.size)
                        outputBuffer.get(chunkPCM)
                        outputBuffer.clear()

                        fos.write(chunkPCM)//数据写入文件中
                        fos.flush()
                        Log.e(TAG, "释放输出流缓冲区：$outputIndex")
                        audioCodec.releaseOutputBuffer(outputIndex, false)

                        if ((decodeBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {  //编解码结束
                            extractor.release()
                            audioCodec.stop()
                            audioCodec.release()
                            codeOver = true
                            decodeOutputDone = true
                        }
                    }
                }
            }

            fos.close()
            mListener.success()
        } catch (e: IOException) {
            e.printStackTrace()
            mListener.fail()
        }
    }
}