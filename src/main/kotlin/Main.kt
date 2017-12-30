import com.github.pgasync.ConnectionPoolBuilder
import com.github.pgasync.Db
import io.vertx.core.*
import io.vertx.core.http.*
import io.vertx.ext.web.*
import io.vertx.core.json.*
import controller.*
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.TemplateHandler
import io.vertx.ext.web.templ.HandlebarsTemplateEngine
import java.io.File
import kotlin.concurrent.thread


/**
 * Main function that fires the HTTP server up
 */
fun main(args: Array<String>) {

    // Gets configs
    val envStr: String = File("configs/env.json").readText()
    val dbStr: String = File("configs/db.json").readText()
    val envCfg = JsonObject(envStr)
    val dbCfg = JsonObject(dbStr)


    // Determines if docker is being used
    val docker: Boolean = envCfg.getBoolean("docker")

    // If not using docker, run app in background thread, and
    // allow it to die by pressing 'enter' in command line.
    if(!docker) {

        thread { start(dbCfg) }
        println("Please press 'enter' to close application. Pressing ^C will kill the gradle daemon making builds take longer.")
        readLine()
        System.exit(0)
    }

    // Otherwise, run it on current thread
    else start(dbCfg)
}

/**
 * Starts the application
 */
fun start(dbCfg: JsonObject) {

    // Creates a Db client
    val db: Db = fetchDb(dbCfg)

    // Creates operation to start application
    println("Starting HTTP server")

    // Creates handebar engine and handler
    val engine = HandlebarsTemplateEngine.create()
    val templateHandler = TemplateHandler.create(engine)

    // Creates root vertx for HTTP server
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)

    // Routes static files
    val imageHandler = StaticHandler.create("images")
    val cssHandler = StaticHandler.create("css")
    val jsHandler = StaticHandler.create("js")
    router.get("/images/*").handler(imageHandler)
    router.get("/js/*").handler(imageHandler)
    router.get("/css/*").handler(imageHandler)

    // Routes pages
    Index(db).configure(router)


    // Routes all other pages to template engine
    router.get().handler(templateHandler)


    // Creates HTTP server, assigns routes, and listens on port
    val server: HttpServer = vertx.createHttpServer()
    server.requestHandler(router::accept)
    server.listen(8080)
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