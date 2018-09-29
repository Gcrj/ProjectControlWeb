package com.gcrj.web.bean

class ActivityRelatedBean {

    var id: Int? = null
    var activity_id: Int? = null
    var name: String? = null
    var progress: Int? = null

    @Transient
    var time: String? = null

}