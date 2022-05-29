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

    val POSITION_COMPONENT_COUNT = 2
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
            //fill screen triangle
            -1.0f,-1.0f,
            1.0f,1.0f,
            -1.0f,1.0f,

            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f,

            //3/4 fill screen triangle
            -0.75f,-0.75f,
            0.75f,0.75f,
            -0.75f,0.75f,

            -0.75f, -0.75f,
            0.75f, -0.75f,
            0.75f, 0.75f,

            //triangle 1
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,

            //triangle 2
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,

            //line 1
            -0.5f, 0f,
            0.5f, 0f,

            //mallets
            0f, -0.25f,
            0f, 0.25f
        )

        //这里是在本地内存中存储数据 这里的本地内存不是jvm管理的内存 它不受垃圾回收期管控
        vertexData = ByteBuffer.allocateDirect(tableVertices.size * BYTES_PRE_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData.put(tableVertices)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

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
            POSITION_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            0,
            vertexData
        )
        glEnableVertexAttribArray(aPositionLocation)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置视窗尺寸
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //擦除屏幕上所有颜色，并用之前glClearColor调用定义的颜色填充整个屏幕
        glClear(GL_COLOR_BUFFER_BIT)
        //draw full screen 2 triangles,take 6 item from input array
        //because of each take 2 bytes for x&y ,see POSITION_COMPONENT_COUNT
        glUniform4f(colorLocation,0.0f,1.0f,1.0f,1.0f)
        glDrawArrays(GL_TRIANGLES,0,6)
        //draw 3/4 full screen 2 triangles,take 6 item from input array
        glUniform4f(colorLocation,1.0f,0.0f,1.0f,1.0f)
        glDrawArrays(GL_TRIANGLES,6,6)
        //draw 2 triangles，take 6 item from input array
        glUniform4f(colorLocation,1.0f,1.0f,1.0f,1.0f)
        glDrawArrays(GL_TRIANGLES,12,6)
        //draw line, take 2 item from input array
        glUniform4f(colorLocation,1.0f,0.0f,0.0f,1.0f)
        glDrawArrays(GL_LINES,18,2)
        //draw first mallet blue,first is the 8th item
        glUniform4f(colorLocation,0.0f,0.0f,1.0f,1.0f)
        glDrawArrays(GL_POINTS,20,1)
        //draw second mallet red,second is the 9th item
        glUniform4f(colorLocation,1.0f,0.0f,0.0f,1.0f)
        glDrawArrays(GL_POINTS,21,1)
    }
}