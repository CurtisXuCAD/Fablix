let login_form = $("#login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    window.location.replace("login.html");

}


function doFunc(resultDataString) {
    resultDataString.preventDefault();
    $.ajax(
        "api/main", {
            method: "GET",
            data: { action: "Logout" },
            success: handleLoginResult
        }
    );
}



// Bind the submit action of the form to a handler function
login_form.submit(doFunc);