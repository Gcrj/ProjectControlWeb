package com.gcrj.web.bean

open class ProjectBean {

    var id: Int? = null
    var name: String? = null
    var create_user: UserBean? = null

    var subProject: List<SubProjectBean>? = null

}