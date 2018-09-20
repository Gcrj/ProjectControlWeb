package com.gcrj.web.util

import com.gcrj.web.bean.ResponseBean
import com.google.gson.Gson
import javax.servlet.http.HttpServletResponse

fun HttpServletResponse.output(responseBean: ResponseBean<*>) {
    this.contentType = "text/html;charset=utf-8"
    val out = this.writer
    out.println(Gson().toJson(responseBean))
    out.flush()
    out.close()
}