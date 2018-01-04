package controller
import com.github.pgasync.Db
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.TemplateHandler
import io.vertx.core.impl.VertxImpl.context
import io.vertx.ext.web.impl.Utils
import io.vertx.ext.web.impl.Utils.pathOffset




/**
 * Object responsible for routing signup pages.
 */
class SignUp(val db: Db, val templateHandler: TemplateHandler) {

    /**
     * Routes the SignUp page
     */
    fun configure(router: Router) {

        router.get("/signup").handler(::signUp)
        router.getWithRegex("/signup").handler(templateHandler)
    }


    /**
     * Sign in code
     */
    fun signUp(ctx: RoutingContext) {

        val path = ctx.normalisedPath()
        val file = Utils.pathOffset(ctx.normalisedPath(), ctx)
        ctx.next()
    }
}