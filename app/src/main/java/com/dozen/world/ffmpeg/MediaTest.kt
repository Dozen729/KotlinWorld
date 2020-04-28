package com.dozen.world.ffmpeg

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Handler
import android.util.Log
import com.dozen.world.Constant
import com.dozen.world.impl.CommonCallback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * Created by Hugo on 20-4-27.
 * Describe:
 *
 *需要放置一个视频：Constant.KotlinFilePath + "/video.mp4"
 *
 */
class MediaTest {

    private lateinit var mediaExtractor: MediaExtractor
    private lateinit var mediaMuxer: MediaMuxer
    private var mCommonCallback: CommonCallback? = null

    companion object {

        private const val TAG = "MediaTest"

        val instance: MediaTest by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MediaTest()
        }
    }

    fun splitMP4() {
        Thread(Runnable { extractorMedia() }).start()
    }

    fun splitMP4ToVideo(mp4Path: String,outputPath: String) {
        Thread(Runnable {
            muxerVideo(mp4Path,outputPath)
        }).start()
    }

    fun splitMP4ToAudio(mp4Path:String,outputPath:String) {
        Thread(Runnable {
            muxerAudio(mp4Path,outputPath)
        }).start()
    }

    fun combineToMP4(videoPath:String,audioPath:String,outputPath:String) {
        Thread(Runnable {
            combineVideo(videoPath,audioPath,outputPath)
        }).start()
    }

    fun setCommonCallback(ccb: CommonCallback) {
        mCommonCallback = ccb
    }

    //测试专用,请勿模仿
    private fun extractorMedia() {

        mediaExtractor = MediaExtractor()

        lateinit var videoOutputStream: FileOutputStream
        lateinit var audioOutputStream: FileOutputStream

        try {
            //分离的视频文件
            val videoFile = File(Constant.KotlinFilePath, "output_video.mp4")
            //分离的音频文件
            val audioFile = File(Constant.KotlinFilePath, "output_audio")

            videoOutputStream = FileOutputStream(videoFile)
            audioOutputStream = FileOutputStream(audioFile)

            //源文件
            mediaExtractor.setDataSource(Constant.KotlinFilePath + "/video.mp4")

            //信道总数
            val trackCount: Int = mediaExtractor.trackCount
            var audioTrackIndex = -1
            var videoTrackIndex = -1
            for (i in 0 until trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(i)
                val mineType = trackFormat.getString(MediaFormat.KEY_MIME)!!

                //视频信道
                if (mineType.startsWith("video/")) {
                    videoTrackIndex = i
                }
                //音频信道
                if (mineType.startsWith("audio/")) {
                    audioTrackIndex = i
                }
            }

            val byteBuffer = ByteBuffer.allocate(500 * 1024)

            //切换到视频信道
            mediaExtractor.selectTrack(videoTrackIndex)
            while (true) {
                val readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleCount < 0) break

                //保存视频信道信息
                val byteArray = ByteArray(readSampleCount)
                byteBuffer.get(byteArray)
                videoOutputStream.write(byteArray)
                byteBuffer.clear()
                mediaExtractor.advance()
            }

            //切换到音频信道
            mediaExtractor.selectTrack(audioTrackIndex)
            while (true) {
                val readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleCount < 0) break

                //保存音频信息
                val byteArray = ByteArray(readSampleCount)
                byteBuffer.get(byteArray)
                audioOutputStream.write(byteArray)
                byteBuffer.clear()
                mediaExtractor.advance()
            }

            mediaExtractor.release()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //分离MP4文件中的纯视频文件
    private fun muxerVideo(inputMP4FilePath: String, outputVideoFilePath: String) {
        mediaExtractor = MediaExtractor()

        var videoIndex = -1
        try {
            mediaExtractor.setDataSource(inputMP4FilePath)
            val trackCount = mediaExtractor.trackCount
            for (i in 0 until trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(i)
                val miniType = trackFormat.getString(MediaFormat.KEY_MIME)!!

                //取出视频信号
                if (miniType.startsWith("video/")) videoIndex = i
            }

            //切换视频信号的信道
            mediaExtractor.selectTrack(videoIndex)
            val trackFormat = mediaExtractor.getTrackFormat(videoIndex)
            mediaMuxer =
                MediaMuxer(outputVideoFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            //追踪此信道
            val trackIndex = mediaMuxer.addTrack(trackFormat)
            mediaMuxer.start()
            val byteBuffer = ByteBuffer.allocate(1024 * 500)
            val bufferInfo = MediaCodec.BufferInfo()
            val videoSampleTime: Long

            //获取每帧之间的时间
            mediaExtractor.readSampleData(byteBuffer, 0)
            //skip first I frame
            if (mediaExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC) mediaExtractor.advance()
            mediaExtractor.readSampleData(byteBuffer, 0)
            val firstVideoPTS = mediaExtractor.sampleTime
            mediaExtractor.advance()
            mediaExtractor.readSampleData(byteBuffer, 0)
            val secondVideoPTS = mediaExtractor.sampleTime
            videoSampleTime = abs(secondVideoPTS - firstVideoPTS)


            //重新切换信道,不然上面跳过了3帧造成前面的帧数模糊
            mediaExtractor.unselectTrack(videoIndex)
            mediaExtractor.selectTrack(videoIndex)

            while (true) {
                val readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleSize < 0) break

                mediaExtractor.advance()
                bufferInfo.size = readSampleSize
                bufferInfo.offset = 0
                bufferInfo.flags = mediaExtractor.sampleFlags
                bufferInfo.presentationTimeUs += videoSampleTime
                //写入帧的数据
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo)
            }

            //release
            mediaMuxer.stop()
            mediaExtractor.release()
            mediaMuxer.release()

        } catch (e: IOException) {
            e.printStackTrace()
            stateHandle.sendEmptyMessage(-1)
        }
        stateHandle.sendEmptyMessage(1)
    }

    //将MP4文件中的音频提取成纯音频文件并输出
    private fun muxerAudio(inputMP4FilePath: String, outputAudioFilePath: String) {

        mediaExtractor = MediaExtractor()

        var audioIndex = -1

        try {
            mediaExtractor.setDataSource(inputMP4FilePath)
            val trackCount = mediaExtractor.trackCount
            for (i in 0 until trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(i)
                val trackType = trackFormat.getString(MediaFormat.KEY_MIME)!!
                if (trackType.startsWith("audio/")) audioIndex = i
            }

            mediaExtractor.selectTrack(audioIndex)
            val trackFormat = mediaExtractor.getTrackFormat(audioIndex)
            mediaMuxer =
                MediaMuxer(outputAudioFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val writeAudioIndex = mediaMuxer.addTrack(trackFormat)
            mediaMuxer.start()
            val byteBuffer = ByteBuffer.allocate(500 * 1024)
            val bufferInfo = MediaCodec.BufferInfo()

            val stampTime: Long

            //获取帧之间的间隔时间
            mediaExtractor.readSampleData(byteBuffer, 0)
            if (mediaExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC) mediaExtractor.advance()
            mediaExtractor.readSampleData(byteBuffer, 0)
            val secondTime = mediaExtractor.sampleTime
            mediaExtractor.advance()
            mediaExtractor.readSampleData(byteBuffer, 0)
            val thirdTime = mediaExtractor.sampleTime
            stampTime = abs(thirdTime - secondTime)


            mediaExtractor.unselectTrack(audioIndex)
            mediaExtractor.selectTrack(audioIndex)

            while (true) {
                val readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleSize < 0) break
                mediaExtractor.advance()

                bufferInfo.size = readSampleSize
                bufferInfo.flags = mediaExtractor.sampleFlags
                bufferInfo.offset = 0
                bufferInfo.presentationTimeUs += stampTime

                mediaMuxer.writeSampleData(writeAudioIndex, byteBuffer, bufferInfo)
            }

            mediaMuxer.stop()
            mediaExtractor.release()
            mediaMuxer.release()

        } catch (e: IOException) {
            e.printStackTrace()
            stateHandle.sendEmptyMessage(-1)
        }
        stateHandle.sendEmptyMessage(1)

    }

    //将视频文件和视频文件合并成一个新的MP4文件
    private fun combineVideo(
        inputVideoFilePath: String,
        inputAudioFilePath: String,
        outputFilePath: String
    ) {
        try {
            mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(inputVideoFilePath)
            lateinit var videoFormat: MediaFormat
            var videoTrackIndex = -1
            val videoTrackCount = mediaExtractor.trackCount
            for (i in 0 until videoTrackCount) {
                videoFormat = mediaExtractor.getTrackFormat(i)
                val mimeType = videoFormat.getString(MediaFormat.KEY_MIME)!!
                if (mimeType.startsWith("video/")) {
                    videoTrackIndex = i
                    break
                }
            }

            val audioExtractor = MediaExtractor()
            audioExtractor.setDataSource(inputAudioFilePath)
            lateinit var audioFormat: MediaFormat
            var audioTrackIndex = -1
            val audioTrackCount = audioExtractor.trackCount
            for (i in 0 until audioTrackCount) {
                audioFormat = audioExtractor.getTrackFormat(i)
                val mimeType = audioFormat.getString(MediaFormat.KEY_MIME)!!
                if (mimeType.startsWith("audio/")) {
                    audioTrackIndex = i
                    break
                }
            }

            mediaExtractor.selectTrack(videoTrackIndex)
            audioExtractor.selectTrack(audioTrackIndex)

            val videoBufferInfo = MediaCodec.BufferInfo()
            val audioBufferInfo = MediaCodec.BufferInfo()

            val mediaMuxer = MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val writeVideoTrackIndex = mediaMuxer.addTrack(videoFormat)
            val writeAudioTrackIndex = mediaMuxer.addTrack(audioFormat)
            mediaMuxer.start()

            val byteBuffer = ByteBuffer.allocate(1024 * 500)
            val sampleTime: Long
            mediaExtractor.readSampleData(byteBuffer, 0)
            if (mediaExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC) {
                mediaExtractor.advance()
            }
            mediaExtractor.readSampleData(byteBuffer, 0)
            val secondTime = mediaExtractor.sampleTime
            mediaExtractor.advance()
            val thirdTime = mediaExtractor.sampleTime
            sampleTime = abs(thirdTime - secondTime)

            mediaExtractor.unselectTrack(videoTrackIndex)
            mediaExtractor.selectTrack(videoTrackIndex)

            while (true) {
                val readVideoSampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readVideoSampleSize < 0) {
                    break
                }
                videoBufferInfo.size = readVideoSampleSize
                videoBufferInfo.presentationTimeUs += sampleTime
                videoBufferInfo.offset = 0
                videoBufferInfo.flags = mediaExtractor.sampleFlags
                mediaMuxer.writeSampleData(writeVideoTrackIndex, byteBuffer, videoBufferInfo)
                mediaExtractor.advance()
            }

            val byteBufferAudio = ByteBuffer.allocate(500 * 1024)
            val sampleTimeAudio: Long
            audioExtractor.readSampleData(byteBufferAudio, 0)
            if (audioExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC) audioExtractor.advance()
            audioExtractor.readSampleData(byteBufferAudio, 0)
            val sixTime = audioExtractor.sampleTime
            audioExtractor.advance()
            val sevenTime = audioExtractor.sampleTime
            sampleTimeAudio = abs(sevenTime - sixTime)

            audioExtractor.unselectTrack(audioTrackIndex)
            audioExtractor.selectTrack(audioTrackIndex)

            while (true) {
                val readAudioSampleSize = audioExtractor.readSampleData(byteBufferAudio, 0)
                if (readAudioSampleSize < 0) {
                    break
                }

                audioBufferInfo.size = readAudioSampleSize
                audioBufferInfo.presentationTimeUs += sampleTimeAudio
                audioBufferInfo.offset = 0
                audioBufferInfo.flags = audioExtractor.sampleFlags
                mediaMuxer.writeSampleData(writeAudioTrackIndex, byteBufferAudio, audioBufferInfo)
                audioExtractor.advance()
            }

            mediaMuxer.stop()
            mediaMuxer.release()
            mediaExtractor.release()
            audioExtractor.release()
        } catch (e: IOException) {
            e.printStackTrace()
            stateHandle.sendEmptyMessage(-1)
        }
        stateHandle.sendEmptyMessage(1)
    }

    private val stateHandle = Handler {

        if (mCommonCallback != null) {
            when (it.what) {
                -1 -> mCommonCallback?.fail()
                1 -> mCommonCallback?.success()
            }
        }

        return@Handler false
    }

}