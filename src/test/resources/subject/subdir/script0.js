$(function () {
    $("#contactForm input,#contactForm textarea").jqBootstrapValidation({
        preventSubmit: true,
        submitError: function ($form, event, errors) {
            // additional error messages or events
            var $submitButton = $("#btnSubmit").addClass("animated shake");

            setTimeout(function () {
                $submitButton.removeClass("animated shake");
            }, 800);
        },
        submitSuccess: function ($form, event) {
            $("#btnSubmit").button("loading");
            event.preventDefault(); // Prevent spam click and default submit behaviour

            try {
                // Invoke the invisible reCAPTCHA challenge after client side validation
                grecaptcha.execute();
            } catch (err) {
                console.log(err.message);
                $("#btnSubmit").button("reset");
                showError("Veuillez s&apos;assurer que vous &ecirc;tes connect&eacute; &agrave; internet, puis r&eacute;essayer.");
                $('#contactForm').trigger("reset");
                return;
            }
        },
        filter: function () {
            return $(this).is(":visible");
        },
    });

    $("a[data-toggle=\"tab\"]").click(function (e) {
        e.preventDefault();
        $(this).tab("show");
    });

    // When clicking on Full hide fail/success boxes
    $('#name').focus(function () {
        $('#success').fadeOut("slow");
    });
});

function showError(message) {
    $('#success').hide().html("<div class='alert alert-danger'>").fadeIn("slow");
    $('#success > .alert-danger').html("<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;")
        .append("</button>");
    $('#success > .alert-danger')
        .append("<strong>").append(message).append("</strong>");
    $('#success > .alert-danger').append('</div>');
}

function reCaptchaCallBack(token) {
    $(document).ready(function () {

        if (!token) {
            $("#btnSubmit").button("reset");
            showError("Abscence de la r&eacute;ponse reCAPTCHA");
            $('#contactForm').trigger("reset");
            return;
        }

        // Get values from FORM
        var name = $("input#name").val();
        var email = $("input#email").val();
        var subject = $("input#subject").val();
        var message = $("textarea#message").val();

        $.ajax({
            url: "send/mail",
            type: "POST",
            data: {
                name: name,
                subject: subject,
                email: email,
                message: message,
                gRecaptchaResponse: token
            },
            cache: false,
            success: function (data, textStatus, jqXHR) {
                var response = JSON.parse(data);

                $("#btnSubmit").button("reset"); // Enable button & show success message
                if (response['success']) {
                    $('#success').hide().html("<div class='alert alert-success'>").fadeIn("slow");
                    $('#success > .alert-success').html("<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;")
                        .append("</button>");
                    $('#success > .alert-success')
                        .append("<strong>").append(response['success']).append("</strong>");
                    $('#success > .alert-success')
                        .append('</div>');
                } else {
                    showError(response['error']);
                }

                $('#contactForm').trigger("reset"); // Clear all fields
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus, errorThrown);
                $("#btnSubmit").button("reset");
                showError("Il semble que mon serveur de messagerie ne r&eacute;pond pas. Veuillez r&eacute;essayer plus tard!");
                $('#contactForm').trigger("reset"); // Clear all fields
            }
        });
    });
}