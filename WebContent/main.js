let login_form = $("#login_form");
let search_form = $("#search_form");
let startIndex = 0;
let numRecords = 20;
/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    window.location.replace("login.html");

}

function handleUserInfo(resultDataString) {
    console.log(resultDataString);
    let resultDataJson = JSON.parse(resultDataString);
    username = resultDataJson["username"];
    $("#username").text("Username: " + username);
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

// function handleSearchResult(resultDataString) {
//     window.location.replace("api/movies?" + search_form.serialize() + "&startIndex=" + startIndex);
// }

function handleAdvanceSearch(resultDataString) {
    resultDataString.preventDefault();
    window.location.replace("movie.html?" + search_form.serialize() +
        "&numRecords=" + numRecords + "&startIndex=" + startIndex);
    // $.ajax(
    //     "api/movies", {
    //         method: "GET",
    //         data: search_form.serialize() + "&startIndex=" + startIndex,
    //         success: handleMovieResult
    //     }
    // );
}

$.ajax(
    "api/main", {
        method: "Post",
        success: handleUserInfo
    }
);

// Bind the submit action of the form to a handler function
login_form.submit(doFunc);
search_form.submit(handleAdvanceSearch);