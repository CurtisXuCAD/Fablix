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
    for (let i = 0; i < Math.min(101 - 1, resultData.length - 1); i++) {

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
                '<a href="movie.html?movies?name=&director=&stars=&year=&genre=' + g_name + '&AZ=' + '">' +
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





        rowHTML += "<th><BUTTON id='add_to_cart'  onclick=\"handleCart('" + resultData[i]['movie_id'] + "','" + resultData[i]['movie_title'] + "')\">add</BUTTON></th>";



        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }



    totalResults = resultData[resultData.length - 1]["totalResults"];
    var t = parseInt(totalResults)
    console.log(totalResults);
    let paginationElement = jQuery("#pagination");
    let htmlContent = "";
    var a = parseInt(startIndex);
    var b = parseInt(movieNum);

    htmlContent += '<div class="pagination">'
    let first_page_url = "movie.html?name=" + movieName + "&director=" + movieDirector + "&stars=" + movieStars + "&year=" + movieYear + "&genre=" + movieGenre + "&AZ=" + movieAZ +
        "&numRecords=" + movieNum + "&startIndex=" + "0" + "&totalResults=" + totalResults +
        "&sortBy=" + sortBy + "&order=" + order;
    htmlContent += '<a href="' + first_page_url + '">' + "&laquo;" + '</a>';

    for (let i = 0; i < Math.ceil(t / b); i++) {
        if (Math.abs(a - (b * i)) < 6 * b) {
            let page_url = "movie.html?name=" + movieName + "&director=" + movieDirector + "&stars=" + movieStars + "&year=" + movieYear + "&genre=" + movieGenre + "&AZ=" + movieAZ +
                "&numRecords=" + movieNum + "&startIndex=" + (b * i).toString() + "&totalResults=" + totalResults +
                "&sortBy=" + sortBy + "&order=" + order;

            htmlContent += '<a href="' + page_url + '"';
            if (a == b * i) {
                htmlContent += 'class="active"'
            }
            htmlContent += '>' + (i + 1).toString() + '</a>';
        }
    }
    let last_page_url = "movie.html?name=" + movieName + "&director=" + movieDirector + "&stars=" + movieStars + "&year=" + movieYear + "&genre=" + movieGenre + "&AZ=" + movieAZ +
        "&numRecords=" + movieNum + "&startIndex=" + (Math.floor(t / b) * b).toString() + "&totalResults=" + totalResults +
        "&sortBy=" + sortBy + "&order=" + order;
    htmlContent += '<a href="' + last_page_url + '">' + "&raquo;" + '</a>';

    htmlContent += '</div>';



    if (a > 0) {
        var prev_index = (a - b).toString();
        if (prev_index < 0) {
            prev_index = 0;
        }
        let prev_url = "movie.html?name=" + movieName + "&director=" + movieDirector + "&stars=" + movieStars + "&year=" + movieYear + "&genre=" + movieGenre + "&AZ=" + movieAZ +
            "&numRecords=" + movieNum + "&startIndex=" + prev_index + "&totalResults=" + totalResults +
            "&sortBy=" + sortBy + "&order=" + order;
        htmlContent +=
            '<div class = "link">' +
            '<a class="link-dec" href="' + prev_url + '">' +
            '<div class="back-button-text" align="center">' +
            'Prev Page' +
            '</div>' +
            '</a>' +
            '</div>';
    }

    var next_index = (a + b).toString();
    if (next_index < t) {
        let next_url = "movie.html?name=" + movieName + "&director=" + movieDirector + "&stars=" + movieStars + "&year=" + movieYear + "&genre=" + movieGenre + "&AZ=" + movieAZ +
            "&numRecords=" + movieNum + "&startIndex=" + next_index + "&totalResults=" + totalResults +
            "&sortBy=" + sortBy + "&order=" + order;
        htmlContent +=
            '<div class = "link">' +
            '<a class="link-dec" href="' + next_url + '">' +
            '<div class="back-button-text" align="center">' +
            'Next Page' +
            '</div>' +
            '</a>' +
            '</div>';
    }



    // htmlContent += "<form action=\"" +
    //     prev_url +
    //     "\"> \n" + "<button type=\"submit\">Prev Page</button>" + "</button>";

    // htmlContent += "<form action=\"" +
    //     next_url +
    //     "\"> \n" + "<button type=\"submit\">Next Page</button>" + "</button>";

    paginationElement.append(htmlContent);

    let titleHtml = document.getElementById("title");
    let ratingHtml = document.getElementById("rating");
    titleHtml.classList.remove("th-sort-asc", "th-sort-desc", "th-sort-none");
    ratingHtml.classList.remove("th-sort-asc", "th-sort-desc", "th-sort-none");

    if (sortBy == null || sortBy == "null") {
        titleHtml.classList.toggle("th-sort-none", 1);
        ratingHtml.classList.toggle("th-sort-none", 1);
    } else if (sortBy == "title") {
        if (order == null) {
            titleHtml.classList.toggle("th-sort-none", 1);
        } else if (order == "asc") {
            titleHtml.classList.toggle("th-sort-asc", 1);
        } else {
            titleHtml.classList.toggle("th-sort-desc", 1);
        }
        ratingHtml.classList.toggle("th-sort-none", 1);
    } else if (sortBy == "rating") {
        if (order == null) {
            ratingHtml.classList.toggle("th-sort-none", 1);
        } else if (order == "asc") {
            ratingHtml.classList.toggle("th-sort-asc", 1);
        } else {
            ratingHtml.classList.toggle("th-sort-desc", 1);
        }
        titleHtml.classList.toggle("th-sort-none", 1);
    }



}

function handleCartInfo1(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/movies?name=" + movieName + "&director=" + movieDirector + "&stars=" + movieStars + "&year=" + movieYear + "&genre=" + movieGenre + "&AZ=" + movieAZ, {
        method: "POST",
        data: cart.serialize()

    });

    // clear input form
    cart[0].reset();
}
const popup = document.querySelector('.popup');

function handleCart(id, title) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/index?id=" + title + "-" + id, // Setting request url, which is mapped to the TestServlet
        // success: (resultData) => handleSearchResult(resultData) //

    });


    popup.classList.add('open');




}

function closepop() {
    popup.classList.remove('open');
}


document.getElementById("title").addEventListener("click", () => {
    const currentIsAscending = document.getElementById("title").classList.contains("th-sort-asc");
    console.log(currentIsAscending);
    sortBy = "title";
    if (currentIsAscending) {
        order = 'desc';
    } else {
        order = 'asc';
    }

    window.location.replace("movie.html?name=" + movieName + "&director=" + movieDirector + "&stars=" +
        movieStars + "&year=" + movieYear + "&genre=" + movieGenre + "&AZ=" +
        movieAZ + "&numRecords=" + movieNum + "&startIndex=" +
        startIndex + "&totalResults=" + totalResults + "&sortBy=" + sortBy + "&order=" + order);
});

document.getElementById("rating").addEventListener("click", () => {
    const currentIsAscending = document.getElementById("rating").classList.contains("th-sort-asc");
    console.log(currentIsAscending);
    sortBy = "rating";
    if (currentIsAscending) {
        order = 'desc';
    } else {
        order = 'asc';
    }

    window.location.replace("movie.html?name=" + movieName + "&director=" + movieDirector + "&stars=" +
        movieStars + "&year=" + movieYear + "&genre=" + movieGenre + "&AZ=" +
        movieAZ + "&numRecords=" + movieNum + "&startIndex=" +
        startIndex + "&totalResults=" + totalResults + "&sortBy=" + sortBy + "&order=" + order);
});

// document.querySelectorAll(".table-sortable th").forEach(headerCell => {
//     headerCell.addEventListener("click", () => {
//         const tableElement = headerCell.parentElement.parentElement.parentElement;
//         const headerIndex = Array.prototype.indexOf.call(headerCell.parentElement.children, headerCell);
//         const currentIsAscending = headerCell.classList.contains("th-sort-asc");

//         sortTableByColumn(tableElement, headerIndex, !currentIsAscending);
//     });
// });


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let movieName = getParameterByName('name');
let movieDirector = getParameterByName('director');
let movieStars = getParameterByName('stars');
let movieYear = getParameterByName('year');
let movieGenre = getParameterByName('genre');
let movieAZ = getParameterByName('AZ');
let movieNum = getParameterByName('numRecords');
if (movieNum == null) {
    movieNum = 20;
}
let startIndex = getParameterByName('startIndex');
if (startIndex == null) {
    startIndex = 0;
}
let totalResults = getParameterByName('totalResults');
let sortBy = getParameterByName('sortBy');
let order = getParameterByName('order');
// Makes the HTTP GET request and registers on success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies?name=" + movieName + "&director=" + movieDirector + "&stars=" +
        movieStars + "&year=" + movieYear + "&genre=" + movieGenre + "&AZ=" +
        movieAZ + "&numRecords=" + movieNum + "&startIndex=" +
        startIndex + "&totalResults=" + totalResults + "&sortBy=" + sortBy + "&order=" + order, // Setting request url, which is mapped by MoviesServlet in MoviesServlet.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});