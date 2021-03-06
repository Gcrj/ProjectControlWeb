package com.gcrj.web.servlet

import com.gcrj.web.bean.ActivityRelatedBean
import com.gcrj.web.bean.ProjectBean
import com.gcrj.web.bean.SheetListBean
import com.gcrj.web.bean.XlsProjectBean
import com.gcrj.web.dao.ActivityRelatedDao
import com.gcrj.web.dao.UserDao
import com.gcrj.web.dao.UtilDao
import com.gcrj.web.util.Constant
import com.gcrj.web.util.createXls
import com.gcrj.web.util.output
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "SheetServlet", urlPatterns = ["/sheet", "/sheetInfo"])
class SheetServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        when (request.requestURI) {
            "/sheet" -> {
            }
            "/sheetInfo" -> {
                //获取列表
                //今日是否已上传
                val pair = UserDao.tokenVerify<SheetListBean>(request)
                val responseBean = pair.first
                if (responseBean.status == 1) {
                    val bean = SheetListBean()
                    bean.list = UtilDao.queryAllInfo(pair.second?.id ?: 0)
                    bean.hasSubmitted = File(Constant.XLS_PATH, "Tech3-周报-${pair.second?.username}_${SimpleDateFormat("yyMMdd").format(Date())}.xlsx").exists()
                    responseBean.result = bean
                }

                response.output(responseBean)
            }
            else -> {

            }
        }
    }

    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        when (request.requestURI) {
            "/sheet" -> {
                //预览
                val pair = UserDao.tokenVerify<Nothing>(request)
                val responseBean = pair.first
                if (responseBean.status == 1) {
                    val inputStream = request.inputStream
                    val sb = StringBuilder()
                    try {
                        val b = ByteArray(1024)
                        var read = inputStream.read(b)
                        while (read != -1) {
                            sb.append(String(b, 0, read))
                            read = inputStream.read(b)
                        }
                    } catch (e: Exception) {
                        responseBean.status = 0
                        responseBean.msg = e.message ?: "未知错误"
                        response.output(responseBean)
                    } finally {
                        inputStream.close()
                    }

                    if (sb.isEmpty()) {
                        responseBean.status = 0
                        responseBean.msg = "参数有误"
                        response.output(responseBean)
                    } else {
                        try {
                            val list = Gson().fromJson<List<XlsProjectBean>>(sb.toString(), object : TypeToken<List<XlsProjectBean>>() {}.type)
                            response.contentType = "application/octet-stream"
                            val fileName = URLEncoder.encode("Tech3-周报-${pair.second?.username}_${SimpleDateFormat("yyMMdd").format(Date())}.xlsx", "utf-8")
                            response.addHeader("Content-Disposition", "attachment;filename=$fileName")
                            val out = response.outputStream
                            createXls(list, out)
                            out.flush()
                            out.close()
                        } catch (e: Exception) {
                            responseBean.status = 0
                            responseBean.msg = e.message ?: "未知错误"
                            response.output(responseBean)
                        }
                    }
                } else {
                    response.output(responseBean)
                }
            }
            "/sheetInfo" -> {
                //提交
                val pair = UserDao.tokenVerify<Nothing>(request)
                val responseBean = pair.first
                if (responseBean.status == 1) {
                    val inputStream = request.inputStream
                    val sb = StringBuilder()
                    try {
                        val b = ByteArray(1024)
                        var read = inputStream.read(b)
                        while (read != -1) {
                            sb.append(String(b, 0, read))
                            read = inputStream.read(b)
                        }
                    } catch (e: Exception) {
                        responseBean.status = 0
                        responseBean.msg = e.message ?: "未知错误"
                        response.output(responseBean)
                    } finally {
                        inputStream.close()
                    }

                    if (sb.isEmpty()) {
                        responseBean.status = 0
                        responseBean.msg = "参数有误"
                        response.output(responseBean)
                    } else {
                        try {
                            val list = Gson().fromJson<List<XlsProjectBean>>(sb.toString(), object : TypeToken<List<XlsProjectBean>>() {}.type)
                            val dir = File(Constant.XLS_PATH)
                            if (!dir.exists()) {
                                if (!dir.mkdirs()) {
                                    responseBean.status = 0
                                    responseBean.msg = "IO异常"
                                    response.output(responseBean)
                                    return
                                }
                            }

                            val file = File(dir, "Tech3-周报-${pair.second?.username}_${SimpleDateFormat("yyMMdd").format(Date())}.xlsx")
                            val out = FileOutputStream(file)
                            createXls(list, out)
                            out.flush()
                            out.close()

                            response.output(responseBean)
                        } catch (e: Exception) {
                            responseBean.status = 0
                            responseBean.msg = e.message ?: "未知错误"
                            response.output(responseBean)
                        }
                    }
                } else {
                    response.output(responseBean)
                }
            }
            else -> {

            }
        }
    }

}