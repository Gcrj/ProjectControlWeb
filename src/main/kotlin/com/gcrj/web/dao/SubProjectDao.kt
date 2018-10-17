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
            conn = DriverManager.getConnection(Constant.WEB_DB_PATH, null, null)
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
            conn = DriverManager.getConnection(Constant.WEB_DB_PATH, null, null)
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("select sub_project.*, project.name from sub_project, project_user, project" +
                    " where project_user.user_id = $userId and project_user.project_id = sub_project.project_id and project._id = sub_project.project_id order by sub_project._id desc")
            val list = mutableListOf<SubProjectBean>()
            while (rs.next()) {
                val subProjectBean = SubProjectBean()
                subProjectBean.id = rs.getInt(1)
                subProjectBean.project_id = rs.getInt(2)
                subProjectBean.name = rs.getString(3)
                subProjectBean.progress = rs.getInt(4)
                subProjectBean.deadline = rs.getString(5)
                subProjectBean.completionTime = rs.getString(6)
                subProjectBean.versionName = rs.getString(7)
                subProjectBean.projectName = rs.getString(8)
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
            conn = DriverManager.getConnection(Constant.WEB_DB_PATH, null, null)
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("select sub_project.*, project.name from sub_project, project where project_id = '$projectId'and project._id = '$projectId' order by sub_project._id desc")
            val list = mutableListOf<SubProjectBean>()
            while (rs.next()) {
                val subProjectBean = SubProjectBean()
                subProjectBean.id = rs.getInt(1)
                subProjectBean.project_id = rs.getInt(2)
                subProjectBean.name = rs.getString(3)
                subProjectBean.progress = rs.getInt(4)
                subProjectBean.deadline = rs.getString(5)
                subProjectBean.completionTime = rs.getString(6)
                subProjectBean.versionName = rs.getString(7)
                subProjectBean.projectName = rs.getString(8)
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

    fun update(id: Int, name: String?, deadline: String?, completionTime: String?, versionName: String?) {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.WEB_DB_PATH, null, null)
            val sb = StringBuilder()
            if (name != null) {
                sb.append(" name = '").append(name).append("'")
            }

            if (deadline != null) {
                if (sb.isNotEmpty()) {
                    sb.append(" ,")
                }

                sb.append(" deadline = '").append(deadline).append("'")
            }

            if (completionTime != null) {
                if (sb.isNotEmpty()) {
                    sb.append(" ,")
                }

                sb.append(" completion_time = '").append(completionTime).append("'")
            }

            if (versionName != null) {
                if (sb.isNotEmpty()) {
                    sb.append(" ,")
                }

                sb.append(" version_name = '").append(versionName).append("'")
            }

            val ps = conn.prepareStatement("update sub_project set $sb WHERE _id = $id")
            ps.executeUpdate()
            ps.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                conn?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    fun delete(id: Int) {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.WEB_DB_PATH, null, null)
            val st = conn.createStatement()
            st.executeUpdate("delete from sub_project where _id = $id")

            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("select _id from activity where sub_project_id = $id")
            while (rs.next()) {
                st.executeUpdate("delete from activity_related where activity_id = ${rs.getInt(1)}")
            }

            rs.close()
            stmt.close()

            st.executeUpdate("delete from activity where sub_project_id = $id")
            st.close()
            UtilDao.updateProgress(id)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                conn?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

}