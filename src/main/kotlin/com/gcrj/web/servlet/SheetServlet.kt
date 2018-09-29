package com.gcrj.web.servlet

import com.gcrj.web.dao.ActivityRelatedDao
import com.gcrj.web.dao.UserDao
import com.gcrj.web.util.output
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "SheetServlet", urlPatterns = ["/sheet"])
class SheetServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserDao.tokenVerify<Nothing>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            response.contentType = "application/octet-stream"
            val fileName = URLEncoder.encode("Tech3-周报-${pair.second?.username}_${SimpleDateFormat("yyMMdd").format(Date())}.xlsx", "utf-8")
            response.addHeader("Content-Disposition", "attachment;filename=$fileName")
            val out = response.outputStream
            ActivityRelatedDao.getXls(pair.second?.id ?: 0, out)
            out.flush()
            out.close()
        } else {
            response.output(responseBean)
        }
    }

}