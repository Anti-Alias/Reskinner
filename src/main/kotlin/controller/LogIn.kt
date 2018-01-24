package controller
import com.github.pgasync.Db
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mail.mailencoder.EmailAddress
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.TemplateHandler





/**
 * Object responsible for routing signup pages.
 */
class LogIn(val db: Db, val templateHandler: TemplateHandler) {

    /**
     * Routes the SignUp page
     */
    fun configure(router: Router) {

        router.get("/login").handler(templateHandler)
        router.post("/login").handler(::signUp)
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
            val emailStr:String = atts["email"]
            val pass:String = atts["pass"]

            // Checks parameters.
            // Appends error messages if encountered
            var error = false
            val messages = mutableListOf<String>()
            val response = mutableMapOf<String, Any>("messages" to messages)
            if(emailStr.isEmpty()) {
                messages.add("Email not specified")
                error = true
            }
            if(pass.isEmpty()) {
                messages.add("Password not specified")
                error = true
            }
            response.put("error", error)

            // Sets error flag
            if(!error) {
                messages.add("Welcome back!")
            }

            // Sends all responses as a json array.
            resp.end(JsonObject(response).toString())
        }
    }
}