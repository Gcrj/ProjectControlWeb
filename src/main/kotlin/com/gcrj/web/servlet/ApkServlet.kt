package com.gcrj.web.servlet

import com.gcrj.web.bean.CheckUpdateBean
import com.gcrj.web.bean.ResponseBean
import com.gcrj.web.bean.XlsProjectBean
import com.gcrj.web.dao.UserDao
import com.gcrj.web.util.Constant
import com.gcrj.web.util.createXls
import com.gcrj.web.util.output
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

@WebServlet(name = "ApkServlet", urlPatterns = ["/apk"])
class ApkServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val packageName = request.getParameter("packageName")
        val responseBean = ResponseBean<Nothing>()
        if (packageName == null) {
            responseBean.status = 0
            responseBean.msg = "参数有误"
            response.output(responseBean)
        } else {
            val file = File(Constant.APK_PATH, "$packageName.apk")
            if (file.exists()) {
                try {
                    response.contentType = "application/octet-stream"
                    response.addHeader("Content-Disposition", "attachment;filename=ProjectControl.apk")
                    response.addHeader("Content-Length", file.length().toString())

                    val out = response.outputStream
                    val fis = FileInputStream(file)
                    val byteArray = ByteArray(1024)
                    var read = fis.read(byteArray)
                    while (read != -1) {
                        out.write(byteArray, 0, read)
                        read = fis.read(byteArray)
                    }

                    out.flush()
                    out.close()
                } catch (e: Exception) {
                    responseBean.status = 0
                    responseBean.msg = e.message ?: "未知错误"
                    response.output(responseBean)
                }
            } else {
                responseBean.status = 0
                responseBean.msg = "文件不存在"
                response.output(responseBean)
            }
        }
    }

}
