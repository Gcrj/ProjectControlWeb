import com.gcrj.web.manager.ActivityManager
import com.gcrj.web.manager.PushService
import com.gcrj.web.manager.SubProjectManager
import com.google.gson.Gson
import org.junit.Test
import java.text.DecimalFormat


/**
 * Created by zhangxin on 2018/8/14.
 */
class Test {

    @Test
    fun test() {
//        PushService.push()

//        println(SubProjectManager.insert(2, "短视频1.0", 1))


//        val pi = 3.1//圆周率
//        println(DecimalFormat("#.#######").format(pi))
//        println(DecimalFormat("#.0000000").format(pi))

//        userId: Int, projectId: Int, subProjectId: Int, subProjectName: String, type: List<Int>
//        println(ActivityManager.insert(1, 2, 3, "关注界面", listOf(1, 2)))

        println(Gson().toJson(ActivityManager.query(3)))
    }

}