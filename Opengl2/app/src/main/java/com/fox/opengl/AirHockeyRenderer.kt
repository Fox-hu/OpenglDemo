package com.fox.opengl

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
class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    companion object {
        const val POSITION_COMPONENT_COUNT = 2
        const val COLOR_COMPONENT_COUNT = 3
        const val BYTES_PRE_FLOAT = 4
        const val A_POSITION = "a_Position"
        const val A_COLOR = "a_Color"

        // each vertex has how many bytes,2 bytes for position ,3 bytes for color
        const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PRE_FLOAT
    }

    private val vertexData: FloatBuffer

    private var program = 0
    private var aColorLocation = 0
    private var aPositionLocation = 0

    init {
        //顶点属性数组
        val tableVertices = floatArrayOf(
            // Order of coordinates: X, Y, R, G, B

            // Triangle Fan
            0f,    0f,   1f,   1f,   1f,
            -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
            0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
            0.5f,  0.5f, 0.7f, 0.7f, 0.7f,
            -0.5f,  0.5f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,

            // Line 1
            -0.5f, 0f, 1f, 0f, 0f,
            0.5f, 0f, 1f, 0f, 0f,

            // Mallets
            0f, -0.25f, 0f, 0f, 1f,
            0f,  0.25f, 1f, 0f, 0f
        )

        //这里是在本地内存中存储数据 这里的本地内存不是jvm管理的内存 它不受垃圾回收器控制
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

        //get varying location
        aColorLocation = glGetAttribLocation(program, A_COLOR)

        //get attrib location
        aPositionLocation = glGetAttribLocation(program, A_POSITION)

        //tell opengl where to find a_Position's data
        vertexData.position(0)
        glVertexAttribPointer(
            aPositionLocation,
            POSITION_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        glEnableVertexAttribArray(aPositionLocation)

        //tell opengl where to find a_Color's data
        vertexData.position(POSITION_COMPONENT_COUNT)
        glVertexAttribPointer(
            aColorLocation,
            COLOR_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        glEnableVertexAttribArray(aColorLocation)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置视窗尺寸
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //擦除屏幕上所有颜色，并用之前glClearColor调用定义的颜色填充整个屏幕
        glClear(GL_COLOR_BUFFER_BIT)
        // draw the table
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
        // draw the center dividing line
        glDrawArrays(GL_LINES, 6, 2)
        // draw the first mallet
        glDrawArrays(GL_POINTS, 8, 1)
        // draw the second mallet
        glDrawArrays(GL_POINTS, 9, 1)
    }
}