package com.dozen.world.ffmpeg

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Hugo on 20-4-28.
 * Describe:
 *
 *
 *
 */
class TriangleRenderer : GLSurfaceView.Renderer {

    private var mProgramId: Int = 0
    private var mColorId: Int = 0
    private var mPositionId: Int = 0
    private lateinit var mVertexBuffer: FloatBuffer

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(mProgramId)
        GLES20.glEnableVertexAttribArray(mPositionId)
        GLES20.glVertexAttribPointer(
            mPositionId,
            CoordsPerVertex,
            GLES20.GL_FLOAT,
            false,
            VertexStrid,
            mVertexBuffer
        )

        GLES20.glUniform4fv(mColorId, 1, TriangleCoords.toFloatArray(), 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VertexCount)

        GLES20.glDisableVertexAttribArray(mPositionId)
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        GLES20.glViewport(0, 0, p1, p2)

        val bb = ByteBuffer.allocateDirect(TriangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        mVertexBuffer = bb.asFloatBuffer()
        mVertexBuffer.put(TriangleCoords.toFloatArray())
        mVertexBuffer.position(0)

    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Log.d(TAG, "onSurfaceCreated")
        mProgramId = loadProgram(VertexShader, FragmentShader)
        mPositionId = GLES20.glGetAttribLocation(mProgramId, "vPosition")
        mColorId = GLES20.glGetUniformLocation(mProgramId, "vColor")
    }

    fun destroy() {
        GLES20.glDeleteProgram(mProgramId)
    }

    companion object {
        private val TAG = "TriangleRenderer"

        val TriangleCoords: List<Float> =
            arrayListOf(0.0f, 1.0f, 0.0f, -1.0f, -0.0f, 0.0f, 1.0f, -0.0f, 0.0f)
        val VertexShader: String = "attribute vec4 vPosition; void main(){ gl_Position=vPosition; }"
        val FragmentShader: String =
            "precision mediump float; uniform vec4 vColor; void main(){ gl_FragColor = vColor; }"
        val color: List<Float> = arrayListOf(1.0f, 0.0f, 0f, 1.0f)

        private val CoordsPerVertex = 3
        private val VertexStrid = CoordsPerVertex * 4
        private val VertexCount = TriangleCoords.size / CoordsPerVertex

        val instance: TriangleRenderer by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TriangleRenderer()
        }

    }

    private fun loadShader(strSource: String, iType: Int): Int {
        val compiled: IntArray = IntArray(1)
        val iShader = GLES20.glCreateShader(iType)
        GLES20.glShaderSource(iShader, strSource)
        GLES20.glCompileShader(iShader)

        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0)

        return if (compiled[0] == 0) 0 else iShader
    }

    private fun loadProgram(strVSource: String, strFSource: String): Int {
        val iVShader: Int = loadShader(strVSource, GLES20.GL_VERTEX_SHADER)
        val iFShader: Int = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER)
        val iProId: Int = GLES20.glCreateProgram()
        val link: IntArray = IntArray(1)


        if (iVShader == 0) return 0

        if (iFShader == 0) return 0

        GLES20.glAttachShader(iProId, iVShader)
        GLES20.glAttachShader(iProId, iFShader)
        GLES20.glLinkProgram(iProId)
        GLES20.glGetProgramiv(iProId, GLES20.GL_LINK_STATUS, link, 0)

        if (link[0] == 0) return 0

        GLES20.glDeleteShader(iVShader)
        GLES20.glDeleteShader(iFShader)
        return iProId
    }


}