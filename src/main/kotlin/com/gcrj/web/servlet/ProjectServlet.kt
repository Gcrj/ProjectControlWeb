package com.gcrj.web.servlet

import com.gcrj.web.bean.ProjectBean
import com.gcrj.web.bean.ResponseBean
import com.gcrj.web.bean.UserBean
import com.gcrj.web.manager.ProjectManager
import com.gcrj.web.manager.UserManager
import com.gcrj.web.util.Util
import com.google.gson.Gson
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.nio.charset.Charset

@WebServlet(name = "ProjectServlet", urlPatterns = ["/projectList"])
class ProjectServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        doGet(request, response)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val userBean = UserManager.tokenVerify(request)
        val responseBean = ResponseBean<List<ProjectBean>>()
        if (userBean == null) {
            responseBean.status = 0
            responseBean.msg = "token错误"
        } else {
            responseBean.status = 1
            responseBean.result = ProjectManager.query(userBean.id ?: 0)
        }

        response.contentType = "text/html;charset=utf-8"
        val out = response.writer
        out.println(Gson().toJson(responseBean))
        out.flush()
        out.close()
    }

}
