package com.gcrj.web.servlet

import com.gcrj.web.bean.ResponseBean
import com.gcrj.web.bean.UserBean
import com.gcrj.web.dao.UserDao
import com.gcrj.web.util.output
import java.io.IOException
import java.nio.charset.Charset
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "LoginServlet", urlPatterns = ["/login"])
class LoginServlet : HttpServlet() {

    @Throws(javax.servlet.ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        val originUsername = request.getParameter("username")
        val password = request.getParameter("password")

        val responseBean = ResponseBean<UserBean>()
        if (originUsername == null || password == null) {
            responseBean.status = 0
            responseBean.msg = "用户名密码错误"
        } else {
            val username = String(originUsername.toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))
            val userBean = UserDao.query(username, password)
            if (userBean == null) {
                responseBean.status = 0
                responseBean.msg = "用户名密码错误"
            } else {
                responseBean.status = 1
                val bean = UserBean()
                responseBean.result = bean
                bean.id = userBean.id
                bean.username = userBean.username
                bean.token = userBean.token
            }
        }

        response.output(responseBean)
    }

}