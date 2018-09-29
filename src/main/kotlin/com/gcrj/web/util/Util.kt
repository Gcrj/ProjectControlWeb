package com.gcrj.web.util

import com.gcrj.web.bean.ResponseBean
import com.google.gson.Gson
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.Color
import javax.servlet.http.HttpServletResponse

fun HttpServletResponse.output(responseBean: ResponseBean<*>) {
    this.contentType = "text/html;charset=utf-8"
    val out = this.writer
    out.println(Gson().toJson(responseBean))
    out.flush()
    out.close()
}

fun XSSFWorkbook.createStyle(horizontalAlignment: HorizontalAlignment? = HorizontalAlignment.CENTER, verticalAlignment: VerticalAlignment? = VerticalAlignment.CENTER, foregroundColor: Color? = null,
                             fontName: String = "微软雅黑", fontHeightInPoints: Short? = null, bold: Boolean = false, fontColor: Color? = null): XSSFCellStyle? {
    val style = createCellStyle()
    style.setAlignment(horizontalAlignment)
    style.setVerticalAlignment(verticalAlignment)
    foregroundColor?.apply {
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        style.setFillForegroundColor(XSSFColor(this))
    }

    val font = createFont()
    font.fontName = fontName
    fontHeightInPoints?.apply {
        font.fontHeightInPoints = this
    }
    font.bold = bold
    fontColor?.apply {
        font.setColor(XSSFColor(this))
    }
    style.setFont(font)

    style.wrapText = true

    return style
}