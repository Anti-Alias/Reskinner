package controller
import com.github.pgasync.Db
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.TemplateHandler





/**
 * Object responsible for routing signup pages.
 */
class SignUp(val db: Db, val templateHandler: TemplateHandler) {

    /**
     * Routes the SignUp page
     */
    fun configure(router: Router) {

        router.get("/signup")
                .handler(::signUp)
                .handler(templateHandler)
    }


    /**
     * Sign in code
     */
    fun signUp(ctx: RoutingContext) {

        ctx.next()
    }
}