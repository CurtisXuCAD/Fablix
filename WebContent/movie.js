/**
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */


function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' +
            resultData[i]["movie_title"] + // display movie_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        let gnames = resultData[i]["movie_gnames"];
        let html_contentg = "";
        const id_name_arrayg = gnames.split(', ');
        for (let j = 0; j < id_name_arrayg.length; j++) {
            const g_name = id_name_arrayg[j];
            html_contentg +=
                '<a href="movie.html?movies?name=&director=&stars=&year=&genre=' + g_name  + '&AZ='  + '">' +
                g_name + // display movie_name for the link text
                '</a>' + ', '
        }
        rowHTML += "<th>" + html_contentg.slice(0, -2) + "</th>";


        let snames = resultData[i]["movie_snames"];
        let html_content = "";
        const id_name_array = snames.split(', ');
        for (let j = 0; j < id_name_array.length; j++) {
            const star_id = id_name_array[j].split("-")[0];
            const star_name = id_name_array[j].split("-")[1];
            html_content +=
                '<a href="single-star.html?id=' + star_id + '">' +
                star_name + // display star_name for the link text
                '</a>' + ', '
        }
        rowHTML += "<th>" + html_content.slice(0, -2) + "</th>";

        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";





        rowHTML += "<th><BUTTON id='add_to_cart'  onclick=\"handleCart('"+resultData[i]['movie_id']+"','"+resultData[i]['movie_title']+"')\">add</BUTTON></th>";




        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }



    movieTableBodyElement.append(rowHTML);
}

function handleCartInfo1(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax( "api/movies?name=" + movieName + "&director=" + movieDirector + "&stars=" + movieStars + "&year=" + movieYear + "&genre=" + movieGenre + "&AZ=" +movieAZ,{
        method: "POST",
        data: cart.serialize()

    });

    // clear input form
    cart[0].reset();
}

function handleCart(id,title) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/index?id="+title+"-"+ id, // Setting request url, which is mapped to the TestServlet
        success: (resultData) => handleSearchResult(resultData) //

    });


}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let movieName = getParameterByName('name');
let movieDirector = getParameterByName('director');
let movieStars = getParameterByName('stars');
let movieYear = getParameterByName('year');
let movieGenre = getParameterByName('genre');
let movieAZ = getParameterByName('AZ');
// Makes the HTTP GET request and registers on success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies?name=" + movieName + "&director=" + movieDirector + "&stars=" + movieStars + "&year=" + movieYear + "&genre=" + movieGenre + "&AZ=" +movieAZ, // Setting request url, which is mapped by MoviesServlet in MoviesServlet.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});

