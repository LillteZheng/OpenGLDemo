#version 300 es
layout(location = 0) in vec4 a_Position;
// mat4：4×4的矩阵
uniform mat4 u_Matrix;
//定义可以给外部赋值的顶点数据
layout(location = 1) in vec4 a_Color;
//给片段着色器的颜色顶点
out vec4 vTextColor;
uniform sampler2d ourTexture;
void main()
{
    // 矩阵与向量相乘得到最终的位置
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = 30.0;
    //传递给片段着色器的颜色
    vTextColor = a_Color;



}