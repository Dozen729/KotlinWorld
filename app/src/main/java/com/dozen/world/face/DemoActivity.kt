package com.dozen.world.face

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dozen.world.R
import com.dozen.world.custom.TopTabClickListener
import com.dozen.world.ffmpeg.AudioTest
import com.dozen.world.ffmpeg.CameraShowTest
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


        audioTest = AudioTest.instance
        mCameraShowTest=CameraShowTest.instance


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


}