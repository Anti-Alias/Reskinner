// On document being ready...
$(document).ready(function() {

    // --------- Forms ---------------
    var signupButton = $("#signup-button")
    signupButton.click(function(event) {

        // Prevents page from navigating away.
        event.preventDefault()

        // Sends post request for signing up
        var signupForm = $("#signup")
        var request = $.post("/signup", signupForm.serialize())

        // Handles result
        request.done( function(result) {
            var temp = ""
            $("#message").html(result)
        })
        request.fail( function(data) {
            $("#message").html("Server-side error")
        })
    })
})