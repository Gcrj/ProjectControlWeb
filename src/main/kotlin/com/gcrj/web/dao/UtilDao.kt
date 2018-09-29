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

}