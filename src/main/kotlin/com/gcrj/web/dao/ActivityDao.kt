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
            val rs = stmt.executeQuery("select * from activity, activity_related where activity.sub_project_id = '$subProjectId' and activity._id = activity_related.activity_id order by activity._id asc")
            val list = mutableListOf<ActivityBean>()
            var lastActivityId = -1
            while (rs.next()) {
                val id = rs.getInt(1)
                if (id == lastActivityId) {
                    val activityRelatedBean = ActivityRelatedBean()
                    activityRelatedBean.id = rs.getInt(5)
                    activityRelatedBean.activity_id = rs.getInt(6)
                    activityRelatedBean.name = rs.getString(7)
                    activityRelatedBean.progress = rs.getInt(8)
                    (list.last().activityRelated as MutableList).add(activityRelatedBean)
                } else {
                    val activityBean = ActivityBean()
                    activityBean.id = id
                    activityBean.sub_project_id = rs.getInt(2)
                    activityBean.name = rs.getString(3)
                    activityBean.progress = rs.getInt(4)
                    activityBean.activityRelated = mutableListOf()
                    val activityRelatedBean = ActivityRelatedBean()
                    activityRelatedBean.id = rs.getInt(5)
                    activityRelatedBean.activity_id = rs.getInt(6)
                    activityRelatedBean.name = rs.getString(7)
                    activityRelatedBean.progress = rs.getInt(8)
                    (activityBean.activityRelated as MutableList).add(activityRelatedBean)
                    list.add(activityBean)
                }

                lastActivityId = id
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