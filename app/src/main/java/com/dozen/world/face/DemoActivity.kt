package com.dozen.world.face

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dozen.world.Constant
import com.dozen.world.R
import com.dozen.world.custom.TopTabClickListener
import com.dozen.world.ffmpeg.*
import com.dozen.world.ffmpeg.h264.Camera2Preview
import com.dozen.world.ffmpeg.h264.PlayMedia
import com.dozen.world.ffmpeg.mp3toaac.AudioCodecTest
import com.dozen.world.impl.CommonCallback
import kotlinx.android.synthetic.main.activity_demo.*
import java.io.File

/**
 * Created by Hugo on 20-4-23.
 * Describe:
 *
 *
 *
 */
class DemoActivity : AppCompatActivity() {

    private val TAG = this.toString()

    private lateinit var audioTest: AudioTest
    private lateinit var mCameraShowTest: CameraShowTest
    private lateinit var mediaTest: MediaTest
    private var camera2Preview: Camera2Preview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        audio_record_track.initSwitchData(
            arrayListOf(
                "AudioRecord开始录制",
                "AudioRecord停止录制",
                "AudioTrack开始播放",
                "AudioTrack停止播放",
                "删除PCM",
                "PCM转WAV"
            )
        )
        audio_record_track.ttcl = artListener

        camera_show_test.initSwitchData(
            arrayListOf(
                "SurfaceView",
                "TextureView",
                "NV21",
                "ViewGONE"
            )
        )
        camera_show_test.ttcl = cameraListener
        camera_surface_view.visibility = View.GONE
        camera_texture_view.visibility = View.GONE

        media_control_test.initSwitchData(
            arrayListOf(
                "Open FileManager",
                "分离MP4音频和视频",
                "分离纯视频",
                "分离纯音频",
                "合并音频和视频为MP4"
            )
        )
        media_control_test.ttcl = mediaListener

        open_gl_es_test.initSwitchData(arrayListOf(
            "三角形测试",
            "加载图片",
            "四边形",
            "close"
        ))
        open_gl_es_test.ttcl=openGLListener

        open_gl_media_codec.initSwitchData(
            arrayListOf(
                "MP3转AAC",
                "录制h264",
                "录制MP4",
                "停止录制视频",
                "播放视频"
            )
        )
        open_gl_media_codec.ttcl = mediaCodecListener

        audioTest = AudioTest.instance
        mCameraShowTest=CameraShowTest.instance
        mediaTest = MediaTest.instance

        mediaTest.setCommonCallback(object :CommonCallback{
            override fun success(id: Int?) {
                Toast.makeText(baseContext,"success",Toast.LENGTH_LONG).show()
            }

            override fun fail(id: Int?) {
            }

            override fun result(data: String) {
            }

        })

    }

    private var artListener = object : TopTabClickListener {
        override fun clickListener(i: Int) {
            when (i) {
                0 -> {
                    Log.d(TAG, "开始录制")
                    audioTest.startRecord()
                }
                1 -> {
                    Log.d(TAG, "停止录制")
                    audioTest.stopRecord()
                }
                2 -> {
                    Log.d(TAG, "开始播放")
                    audioTest.startTrack()
                }
                3 -> {
                    Log.d(TAG, "停止播放")
                    audioTest.stopTrack()
                }
                4 -> {
                    Log.d(TAG, "删除PCM")
                    audioTest.deletePCMFile()
                }
                5 -> {
                    audioTest.PCMToWAV()
                }
            }
        }
    }

    private var cameraListener = object : TopTabClickListener {
        override fun clickListener(i: Int) {
            when (i) {
                0 -> {
                    mCameraShowTest.openSurface(camera_surface_view)
                    Log.d(TAG, "camera")
                }
                1 -> {
                    mCameraShowTest.openTexture(camera_texture_view)

                }
                2 -> {
                    mCameraShowTest.showNV21Data(camera_bitmap_show)
                }
                3 -> {
                    mCameraShowTest.closeCamera()
                }
            }

        }
    }

    private var mediaListener = object : TopTabClickListener {
        override fun clickListener(i: Int) {
            when (i) {
                0 -> {
                    //打开文件夹
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.type = "*/*"
                    startActivity(intent)
                }
                1 -> {
                    mediaTest.splitMP4()
                }
                2 -> {
                    mediaTest.splitMP4ToVideo(
                        Constant.KotlinFilePath + "/video.mp4",
                        Constant.KotlinFilePath + "/output_muxer_video.mp4"
                    )
                }
                3 -> {
                    mediaTest.splitMP4ToAudio(
                        Constant.KotlinFilePath + "/video.mp4",
                        Constant.KotlinFilePath + "/output_muxer_audio.mp4"
                    )
                }
                4->{
                    mediaTest.combineToMP4(
                        Constant.KotlinFilePath + "/output_muxer_video.mp4",
                        Constant.KotlinFilePath + "/output_muxer_audio.mp4",
                        Constant.KotlinFilePath + "/output_combine.mp4")
                }
            }
        }
    }

    private var openGLListener=object :TopTabClickListener{
        override fun clickListener(i: Int) {
            when(i){
                0->{
                    open_gl_show_triangle.visibility = View.VISIBLE
                }
                1 -> {
                    open_gl_show_picture.visibility = View.VISIBLE
                }
                2 -> {
                    open_gl_show_square.visibility = View.VISIBLE
                }
                3 -> {
                    open_gl_show_triangle.visibility = View.GONE
                    open_gl_show_picture.visibility = View.GONE
                    open_gl_show_square.visibility = View.GONE
                }

            }
        }

    }

    private var mediaCodecListener = object : TopTabClickListener {
        override fun clickListener(i: Int) {
            when (i) {
                0 -> {

                    AudioCodecTest.getPCMFromAudio(
                        Constant.KotlinFilePath + "/audio_test_wav.wav",
                        Constant.KotlinFilePath + "/codec_pcm.pcm",
                        object : CommonCallback {
                            override fun success(id: Int?) {
                                AudioCodecTest.pcmToAudio(
                                    Constant.KotlinFilePath + "/codec_pcm.pcm",
                                    Constant.KotlinFilePath + "/audio_aac.m4a",
                                    object : CommonCallback {
                                        override fun fail(id: Int?) {
                                            Toast.makeText(baseContext, "fail", Toast.LENGTH_LONG)
                                                .show()
                                        }

                                        override fun result(data: String) {}

                                        override fun success(id: Int?) {
                                            Toast.makeText(
                                                baseContext,
                                                "success",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    })
                            }

                            override fun fail(id: Int?) {
                                Toast.makeText(baseContext, "fail fail", Toast.LENGTH_LONG).show()

                            }

                            override fun result(data: String) {}
                        })


                }
                1->{
                    open_gl_h264_show.visibility=View.VISIBLE
                    if (camera2Preview == null) {
                        camera2Preview = Camera2Preview(this@DemoActivity)
                        open_gl_h264_show.addView(camera2Preview)
                    }
                    camera2Preview?.startVideo(Constant.KotlinFilePath + "/" + "MP4_h264_Video.h264")
                }
                2->{
                    open_gl_h264_show.visibility = View.VISIBLE
                    if (camera2Preview == null) {
                        camera2Preview = Camera2Preview(this@DemoActivity)
                        open_gl_h264_show.addView(camera2Preview)
                    }
                    //MP4格式
                    camera2Preview?.startVideo(Constant.KotlinFilePath + "/" + "MP4_h264_Video.mp4")
                }
                3 -> {
                    if (camera2Preview != null) {
                        camera2Preview?.stopVideo()
                        open_gl_h264_show.visibility = View.GONE

                    }
                }
                4 -> {
                    val filePath =
                        when {
                            File(Constant.KotlinFilePath + "/MP4_h264_Video.h264").exists() -> Constant.KotlinFilePath + "/MP4_h264_Video.h264"
                            File(Constant.KotlinFilePath + "/MP4_h264_Video.mp4").exists() -> Constant.KotlinFilePath + "/MP4_h264_Video.mp4"
                            else -> null
                        }
                    if (filePath != null) {
                        open_gl_h264_play.visibility = View.VISIBLE
                        val playMedia = PlayMedia()

                        playMedia.initCallback(object : CommonCallback {
                            override fun success(id: Int?) {
                                open_gl_h264_play.visibility = View.GONE
                                Toast.makeText(baseContext, "播放完成", Toast.LENGTH_SHORT).show()
                            }

                            override fun fail(id: Int?) {}

                            override fun result(data: String) {}
                        })

                        playMedia.setFileInputStream(filePath, open_gl_h264_play)
                        playMedia.initMediaCodec()
                    } else {
                        Toast.makeText(baseContext, "文件不存在", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

    }

}