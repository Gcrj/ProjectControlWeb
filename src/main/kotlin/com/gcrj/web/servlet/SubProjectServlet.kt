package com.gcrj.web.servlet

import com.gcrj.web.bean.ProjectBean
import com.gcrj.web.bean.SubProjectBean
import com.gcrj.web.manager.ProjectManager
import com.gcrj.web.manager.SubProjectManager
import com.gcrj.web.manager.UserManager
import com.gcrj.web.util.output
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.nio.charset.Charset

@WebServlet(name = "SubProjectServlet", urlPatterns = ["/subProject"])
class SubProjectServlet : HttpServlet() {

    /**
     * 增
     */
    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserManager.tokenVerify<Nothing>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            val originName = request.getParameter("name")
            val projectId = request.getParameter("projectId")
            if (originName == null || projectId == null) {
                responseBean.status = 0
                responseBean.msg = "参数有误"
            } else {
                val name = String(originName.toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))
                if (!SubProjectManager.insert(projectId.toIntOrNull() ?: 0, name, pair.second?.id ?: 0)) {
                    responseBean.status == 0
                    responseBean.msg == "插入失败"
                }
            }

        }

        response.output(responseBean)
    }

    /**
     * 查
     */
    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val pair = UserManager.tokenVerify<List<SubProjectBean>>(request)
        val responseBean = pair.first
        if (responseBean.status == 1) {
            responseBean.result = SubProjectManager.query(pair.second?.id ?: 0)
        }

        response.output(responseBean)
    }

    /**
     * 改
     */
    @Throws(ServletException::class, IOException::class)
    override fun doPut(req: HttpServletRequest?, resp: HttpServletResponse?) {
        super.doPut(req, resp)
    }

    /**
     * 删
     */
    @Throws(ServletException::class, IOException::class)
    override fun doDelete(req: HttpServletRequest?, resp: HttpServletResponse?) {
        super.doDelete(req, resp)
    }

}
