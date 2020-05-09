package com.dozen.world.ffmpeg.mp3toaac

import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.dozen.world.impl.CommonCallback

import java.io.IOException

/**
 * Created by Hugo on 20-5-6.
 * Describe:
 *
 *
 *
 */
class AudioCodecTest {

    companion object {
        private val TAG = "AudioCodec"
        private var handler = Handler(Looper.getMainLooper())

        /**
         * 将音频文件解码成原始的PCM数据
         * @param audioPath         音频文件目录
         * @param audioSavePath     pcm文件保存位置
         * @param listener
         */
        fun getPCMFromAudio(audioPath: String, audioSavePath: String, listener: CommonCallback) {
            val extractor = MediaExtractor()//此类可分离视频文件的音轨和视频轨道
            var audioTrack = -1//音频MP3文件其实只有一个音轨
            var hasAudio = false//判断音频文件是否有音频音轨

            try {
                extractor.setDataSource(audioPath)
                for (i in 0 until extractor.trackCount) {
                    val format = extractor.getTrackFormat(i)
                    val mime = format.getString(MediaFormat.KEY_MIME)!!
                    if (mime.startsWith("audio/")) {
                        audioTrack = i
                        hasAudio = true
                        break
                    }
                }

                if (hasAudio) {
                    extractor.selectTrack(audioTrack)

                    //原始音频解码

                    val adr = AudioDecodeRunnable()
                    adr.initAudioDecode(
                        extractor,
                        audioTrack,
                        audioSavePath,
                        object : CommonCallback {
                            override fun success(id: Int?) {
                                handler.post {
                                    listener.success()
                                }
                            }

                            override fun fail(id: Int?) {
                                handler.post {
                                    listener.fail()
                                }
                            }

                            override fun result(data: String) {

                            }
                        })

                    Thread(adr).start()

                } else {//如果音频文件没有音频音轨
                    Log.e(TAG, "音频文件没有音频音轨")
                    listener.fail()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "解码失败")
                listener.fail()
            }
        }


        /**
         * pcm文件转音频
         * @param pcmPath       pcm文件目录
         * @param audioPath     音频文件目录
         * @param listener
         */
        fun pcmToAudio(pcmPath: String, audioPath: String, listener: CommonCallback) {
            val aer = AudioEncodeRunnable()
            aer.initAudioEncode(pcmPath, audioPath, object : CommonCallback {
                override fun success(id: Int?) {
                    handler.post {
                        listener.success()
                    }
                }

                override fun fail(id: Int?) {
                    handler.post {
                        listener.fail()
                    }
                }

                override fun result(data: String) {
                }
            })
            Thread(aer).start()
        }

        /**
         * 写入ADTS头部数据
         * @param packet
         * @param packetLen
         */
        fun addADTStoPacket(packet: ByteArray, packetLen: Int) {
            val profile = 2 // AAC LC
            val freqIdx = 4 // 44.1KHz
            val chanCfg = 2 // CPE

            packet[0] = 0xFF.toByte()
            packet[1] = 0xF9.toByte()
            packet[2] = (((profile - 1) shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
            packet[3] = (((chanCfg and 3) shl 6) + (packetLen shr 11)).toByte()
            packet[4] = ((packetLen and 0x7FF) shr 3).toByte()
            packet[5] = (((packetLen and 7) shl 5) + 0x1F).toByte()
            packet[6] = 0xFC.toByte()
        }
    }
}