package com.gcrj.web.servlet

import com.gcrj.web.bean.ActivityRelatedBean
import com.gcrj.web.dao.ActivityDao
import com.gcrj.web.dao.ActivityRelatedDao
import com.gcrj.web.dao.UserDao
import com.gcrj.web.util.output
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.nio.charset.Charset

@WebServlet(name = "ActivityRelatedServlet", urlPatterns = ["/activityRelated", "/addActivityRelated", "/updateActivityRelated"])
class ActivityRelatedServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserDao.tokenVerify<Nothing>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            if (request.requestURI == "/updateActivityRelated") {
                val subProjectId = request.getParameter("subProjectId")
                val activityRelated = request.getParameter("activityRelated")
                if (subProjectId == null || activityRelated == null) {
                    responseBean.status = 0
                    responseBean.msg = "参数有误"
                } else {
                    val list: List<ActivityRelatedBean>?
                    try {
                        list = Gson().fromJson<List<ActivityRelatedBean>>(activityRelated, object : TypeToken<List<ActivityRelatedBean>>() {}.type)
                        if (!ActivityRelatedDao.update(subProjectId.toIntOrNull() ?: 0, list)) {
                            responseBean.status == 0
                            responseBean.msg == "插入失败"
                        }
                    } catch (e: JsonParseException) {
                        responseBean.status = 0
                        responseBean.msg = "参数有误"
                    }
                }
            } else {
                val subProjectId = request.getParameter("subProjectId")
                val activityId = request.getParameter("activityId")
                val originActivityRelatedName = request.getParameter("activityRelatedName")
                if (subProjectId == null || activityId == null || originActivityRelatedName == null || originActivityRelatedName.split(",").none { it.trim() != "" }) {
                    responseBean.status = 0
                    responseBean.msg = "参数有误"
                } else {
                    val activityRelatedName = String(originActivityRelatedName.toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))
                    if (!ActivityRelatedDao.insert(subProjectId.toIntOrNull() ?: 0, activityId.toIntOrNull()
                                    ?: 0, activityRelatedName.split(",").filter { it.trim() != "" })) {
                        responseBean.status == 0
                        responseBean.msg == "插入失败"
                    }
                }
            }
        }

        response.output(responseBean)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserDao.tokenVerify<List<ActivityRelatedBean>>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            val activityId = request.getParameter("activityId")
            if (activityId == null) {
                responseBean.status = 0
                responseBean.msg = "参数有误"
            } else {
                responseBean.result = ActivityRelatedDao.query(activityId.toIntOrNull() ?: 0)
            }
        }

        response.output(responseBean)
    }

}
