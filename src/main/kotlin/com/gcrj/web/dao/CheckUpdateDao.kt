package com.gcrj.web.dao

import com.gcrj.web.bean.CheckUpdateBean
import com.gcrj.web.util.Constant
import java.net.InetAddress
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object CheckUpdateDao {

    init {
        Class.forName("org.sqlite.JDBC")
    }

    fun query(packageName: String): CheckUpdateBean? {
        var checkUpdateBean: CheckUpdateBean? = null
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.APK_DB_PATH, null, null)
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("select * from apk where packageName = '$packageName'")
            if (rs.next()) {
                checkUpdateBean = CheckUpdateBean()
                checkUpdateBean.id = rs.getInt(1)
                checkUpdateBean.version_name = rs.getString(2)
                checkUpdateBean.version_code = rs.getInt(3)
                checkUpdateBean.url = "http://${InetAddress.getLocalHost().hostAddress}:${Constant.PORT}/apk?packageName=$packageName"
                checkUpdateBean.message = rs.getString(4)
                checkUpdateBean.force_update = rs.getBoolean(5)
            }

            rs.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                conn?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        return checkUpdateBean
    }

}