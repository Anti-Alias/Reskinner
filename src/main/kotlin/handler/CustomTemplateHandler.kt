package handler

import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.handler.TemplateHandler
import io.vertx.ext.web.templ.TemplateEngine
import io.vertx.ext.web.RoutingContext

/**
 * Custom template handler modified from the source code located here:
 * https://github.com/vert-x3/vertx-web/blob/master/vertx-web/src/main/java/io/vertx/ext/web/handler/impl/TemplateHandlerImpl.java
 *
 * The motivation for creating a custom one is to more easily specify the exact path of template files,
 * as the default one is frustrating to use without resorting to regex paths.
 */
class CustomTemplateHandler(private val engine: TemplateEngine, templateDirectory: String?, private val contentType: String) : TemplateHandler {
    private val templateDirectory: String
    private var indexTemplate: String? = null

    init {
        this.templateDirectory = if (templateDirectory == null || templateDirectory.isEmpty()) "." else templateDirectory
        this.indexTemplate = TemplateHandler.DEFAULT_INDEX_TEMPLATE
    }

    override fun handle(context: RoutingContext) {

        // Gets path without trailing slashes
        var path:String = context.normalisedPath()
        if(path.endsWith("/") && null != indexTemplate)
            path += indexTemplate

        // Gets last forward slash
        val lastSlash:Int = path.lastIndexOf('/')
        if(lastSlash == -1)
            throw RuntimeException("Cannot route template without any slashes in route. Got '$path'")

        // Gets filename
        val file:String = path.substring(lastSlash)

        // Renders
        engine.render(context, templateDirectory, file) { res ->
            if (res.succeeded()) {
                context.response().putHeader(HttpHeaders.CONTENT_TYPE, contentType).end(res.result())
            } else {
                context.fail(res.cause())
            }
        }
    }

    override fun setIndexTemplate(indexTemplate: String): TemplateHandler {
        this.indexTemplate = indexTemplate
        return this
    }
}