/**
 * SOABA - SOA for Building Automation Web UI
 *
 * @author João Pinho
 */
"use strict";

var App = Ember.Application.create();

var soaba = Ember.Namespace.create({
    VERSION: '1.0.0',
    APP_URL: 'http://sb-dev.tagus.ist.utl.pt:9095/soaba/'
});

/**
 * App Startup
 */
$(function(){
    Highcharts.setOptions({ global: { useUTC: false }});

    /** page navigation setup **/
    $('.nav.nav-sidebar a').click(function(e){
        e.preventDefault();

        var $page;
        if(($page = $($(this).attr('href'))).length != 0){
            $page.siblings('.page').hide();
            $page.show('fade');
            $('.nav.nav-sidebar li').removeClass('active');
            $(this).parent().addClass('active');
        }

        return false;
    });
});