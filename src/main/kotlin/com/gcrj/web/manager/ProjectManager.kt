package com.gcrj.web.manager

import com.gcrj.web.bean.ProjectBean
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object ProjectManager {

    init {
        Class.forName("org.sqlite.JDBC")
    }

    fun query(userId: Int): List<ProjectBean> {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:D:/Java/IntellijIdeaWorkSpace/ProjectControlWeb/db/pc_web.db", null, null)
            val stmt = conn!!.createStatement()

            val rsProjectUser = stmt.executeQuery("select project_id from project_user where user_id = '$userId'")
            val listProjectId = mutableListOf<Int>()
            while (rsProjectUser.next()) {
                listProjectId.add(rsProjectUser.getInt(1))
            }

            rsProjectUser.close()

            if (listProjectId.isEmpty()) {
                return emptyList()
            }

            val sb = StringBuilder("select * from project where _id in ")
            listProjectId.forEach {
                sb.append(" ('").append(userId).append("'), ")
            }
//            sb.deleteCharAt(sb.lastIndex)

            println(sb.toString())

            val rsProject = stmt.executeQuery(sb.toString())
            val list = mutableListOf<ProjectBean>()
            while (rsProject.next()) {
                val projectBean = ProjectBean()
                projectBean.id = rsProjectUser.getInt(1)
                projectBean.name = rsProjectUser.getString(2)
                list.add(projectBean)
            }

            rsProject.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                conn?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        return emptyList()
    }

}