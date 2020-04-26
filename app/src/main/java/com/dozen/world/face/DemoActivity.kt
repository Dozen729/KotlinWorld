package com.dozen.world.face

import android.media.*
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dozen.world.R
import com.dozen.world.custom.TopTabClickListener
import com.dozen.world.ffmpeg.AudioTest
import kotlinx.android.synthetic.main.activity_demo.*
import java.io.*

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

        audioTest = AudioTest.instance

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

}