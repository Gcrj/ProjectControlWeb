package com.gcrj.web.util

object Constant {

//    const val DB_PATH = "jdbc:sqlite:D:/Java/IntellijIdeaWorkSpace/ProjectControlWeb/db/pc_web.db"

    val DB_PATH: String

    init {
        val path = this::class.java.classLoader.getResource("").path
        DB_PATH = "jdbc:sqlite:${path.substring(1, path.indexOf("build"))}db/pc_web.db"
    }

}