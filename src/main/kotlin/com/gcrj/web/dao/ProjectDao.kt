package com.gcrj.web.dao

import com.gcrj.web.bean.ProjectBean
import com.gcrj.web.bean.UserBean
import com.gcrj.web.util.Constant
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object ProjectDao {

    init {
        Class.forName("org.sqlite.JDBC")
    }

    /**
     * 用户相关的项目
     */
    fun query(userId: Int): List<ProjectBean> {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.WEB_DB_PATH, null, null)
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("select project._id, project.name, project.create_user, user.username from project, project_user, user where project_user.user_id = '$userId' and project._id = project_user.project_id and user._id = project.create_user order by project._id desc")
            val list = mutableListOf<ProjectBean>()
            while (rs.next()) {
                val projectBean = ProjectBean()
                projectBean.id = rs.getInt(1)
                projectBean.name = rs.getString(2)
                val userBean = UserBean()
                userBean.id = rs.getInt(3)
                userBean.username = rs.getString(4)
                projectBean.create_user = userBean
                list.add(projectBean)
            }

            rs.close()
            return list
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