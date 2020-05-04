function getShort() {
    var obj = new Object();
    var now = new Date();

    obj.custom = $('#custom_link').val();
    obj.expiry = $('#custom_deadline').val() + "T" + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
    obj.original = $('#link').val();
    obj.shortened = "";

    $.ajax({
        url: '/b2c/short',
        type: 'POST',
        data: JSON.stringify(obj),
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        success: function (res) {
            console.log(res);

            if (!($('#errorMsg').hasClass('d-none')))
                $('#errorMsg').toggleClass('d-none')
            if (!($('#status').hasClass('d-none')))
                $('#status').toggleClass('d-none')
            if (!($('#description').hasClass('d-none')))
                $('#description').toggleClass('d-none')

            if ($('#successMsg').hasClass('d-none'))
                $('#successMsg').toggleClass('d-none');
            if ($('#shortened_link').hasClass('d-none'))
                $('#shortened_link').toggleClass('d-none');
            if ($('#link_expiry').hasClass('d-none'))
                $('#link_expiry').toggleClass('d-none');

            $('#shortened_link').html('Short link: <code>' + res.shortened_url + '</code>');
            $('#link_expiry').html('Expires: <code>' + res.expiration_date + '</code>')
        },
        error: function (res) {
            console.log("Error:\n\nStatus: " + res.status + '\nStatusText: ' + res.statusText + '\nResponseText: ' + res.responseText);

            if (!($('#successMsg').hasClass('d-none')))
                $('#successMsg').toggleClass('d-none');
            if (!($('#shortened_link').hasClass('d-none')))
                $('#shortened_link').toggleClass('d-none');
            if (!($('#link_expiry').hasClass('d-none')))
                $('#link_expiry').toggleClass('d-none');

            if ($('#errorMsg').hasClass('d-none'))
                $('#errorMsg').toggleClass('d-none')
            if ($('#status').hasClass('d-none'))
                $('#status').toggleClass('d-none')
            if ($('#description').hasClass('d-none'))
                $('#description').toggleClass('d-none')

            $('#status').html('Status Code: <code>' + res.status + '</code>')
            $('#description').html(res.statusText + ': <code>' + res.responseText + '</code>')

        }
    });
}

function getAnalytics() {
    $.ajax({
        url: '/b2c/analytics/' + $('#shortlink').val(),
        type: 'GET',
        success: function (res) {
            console.log(res);
            if ($('#linkAnalytics').hasClass('d-none'))
                $('#linkAnalytics').toggleClass('d-none');

            $('#short_link').html('<code>Link: </code>' + $('#shortlink').val());
            $('#all_clicks').html('<code>Clicks: </code>' + res.all_clicks);
        },
        error: function (res) {
            console.log("Error:\n" + res);
            if (!($('#linkAnalytics').hasClass('d-none')))
                $('#linkAnalytics').toggleClass('d-none');
            if ($('#errorAnalytics').hasClass('d-none'))
                $('#errorAnalytics').toggleClass('d-none')
            
            $('#status_code').html('Status Code: <code>' + res.status + '</code>');
            $('#description').html(res.statusText + ': <code>' + res.responseText + '</code>');
        }
    });
}