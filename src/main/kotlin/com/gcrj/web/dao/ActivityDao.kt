package com.gcrj.web.dao

import com.gcrj.web.bean.ActivityBean
import com.gcrj.web.bean.ActivityRelatedBean
import com.gcrj.web.util.Constant
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

object ActivityDao {

    init {
        Class.forName("org.sqlite.JDBC")
    }

    /**
     * 增加子项目页面和页面相关
     */
    fun insert(userId: Int, subProjectId: Int, activityName: String, list: List<String>): Boolean {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.WEB_DB_PATH, null, null)
            val stmt = conn.createStatement()
            //查有这个子项目
            val rsSubProject = stmt.executeQuery("select * from sub_project where _id = $subProjectId")
            rsSubProject.use {
                if (!rsSubProject.next()) {
                    return false
                }
            }
            rsSubProject.close()

            //这个子项目新增界面
            var sql = "insert into activity (sub_project_id, name) values('$subProjectId','$activityName')"
            val st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            st.executeUpdate()
            val rs = st.generatedKeys
            if (rs.next()) {
                val newActivityId = rs.getInt(1)
                //这个界面新增界面相关
                list.forEach {
                    sql = "insert into activity_related (activity_id, name) values('$newActivityId','$it')"
                    conn.prepareStatement(sql).executeUpdate()
                }
                conn.close()
                return UtilDao.updateProgress(subProjectId)
            }

            conn.close()
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
     * 子项目相关的界面
     */
    fun query(subProjectId: Int): List<ActivityBean> {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.WEB_DB_PATH, null, null)
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("select * from activity where activity.sub_project_id = '$subProjectId' order by _id asc")
            val list = mutableListOf<ActivityBean>()
            while (rs.next()) {
                val activityBean = ActivityBean()
                activityBean.id = rs.getInt(1)
                activityBean.sub_project_id = rs.getInt(2)
                activityBean.name = rs.getString(3)
                activityBean.progress = rs.getInt(4)
                list.add(activityBean)
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

    fun update(id: Int, name: String) {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.WEB_DB_PATH, null, null)
            val ps = conn.prepareStatement("update activity set name = '$name' WHERE _id = $id")
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

    fun delete(subProjectId: Int, activityId: Int) {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.WEB_DB_PATH, null, null)
            val st = conn.createStatement()
            st.executeUpdate("delete from activity_related where activity_id = $activityId")
            st.executeUpdate("delete from activity where _id = $activityId")
            st.close()
            UtilDao.updateProgress(subProjectId)
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