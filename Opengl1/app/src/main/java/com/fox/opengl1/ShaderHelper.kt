package com.fox.opengl1

import android.opengl.GLES20.*
import android.util.Log

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
            Log.w("TAG", "could not create new shader")
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
    return 0
}

//check the program validate
fun validateProgram(programObjectId: Int): Boolean {
    glValidateProgram(programObjectId)

    val validateStatus = IntArray(1)
    glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0)
    Log.v(
        "TAG",
        "Results of validating program: ${validateStatus[0]} , nLog : ${
            glGetProgramInfoLog(programObjectId)
        }"
    )
    return validateStatus[0] != 0
}
