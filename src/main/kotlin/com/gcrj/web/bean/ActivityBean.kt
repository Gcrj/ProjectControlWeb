package com.gcrj.web.bean

class ActivityBean {

    var id: Int? = null
    var sub_project_id: Int? = null
    var name: String? = null
    var progress: Int? = null

    @Transient
    var activityRelated: List<ActivityRelatedBean>? = null
}