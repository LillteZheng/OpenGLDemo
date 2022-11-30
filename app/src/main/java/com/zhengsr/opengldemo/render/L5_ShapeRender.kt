package com.zhengsr.opengldemo.render

import android.opengl.GLES30
import android.opengl.Matrix
import com.zhengsr.opengldemo.utils.BufferUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author by zhengshaorui 2022/9/16
 * describe：优化缓冲区数据
 *
 */
class L5_ShapeRender : BaseRender() {


    companion object {

        /**
         * 顶点着色器：之后定义的每个都会传1次给顶点着色器
         * 修改顶部着色器的坐标值，即增加个举证x向量
         */
        private const val VERTEX_SHADER = """#version 300 es
                layout(location = 0) in vec4 a_Position;
                // mat4：4×4的矩阵
                uniform mat4 u_Matrix;
                layout(location = 1) in vec4 a_Color;
                out vec4 vTextColor;
                void main()
                {
                 // 矩阵与向量相乘得到最终的位置
                    gl_Position = u_Matrix * a_Position;
                    gl_PointSize = 30.0;
                    vTextColor = a_Color;
                    
                }
        """

        /**
         * 片段着色器
         */
        private const val FRAGMENT_SHADER = """#version 300 es
                // 定义所有浮点数据类型的默认精度；有lowp、mediump、highp 三种，但只有部分硬件支持片段着色器使用highp。(顶点着色器默认highp)
                precision mediump float;
                out vec4 FragColor;
                in vec4 vTextColor;
                void main()
                {
                  FragColor = vTextColor;
                }
        """

        private val POINT_DATA = floatArrayOf(
            //三角形
            0f,0.5f,
            -0.5f,-0.5f,
            0.5f,-0.5f
        )
        private val COLOR_DATA = floatArrayOf(
            //颜色值 RGB
            1f,0.5f,0.5f,
            1f,0f,1f,
            0f,0.5f,1f
        )
        private val POINT_COLOR_DATA = floatArrayOf(
            //定点+颜色
            0f,0.5f,1f,0.5f,0.5f,
            -0.5f,-0.5f,1f,0f,1f,
            0.5f,-0.5f,0f,0.5f,1f
        )
      //  private const val U_COLOR = "u_Color"
        private const val U_MATRIX = "u_Matrix"
        //单位矩阵，单位矩阵乘以任何数都等于乘数本身
        private val UnitMatrix = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
       // private var uColor = 0
    }

    private var vertexData = BufferUtil.createFloatBuffer(POINT_COLOR_DATA)
   // private var colorData = BufferUtil.createFloatBuffer(COLOR_DATA)


    /**
     * 每个顶点数据关联的分量个数：当前案例只有x、y，故为2
     */
    private val POSITION_COMPONENT_COUNT = 2
    //颜色分量为3
    private val COLOR_COMPONENT_COUNT = 3
    private var uMatrix = 0
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(1f, 1f, 1f, 1f)
        makeProgram(VERTEX_SHADER, FRAGMENT_SHADER)

        uMatrix = getUniform(U_MATRIX)
        vertexData.position(0)
        GLES30.glVertexAttribPointer(
            0, POSITION_COMPONENT_COUNT, GLES30.GL_FLOAT,
            false, 20, vertexData
        )
        GLES30.glEnableVertexAttribArray(0)

        vertexData.position(2)
        GLES30.glVertexAttribPointer(
            1, COLOR_COMPONENT_COUNT, GLES30.GL_FLOAT,
            false, 20, vertexData
        )
        GLES30.glEnableVertexAttribArray(1)



    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val aspectRatio = if (width > height) {
            width.toFloat() / height
        } else {
            height.toFloat() / width
        }
        // 1. 矩阵数组
        // 2. 结果矩阵起始的偏移量
        // 3. left：x的最小值
        // 4. right：x的最大值
        // 5. bottom：y的最小值
        // 6. top：y的最大值
        // 7. near：z的最小值
        // 8. far：z的最大值
        // 由于是正交矩阵，所以偏移量为0，near 和 far 也不起作用，让他们不相等即可
        if (width > height){
           Matrix.orthoM(UnitMatrix,0,-aspectRatio,aspectRatio,-1f,1f,0f,1f)
        }else{
            Matrix.orthoM(UnitMatrix,0,-1f,1f,-aspectRatio,aspectRatio,0f,1f)
        }
        //更新 matrix 的值，即把 UnitMatrix 值，更新到 uMatrix 这个索引
        GLES30.glUniformMatrix4fv(uMatrix,1,false, UnitMatrix,0)
    }

    override fun onDrawFrame(gl: GL10?) {
        //步骤1：使用glClearColor设置的颜色，刷新Surface
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,3)

    }


}