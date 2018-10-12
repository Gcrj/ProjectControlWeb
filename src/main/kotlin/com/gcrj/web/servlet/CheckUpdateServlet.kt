package com.gcrj.web.servlet

import com.gcrj.web.bean.CheckUpdateBean
import com.gcrj.web.bean.ResponseBean
import com.gcrj.web.dao.CheckUpdateDao
import com.gcrj.web.util.output
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException

@WebServlet(name = "CheckUpdateServlet", urlPatterns = ["/checkUpdate"])
class CheckUpdateServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val packageName = request.getParameter("packageName")
        val versionCode = request.getParameter("versionCode")?.toIntOrNull()

        val responseBean = ResponseBean<CheckUpdateBean>()
        if (packageName == null || versionCode == null) {
            responseBean.status = 0
            responseBean.msg = "参数有误"
        } else {
            val checkUpdateBean = CheckUpdateDao.query(packageName)
            if (checkUpdateBean == null) {
                responseBean.status = 0
                responseBean.msg = "用户名密码错误"
            } else {
                checkUpdateBean.hasUpdate = (checkUpdateBean.version_code ?: 0) > versionCode

                responseBean.status = 1
                responseBean.result = checkUpdateBean
            }
        }

        response.output(responseBean)
    }

}
