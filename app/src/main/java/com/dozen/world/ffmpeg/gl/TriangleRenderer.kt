package com.dozen.world.ffmpeg.gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.dozen.world.utils.OpenGLESUtil
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

    companion object {
        private val TAG = "TriangleRenderer"

        val TriangleCoords: List<Float> =
            arrayListOf(0.0f, 1.0f, 0.0f, -1.0f, -0.0f, 0.0f, 1.0f, -0.0f, 0.0f)    //顶点,左下角,右下角
        const val VertexShader: String =
            "attribute vec4 vPosition; void main(){ gl_Position=vPosition; }"    //根据所设置的顶点数据而插值后的顶点坐标

        //设置float类型默认精度，顶点着色器默认highp,片元着色器需要用户声明
        //颜色值,vec4代表四维向量,此处由用户传入,数据格式为{r,g,b,a}
        const val FragmentShader: String =
            "precision mediump float; uniform vec4 vColor; void main(){ gl_FragColor = vColor; }"

        //设置每个顶点的坐标数
        private const val CoordsPerVertex = 3
        //下一个顶点与上一个顶点之间的步长,以字节为单位,每个float类型变量为4字节
        private val VertexStrid = CoordsPerVertex * 4
        //顶点个数
        private val VertexCount = TriangleCoords.size / CoordsPerVertex

        val instance: TriangleRenderer by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TriangleRenderer()
        }
    }

    override fun onDrawFrame(p0: GL10?) {
        //通过所设置的颜色清空颜色缓冲区,设置三角形颜色和透明度
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        //告知OpenGL所要使用的Program
        GLES20.glUseProgram(mProgramId)
        //启用指向三角形顶点数据的句柄
        GLES20.glEnableVertexAttribArray(mPositionId)
        //绑定三角形的坐标数据
        GLES20.glVertexAttribPointer(
            mPositionId,
            CoordsPerVertex,
            GLES20.GL_FLOAT,
            false,
            VertexStrid,
            mVertexBuffer
        )

        //绑定颜色数据
        GLES20.glUniform4fv(mColorId, 1, TriangleCoords.toFloatArray(), 0)
        //绘制三角形
        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLES, 0,
            VertexCount
        )
        //禁用指向三角形的顶点数据
        GLES20.glDisableVertexAttribArray(mPositionId)
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        //设置显示在屏幕的区域
        GLES20.glViewport(0, 0, p1, p2)

        //初始化顶点字节缓冲区,用于存放三角的顶点数据
        val bb = ByteBuffer.allocateDirect(TriangleCoords.size * 4)
        //每个浮点数占用4个字节
        bb.order(ByteOrder.nativeOrder())
        //设置使用设备硬件的原生字节序
        mVertexBuffer = bb.asFloatBuffer()
        //把坐标都添加到FloatBuffer中
        mVertexBuffer.put(TriangleCoords.toFloatArray())
        //设置buffer从第一个位置开始读,因为在每次调用put加入数据后position都会加1,因此要将position重置为0
        mVertexBuffer.position(0)

    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Log.d(TAG, "onSurfaceCreated")
        //编译着色器并链接顶点与片元着色器生成 OpenGL程序句柄
        mProgramId = OpenGLESUtil.loadProgram(
            VertexShader,
            FragmentShader
        )
        //通过OpenGL程序句柄查找获取顶点着色器中的位置句柄
        mPositionId = GLES20.glGetAttribLocation(mProgramId, "vPosition")
        //通过OpenGL程序句柄查找获取片元着色器中的颜色句柄
        mColorId = GLES20.glGetUniformLocation(mProgramId, "vColor")
    }

    fun destroy() {
        GLES20.glDeleteProgram(mProgramId)
    }
}