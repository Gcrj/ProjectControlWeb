package com.gcrj.web.dao

import com.gcrj.web.bean.ResponseBean
import com.gcrj.web.bean.UserBean
import com.gcrj.web.util.Constant
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import javax.servlet.http.HttpServletRequest

object UserDao {

    init {
        Class.forName("org.sqlite.JDBC")
    }

    fun query(username: String, password: String): UserBean? {
        var userBean: UserBean? = null
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("select * from user where username = '$username' and password = '$password'")
            if (rs.next()) {
                userBean = UserBean()
                userBean.id = rs.getInt(1)
                userBean.username = rs.getString(2)
//                userBean.password = rs.getString(3)
                userBean.token = rs.getString(4)
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

        return userBean
    }

    fun tokenVerify(request: HttpServletRequest): UserBean? {
        val token = request.getHeader("token")
        if (token == null || token == "") {
            return null
        } else {
            var userBean: UserBean? = null
            var conn: Connection? = null
            try {
                conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
                val stmt = conn.createStatement()

                // 执行SQL语句
                val rs = stmt.executeQuery("select * from user where token = '$token'")
                if (rs.next()) {
                    userBean = UserBean()
                    userBean.id = rs.getInt(1)
                    userBean.username = rs.getString(2)
                    userBean.token = rs.getString(3)
                    userBean.password = rs.getString(4)
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

            return userBean
        }
    }

    fun <T> tokenVerify(request: HttpServletRequest): Pair<ResponseBean<T>, UserBean?> {
        val responseBean = ResponseBean<T>()
        val token = request.getHeader("token")
        if (token == null || token == "") {
            responseBean.status = 0
            responseBean.msg = "token错误"
            return responseBean to null
        }

        var userBean: UserBean? = null
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(Constant.DB_PATH, null, null)
            val stmt = conn.createStatement()

            // 执行SQL语句
            val rs = stmt.executeQuery("select * from user where token = '$token'")
            if (rs.next()) {
                userBean = UserBean()
                userBean.id = rs.getInt(1)
                userBean.username = rs.getString(2)
                userBean.token = rs.getString(3)
                userBean.password = rs.getString(4)

                responseBean.status = 1
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

        return responseBean to userBean
    }

}