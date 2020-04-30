package com.dozen.world.utils

import android.opengl.GLES20

/**
 * Created by Hugo on 20-4-29.
 * Describe:
 *
 *
 *
 */
class OpenGLESUtil {
    companion object{

        private fun loadShader(strSource: String, iType: Int): Int {
            val compiled: IntArray = IntArray(1)
            //创建指定类型的着色器
            val iShader = GLES20.glCreateShader(iType)
            //将源码添加到iShader并编译它
            GLES20.glShaderSource(iShader, strSource)
            GLES20.glCompileShader(iShader)
            //获取编译后着色器句柄存在compiled数组容器中
            GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0)

            return if (compiled[0] == 0) 0 else iShader
        }

        fun loadProgram(strVSource: String, strFSource: String): Int {
            //获取编译后的顶点着色器顶点
            val iVShader: Int = loadShader(strVSource, GLES20.GL_VERTEX_SHADER)
            //获取编译后的片元着色器句柄
            val iFShader: Int = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER)
            //创建一个Program
            val iProId: Int = GLES20.glCreateProgram()
            val link: IntArray = IntArray(1)


            if (iVShader == 0) return 0

            if (iFShader == 0) return 0

            //添加顶点着色器与片无着色器到Program中
            GLES20.glAttachShader(iProId, iVShader)
            GLES20.glAttachShader(iProId, iFShader)
            //链接生成可执行的Program
            GLES20.glLinkProgram(iProId)
            //获取Program句柄,并存在在link数组容器中
            GLES20.glGetProgramiv(iProId, GLES20.GL_LINK_STATUS, link, 0)

            if (link[0] == 0) return 0

            //删除已链接后的着色器
            GLES20.glDeleteShader(iVShader)
            GLES20.glDeleteShader(iFShader)
            return iProId
        }

    }
}