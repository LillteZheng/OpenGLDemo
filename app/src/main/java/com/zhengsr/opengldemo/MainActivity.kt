package com.zhengsr.opengldemo

import android.content.Context
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.zhengsr.opengldemo.render.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var glSurfaceView: GLSurfaceView? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val data = arrayListOf<RenderItem>(
            RenderItem(L1_PointRender::class.java, "L1 - 基础类型，点"),
            RenderItem(L2_ShapeRender::class.java, "L2 - 点,线，三角形"),
            RenderItem(L3_ShapeRender::class.java, "L3 - 正交投影，修复横竖屏，图形变形的问题"),
            RenderItem(L4_ShapeRender::class.java, "L4 - 渐变色"),
            RenderItem(L5_ShapeRender::class.java, "L5 - 优化数据VBO,VAO"),
            RenderItem(L6_ShapeRender::class.java, "L6 - 纹理"),
            RenderItem(L6_ShapeRender_1::class.java, "L6-1 - 多纹理-图片混合"),
        )
        with(recycleView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val testAdapter = TestAdapter()
            testAdapter.submitList(data)
            testAdapter.setOnItemClickListener { adapter, view, position ->
                val render = getRenderer(data[position].className)
                glSurfaceView = GLSurfaceView(this@MainActivity).apply {
                    setEGLContextClientVersion(3)
                    setEGLConfigChooser(false)
                    setOnClickListener {
                        requestRender()
                    }
                    visibility = View.VISIBLE
                    recycleView.visibility = View.GONE
                    setRenderer(render)
                    //等待点击才会刷帧
                    renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
                    rootContent.addView(this)
                }

            }
            adapter = testAdapter

        }

    }

    override fun onBackPressed() {

        if (recycleView.visibility == View.GONE) {
            recycleView.visibility = View.VISIBLE
            glSurfaceView?.visibility = View.GONE
            rootContent.removeView(glSurfaceView)
            glSurfaceView = null
            return
        }
        super.onBackPressed()
    }

    class TestAdapter : BaseQuickAdapter<RenderItem, QuickViewHolder>() {

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): QuickViewHolder {
            // 返回一个 ViewHolder
            return QuickViewHolder(R.layout.layout_item, parent)
        }

        override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: RenderItem?) {
            // 设置item数据
            item?.let {
                holder.setText(R.id.item_text, it.content)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    data class RenderItem(val className: Class<*>, val content: String)

    fun getRenderer(className: Class<*>): GLSurfaceView.Renderer? {
        try {
            val constructor = className.getConstructor()
            return constructor.newInstance() as GLSurfaceView.Renderer
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}