package com.zhengsr.opengldemo

import android.graphics.Shader
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author by zhengshaorui 2022/9/15
 * describe：
 */
class PointRender : GLSurfaceView.Renderer {
    companion object {
        private const val TAG = "PointRender"

        /**
         * 顶点着色器：之后定义的每个都会传1次给顶点着色器
         */
        private val VERTEX_SHADER = """
                // vec4：4个分量的向量：x、y、z、w
                attribute vec4 a_Position;
                void main()
                {
                // gl_Position：GL中默认定义的输出变量，决定了当前顶点的最终位置
                    gl_Position = a_Position;
                // gl_PointSize：GL中默认定义的输出变量，决定了当前顶点的大小
                    gl_PointSize = 100.0;
                }
        """

        /**
         * 片段着色器
         */
        private val FRAGMENT_SHADER = """
                // 定义所有浮点数据类型的默认精度；有lowp、mediump、highp 三种，但只有部分硬件支持片段着色器使用highp。(顶点着色器默认highp)
                precision mediump float;
                uniform vec4 u_Color;
                void main()
                {
                // gl_FragColor：GL中默认定义的输出变量，决定了当前片段的最终颜色
                   gl_FragColor = u_Color;
                }
        """

        private val U_COLOR = "u_Color"
        private val A_POSITION = "a_Position"
        private val POINT_DATA = floatArrayOf(0f, 0f)

        /**
         * Float类型占4Byte
         */
        private val BYTES_PER_FLOAT = 4
        /**
         * 每个顶点数据关联的分量个数：当前案例只有x、y，故为2
         */
        private val POSITION_COMPONENT_COUNT = 2
    }

    private var programId = 0
    private var u_color = 0
    private var a_position = 0
    private var vertexData: FloatBuffer = ByteBuffer.allocateDirect(POINT_DATA.size * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    init {
        vertexData.put(POINT_DATA)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)
        //需要编译着色器，编译成一段可执行的bin，去与显卡交流
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        //步骤2，编译片段着色器
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)

        // 步骤3：将顶点着色器、片段着色器进行链接，组装成一个OpenGL程序
        programId = linkProgram(vertexShader, fragmentShader)

        //通过OpenGL 使用该程序
        GLES20.glUseProgram(programId)
        // 步骤5：获取颜色Uniform在OpenGL程序中的索引
        u_color = GLES20.glGetUniformLocation(programId, U_COLOR)
        // 步骤6：获取顶点坐标属性在OpenGL程序中的索引
        a_position = GLES20.glGetUniformLocation(programId, A_POSITION)

        //将缓冲区的指针指到头部，保证数据从头开始
        vertexData.position(0)

        // 步骤7：关联顶点坐标属性和缓存数据
        // 1. 位置索引；
        // 2. 每个顶点属性需要关联的分量个数(必须为1、2、3或者4。初始值为4。)；
        // 3. 数据类型；
        // 4. 指定当被访问时，固定点数据值是否应该被归一化(GL_TRUE)或者直接转换为固定点值(GL_FALSE)(只有使用整数数据时)
        // 5. 指定连续顶点属性之间的偏移量。如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。初始值为0。
        // 6. 数据缓冲区
        GLES20.glVertexAttribPointer(a_position, POSITION_COMPONENT_COUNT,GLES20.GL_FLOAT,
            false,0,vertexData)
        // 步骤8：通知GL程序使用指定的顶点属性索引
        GLES20.glEnableVertexAttribArray(a_position)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //填充整个页面
        GLES20.glViewport(0,0,width,height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //步骤1：使用glClearColor设置的颜色，刷新Surface
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        // 步骤2：更新u_Color的值，即更新画笔颜色，RGBA，百分百的意思
        GLES20.glUniform4f(u_color,0f,1f,0f,1f)
        // 步骤3：使用数组绘制图形：1.绘制的图形类型；2.从顶点数组读取的起点；3.从顶点数组读取的顶点个数 ,这里只绘制一个点
        GLES20.glDrawArrays(GLES20.GL_POINTS,0,1)
    }


    private fun compileShader(type: Int, shaderCode: String): Int {
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

    private fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
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