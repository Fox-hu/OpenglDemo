package com.fox.opengl

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder

/**
 * @Author fox
 * @Date 2022/5/28 00:05
 */

fun readTextFileFromRes(context: Context, resId: Int): String {
    val body = StringBuilder()
    val inputStream = context.resources.openRawResource(resId)
    val inputStreamReader = InputStreamReader(inputStream)
    val bufferedReader = BufferedReader(inputStreamReader)

    var nextLine: String?
    while (bufferedReader.readLine().also { nextLine = it } != null) {
        body.append(nextLine)
        body.append('\n')
    }
    return body.toString()
}