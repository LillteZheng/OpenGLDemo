package com.zhengsr.opengldemo.render

import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import com.zhengsr.opengldemo.MainApplication
import com.zhengsr.opengldemo.R
import com.zhengsr.opengldemo.utils.BufferUtil
import com.zhengsr.opengldemo.utils.TextureBean
import com.zhengsr.opengldemo.utils.loadTexture
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author by zhengshaorui 2022/9/16
 * describe：纹理
 *
 */
class L6_ShapeRender_1 : BaseRender() {


    companion object {
        /**
         * 顶点着色器：之后定义的每个都会传1次给顶点着色器
         * 修改顶部着色器的坐标值，即增加个举证x向量
         */
        private const val VERTEX_SHADER = """#version 300 es
                uniform mat4 u_Matrix;
                layout(location = 0) in vec4 a_Position;
                layout(location = 1) in vec2 aTexture;
                out vec2 vTexture;
                void main()
                {
                    // 矩阵与向量相乘得到最终的位置
                    gl_Position = u_Matrix * a_Position;
                    //传递给片段着色器的颜色
                    vTexture = aTexture;
                
                }
        """
        private val TAG = L6_ShapeRender_1::class.java.simpleName

        /**
         * 片段着色器
         */
        private const val FRAGMENT_SHADER = """#version 300 es
            precision mediump float;
            out vec4 FragColor;
            in vec2 vTexture;
            uniform sampler2D ourTexture;
            uniform sampler2D ourTexture2;
            void main()
            {
                vec4 texture1 = texture(ourTexture,vTexture) ;
                vec4 texture2 = texture(ourTexture2,vTexture) ;
                FragColor = mix(texture1,texture2,0.5);
            }
        """



        private val POINT_RECT_DATA2 = floatArrayOf(
            // positions           // texture coords
            0.8f, 0.8f, 0.0f, 1.0f, 0.0f, // top right
            0.8f, -0.8f, 0.0f, 1.0f, 1.0f, // bottom right
            -0.8f, -0.8f, 0.0f, 0.0f, 1.0f, // bottom left
            -0.8f, 0.8f, 0.0f, 0.0f, 0.0f  // top left
        )


        private val indeices = intArrayOf(
            // 注意索引从0开始!
            // 此例的索引(0,1,2,3)就是顶点数组vertices的下标，
            // 这样可以由下标代表顶点组合成矩形

            0, 1, 3, // 第一个三角形
            1, 2, 3  // 第二个三角形
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
    }


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
        val programId = makeProgram(VERTEX_SHADER, FRAGMENT_SHADER)

        uMatrix = getUniform(U_MATRIX)
        GLES30.glUniform1i(GLES30.glGetUniformLocation(programId,"ourTexture"),0)
        GLES30.glUniform1i(GLES30.glGetUniformLocation(programId,"ourTexture2"),1)

        // useVbo()
        //useEboAndVbo()
        //  useVaoVbo()
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
        if (width > height) {
            Matrix.orthoM(UnitMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, 0f, 1f)
        } else {
            Matrix.orthoM(UnitMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 0f, 1f)
        }
        //更新 matrix 的值，即把 UnitMatrix 值，更新到 uMatrix 这个索引
        GLES30.glUniformMatrix4fv(uMatrix, 1, false, UnitMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        //步骤1：使用glClearColor设置的颜色，刷新Surface
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        val vertexData = BufferUtil.createFloatBuffer(POINT_RECT_DATA2)
        val texture = useVaoTexture(vertexData,R.mipmap.wuliuqi)
        texture.bean1?.let {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, it.id)
        }
        texture.bean2?.let {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, it.id)
        }
        GLES30.glBindVertexArray(texture.vao)
        GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP, 6, GLES30.GL_UNSIGNED_INT, 0)

    }

    private fun drawTexture1() {

    }

    private fun drawTexture2() {
        /*val vertexData = BufferUtil.createFloatBuffer(POINT_RECT_DATA2)
        val texture = useVaoTexture(vertexData,R.mipmap.wuliuqi2)
        texture.bean?.let {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, it.id)
        }
        GLES30.glBindVertexArray(texture.vao)
        GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP, 6, GLES30.GL_UNSIGNED_INT, 0)*/
    }


    private fun useVaoTexture(vertexData: FloatBuffer, resId: Int): VaoTexture {
        val texture = VaoTexture()
        val vbo = IntArray(1)
        val ebo = IntArray(1)
        val vao = IntArray(1)

        val indexData = BufferUtil.createIntBuffer(indeices)


        //使用 vbo,vao 优化数据传递
        //创建 VAO
        GLES30.glGenVertexArrays(1, vao, 0)
        // //创建 VBO
        GLES30.glGenBuffers(1, vbo, 0)
        //绑定 VAO ,之后再绑定 VBO
        GLES30.glBindVertexArray(vao[0])
        //绑定VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertexData.capacity() * 4,
            vertexData,
            GLES30.GL_STATIC_DRAW
        )
        //创建 ebo
        GLES30.glGenBuffers(1, ebo, 0)
        //绑定 ebo 到上下文
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
        GLES30.glBufferData(
            GLES30.GL_ELEMENT_ARRAY_BUFFER,
            indexData.capacity() * 4,
            indexData,
            GLES30.GL_STATIC_DRAW
        )

        //绘制位置
        GLES30.glVertexAttribPointer(
            0, 3, GLES30.GL_FLOAT,
            false, 5 * 4, 0
        )
        GLES30.glEnableVertexAttribArray(0)


        texture.bean1 = loadTexture(TAG, MainApplication.context, R.mipmap.wuliuqi)
        texture.bean2 = loadTexture(TAG, MainApplication.context, R.mipmap.wuliuqi2)
        //纹理在位置和颜色之后，偏移量为6
        vertexData.position(3)
        GLES30.glVertexAttribPointer(
            1, 2, GLES30.GL_FLOAT,
            false, 5 * 4, 3 * 4 //需要指定颜色的地址 3 * 4
        )
        GLES30.glEnableVertexAttribArray(1)


        Log.d(TAG, " useVaoVboAndEbo,get texture $texture")
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
        //注意顺序，ebo 要在 eao 之后
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
        texture.vao = vao[0]
        return texture
    }
}

data class VaoTexture(var vao: Int, var bean1: TextureBean?,var bean2: TextureBean?) {
    constructor() : this(0, null,null)
}