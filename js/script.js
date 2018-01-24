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
                .post(url, form.serialize())
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
        // Gets form, and possibly exists early if form is invalid
        let loginForm = $("#signup")
        if(!signupForm[0].checkValidity()) return

        // Prevents navigating away, and makes post.
        event.preventDefault()
        let request = $.post("/signup", signupForm.serialize())

        // Handles post result
        let messages
        request
            .then((responseText) =>
            {
                // Parses response and stores message
                let response = JSON.parse(responseText)
                messages = response.messages

                // Determines template to use
                let templateUrl = null
                if(response.error) templateUrl = "/client/error.hbs"
                else templateUrl = "/client/success.hbs"

                // Continues with deferred template
                return site.loadTemplate(templateUrl)
            })
            .then((template) =>
            {
                // Renders template and displays
                let resultHtml = template({ "messages" : messages})
                $("#message").html(resultHtml)
            })
    })
})