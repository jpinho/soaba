/**
 * SOABA Utility Functions
 *
 * @author João Pinho
 */
"use strict";

soaba.utils = Ember.Namespace.create({
    scrollIntoView: function(target){
        return $('body').animate({scrollTop: $(target).offset().top});
    },

    createGauge: function($container, datapoint, fnUpdate, updateFrequency) {
        return $container.highcharts({
            chart: {
                type: 'gauge',
                plotBackgroundColor: null,
                plotBackgroundImage: null,
                plotBorderWidth: 0,
                plotShadow: false
            },
            title: { text: datapoint.displayName },
            pane: {
                startAngle: -150,
                endAngle: 150,
                background: [{
                    backgroundColor: {
                        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                        stops: [[0, '#FFF'], [1, '#333']]
                    },
                    borderWidth: 0,
                    outerRadius: '109%'
                }, {
                    backgroundColor: {
                        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                        stops: [[0, '#333'], [1, '#FFF']]
                    },
                    borderWidth: 1,
                    outerRadius: '107%'
                }, {
                    /* default background */
                }, {
                    backgroundColor: '#DDD',
                    borderWidth: 0,
                    outerRadius: '105%',
                    innerRadius: '103%'
                }]
            },
            yAxis: { /* the value axis */
                min: 0,
                max: 200,
                minorTickInterval: 'auto',
                minorTickWidth: 1,
                minorTickLength: 10,
                minorTickPosition: 'inside',
                minorTickColor: '#666',
                tickPixelInterval: 30,
                tickWidth: 2,
                tickPosition: 'inside',
                tickLength: 10,
                tickColor: '#666',
                labels: { step: 2, rotation: 'auto' },
                title: { text: (typeof datapoint.unit === 'undefined' ? '' : datapoint.unit) },
                plotBands: [
                    { from: 0, to: 120, color: '#55BF3B'    /* green */ },
                    { from: 120, to: 160, color: '#DDDF0D'  /* yellow */ },
                    { from: 160, to: 200, color: '#DF5353'  /* red */ }]
            },
            series: [{
                name: typeof datapoint.displayName === 'undefined' ? 'Value' : datapoint.displayName,
                data: [0],
                tooltip: { valueSuffix: typeof datapoint.unit === 'undefined' ? '' : datapoint.unit }
            }]
        },
        function (chart) {
            if (!chart.renderer.forExport)
                setInterval(function(){ fnUpdate(chart); }, updateFrequency);
        });
    },

    createLineChart: function($container, datapoint, fnUpdate, updateInterval){
        return $container.highcharts({
            chart: {
                type: 'spline',
                animation: Highcharts.svg, /* don't animate in old IE */
                marginRight: 10,
                events: {
                    load: function () {
                        /* set up the updating of the chart each second */
                        var series = this.series[0];

                        setInterval(function () {
                            fnUpdate(series);
                        }, updateInterval);
                    }
                }
            },
            title: {
                text: 'Live random data'
            },
            xAxis: {
                type: 'datetime',
                tickPixelInterval: 150
            },
            yAxis: {
                title: {
                    text: 'Value'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function () {
                    return '<b>' + this.series.name + '</b><br/>' +
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
                }
            },
            legend: {
                enabled: false
            },
            exporting: {
                enabled: false
            },
            series: [{
                name: 'Data',
                data: null
            }]
        });
    }
});