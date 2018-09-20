package com.gcrj.web.servlet

import com.gcrj.web.bean.ActivityRelatedBean
import com.gcrj.web.manager.ActivityManager
import com.gcrj.web.manager.ActivityRelatedManager
import com.gcrj.web.manager.UserManager
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

@WebServlet(name = "ActivityRelatedServlet", urlPatterns = ["/activityRelated"])
class ActivityRelatedServlet : HttpServlet() {

    /**
     * 修改进度
     */
    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserManager.tokenVerify<Nothing>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            val subProjectId = request.getParameter("subProjectId")
            val activityRelated = request.getParameter("activityRelated")
            if (subProjectId == null || activityRelated == null) {
                responseBean.status = 0
                responseBean.msg = "参数有误"
            } else {
                val list: List<ActivityRelatedBean>?
                try {
                    list = Gson().fromJson<List<ActivityRelatedBean>>(activityRelated, object : TypeToken<List<ActivityRelatedBean>>() {}.type)
                    if (!ActivityRelatedManager.update(subProjectId.toIntOrNull() ?: 0, list)) {
                        responseBean.status == 0
                        responseBean.msg == "插入失败"
                    }
                } catch (e: JsonParseException) {
                    responseBean.status = 0
                    responseBean.msg = "参数有误"
                }
            }
        }

        response.output(responseBean)
    }

}
