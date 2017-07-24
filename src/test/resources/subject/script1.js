!function (o) {
    "use strict";
    var resizeHeader = function() {
        var vph = o(window).height();
        o("header").css({'height': vph + 'px'});
    };
    o("html,#resumeModal,#biographyModal,#portfolioModal1," +
        "#portfolioModal2,#portfolioModal3,#portfolioModal4,#portfolioModal5," +
        "#portfolioModal6,#servicesModal").niceScroll({
        cursordragontouch: true
    }), resizeHeader(),
        o(window).on('resize', resizeHeader),
        o(".page-scroll a").bind("click", function (t) {
            var l = o(this);
            o("html, body").stop().animate({scrollTop: o(l.attr("href")).offset().top - 50}, 1250, "easeInOutExpo"), t.preventDefault()
    }), o("body").scrollspy({
        target: ".navbar-fixed-top",
        offset: 51
    }), o(".navbar-collapse ul li a").click(function () {
        o(".navbar-toggle:visible").click()
    }), o("#mainNav").affix({offset: {top: 100}}), o(function () {
        o("body").on("input propertychange", ".floating-label-form-group", function (t) {
            o(this).toggleClass("floating-label-form-group-with-value", !!o(t.target).val())
        }).on("focus", ".floating-label-form-group", function () {
            o(this).addClass("floating-label-form-group-with-focus")
        }).on("blur", ".floating-label-form-group", function () {
            o(this).removeClass("floating-label-form-group-with-focus")
        })
    }), o('.view-section').addClass('hide-view-section').viewportChecker({
        classToAdd: 'show-view-section animated fadeIn',
        offset: 100,
        repeat: true
    }), o("img.lazy").lazyload({
        effect: "fadeIn"
    }), o('.lazy-load-modal').on("show.bs.modal", function () {
        o('.lazy-load-modal .modal-body .modal-lazy').each(function () {
            var img = o(this);
            img.attr('src', img.data('original'));
        })
    }) , o('[data-toggle="tooltip"]').tooltip({
        template: '<div class="tooltip">' +
        '<div class="tooltip-arrow"></div>' +
        '<div class="tooltip-head">' +
        '<h5>' +
        '<i class="fa fa-info-circle" aria-hidden="true"></i> Contact' +
        '</h5>' +
        '</div>' +
        '<div class="tooltip-inner"></div>' +
        '</div>',
        html: true
    })
}(jQuery);