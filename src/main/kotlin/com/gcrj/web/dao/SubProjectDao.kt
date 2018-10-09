package com.gcrj.web.dao

import com.gcrj.web.bean.SubProjectBean
import com.gcrj.web.util.Constant
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object SubProjectDao {

    init {
        Class.forName("org.sqlite.JDBC")
    }

    /**
     * 增加子项目
     */
    fun insert(projectId: Int, name: String, deadline: String, userId: Int): Boolean {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
            val stmt = conn.createStatement()
            val rsProject = stmt.executeQuery("select * from project, project_user where project_id = $projectId and project_user.user_id=$userId")
            rsProject.use {
                if (!rsProject.next()) {
                    return false
                }
            }
            rsProject.close()

            val sql = "insert into sub_project (project_id, name, deadline) values('$projectId', '$name', '$deadline')"
            conn.prepareStatement(sql).executeUpdate()
            conn.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                conn?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        return false
    }

    /**
     * 用户相关的子项目
     */
    fun queryByUser(userId: Int): List<SubProjectBean> {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("select sub_project.* from sub_project, project_user" +
                    " where project_user.user_id = $userId and project_user.project_id = sub_project.project_id order by sub_project._id desc")
            val list = mutableListOf<SubProjectBean>()
            while (rs.next()) {
                val subProjectBean = SubProjectBean()
                subProjectBean.id = rs.getInt(1)
                subProjectBean.project_id = rs.getInt(2)
                subProjectBean.name = rs.getString(3)
                subProjectBean.progress = rs.getInt(4)
                list.add(subProjectBean)
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

    /**
     * 项目下的子项目
     */
    fun queryByProject(projectId: Int): List<SubProjectBean> {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("select * from sub_project where project_id = $projectId order by _id desc")
            val list = mutableListOf<SubProjectBean>()
            while (rs.next()) {
                val subProjectBean = SubProjectBean()
                subProjectBean.id = rs.getInt(1)
                subProjectBean.project_id = rs.getInt(2)
                subProjectBean.name = rs.getString(3)
                subProjectBean.progress = rs.getInt(4)
                list.add(subProjectBean)
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