import com.github.pgasync.ConnectionPoolBuilder
import com.github.pgasync.Db
import io.vertx.core.*
import io.vertx.core.http.*
import io.vertx.ext.web.*
import io.vertx.core.json.*
import controller.*
import handler.CustomTemplateHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.templ.HandlebarsTemplateEngine
import java.io.File



/**
 * Main function that fires the HTTP server up
 */
fun main(args: Array<String>) {

    // Gets configs
    val appStr: String = File("configs/app.json").readText()
    val dbStr: String = File("configs/db.json").readText()
    val appCfg = JsonObject(appStr)
    val dbCfg = JsonObject(dbStr)

    // Gets data from configs
    val docker: Boolean = appCfg.getBoolean("docker")
    val environment: String = appCfg.getString("environment")

    // Starts multiple instances of server. Each instance
    // will be load-balanced between threads.
    val vertx = Vertx.vertx()
    val processors = Runtime.getRuntime().availableProcessors()
    println("Creating $processors http servers, matching the number of available processors.")
    repeat(processors) {
        val server: HttpServer = makeServer(vertx, appCfg, dbCfg)
        server.listen(8080)
    }

    // If we are testing on raw hardware locally,
    // we need to prevent the main thread from terminating.
    // This prevents the gradle daemon from getting destroyed
    // when the programmer exists when pressing ^C.
    // If the daemon terminates, builds will take substantially longer.
    if(!docker && environment == "local") {

        println("Press 'enter' to close application.")
        readLine()
        vertx.close {
            println("Bye")
        }
    }
}

/**
 * Starts the application
 */
fun makeServer(vertx: Vertx, appCfg: JsonObject, dbCfg: JsonObject): HttpServer {

    // Creates a Db client
    val templateCacheSize: Int = appCfg.getInteger("template-cache-size")
    val db: Db = fetchDb(dbCfg)

    // Creates operation to start application
    println("Starting HTTP server")

    // Creates handlebar engine and handler
    val engine = HandlebarsTemplateEngine.create()
    engine.setMaxCacheSize(templateCacheSize)
    val templateHandler = CustomTemplateHandler(engine, "templates", "text/html")

    // Creates Router for HTTP server
    val router = Router.router(vertx)

    // Routes static files
    val imageHandler = StaticHandler.create("images")
    val cssHandler = StaticHandler.create("css")
    val jsHandler = StaticHandler.create("js")
    router.get("/images/*").handler(imageHandler)
    router.get("/js/*").handler(jsHandler)
    router.get("/css/*").handler(cssHandler)

    // Routes pages
    Index(db, templateHandler).configure(router)
    SignUp(db, templateHandler).configure(router)

    // Creates HTTP server, assigns routes, and listens on port
    val server: HttpServer = vertx.createHttpServer()
    server.requestHandler(router::accept)

    // Returns HTTP server
    return server
}


/**
 * Fetches database object from config file
 */
fun fetchDb(config: JsonObject): Db {

    // Gets DB credentials
    val hostname: String = config.getString("hostname")
    val port: Int = config.getInteger("port")
    val database: String = config.getString("database")
    val user: String = config.getString("user")
    val password: String = config.getString("password")
    val poolSize: Int = config.getInteger("pool-size")


    // Creates DB client
    val db: Db = ConnectionPoolBuilder()
        .hostname(hostname)
        .port(port)
        .database(database)
        .username(user)
        .password(password)
        .poolSize(poolSize)
        .build()

    // Returns db
    return db
}