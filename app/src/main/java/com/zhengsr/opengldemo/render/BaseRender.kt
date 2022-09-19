package com.zhengsr.opengldemo.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log

/**
 * @author by zhengshaorui 2022/9/16
 * describe：
 */
abstract class BaseRender: GLSurfaceView.Renderer {
    companion object{
        private const val TAG = "BaseRender"
    }
    protected var programId = 0

    protected fun makeProgram(vertexShaderCode:String,fragmentShaderCode:String){
        //需要编译着色器，编译成一段可执行的bin，去与显卡交流
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        //步骤2，编译片段着色器
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // 步骤3：将顶点着色器、片段着色器进行链接，组装成一个OpenGL程序
        programId = linkProgram(vertexShader, fragmentShader)

        //通过OpenGL 使用该程序
        GLES20.glUseProgram(programId)
    }

    protected fun getUniform(name: String): Int {
        return GLES20.glGetUniformLocation(programId, name)
    }

    protected fun getAttrib(name: String): Int {
        return GLES20.glGetAttribLocation(programId, name)
    }

    open fun compileShader(type: Int, shaderCode: String): Int {
        //创建一个shader 对象
        val shaderId = GLES20.glCreateShader(type)
        Log.d(TAG, "zsr compileShader id: $shaderId")
        if (shaderId == 0) {
            Log.d(TAG, "zsr 创建失败")
            return 0
        }
        //将着色器代码上传到着色器对象中
        GLES20.glShaderSource(shaderId, shaderCode)
        //编译对象
        GLES20.glCompileShader(shaderId)
        //获取编译状态，OpenGL 把想要获取的值放入长度为1的数据首位
        val compileStatus = intArrayOf(1)
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        Log.d(TAG, "zsr compileShader: ${compileStatus[0]}")

        if (compileStatus[0] == 0) {
            Log.d(TAG, "zsr 编译失败")
            GLES20.glDeleteShader(shaderId)
            return 0
        }

        return shaderId
    }

    open fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        //创建一个 OpenGL 程序对象
        val programId = GLES20.glCreateProgram()
        if (programId == 0) {
            Log.d(TAG, "zsr 创建OpenGL程序对象失败")
            return 0
        }
        //关联顶点着色器
        GLES20.glAttachShader(programId, vertexShaderId)
        //关联片段周色漆
        GLES20.glAttachShader(programId, fragmentShaderId)
        //将两个着色器关联到 OpenGL 对象
        GLES20.glLinkProgram(programId)
        //获取链接状态，OpenGL 把想要获取的值放入长度为1的数据首位
        val linkStatus = intArrayOf(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        Log.d(TAG, "zsr linkProgram: ${linkStatus[0]}")

        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(programId)
            Log.d(TAG, "zsr 编译失败")
            return 0
        }
        return programId;

    }
}