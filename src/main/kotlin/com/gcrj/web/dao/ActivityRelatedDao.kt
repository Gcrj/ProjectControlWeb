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

object ActivityRelatedDao {

    init {
        Class.forName("org.sqlite.JDBC")
    }

    /**
     * 新增1个或多个界面相关
     */
    fun insert(subProjectId: Int, activityId: Int, list: List<String>): Boolean {
        if (list.isEmpty()) {
            return false
        }

        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
            val stmt = conn.createStatement()
            //查有这个子项目
            val rsSubProject = stmt.executeQuery("select * from sub_project where _id = $subProjectId")
            rsSubProject.use {
                if (!rsSubProject.next()) {
                    return false
                }
            }
            rsSubProject.close()
            //这个界面新增界面相关
            list.forEach {
                val sql = "insert into activity_related (activity_id, name, time) values('$activityId','$it', datetime(CURRENT_TIMESTAMP,'localtime'))"
                conn.prepareStatement(sql).executeUpdate()
            }
            conn.close()

            return UtilDao.updateProgress(subProjectId)
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
     * 更新子项目、界面和界面相关进度
     */
    fun update(subProjectId: Int, list: List<ActivityRelatedBean>): Boolean {
        if (list.isEmpty()) {
            return false
        }

        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
            val ps = conn.prepareStatement("update activity_related set progress = ?, time = datetime(CURRENT_TIMESTAMP,'localtime') WHERE _id = ?")
            list.forEach {
                ps.setInt(1, it.progress ?: 0)
                ps.setInt(2, it.id ?: 0)
                ps.executeUpdate()
            }
            ps.close()

            return UtilDao.updateProgress(subProjectId)
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

    fun query(activityId: Int): List<ActivityRelatedBean> {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("select * from activity_related where activity_id = $activityId order by _id desc")
            val list = mutableListOf<ActivityRelatedBean>()
            while (rs.next()) {
                val activityRelatedBean = ActivityRelatedBean()
                activityRelatedBean.id = rs.getInt(1)
                activityRelatedBean.activity_id = rs.getInt(2)
                activityRelatedBean.name = rs.getString(3)
                activityRelatedBean.progress = rs.getInt(4)
                activityRelatedBean.time = rs.getString(5)
                list.add(activityRelatedBean)
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

    fun getXls(userId: Int, os: OutputStream) {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
            val stmt = conn.createStatement()
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, 2)
            val monday = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
            calendar.timeInMillis += 7 * 24 * 60 * 60 * 1000
            val nextMonday = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
            val rs = stmt.executeQuery("select project._id, project.name, sub_project._id, sub_project.name, sub_project.progress, activity._id, activity.name, activity.progress, activity_related._id, activity_related.name, activity_related.progress " +
                    "from project, project_user, sub_project, activity, activity_related " +
                    "where project_user.user_id = '$userId' and project_user.project_id = sub_project.project_id and project._id = sub_project.project_id and activity.sub_project_id = sub_project._id " +
                    "and activity_related.activity_id = activity._id and  activity_related.time between '$monday' and '$nextMonday'order by project._id asc , sub_project._id asc , activity._id asc , activity_related._id asc")

            val list = mutableListOf<ProjectBean>()
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
            createXlsx(list, os)
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

    private fun createXlsx(list: MutableList<ProjectBean>, os: OutputStream) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("执行力")
        sheet.defaultColumnWidth = 40

        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 1))
        sheet.addMergedRegion(CellRangeAddress(0, 0, 2, 4))

        var row = sheet.createRow(0)
        var cell = row.createCell(0)
        cell.setCellValue("北京互动百科网络技术股份有限公司技术中心执行力")
        cell.cellStyle = workbook.createStyle(foregroundColor = Color(242, 242, 242), fontHeightInPoints = 10, bold = true)

        cell = row.createCell(2)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, 2)
        val monday = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        calendar.timeInMillis += 4 * 24 * 60 * 60 * 1000
        val friday = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        cell.setCellValue("日期：$monday～$friday")
        cell.cellStyle = workbook.createStyle(foregroundColor = Color(242, 242, 242), fontName = "宋体", fontHeightInPoints = 10, bold = true)

        row = sheet.createRow(1)
        val row1Style = workbook.createStyle(foregroundColor = Color(150, 150, 150), fontHeightInPoints = 10, fontColor = Color(255, 255, 255), bold = true)
        cell = row.createCell(0)
        cell.setCellValue("重要工作（项目）")
        cell.cellStyle = row1Style
        cell = row.createCell(1)
        cell.setCellValue("计划完成时间")
        cell.cellStyle = row1Style
        cell = row.createCell(2)
        cell.setCellValue("实际完成时间")
        cell.cellStyle = row1Style
        cell = row.createCell(3)
        cell.setCellValue("任务目标")
        cell.cellStyle = row1Style
        cell = row.createCell(4)
        cell.setCellValue("完成情况")
        cell.cellStyle = row1Style

        var index = 2
        list.forEach { project ->
            project.subProject?.forEach { subProject ->
                row = sheet.createRow(index)
                cell = row.createCell(0)
                cell.setCellValue("${project.name}(${subProject.name})")
                cell.cellStyle = workbook.createStyle(fontHeightInPoints = 10)

                val completePercent = "完成度${subProject.progress}%"
                val richTextString = XSSFRichTextString()
                richTextString.append(completePercent)
                subProject.activity?.forEach { activity ->
                    richTextString.append("\r\n")
                    richTextString.append(activity.name)
                    richTextString.append(" 完成度")
                    richTextString.append(activity.progress?.toString())
                    richTextString.append("%")
                    activity.activityRelated?.forEach { activityRelated ->
                        richTextString.append("\r\n")
                        richTextString.append("    ——")
                        richTextString.append(activityRelated.name)
                        richTextString.append(" 完成度")
                        richTextString.append(activityRelated.progress?.toString())
                        richTextString.append("%")
                    }
                }
                val font = workbook.createFont()
                font.fontHeightInPoints = 10
                font.fontName = "微软雅黑"
                richTextString.applyFont(font)

                val boldFont = workbook.createFont()
                boldFont.fontHeightInPoints = 10
                boldFont.fontName = "微软雅黑"
                boldFont.bold = true
                richTextString.applyFont(0, completePercent.length, boldFont)

                cell = row.createCell(4)
                cell.setCellValue(richTextString)
                cell.cellStyle = workbook.createStyle(horizontalAlignment = HorizontalAlignment.LEFT, fontHeightInPoints = 10)

                index++
            }
        }


        workbook.write(os)
        workbook.close()
    }

}