package controller
import com.github.pgasync.Db
import io.vertx.ext.web.*
import io.vertx.core.http.*


/**
 * Object responsible for setting index resources
 */
class Index(val db: Db) {


    /**
     * Sets routes for home pages.
     */
    fun configure(router: Router) {
        router.get("/index").handler(::index)
        router.get("/index.hbs").handler(::index)
    }



    // ---------------------- CONTROLLER CODE ---------------------

    /**
     * Routes the home page
     */
    fun index(ctx: RoutingContext) {

        val req: HttpServerRequest = ctx.request()
        val resp: HttpServerResponse = ctx.response()

        ctx.next()
    }
}