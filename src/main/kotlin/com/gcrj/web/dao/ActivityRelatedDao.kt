package com.gcrj.web.dao

import com.gcrj.web.bean.*
import com.gcrj.web.util.Constant
import com.gcrj.web.util.createStyle
import com.gcrj.web.util.createXls
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.Color
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
                val sql = "insert into activity_related (activity_id, name) values('$activityId','$it')"
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
            val ps = conn.prepareStatement("update activity_related set progress = ? WHERE _id = ?")
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
        createXls(UtilDao.queryAllInfo(userId), os)
    }

}