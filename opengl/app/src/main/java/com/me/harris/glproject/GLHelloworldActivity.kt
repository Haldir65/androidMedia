package com.me.harris.glproject

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.me.harris.glproject.shapes.Square
import com.me.harris.glproject.shapes.Triangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLHelloworldActivity:AppCompatActivity() {

    private lateinit var mGLView: GLSurfaceView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = MyGLSurfaceView(this)
        setContentView(mGLView)
    }


    class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

        private val mRenderer: MyGLRenderer

        init {

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2)

            mRenderer = MyGLRenderer()

            // Set the Renderer for drawing on the GLSurfaceView
            setRenderer(mRenderer)
        }
    }

    class MyGLRenderer : GLSurfaceView.Renderer {

        private lateinit var mTriangle: Triangle
        private lateinit var mSquare: Square

        override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

            // initialize a triangle
            mTriangle = Triangle()
            // initialize a square
            mSquare = Square()
            // Set the background frame color
            GLES20.glClearColor(247.0f, 0.0f, 0.0f, 1.0f)
        }

        override fun onDrawFrame(unused: GL10) {
            // Redraw background color

            // Set the camera position (View matrix)
            Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

            // Calculate the projection and view transformation
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
            mTriangle.draw(mMVPMatrix)
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        }

        // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
        private val mMVPMatrix = FloatArray(16)
        private val mProjectionMatrix = FloatArray(16)
        private val mViewMatrix = FloatArray(16)

        override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)

            val ratio: Float = width.toFloat() / height.toFloat()

            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        }
    }


}