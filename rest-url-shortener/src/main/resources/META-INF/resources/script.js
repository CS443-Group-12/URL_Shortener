function getShort() {
    var obj = new Object();
    var now = new Date();

    obj.custom = $('#custom_link').val();
    obj.expiry = $('#custom_deadline').val() + "T" + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
    obj.original = $('#link').val();
    obj.shortened = "";

    $.post("http://localhost:8080/short", obj)
        .done(function(data, status) {
            console.log("Data: " + data)
            console.log("Status: " + status)
        });
}