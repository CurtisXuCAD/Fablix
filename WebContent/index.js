let cart = $("#cart");

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information 
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let item_list = $("#item_list");
    let total_price = $("#total_price");
    let pay = $("#pay");
    // change it to html list
    let res = "<ul>";
    let total = 0;
    for (let i = 0; i < resultArray.length; i++) {
        const title = resultArray[i].split("-")[0];
        const id = resultArray[i].split("-")[1];
        const quantity = resultArray[i].split("-")[2];
        const item = title +"-" + id;
        res += "<li>" +  title +  "     quantity : "+ quantity+ "     price : " +  10*quantity+" <BUTTON id='increase'  onclick=\"handleCart('" +"increase"+"','" + item+"')\">increase</BUTTON><BUTTON id='decrease'  onclick=\"handleCart('" +"decrease"+"','" + item+"')\">decrease</BUTTON><BUTTON id='delete'  onclick=\"handleCart('" +"delete"+"','" + item+"')\">delete</BUTTON></li>";
        total = total + 10*quantity;
    }
    res += "</ul>";

    let pes = "<p>Total Price:" +total+ "</p>";
    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
    total_price.append(pes);

    let pyy = "<form id=\"payment\" method=\"get\" action = \"payment.html\">\n" +
        "<input type = \"text\" name = \"price\" value ="+ total +" >\n" +
        "        <input type=\"submit\" value=\"Pay\">\n" +
        "        </form>";

    pay.append(pyy);



}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/index", {
        method: "POST",
        data: cart.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    cart[0].reset();
}

function handleCart(condition,item) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/index?id=" + item + "&condition="+condition,


    });

    window.location.reload();


}

$.ajax("api/index", {
    method: "GET",
    success: handleSessionData
});

// Bind the submit action of the form to a event handler function
cart.submit(handleCartInfo);
