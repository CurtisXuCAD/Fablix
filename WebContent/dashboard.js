let add_form = $("#add_movie2");
let star_form = $("#add_star");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    console.log(resultDataString);
    var newData = JSON.stringify(resultDataString);
    let resultDataJson = JSON.parse(newData);

    console.log("handle add movie response");

    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    //  if (resultDataJson["status"] === "success") {

   //     window.location.replace("main.html");
   //  } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        add_form[0].reset();
        $("#message").text(resultDataJson["message"]);

        popup.classList.add('open');
    // }
}

const popup = document.querySelector('.popup');


function closepop() {
    popup.classList.remove('open');
}
/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/dashboard", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: add_form.serialize(),
            success: handleLoginResult
        }
    );
}

function submitLoginForm1(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/dashboard", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: star_form.serialize(),
            success: handleLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
add_form.submit(submitLoginForm);
star_form.submit(submitLoginForm1);
