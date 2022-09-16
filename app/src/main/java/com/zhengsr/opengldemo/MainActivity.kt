package com.zhengsr.opengldemo

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.zhengsr.opengldemo.render.L1_PointRender
import com.zhengsr.opengldemo.render.L2_ShapeRender
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private  val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(glSurfaceView){
            setEGLContextClientVersion(2)
            setEGLConfigChooser(false)
            setRenderer(L2_ShapeRender())
            //等待点击才会刷帧
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            setOnClickListener {
                Log.d(TAG, "zsr onCreate: ")
                requestRender()
            }
        }



    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}