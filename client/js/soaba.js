/**
 * SOABA - SOA for Building Automation Web UI
 *
 * @author João Pinho
 */
"use strict";

var App = Ember.Application.create();

var soaba = Ember.Namespace.create({
    VERSION: '1.0.0-alpha',
    APP_URL: 'http://' + (window.location.hostname) + ':9095/soaba/',
    AUTHOR: 'João Pinho',
    AUTHOR_URL: 'http://pinho.icodebox.net',
    appLoadingThreads: []
});

/**
 * App Startup
 */
$(function(){
    Highcharts.setOptions({ global: { useUTC: false }});

    $('.help-ctn').hover(function(){
        if(!$('.help .notice').is(':visible'))
            $('.help .notice').show('fade');
    });
    $('.help .notice').hover(null, function(){
        $('.help .notice').hide();
    });
    $('.login .requestaccess').click(function(){
        if(!$('.help .notice').is(':visible'))
            $('.help .notice').show('fade');
    });

    /**
     * Sand-Signika theme for Highcharts JS
     * @author Torstein Honsi
     */
    // Load the fonts
    Highcharts.createElement('link', {
        href: 'http://fonts.googleapis.com/css?family=Signika:400,700',
        rel: 'stylesheet',
        type: 'text/css'
    }, null, document.getElementsByTagName('head')[0]);

    // Add the background image to the container
    Highcharts.wrap(Highcharts.Chart.prototype, 'getContainer', function (proceed) {
        proceed.call(this);
        this.container.style.background = 'url(http://www.highcharts.com/samples/graphics/sand.png)';
    });

    Highcharts.theme = {
        colors: ["#f45b5b", "#8085e9", "#8d4654", "#7798BF", "#aaeeee", "#ff0066", "#eeaaee",
            "#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
        chart: {
            backgroundColor: null,
            style: {
                fontFamily: "Signika, serif"
            }
        },
        title: {
            style: {
                color: 'black',
                fontSize: '16px',
                fontWeight: 'bold'
            }
        },
        subtitle: {
            style: {
                color: 'black'
            }
        },
        tooltip: {
            borderWidth: 0
        },
        legend: {
            itemStyle: {
                fontWeight: 'bold',
                fontSize: '13px'
            }
        },
        xAxis: {
            labels: {
                style: {
                    color: '#6e6e70'
                }
            }
        },
        yAxis: {
            labels: {
                style: {
                    color: '#6e6e70'
                }
            }
        },
        plotOptions: {
            series: {
                shadow: true
            },
            candlestick: {
                lineColor: '#404048'
            },
            map: {
                shadow: false
            }
        },

        // Highstock specific
        navigator: {
            xAxis: {
                gridLineColor: '#D0D0D8'
            }
        },
        rangeSelector: {
            buttonTheme: {
                fill: 'white',
                stroke: '#C0C0C8',
                'stroke-width': 1,
                states: {
                    select: {
                        fill: '#D0D0D8'
                    }
                }
            }
        },
        scrollbar: {
            trackBorderColor: '#C0C0C8'
        },

        // General
        background2: '#E0E0E8'

    };

    // Apply the theme
    Highcharts.setOptions(Highcharts.theme);

    $('.soaba-footer').append(
        $('<span>Release '+soaba.VERSION+' :: Developed by &nbsp;</span><a target="_blank" href="'
            + soaba.AUTHOR_URL+'"><b>'+soaba.AUTHOR+'</b></a>'
            + ' | <span>Visit SOABA </span><a href="http://soaba.icodebox.net" target="_blank"'
            + ' style="text-transform: uppercase; font-size: 8pt"><b>online</b></a>'));

    var loader = setInterval(function(){
        if(soaba.appLoadingThreads.length > 0)
            return;

        clearInterval(loader);
        $('#loader-wrapper').hide();
    }, 2*1000);
});