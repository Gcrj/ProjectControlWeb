package com.gcrj.web.util

import com.gcrj.web.bean.ProjectBean
import com.gcrj.web.bean.ResponseBean
import com.gcrj.web.bean.XlsProjectBean
import com.google.gson.Gson
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.Color
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletResponse

fun HttpServletResponse.output(responseBean: ResponseBean<*>) {
    this.contentType = "application/json;charset=utf-8"
    val out = this.writer
    out.println(Gson().toJson(responseBean))
    out.flush()
    out.close()
}

fun XSSFWorkbook.createStyle(horizontalAlignment: HorizontalAlignment? = HorizontalAlignment.CENTER, verticalAlignment: VerticalAlignment? = VerticalAlignment.CENTER, foregroundColor: Color? = null,
                             fontName: String = "微软雅黑", fontHeightInPoints: Short? = null, bold: Boolean = false, fontColor: Color? = null): XSSFCellStyle? {
    val style = createCellStyle()
    style.setBorderLeft(BorderStyle.THIN)
    style.setBorderTop(BorderStyle.THIN)
    style.setBorderRight(BorderStyle.THIN)
    style.setBorderBottom(BorderStyle.THIN)
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

fun createXls(list: List<XlsProjectBean>, os: OutputStream) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("执行力")
    sheet.defaultColumnWidth = 40

    sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 1))
    sheet.addMergedRegion(CellRangeAddress(0, 0, 2, 4))

    var row = sheet.createRow(0)
    var cell = row.createCell(0)
    cell.setCellValue("北京互动百科网络技术股份有限公司技术中心执行力")
    cell.cellStyle = workbook.createStyle(foregroundColor = Color(242, 242, 242), fontHeightInPoints = 10, bold = true)

    cell = row.createCell(2)
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, 2)
    val monday = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
    calendar.timeInMillis += 4 * 24 * 60 * 60 * 1000
    val friday = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
    cell.setCellValue("日期：$monday～$friday")
    cell.cellStyle = workbook.createStyle(foregroundColor = Color(242, 242, 242), fontName = "宋体", fontHeightInPoints = 10, bold = true)

    row = sheet.createRow(1)
    val row1Style = workbook.createStyle(foregroundColor = Color(150, 150, 150), fontHeightInPoints = 10, fontColor = Color(255, 255, 255), bold = true)
    cell = row.createCell(0)
    cell.setCellValue("重要工作（项目）")
    cell.cellStyle = row1Style
    cell = row.createCell(1)
    cell.setCellValue("计划完成时间")
    cell.cellStyle = row1Style
    cell = row.createCell(2)
    cell.setCellValue("实际完成时间")
    cell.cellStyle = row1Style
    cell = row.createCell(3)
    cell.setCellValue("任务目标")
    cell.cellStyle = row1Style
    cell = row.createCell(4)
    cell.setCellValue("完成情况")
    cell.cellStyle = row1Style

    var index = 2
    list.forEach { project ->
        when (project.type) {
            XlsProjectBean.TYPE_PROJECT -> {
                project.subProject?.forEach { subProject ->
                    row = sheet.createRow(index)
                    cell = row.createCell(0)
                    cell.setCellValue("${project.name}(${subProject.name})")
                    cell.cellStyle = workbook.createStyle(foregroundColor = Color(221, 235, 247), fontHeightInPoints = 10)
                    cell = row.createCell(1)
                    cell.cellStyle = workbook.createStyle(fontHeightInPoints = 10)
                    cell.setCellValue(subProject.deadline)
                    cell = row.createCell(2)
                    cell.cellStyle = workbook.createStyle()
                    cell = row.createCell(3)
                    cell.setCellValue("发版")
                    cell.cellStyle = workbook.createStyle(fontHeightInPoints = 10)

                    val completePercent = "完成度${subProject.progress}%"
                    val richTextString = XSSFRichTextString()
                    richTextString.append(completePercent)
                    subProject.activity?.forEach { activity ->
                        richTextString.append("\r\n")
                        richTextString.append(activity.name)
                        richTextString.append(" 完成度")
                        richTextString.append(activity.progress?.toString())
                        richTextString.append("%")
                        activity.activityRelated?.forEach { activityRelated ->
                            richTextString.append("\r\n")
                            richTextString.append("    ——")
                            richTextString.append(activityRelated.name)
                            richTextString.append(" 完成度")
                            richTextString.append(activityRelated.progress?.toString())
                            richTextString.append("%")
                        }
                    }
                    val font = workbook.createFont()
                    font.fontHeightInPoints = 10
                    font.fontName = "微软雅黑"
                    richTextString.applyFont(font)

                    val boldFont = workbook.createFont()
                    boldFont.fontHeightInPoints = 10
                    boldFont.fontName = "微软雅黑"
                    boldFont.bold = true
                    richTextString.applyFont(0, completePercent.length, boldFont)

                    cell = row.createCell(4)
                    cell.setCellValue(richTextString)
                    cell.cellStyle = workbook.createStyle(horizontalAlignment = HorizontalAlignment.LEFT, fontHeightInPoints = 10)

                    index++
                }
            }
            XlsProjectBean.TYPE_CUSTOM -> {
                row = sheet.createRow(index)
                cell = row.createCell(0)
                cell.setCellValue(project.title)
                cell.cellStyle = workbook.createStyle(foregroundColor = Color(221, 235, 247), fontHeightInPoints = 10)
                val emptyStyle = workbook.createStyle(fontHeightInPoints = 10)
                cell = row.createCell(1)
                cell.cellStyle = emptyStyle
                cell = row.createCell(2)
                cell.cellStyle = emptyStyle
                cell = row.createCell(3)
                cell.cellStyle = emptyStyle

                cell = row.createCell(4)
                cell.setCellValue(project.content)
                cell.cellStyle = workbook.createStyle(horizontalAlignment = HorizontalAlignment.LEFT, fontHeightInPoints = 10)

                index++
            }
        }
    }


    workbook.write(os)
    workbook.close()
}