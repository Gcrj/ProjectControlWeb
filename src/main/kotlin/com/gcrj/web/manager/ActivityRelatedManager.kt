package com.gcrj.web.manager

import com.gcrj.web.bean.ActivityBean
import com.gcrj.web.bean.ActivityRelatedBean
import com.gcrj.web.util.Constant
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

object ActivityRelatedManager {

    init {
        Class.forName("org.sqlite.JDBC")
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
            var ps = conn.prepareStatement("update activity_related set progress = ? WHERE _id = ?")
            list.forEach {
                ps.setInt(1, it.progress ?: 0)
                ps.setInt(2, it.id ?: 0)
                ps.executeUpdate()
            }
            ps.close()

            ps = conn.prepareStatement("update activity set progress = ? WHERE _id = ?")
            ps.setInt(1, (list.sumBy { it.progress ?: 0 }.toFloat() / list.size).toInt())
            ps.setInt(2, list[0].activity_id ?: 0)
            ps.executeUpdate()
            ps.close()

            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("select progress from activity where sub_project_id = $subProjectId")
            var totalProgress = 0
            var count = 0
            while (rs.next()) {
                totalProgress += rs.getInt(1)
                count++
            }

            stmt.close()

            ps = conn.prepareStatement("update sub_project set progress = ? WHERE _id = $subProjectId")
            ps.setInt(1, if (totalProgress == 0) 0 else (totalProgress.toFloat() / count).toInt())
            ps.executeUpdate()
            ps.close()
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