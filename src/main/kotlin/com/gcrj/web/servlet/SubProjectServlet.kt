package com.gcrj.web.servlet

import com.gcrj.web.bean.SubProjectBean
import com.gcrj.web.dao.SubProjectDao
import com.gcrj.web.dao.UserDao
import com.gcrj.web.util.output
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.nio.charset.Charset

@WebServlet(name = "SubProjectServlet", urlPatterns = ["/subProjectByUser", "/subProjectByProject"])
class SubProjectServlet : HttpServlet() {

    /**
     * 增
     */
    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        if (request.requestURI == "/subProjectByUser") {
            val pair = UserDao.tokenVerify<Nothing>(request)
            val responseBean = pair.first
            if (responseBean.status == 1) {
                val originName = request.getParameter("name")
                val projectId = request.getParameter("projectId")
                val deadline = request.getParameter("deadline")
                if (originName == null || projectId == null || deadline == null) {
                    responseBean.status = 0
                    responseBean.msg = "参数有误"
                } else {
                    val name = String(originName.toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))
                    if (!SubProjectDao.insert(projectId.toIntOrNull() ?: 0, name, deadline, pair.second?.id ?: 0)) {
                        responseBean.status == 0
                        responseBean.msg == "插入失败"
                    }
                }
            }

            response.output(responseBean)
        }
    }

    /**
     * 查
     */
    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserDao.tokenVerify<List<SubProjectBean>>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            when (request.requestURI) {
                "/subProjectByUser" -> responseBean.result = SubProjectDao.queryByUser(pair.second?.id ?: 0)
                "/subProjectByProject" -> {
                    val projectId = request.getParameter("projectId")
                    if (projectId == null) {
                        responseBean.status = 0
                        responseBean.msg = "参数有误"
                    } else {
                        responseBean.result = SubProjectDao.queryByProject(projectId.toIntOrNull() ?: 0)
                    }
                }
                else -> {
                }
            }
        }

        response.output(responseBean)
    }

    /**
     * 改
     */
    @Throws(ServletException::class, IOException::class)
    override fun doPut(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserDao.tokenVerify<Nothing>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            val id = request.getParameter("id")?.toIntOrNull()
            val name = request.getParameter("name")
            val deadline = request.getParameter("deadline")
            val completionTime = request.getParameter("completionTime")
            val versionName = request.getParameter("versionName")
            if (id == null || (name == null && deadline == null && completionTime == null && versionName == null)) {
                responseBean.status = 0
                responseBean.msg = "参数有误"
            } else {
                SubProjectDao.update(id, name, deadline, completionTime, versionName)
            }
        }

        response.output(responseBean)
    }

    /**
     * 删
     */
    @Throws(ServletException::class, IOException::class)
    override fun doDelete(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserDao.tokenVerify<Nothing>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            val id = request.getParameter("id")?.toIntOrNull()
            if (id == null) {
                responseBean.status = 0
                responseBean.msg = "参数有误"
            } else {
                SubProjectDao.delete(id)
            }
        }

        response.output(responseBean)
    }

}
