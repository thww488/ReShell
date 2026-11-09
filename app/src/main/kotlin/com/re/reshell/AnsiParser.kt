package com.re.reshell

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

object AnsiParser {
    // 匹配颜色代码 \u001B[31m 等
    private val ANSI_COLOR_REGEX = Regex("\u001B\\[([0-9;]*)m")
    // 匹配并清理移动光标、清屏等控制码 \u001B[2J, \u001B[H, \u001B[0;0H 等
    private val ANSI_CONTROL_REGEX = Regex("\u001B\\[[0-9;]*[A-HJKSTfisuh]")

    fun parse(text: String): AnnotatedString {
        // 1. 先将不支持的复杂光标控制字符直接抹除，防止 UI 出错
        val cleanedText = text.replace(ANSI_CONTROL_REGEX, "")

        return buildAnnotatedString {
            var lastIndex = 0
            var currentColor: Color? = null

            ANSI_COLOR_REGEX.findAll(cleanedText).forEach { matchResult ->
                val textBefore = cleanedText.substring(lastIndex, matchResult.range.first)
                if (textBefore.isNotEmpty()) {
                    if (currentColor != null) {
                        withStyle(SpanStyle(color = currentColor!!)) { append(textBefore) }
                    } else {
                        append(textBefore)
                    }
                }

                val codes = matchResult.groupValues[1].split(";")
                codes.forEach { code ->
                    when (code) {
                        "0", "" -> currentColor = null
                        "30" -> currentColor = Color.Black
                        "31" -> currentColor = Color(0xFFF44336) // 红
                        "32" -> currentColor = Color(0xFF4CAF50) // 绿
                        "33" -> currentColor = Color(0xFFFFEB3B) // 黄
                        "34" -> currentColor = Color(0xFF2196F3) // 蓝
                        "35" -> currentColor = Color(0xFF9C27B0) // 紫
                        "36" -> currentColor = Color(0xFF00BCD4) // 青
                        "37" -> currentColor = Color.White
                    }
                }
                lastIndex = matchResult.range.last + 1
            }

            val remainingText = cleanedText.substring(lastIndex)
            if (remainingText.isNotEmpty()) {
                if (currentColor != null) {
                    withStyle(SpanStyle(color = currentColor!!)) { append(remainingText) }
                } else {
                    append(remainingText)
                }
            }
        }
    }
}