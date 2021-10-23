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

function handleUserInfo(resultData) {
    console.log(resultData);

    let genre_link =  $("#genres_links");
    let char_link =  $("#char_links");
    let rowHTML = "";
    for (let i = 0; i <  resultData.length-1; i++) {

        rowHTML +="<a class=\"link-dec\" href=\"movie.html?name=&director=&stars=&year=&genre=" + resultData[i]["genres"] + "\">\n" +
            "                "+ resultData[i]["genres"]+"\n" +
            "            </a>";



        // Append the row created to the table body, which will refresh the page
    }
    let res = "";
    for (var i = 65; i <= 90; i++) {
        res += "<a class=\"link-dec\" href=\"movie.html?name=&director=&stars=&year=&genre=&AZ="+String.fromCharCode(i)+"\">\n" +
            "        "+String.fromCharCode(i)+"\n" +
            "    </a>";
    }

    for (var x = 0; x <= 9; x++) {
        res += "<a class=\"link-dec\" href=\"movie.html?name=&director=&stars=&year=&genre=&AZ="+x+"\">\n" +
            "        "+x+"\n" +
            "    </a>";
    }


    genre_link.append(rowHTML);
    char_link.append(res);


    username = resultData[resultData.length-1]["genres"];


    $("#username").text("Welcome to Fablix, " + username);
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

document.getElementById("cart_button").addEventListener("click", () => {
    window.location.replace("index.html");
})

$.ajax(
    "api/main", {
        method: "Post",
        success: handleUserInfo
    }
);

// Bind the submit action of the form to a handler function
login_form.submit(doFunc);
search_form.submit(handleAdvanceSearch);