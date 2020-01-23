const baseUrl = "/security"

function logout() {
    $.ajax({
        type : 'post',
        url : `${baseUrl}/logout`,
        success : function (msg) {
            window.location.href = `${baseUrl}/index`;
        }
    })
}

function redirectToIndexIn5Sec() {
    window.setTimeout(
        ( () => window.location.href = `${baseUrl}/index`),
        5000);
}