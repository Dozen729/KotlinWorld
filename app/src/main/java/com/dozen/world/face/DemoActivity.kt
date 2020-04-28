package com.dozen.world.face

import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dozen.world.Constant
import com.dozen.world.R
import com.dozen.world.custom.TopTabClickListener
import com.dozen.world.ffmpeg.AudioTest
import com.dozen.world.ffmpeg.CameraShowTest
import com.dozen.world.ffmpeg.MediaTest
import com.dozen.world.ffmpeg.TriangleRenderer
import com.dozen.world.impl.CommonCallback
import kotlinx.android.synthetic.main.activity_demo.*

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
            "三角形测试"
        ))
        open_gl_es_test.ttcl=openGLListener

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
                    open_gl_show.visibility=View.VISIBLE
                    open_gl_show.setEGLContextClientVersion(2)
                    open_gl_show.setRenderer(TriangleRenderer.instance)
                    open_gl_show.renderMode=GLSurfaceView.RENDERMODE_WHEN_DIRTY
                }
            }
        }

    }


}