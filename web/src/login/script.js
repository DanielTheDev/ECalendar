const api = APIClient.getInstance();

if(api.isLoggedIn()) {
	window.location.href = "/";
}

$(document).ready(function() {

    const fetch = function() {
        const email = $("#email").val();
        const password = $("#password").val();

        api.login(email, password, function(success, error) {
            if(success) {
                location.reload();
            } else {
                alert("error: " + error);
            }
        });
    }
    
    $("#next-button").click(fetch);
    $("#password").keyup(function(e) {
        if(e.code == "Enter") {
            fetch();
        }
    });
});