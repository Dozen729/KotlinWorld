package com.dozen.world.ffmpeg.gl

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import com.dozen.world.utils.OpenGLESUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Hugo on 20-4-29.
 * Describe:
 *
 *
 *
 */
class PictureRenderer : GLSurfaceView.Renderer {

    private lateinit var bCoord: FloatBuffer
    private lateinit var bPos: FloatBuffer
    private var textureId: Int = 0
    private var glHTexture: Int = 0
    private var glHCoordinate: Int = 0
    private var glHPosition: Int = 0
    private var glHMatrix: Int = 0
    private var mProgramId: Int = 0


    private var mMVPMatrix: FloatArray = FloatArray(16)
    private var mViewMatrix: FloatArray = FloatArray(16)
    private var mProjectMatrix: FloatArray = FloatArray(16)
    private lateinit var mBitmap: Bitmap

    companion object {
        //顶点着色器
        val VertexShader: String =
            "attribute vec4 vPosition;attribute vec2 vCoordinate;uniform mat4 vMatrix;varying vec2 aCoordinate;" +
                    "void main(){ gl_Position=vMatrix*vPosition; aCoordinate=vCoordinate; }"
        //片元着色器
        val FragmentShader: String =
            "precision mediump float;uniform sampler2D vTexture;varying vec2 aCoordinate;" +
                    "void main(){ gl_FragColor=texture2D(vTexture,aCoordinate); }"
        //顶点坐标
        val sPos: List<Float> = arrayListOf(
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f
        )
        //纹理坐标
        val sCoord: List<Float> = arrayListOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
        )

        val instance: PictureRenderer by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PictureRenderer()
        }
    }

    fun addPictureToShow(bitmap: Bitmap) {
        mBitmap = bitmap

        //为存放形状的坐标,初始化顶点字节缓冲,float占4个字节
        val bb: ByteBuffer = ByteBuffer.allocateDirect(sPos.size * 4)
        //使用设备的本点字节序
        bb.order(ByteOrder.nativeOrder())
        //从Bytebuffer创建一个浮点缓冲
        bPos = bb.asFloatBuffer()
        //把顶点坐标加入FloatBuffer中
        bPos.put(sPos.toFloatArray())
        //设置buffer,从第一个坐标开始读
        bPos.position(0)

        val cc: ByteBuffer = ByteBuffer.allocateDirect(sCoord.size * 4)
        cc.order(ByteOrder.nativeOrder())
        bCoord = cc.asFloatBuffer()
        bCoord.put(sCoord.toFloatArray())
        bCoord.position(0)

    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_TEXTURE_2D)

        mProgramId = OpenGLESUtil.loadProgram(
            VertexShader,
            FragmentShader
        )

        //glGetAttribLocation方法,获取着色器程序中,指定为attribute类型的id
        //glGetUniformLocation方法,获取着色器程序中,指定为uniform类型变量的id
        //获取指向vertext shader(顶点着色器)的成员vPosition的handel
        glHPosition = GLES20.glGetAttribLocation(mProgramId, "vPosition")
        glHCoordinate = GLES20.glGetAttribLocation(mProgramId, "vCoordinate")

        glHMatrix = GLES20.glGetUniformLocation(mProgramId, "vMatrix")
        glHTexture = GLES20.glGetUniformLocation(mProgramId, "vTexture")

    }

    override fun onDrawFrame(p0: GL10?) {

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUseProgram(mProgramId)

        GLES20.glUniformMatrix4fv(glHMatrix, 1, false, mMVPMatrix, 0)
        GLES20.glEnableVertexAttribArray(glHPosition)
        GLES20.glEnableVertexAttribArray(glHCoordinate)
        GLES20.glUniform1i(glHTexture, 0)

        textureId = createTexture()
        //传入顶点坐标
        GLES20.glVertexAttribPointer(glHPosition, 2, GLES20.GL_FLOAT, false, 0, bPos)
        //传入纹理坐标
        GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, bCoord)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)


    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        GLES20.glViewport(0, 0, p1, p2)

        val w = mBitmap.width
        val h = mBitmap.height
        val sWH = w / h.toFloat()
        val sWidthHeight = p1 / p2.toFloat()

        if (p1 > p2) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(
                    mProjectMatrix,
                    0,
                    -sWidthHeight * sWH,
                    sWidthHeight * sWH,
                    -1f,
                    1f,
                    3f,
                    7f
                )
            } else {
                Matrix.orthoM(
                    mProjectMatrix,
                    0,
                    -sWidthHeight / sWH,
                    sWidthHeight / sWH,
                    -1f,
                    1f,
                    3f,
                    7f
                )
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(
                    mProjectMatrix,
                    0,
                    -1f,
                    1f,
                    -1 / sWidthHeight * sWH,
                    1 / sWidthHeight * sWH,
                    3f,
                    7f
                )
            } else {
                Matrix.orthoM(
                    mProjectMatrix,
                    0,
                    -1f,
                    1f,
                    -sWidthHeight / sWH,
                    sWidthHeight / sWH,
                    3f,
                    7f
                )
            }
        }

        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
    }


    private fun createTexture(): Int {
        val texture: IntArray = IntArray(1)

        if (!mBitmap.isRecycled) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0)
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST
            )
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色,通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )
            //设置环绕方向S,截取纹理坐标到[1/2n,1-1/2n].将导致永远不会与border融合
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE
            )
            //设置环绕方向T,截取纹理坐标到[1/2n,1-1/2n].将导致永远不会与border融合
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE
            )
            //根据以上指定的参数,生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0)
            return texture[0]
        }
        return 0
    }

    fun destroy() {
        GLES20.glDeleteProgram(mProgramId)
    }
}