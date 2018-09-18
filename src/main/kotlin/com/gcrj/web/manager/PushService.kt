package com.gcrj.web.manager

import cn.jiguang.common.ClientConfig
import cn.jpush.api.push.model.Platform
import cn.jpush.api.push.model.audience.Audience
import cn.jpush.api.push.model.PushPayload
import cn.jpush.api.push.model.notification.Notification
import sun.security.krb5.internal.Krb5.getErrorMessage
import java.awt.SystemColor.info
import cn.jiguang.common.resp.APIRequestException
import cn.jiguang.common.resp.APIConnectionException
import cn.jpush.api.push.PushResult
import cn.jpush.api.JPushClient
import org.jetbrains.annotations.TestOnly


object PushService {

    private fun buildPushObject_android_tag_alertWithTitle(): PushPayload {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.all())
                .setNotification(Notification.android("下面", "上面", null))
                .build()
    }

    fun push() {
        val jpushClient = JPushClient("d2038cf079e6fd29a3a3c498", "795a870e97b9d01f17956de7", null, ClientConfig.getInstance())

        // For push, all you need do is to build PushPayload object.
        val payload = buildPushObject_android_tag_alertWithTitle()

        try {
            val result = jpushClient.sendPush(payload)

        } catch (e: APIConnectionException) {
            // Connection error, should retry later
            println("Connection error, should retry later")

        } catch (e: APIRequestException) {
            // Should review the error, and fix the request
            println("Should review the error, and fix the request " + e.toString())
            println("HTTP Status: " + e.status)
            println("Error Code: " + e.errorCode)
            println("Error Message: " + e.errorMessage)
        }

    }

}