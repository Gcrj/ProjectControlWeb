package com.gcrj.web.dao

import com.gcrj.web.bean.*
import com.gcrj.web.util.Constant
import com.gcrj.web.util.createStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.Color
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*

object UtilDao {

    init {
        Class.forName("org.sqlite.JDBC")
    }


    /**
     * 更新子项目、界面和界面相关进度
     */
    fun updateProgress(subProjectId: Int): Boolean {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
            var stmt = conn.createStatement()
            var rs = stmt.executeQuery("select activity._id, avg(activity_related.progress) from activity, activity_related where activity.sub_project_id = '$subProjectId' and activity_related.activity_id = activity._id group by activity._id")
            while (rs.next()) {
                val ps = conn.prepareStatement("update activity set progress = ? WHERE _id = ?")
                ps.setInt(1, rs.getFloat(2).toInt())
                ps.setInt(2, rs.getInt(1))
                ps.executeUpdate()
                ps.close()
            }

            stmt = conn.createStatement()
            rs = stmt.executeQuery("select avg(progress) from activity where activity.sub_project_id = '$subProjectId'")
            if (rs.next()) {
                val ps = conn.prepareStatement("update sub_project set progress = ${rs.getFloat(1).toInt()} WHERE _id = $subProjectId")
                ps.executeUpdate()
                ps.close()
            }

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

    fun queryAllInfo(userId: Int): List<ProjectBean> {
        val list = mutableListOf<ProjectBean>()
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
            val stmt = conn.createStatement()
//            val calendar = Calendar.getInstance()
//            calendar.set(Calendar.DAY_OF_WEEK, 2)
//            val monday = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
//            calendar.timeInMillis += 7 * 24 * 60 * 60 * 1000
//            val nextMonday = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
            val rs = stmt.executeQuery("select project._id, project.name, sub_project._id, sub_project.name, sub_project.progress, activity._id, activity.name, activity.progress, activity_related._id, activity_related.name, activity_related.progress " +
                    "from project, project_user, sub_project, activity, activity_related " +
                    "where project_user.user_id = '$userId' and project_user.project_id = sub_project.project_id and project._id = sub_project.project_id and activity.sub_project_id = sub_project._id " +
                    "and activity_related.activity_id = activity._id " +
//                    " and activity_related.time between '$monday' and '$nextMonday'" +
                    "order by project._id desc, sub_project._id desc, activity._id desc, activity_related._id desc")

            var lastProjectId = 0
            var lastSubProjectId = 0
            var lastActivityId = 0
            while (rs.next()) {
                val projectId = rs.getInt(1)
                val subProjectId = rs.getInt(3)
                val activityId = rs.getInt(6)
                if (projectId != lastProjectId) {
                    val project = ProjectBean()
                    project.id = projectId
                    project.name = rs.getString(2)
                    project.subProject = mutableListOf()
                    list.add(project)

                    val subProject = SubProjectBean()
                    subProject.id = subProjectId
                    subProject.name = rs.getString(4)
                    subProject.progress = rs.getInt(5)
                    subProject.activity = mutableListOf()
                    (project.subProject as MutableList).add(subProject)

                    val activity = ActivityBean()
                    activity.id = activityId
                    activity.name = rs.getString(7)
                    activity.progress = rs.getInt(8)
                    activity.activityRelated = mutableListOf()
                    (subProject.activity as MutableList).add(activity)

                    val activityRelated = ActivityRelatedBean()
                    activityRelated.id = rs.getInt(9)
                    activityRelated.name = rs.getString(10)
                    activityRelated.progress = rs.getInt(11)
                    (activity.activityRelated as MutableList).add(activityRelated)
                } else {
                    if (subProjectId != lastSubProjectId) {
                        val subProject = SubProjectBean()
                        subProject.id = subProjectId
                        subProject.name = rs.getString(4)
                        subProject.progress = rs.getInt(5)
                        subProject.activity = mutableListOf()
                        (list.last().subProject as MutableList).add(subProject)

                        val activity = ActivityBean()
                        activity.id = activityId
                        activity.name = rs.getString(7)
                        activity.progress = rs.getInt(8)
                        activity.activityRelated = mutableListOf()
                        (subProject.activity as MutableList).add(activity)

                        val activityRelated = ActivityRelatedBean()
                        activityRelated.id = rs.getInt(9)
                        activityRelated.name = rs.getString(10)
                        activityRelated.progress = rs.getInt(11)
                        (activity.activityRelated as MutableList).add(activityRelated)
                    } else {
                        if (activityId != lastActivityId) {
                            val activity = ActivityBean()
                            activity.id = activityId
                            activity.name = rs.getString(7)
                            activity.progress = rs.getInt(8)
                            activity.activityRelated = mutableListOf()
                            (list.last().subProject!!.last().activity as MutableList).add(activity)

                            val activityRelated = ActivityRelatedBean()
                            activityRelated.id = rs.getInt(9)
                            activityRelated.name = rs.getString(10)
                            activityRelated.progress = rs.getInt(11)
                            (activity.activityRelated as MutableList).add(activityRelated)
                        } else {
                            val activityRelated = ActivityRelatedBean()
                            activityRelated.id = rs.getInt(9)
                            activityRelated.name = rs.getString(10)
                            activityRelated.progress = rs.getInt(11)
                            (list.last().subProject!!.last().activity!!.last().activityRelated as MutableList).add(activityRelated)
                        }
                    }
                }

                lastProjectId = projectId
                lastSubProjectId = subProjectId
                lastActivityId = activityId
            }

            stmt.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                conn?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        return list
    }

}