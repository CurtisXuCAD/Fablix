let search_form = $("#search_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {


    window.location.replace("movie.html?name=" + movieName + "&director=" +movieDirector +"&stars=" + movieStars + "&year=" + movieYear);

}



// Bind the submit action of the form to a handler function
search_form.submit(handleLoginResult);

// Get id from URL
let movieName = getParameterByName('name');
let movieDirector = getParameterByName('director');
let movieStars = getParameterByName('stars');
let movieYear = getParameterByName('year');

