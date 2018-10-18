package com.gcrj.web.util

import java.net.InetAddress

object Constant {

//    const val WEB_DB_PATH = "jdbc:sqlite:D:/Java/IntellijIdeaWorkSpace/ProjectControlWeb/db/pc_web.db"

    val URL_PATH: String
    val STATIC_PATH: String
    val IMAGE_PATH: String
    val WEB_DB_PATH: String
    val APK_DB_PATH: String
    val APK_PATH: String
    val XLS_PATH: String
    val LOCAL_STATIC_PATH: String
    val LOCAL_IMAGE_PATH: String
    const val PORT = "8088"

    init {
        URL_PATH = "http://${InetAddress.getLocalHost().hostAddress}:${Constant.PORT}/"
        STATIC_PATH = "${URL_PATH}static/"
        IMAGE_PATH = "${STATIC_PATH}img/"
        val path = this::class.java.classLoader.getResource("").path
        val projectPath = path.substring(1, path.indexOf("build"))
        WEB_DB_PATH = "jdbc:sqlite:${projectPath}db/pc_web.db"
        APK_DB_PATH = "jdbc:sqlite:${projectPath}db/apk.db"
        APK_PATH = "$projectPath/apk"
        XLS_PATH = "$projectPath/xls"
        LOCAL_STATIC_PATH = "$projectPath/static/"
        LOCAL_IMAGE_PATH = "${LOCAL_STATIC_PATH}img"
    }

}