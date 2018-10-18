package com.gcrj.web.servlet

import com.gcrj.web.bean.ResponseBean
import com.gcrj.web.bean.UserBean
import com.gcrj.web.dao.UserDao
import com.gcrj.web.util.Constant
import com.gcrj.web.util.output
import sun.misc.BASE64Decoder
import java.io.*
import java.nio.charset.Charset
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "AvatorServlet", urlPatterns = ["/login", "/avator", "/password"])
class AvatorServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        when (request.requestURI) {
            "/login" -> {
                val originUsername = request.getParameter("username")
                val password = request.getParameter("password")

                val responseBean = ResponseBean<UserBean>()
                if (originUsername == null || password == null) {
                    responseBean.status = 0
                    responseBean.msg = "用户名密码错误"
                } else {
                    val username = String(originUsername.toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))
                    val userBean = UserDao.query(username, password)
                    if (userBean == null) {
                        responseBean.status = 0
                        responseBean.msg = "用户名密码错误"
                    } else {
                        responseBean.status = 1
                        responseBean.result = userBean
                    }
                }

                response.output(responseBean)
            }
            "/avator" -> {
                val pair = UserDao.tokenVerify<String>(request)
                val responseBean = pair.first
                if (responseBean.status == 1) {
                    var inputStream: InputStream? = null
                    var outputStream: OutputStream? = null
                    try {
                        inputStream = request.inputStream
                        val sb = StringBuilder()
                        val b = ByteArray(1024)
                        var read = inputStream.read(b)
                        while (read != -1) {
                            sb.append(String(b, 0, read))
                            read = inputStream.read(b)
                        }

                        val decoder = BASE64Decoder()
                        val db = decoder.decodeBuffer(sb.toString())
                        (0 until db.size).forEach {
                            if (db[it] < 0) {
                                db[it] = (db[it] + 256).toByte()
                            }
                        }
                        val file = File(Constant.LOCAL_IMAGE_PATH, "${System.currentTimeMillis()}.jpg")
                        outputStream = FileOutputStream(file)
                        outputStream.write(db)
                        outputStream.flush()
                        responseBean.result = "${Constant.IMAGE_PATH}${file.name}"
                        UserDao.updateAvator(pair.second?.id ?: 0, "${Constant.IMAGE_PATH}${file.name}")
                    } catch (e: Exception) {
                        responseBean.status = 0
                        responseBean.msg = e.message ?: "未知错误"
                    } finally {
                        inputStream?.close()
                        outputStream?.close()
                    }
                }

                response.output(responseBean)
            }
            "/password" -> {
                val pair = UserDao.tokenVerify<Nothing>(request)
                val responseBean = pair.first
                if (responseBean.status == 1) {
                    val password = request.getParameter("password")
                    if (password == null) {
                        responseBean.status = 0
                        responseBean.msg = "参数有误"
                    } else {
                        UserDao.updatePassword(pair.second?.id ?: 0, password)
                    }
                }

                response.output(responseBean)
            }
        }
    }

}
