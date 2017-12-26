package controller
import io.vertx.ext.web.*
import io.vertx.core.http.*
import io.vertx.ext.sql.SQLClient


/**
 * Object responsible for setting home resources
 */
class Home(val db: SQLClient) {


    /**
     * Sets routes for home pages.
     */
    fun configure(router: Router) {
        router.get("/").handler(::home)
        router.get("/home").handler(::home)
    }



    // ---------------------- CONTROLLER CODE ---------------------

    /**
     * Routes the home page
     */
    fun home(ctx: RoutingContext) {
        val req: HttpServerRequest = ctx.request()
        val resp: HttpServerResponse = ctx.response()
        resp.end("Hello, world!")
    }
}