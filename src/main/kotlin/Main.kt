import io.vertx.core.Vertx
import io.vertx.core.http.*
import io.vertx.ext.web.Router
import io.vertx.ext.jdbc.*
import controller.*
import io.vertx.core.json.JsonObject
import java.io.File


/**
 * Main function that fires the HTTP server up
 */
fun main(args: Array<String>) {

    // Creates root vertx
    val vertx = Vertx.vertx()

    // Creates connection to database
    val dbConfigStr: String = File("configs/db.json").readText()
    val dbConfig = JsonObject(dbConfigStr)
    val db = JDBCClient.createShared(vertx, dbConfig)

    // Configures pages
    val router = Router.router(vertx)
    Home(db).configure(router)

    // Creates HTTP server, sets routes, and listens on port.
    val server: HttpServer = vertx.createHttpServer()
    server.requestHandler(router::accept)
    server.listen(8080)
}