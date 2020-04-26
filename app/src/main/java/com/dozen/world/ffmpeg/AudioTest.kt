package com.dozen.world.ffmpeg

import android.media.*
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by Hugo on 20-4-26.
 * Describe:
 *
 *
 *
 */
class AudioTest {

    companion object {
        private const val TAG: String = "AudioTest"
        private const val AudioSource = MediaRecorder.AudioSource.MIC
        private const val SampleRate = 44100
        private const val channel_in = AudioFormat.CHANNEL_IN_MONO
        private const val channel_out=AudioFormat.CHANNEL_OUT_MONO
        private const val EncodingType = AudioFormat.ENCODING_PCM_16BIT
        
        private val PCMPath =
            Environment.getExternalStorageDirectory().path.toString() + "/hugo/audio_test.pcm"
        private val WAVPath =
            Environment.getExternalStorageDirectory().path.toString() + "/hugo/audio_test_wav.wav"

        val instance: AudioTest by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AudioTest()
        }

    }

    private var bufferSizeInByte = 0
    private var audioRecorder: AudioRecord? = null
    private var isRecord = false

    private var audioTrack: AudioTrack? = null
    private var isTrack = false

    private fun initRecorder() {
        bufferSizeInByte = AudioRecord.getMinBufferSize(SampleRate, channel_in, EncodingType)
        audioRecorder =
            AudioRecord(AudioSource, SampleRate, channel_in, EncodingType, bufferSizeInByte)
    }

    private fun initTrack() {
        bufferSizeInByte = AudioTrack.getMinBufferSize(SampleRate, channel_out, EncodingType)
        audioTrack = AudioTrack(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(
                AudioAttributes.CONTENT_TYPE_MUSIC
            ).build(), AudioFormat.Builder().setSampleRate(
                SampleRate
            ).setChannelMask(channel_out).setEncoding(EncodingType).build(), bufferSizeInByte,
            AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE
        )
    }

    fun startRecord(): Int {
        return if (isRecord) {
            -1
        } else {
            audioRecorder ?: initRecorder()
            audioRecorder?.startRecording()
            isRecord = true

            AudioTestForFile(1).start()

            0
        }
    }

    fun startTrack(): Int {
        return if (isTrack) {
            -1
        } else {
            audioTrack ?: initTrack()
            audioTrack?.play()
            isTrack = true

            AudioTestForFile(2).start()

            0
        }
    }

    fun stopRecord() {
        audioRecorder?.stop()
        audioRecorder?.release()
        isRecord = false
        audioRecorder = null
    }

    fun stopTrack() {
        audioTrack?.stop()
        audioTrack?.release()
        isTrack = false
        audioTrack = null
    }

    //删除pcm文件
    fun deletePCMFile(){
        val file = File(PCMPath)
        if (file.exists()) {
            file.delete()
        }
    }

    //生成wav文件
    fun PCMToWAV(){
        copyWaveFile(PCMPath, WAVPath)
    }

    private inner class AudioTestForFile(var s: Int) : Thread() {
        override fun run() {
            super.run()
            if (s == 1) {
                writeDateToFile()
            } else if (s == 2) {
                playPCMFile()
            }
        }
    }

    //播放录音
    private fun playPCMFile() {

        val file = File(PCMPath)

        val fis = FileInputStream(file)

        val byte = ByteArray(bufferSizeInByte)
        var length = 0

        while (audioTrack != null && isTrack && fis.available() > 0) {
            length = fis.read(byte)

            if (length == AudioTrack.ERROR_INVALID_OPERATION || length == AudioTrack.ERROR_BAD_VALUE) {
                continue
            }
            if (length != 0 && length != -1) {
                audioTrack?.write(byte, 0, length)
            }

        }

        fis.close()

    }

    //录制声音
    private fun writeDateToFile() {

        val audioData = ByteArray(bufferSizeInByte)
        val file = File(PCMPath)
        if (!file.mkdirs()) {
            Log.d(TAG, "directory not created")
        }
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
//        Log.d(TAG,file.path.toString())
        val os = FileOutputStream(file)
        var length = 0
        while (isRecord && audioRecorder != null) {
            length = audioRecorder!!.read(audioData, 0, bufferSizeInByte)
            if (AudioRecord.ERROR_INVALID_OPERATION != length) {
                os.write(audioData)
                os.flush()
            }
        }
        os.close()
    }

    //将pcm格式的文件转换为WAV格式的
    private fun copyWaveFile(pcmPath: String, wavPath: String) {

        val file=File(PCMPath)
        if (!file.exists()){
            return
        }

        val fileIn = FileInputStream(pcmPath)
        val fileOut = FileOutputStream(wavPath)
        val data = ByteArray(bufferSizeInByte)
        val totalAudioLen = fileIn.channel.size()
        val totalDataLen = totalAudioLen + 36
        writeWaveFileHeader(fileOut, totalAudioLen, totalDataLen)
        var count = fileIn.read(data, 0, bufferSizeInByte)
        while (count != -1) {
            fileOut.write(data, 0, count)
            fileOut.flush()
            count = fileIn.read(data, 0, bufferSizeInByte)
        }
        fileIn.close()
        fileOut.close()
    }

    //添加WAV格式的文件头
    private fun writeWaveFileHeader(out:FileOutputStream , totalAudioLen:Long,
                                    totalDataLen:Long){

        val channels = 1
        val byteRate = 16 * SampleRate * channels / 8
        val header = ByteArray(44)
        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        header[12] = 'f'.toByte() // 'fmt ' chunk
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (SampleRate and 0xff).toByte()
        header[25] = (SampleRate shr 8 and 0xff).toByte()
        header[26] = (SampleRate shr 16 and 0xff).toByte()
        header[27] = (SampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (2 * 16 / 8).toByte() // block align
        header[33] = 0
        header[34] = 16 // bits per sample
        header[35] = 0
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        out.write(header, 0, 44)
    }

}