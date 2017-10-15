$(document).ready(function () {

    $("#btnSubmit").click(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();

        fire_ajax_submit();

    });

    $(".link-like").click(function (event) {
        const imageId = event.target.getAttribute('imageid');

        $.ajax({
            type: "POST",
            url: "/api/like",
            data: {
                like: imageId,
                threshold: '30'
            },
            cache: false,
            timeout: 600000,
            success: function (data) {

                $("#result").html(data);
                console.log("SUCCESS : ", data);

            },
            error: function (e) {

                $("#result").text(e.responseText);
                console.log("ERROR : ", e);
                $("#btnSubmit").prop("disabled", false);

            }
        });

        /*
        $.ajax({
            type: "GET",
            url: "/api/like",

        })
        */
    });

});



function fire_ajax_submit() {

    const form = $('#fileUploadForm')[0];

    const data = new FormData(form);

    data.append("CustomField", "This is some extra data, testing");

    $("#btnSubmit").prop("disabled", true);

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/api/upload",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {

            $("#result").text(data);
            console.log("SUCCESS : ", data);
            $("#btnSubmit").prop("disabled", false);

        },
        error: function (e) {

            $("#result").text(e.responseText);
            console.log("ERROR : ", e);
            $("#btnSubmit").prop("disabled", false);

        }
    });

}