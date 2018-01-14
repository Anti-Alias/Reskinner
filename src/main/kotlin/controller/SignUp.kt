package controller
import com.github.pgasync.Db
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonArray
import io.vertx.ext.mail.mailencoder.EmailAddress
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

        router.get("/signup").handler(templateHandler)
        router.post("/signup").handler(::signUp)
    }


    /**
     * Sign in code
     */
    fun signUp(ctx: RoutingContext) {

        // Gets request and response
        val req: HttpServerRequest = ctx.request()
        val resp: HttpServerResponse = ctx.response()

        // Sets callback for when form attributes finish parsing
        req.setExpectMultipart(true)
        req.endHandler {

            // Gets attributes
            val atts = req.formAttributes()
            val email = EmailAddress(atts["email"])
            val username:String = atts["username"]
            val pass:String = atts["pass"]
            val pass2:String = atts["pass2"]

            // Handles attributes
            val errors = JsonArray()
            if(pass != pass2)
                errors.add("Passwords did not match")

            // If there are errors...
            if(errors.isEmpty) {
                resp.end("GJ, person!")
            }
            else {
                resp.statusCode = 500
                resp.end(errors.toString())
            }
        }
    }
}