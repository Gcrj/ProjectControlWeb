package com.gcrj.web.servlet

import com.gcrj.web.bean.ActivityBean
import com.gcrj.web.dao.ActivityDao
import com.gcrj.web.dao.UserDao
import com.gcrj.web.util.output
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.nio.charset.Charset

@WebServlet(name = "ActivityServlet", urlPatterns = ["/activity"])
class ActivityServlet : HttpServlet() {

    /**
     * 添加界面和界面相关
     */
    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserDao.tokenVerify<Nothing>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            val originActivityName = request.getParameter("activityName")
            val subProjectId = request.getParameter("subProjectId")
            val originActivityRelatedName = request.getParameter("activityRelatedName")
            if (originActivityName == null || subProjectId == null || originActivityRelatedName == null || originActivityRelatedName.split(",").none { it.trim() != "" }) {
                responseBean.status = 0
                responseBean.msg = "参数有误"
            } else {
                val activityName = String(originActivityName.toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))
                val activityRelatedName = String(originActivityRelatedName.toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))
                if (!ActivityDao.insert(pair.second?.id ?: 0, subProjectId.toIntOrNull()
                                ?: 0, activityName, activityRelatedName.split(",").filter { it.trim() != "" })) {
                    responseBean.status == 0
                    responseBean.msg == "插入失败"
                }
            }
        }

        response.output(responseBean)
    }

    /**
     * 查询界面和界面相关
     */
    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserDao.tokenVerify<List<ActivityBean>>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            val subProjectId = request.getParameter("subProjectId")
            if (subProjectId == null) {
                responseBean.status = 0
                responseBean.msg = "参数有误"
            } else {
                responseBean.result = ActivityDao.query(subProjectId.toIntOrNull() ?: 0)
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
            if (id == null || name == null) {
                responseBean.status = 0
                responseBean.msg = "参数有误"
            } else {
                ActivityDao.update(id, name)
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
            val subProjectId = request.getParameter("subProjectId")?.toIntOrNull()
            val activityId = request.getParameter("activityId")?.toIntOrNull()
            if (subProjectId == null || activityId == null) {
                responseBean.status = 0
                responseBean.msg = "参数有误"
            } else {
                ActivityDao.delete(subProjectId, activityId)
            }
        }

        response.output(responseBean)
    }

}
