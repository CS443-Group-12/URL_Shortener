function getShort() {
    var obj = new Object();
    var now = new Date();

    obj.custom = $('#custom_link').val();

    if ($('#custom_deadline').val() === "") {
        now.setDate(now.getDate() + 1);
    } else {
        now = new Date($('#custom_deadline').val());
    }

    obj.expiry = now.toISOString();
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

            $('#generated_link').val('http://qshift-myproject.192.168.0.20.nip.io/' + res.shortened_url)
            $('#shortened_link').html('Short link:</h2><h2><code><a id="generated_link" href="http://qshift-myproject.192.168.0.20.nip.io/' + res.shortened_url + '">http://qshift-myproject.192.168.0.20.nip.io/' + res.shortened_url + '</a></code><button class="btn btn-outline-secondary ml-3" onclick="copyText($(\'#generated_link\').html())">Copy</button>');
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
            if (!($('#errorAnalytics').hasClass('d-none')))
                $('#errorAnalytics').toggleClass('d-none')

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

function copyText(str) {
    var el = document.createElement('textarea');
    el.value = str;
    document.body.appendChild(el);
    el.select();
    document.execCommand('copy');
    document.body.removeChild(el);
}