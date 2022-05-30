package com.fox.opengl

import android.opengl.GLES20.*
import android.util.Log

const val TAG = "ShaderHelper"

fun compileVertexShader(shaderCode: String): Int {
    return compileShader(GL_VERTEX_SHADER, shaderCode)
}

fun compileFragmentShader(shaderCode: String): Int {
    return compileShader(GL_FRAGMENT_SHADER, shaderCode)
}

fun compileShader(type: Int, shaderCode: String): Int {
    val shaderObjectId = glCreateShader(type)
    if (shaderObjectId == 0) {
        // if opengl has exception , it will return 0 instead of throw exception
        if (LoggerConfig.ON) {
            Log.w(TAG, "could not create new shader")
        }
        return 0
    }
    // link shaderId and Shader Source
    glShaderSource(shaderObjectId, shaderCode)

    // compile the passing source
    glCompileShader(shaderObjectId)

    //get compile status
    val compilerStatus = IntArray(1)
    glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compilerStatus, 0)

    if (compilerStatus[0] == 0) {
        //compile failed,delete the shader object
        glDeleteShader(shaderObjectId)
        return 0
    }

    return shaderObjectId
}

//link the vertexShader adn fragmentShader program
fun linkProgram(vertexShader: Int, fragmentShader: Int): Int {
    val programObjectId = glCreateProgram()
    if (programObjectId == 0) {
        if (LoggerConfig.ON) {
            Log.w(TAG, "Could not create new program")
        }
        return 0
    }
    //attach the vertex and fragment shader to the program
    glAttachShader(programObjectId, vertexShader)
    glAttachShader(programObjectId, fragmentShader)

    //link the two shaders together into a program
    glLinkProgram(programObjectId)

    // verify the link status
    val linkStatus = IntArray(1)
    glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0)
    if (LoggerConfig.ON) {
        Log.w(TAG, "Result of linking program = ${glGetProgramInfoLog(programObjectId)}")
    }
    if (linkStatus[0] == 0) {
        glDeleteProgram(programObjectId)
        return 0
    }
    return programObjectId
}

//check the program validate
fun validateProgram(programObjectId: Int): Boolean {
    glValidateProgram(programObjectId)

    val validateStatus = IntArray(1)
    glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0)
    Log.v(
        TAG,
        "Results of validating program: ${validateStatus[0]} , nLog : ${
            glGetProgramInfoLog(programObjectId)
        }"
    )
    return validateStatus[0] != 0
}
