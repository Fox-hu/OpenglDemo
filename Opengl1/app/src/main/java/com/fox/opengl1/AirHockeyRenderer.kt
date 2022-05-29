package com.fox.opengl1

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @Author fox
 * @Date 2022/5/27 18:35
 */
class AirHockeyRenderer(val context: Context) : GLSurfaceView.Renderer {

    val POSITION_COMPONNT_COUNT = 2
    val BYTES_PRE_FLOAT = 4
    val U_COLOR = "u_Color"
    val A_POSITION = "a_Position"

    private val vertexData: FloatBuffer

    private var program = 0
    private var colorLocation = 0
    private var aPositionLocation = 0

    init {
        //顶点属性数组
        val tableVertices = floatArrayOf(
            //triangle 1
            0f, 0f,
            0f, 14f,
            0f, 14f,

            //triangle 2
            0f, 0f,
            9f, 14f,
            9f, 0f,

            //line 1
            0f, 7f,
            9f, 7f,

            //mallets
            4.5f, 2f,
            4.5f, 12f
        )

        //这里是在本地内存中存储数据 这里的本地内存不是jvm管理的内存 它不受垃圾回收期管控
        vertexData = ByteBuffer.allocateDirect(tableVertices.size * BYTES_PRE_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData.put(tableVertices)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //将屏幕清空为红色
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f)

        val vertexShaderSource = readTextFileFromRes(context, R.raw.simple_vertex_shader)
        val fragmentShaderSource = readTextFileFromRes(context, R.raw.simple_fragment_shader)

        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)

        //link and use program
        program = linkProgram(vertexShader, fragmentShader)
        if (LoggerConfig.ON) {
            validateProgram(program)
        }
        glUseProgram(program)

        //get uniform location
        colorLocation = glGetUniformLocation(program, U_COLOR)

        //get attrib location
        aPositionLocation = glGetAttribLocation(program, A_POSITION)

        //tell opengl where to find a_Position's data
        vertexData.position(0)
        glVertexAttribPointer(
            aPositionLocation,
            POSITION_COMPONNT_COUNT,
            GL_FLOAT,
            false,
            0,
            vertexData
        )
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置视窗尺寸
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //擦除屏幕上所有颜色，并用之前glClearColor调用定义的颜色填充整个屏幕
        glClear(GL_COLOR_BUFFER_BIT)
    }
}