package controller
import com.github.pgasync.Db
import io.vertx.ext.web.*
import io.vertx.ext.web.handler.TemplateHandler


/**
 * Object responsible for setting index resources
 */
class Index(val db: Db, val templateHandler: TemplateHandler) {


    /**
     * Sets routes for home pages.
     */
    fun configure(router: Router) {
        router.get("/").handler(::index)
        router.get("/").handler(templateHandler)
    }



    // ---------------------- CONTROLLER CODE ---------------------

    /**
     * Routes the home page
     */
    fun index(ctx: RoutingContext) {

        val exampleImages = (1 .. 6).map{ i -> "/images/example$i.png"}
        ctx.put("example-images", exampleImages)
        ctx.next()
    }
}