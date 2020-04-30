package com.dozen.world.ffmpeg.gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.dozen.world.utils.OpenGLESUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Hugo on 20-4-30.
 * Describe:
 *
 *
 *
 */
class SquareRenderer : GLSurfaceView.Renderer {

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer
    private val vertexShaderCode =
        "attribute vec4 vPosition" +
                "uniform mat4 vMatrix" +
                "void main() {" +
                "  gl_Position = vMatrix*vPosition" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float" +
                "uniform vec4 vColor" +
                "void main() {" +
                "  gl_FragColor = vColor" +
                "}"

    private var mProgram: Int = 0

    private val triangleCoords: List<Float> = arrayListOf(
        -0.5f, 0.5f, 0.0f, // top left
        -0.5f, -0.5f, 0.0f, // bottom left
        0.5f, -0.5f, 0.0f, // bottom right
        0.5f, 0.5f, 0.0f  // top right
    )

    private val index: List<Short> = arrayListOf(0, 1, 2, 0, 3, 2)

    private var mPositionHandle: Int = 0
    private var mColorHandle: Int = 0

    private var mViewMatrix: FloatArray = FloatArray(16)
    private var mProjectMatrix: FloatArray = FloatArray(16)
    private var mMVPMatrix: FloatArray = FloatArray(16)

    private val coordsPerVertex = 3

    //顶点之间的偏移量
    private val vertexStride: Int = coordsPerVertex * 4 // 每个顶点四个字节

    private var mMatrixHandler: Int = 0

    //设置颜色，依次为红绿蓝和透明通道
    private val color: List<Float> = arrayListOf(1.0f, 0.0f, 0.0f, 1.0f)


    override fun onDrawFrame(p0: GL10?) {

        GLES20.glClearColor(1.0f,0.5f,0.5f,1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram)
        //获取变换矩阵vMatrix成员句柄
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0)
        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(
            mPositionHandle, coordsPerVertex,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer
        )
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color.toFloatArray(), 0)
        //索引法绘制正方形
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            index.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        //计算宽高比
        val ratio = p1 / p2.toFloat()
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)

    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        val bb = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(triangleCoords.toFloatArray())
        vertexBuffer.position(0)

        val cc = ByteBuffer.allocateDirect(index.size * 2)
        cc.order(ByteOrder.nativeOrder())
        indexBuffer = cc.asShortBuffer()
        indexBuffer.put(index.toShortArray())
        indexBuffer.position(0)

        mProgram = OpenGLESUtil.loadProgram(vertexShaderCode, fragmentShaderCode)

    }
}