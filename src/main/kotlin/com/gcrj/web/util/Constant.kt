package com.gcrj.web.util

object Constant {

//    const val WEB_DB_PATH = "jdbc:sqlite:D:/Java/IntellijIdeaWorkSpace/ProjectControlWeb/db/pc_web.db"

    val WEB_DB_PATH: String
    val APK_DB_PATH: String
    val APK_PATH: String
    const val PORT = "8088"

    init {
        val path = this::class.java.classLoader.getResource("").path
        WEB_DB_PATH = "jdbc:sqlite:${path.substring(1, path.indexOf("build"))}db/pc_web.db"
        APK_DB_PATH = "jdbc:sqlite:${path.substring(1, path.indexOf("build"))}db/apk.db"
        APK_PATH = "${path.substring(1, path.indexOf("build"))}/apk"
    }

}