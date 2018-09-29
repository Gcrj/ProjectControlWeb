package com.gcrj.web.servlet

import com.gcrj.web.bean.ProjectBean
import com.gcrj.web.dao.ProjectDao
import com.gcrj.web.dao.UserDao
import com.gcrj.web.util.output
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException

@WebServlet(name = "ProjectServlet", urlPatterns = ["/projectList"])
class ProjectServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        doGet(request, response)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserDao.tokenVerify<List<ProjectBean>>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            responseBean.result = ProjectDao.query(pair.second?.id ?: 0)
        }

        response.output(responseBean)
    }

}
