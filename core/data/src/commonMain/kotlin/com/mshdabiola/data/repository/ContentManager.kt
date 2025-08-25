package com.mshdabiola.data.repository

interface ContentManager {
    fun saveImage(uri: String): Long
    fun saveVoice(uri: String): Long
    fun pictureUri(): String
    fun getImagePath(data: Long): String
    fun getVoicePath(data: Long): String
//    fun saveBitmap(path: String, bitmap: Bitmap)
//    fun dataFile(drawingId: Long): File
fun dataFile(drawingId: Long): String


    fun getAudioLength(path: String): Long
    fun imageToText(path: String): String
}
