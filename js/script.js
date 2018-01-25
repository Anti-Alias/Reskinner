let site = {

    /**
     * Dictionary serving as a template cache.
     */
    templateCache : {},

    /**
     * Takes a template URL and returns a deferred handlebars template
     * Caches value in dictionary.
     * @param templateUrl URL to load template from.
     * @returns {Promise<String>}
     */
    loadTemplate : function(templateUrl)
    {
        return new Promise((resolve, reject)=>
        {
            // Tries to get template string from cache.
            let result = this.templateCache[templateUrl]

            // If fails, loads it from URL
            if(result === undefined)
            {
                $.get(templateUrl)
                    .done( templateStr =>
                    {
                        let template = Handlebars.compile(templateStr)
                        this.templateCache[templateUrl] = template
                        resolve(template)
                    })
                    .fail(()=> reject("Failed to load " + templateUrl))
            }

            // Otherwise, finishes promise with cached result.
            else
            {
                resolve(result)
            }
        })
    },

    /**
    * Makes a post request to a URL and a returns a Promise
    * that emits a JSON object.
    * @param url URL to post to
    * @param selector Name of form element as a jquery selector.
    */
    postForm : function(url, selector) {

            // Checks validity
            let form = $(selector)
            if(!form[0].checkValidity()) return Promise.reject("Form had invalid input.")

            // Handles post result
            return $
                .ajax({
                   type: "POST",
                   url: url,
                   data: form.serialize(),
                   dataType: "text"
                 })
                .then((responseText) =>
                {
                    // Parses response
                    let response = JSON.parse(responseText)

                    // Determines template to use
                    let templateUrl = null
                    if(response.error) templateUrl = "/client/error.hbs"
                    else templateUrl = "/client/success.hbs"

                    // Gets messages of response, and promised template
                    let promisedTemplate = site.loadTemplate(templateUrl)
                    let messages = response.messages

                    // Returns joint results
                    return Promise.all([promisedTemplate, messages])
                })
                .then((results) =>
                {

                    let template = results[0]
                    let messages = results[1]
                    return template({ "messages" : messages })
                })
    }
}


// On document being ready...
$(document).ready(() =>
{

    // --------- Forms Controls ---------------
    let signupButton = $("#signup-button")
    let loginButton = $("#login-button")



    // ---------- Signup handler -------------
    signupButton.click( event =>
    {
        // Checks validity
        let signupForm = $("#signup")
        if(!signupForm[0].checkValidity()) return

        // Handles request
        event.preventDefault()
        site
            .postForm("/signup", "#signup")
            .then((html) => {
                $("#message").html(html)
            })
            .catch((error) => { console.log(error) })
    })


    // ---------- Login handler -------------
    loginButton.click( event =>
    {
        // Checks validity
        let loginForm = $("#login")
        if(!loginForm[0].checkValidity()) return

        // Handles request
        event.preventDefault()
        site
            .postForm("/login", "#login")
            .then((html) => {
                $("#message").html(html)
            })
            .catch((error) => { console.log(error) })
    })
})