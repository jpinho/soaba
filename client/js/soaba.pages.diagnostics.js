/**
 * SOABA Diagnostics Page
 *
 * @author João Pinho
 */
"use strict";

(function() {
    var GAUGE_UPD_INTERVAL = 5 * 1000, LINECHART_UPD_INTERVAL = 2 * 1000;
    var METEO_STATION_PREFIX = 'meteo station';

    /**
     * Page Load
     */
    $(function () {
        var gaugeDatapoints = ['0.4.0', '0.4.1', '0.4.3'];
        var lineChartDatapoints = ['0.6.27', '0.6.22', '0.6.25','0.6.28'];

        $.each(gaugeDatapoints, function(i, datapointAddress){
            var $cont = $('#gaugeContainer');
            console.log('Adding datapoint "' + datapointAddress+ '" to diagnostics panel.');

            $.getJSON(soaba.APP_URL + 'datapoints/' + datapointAddress, function(rsp){
                console.log('Datapoint "' + datapointAddress+ '" info received.');
                if(typeof rsp.stackTrace !== 'undefined'){
                    console.log('Error: datapoint ' + datapointAddress + ' read exception '
                    + rsp.message + ' -->> ' + rsp.cause.class);
                    return;
                }
                console.log('Datapoint "' + datapointAddress+ '" info received was "'+rsp.value+'".');
                var datapoint = rsp.datapoint;

                var $dpointCont = $('<div class="container soaba-gauge"></div>');
                $cont.append($dpointCont);

                soaba.utils.createGauge($dpointCont, datapoint,
                    function (chart) {
                         $.getJSON(soaba.APP_URL + 'datapoints/' + datapoint.id, function(rsp){
                             if(typeof rsp.stackTrace !== 'undefined'){
                                 console.log('Error: datapoint ' + datapoint.name + ' read exception '
                                 + rsp.message + ' -->> ' + rsp.cause.class);
                                 return;
                             }

                             var point = chart.series[0].points[0];
                             point.update(rsp.value);
                         });
                    }, GAUGE_UPD_INTERVAL);

                $dpointCont.highcharts().series[0].points[0].update(rsp.value);
            });
        });

        $.each(lineChartDatapoints, function(i, datapointAddress){
            var $cont = $('#lineChartContainer');
            console.log('Adding datapoint "' + datapointAddress+ '" to diagnostics panel.');

            $.getJSON(soaba.APP_URL + 'datapoints/' + datapointAddress, function(rsp){
                console.log('Datapoint "' + datapointAddress+ '" info received.');
                if(typeof rsp.stackTrace !== 'undefined'){
                    console.log('Error: datapoint ' + datapointAddress + ' read exception '
                    + rsp.message + ' -->> ' + rsp.cause.class);
                    return;
                }
                console.log('Datapoint "' + datapointAddress+ '" info received was "'+rsp.value+'".');
                var datapoint = rsp.datapoint;
                
                var $dpointCont = $('<div class="container soaba-linechart"></div>');
                $cont.append($dpointCont);

                soaba.utils.createLineChart($dpointCont, datapoint,
                    function (chart) {
                        $.getJSON(soaba.APP_URL + 'datapoints/' + datapoint.id, function(rsp){
                            if(typeof rsp.stackTrace !== 'undefined'){
                                console.log('Error: datapoint ' + datapoint.readAddress + ' read exception '
                                + rsp.message + ' -->> ' + rsp.cause.class);
                                return;
                            }

                            var x = (new Date()).getTime(), // current time
                                y = parseFloat(rsp.value);

                            console.log('Adding point values x:"'+x+'" and y:"'+y+'"');
                            chart.series[0].addPoint([x, y], true, true);
                        });
                    }, LINECHART_UPD_INTERVAL);

                $dpointCont.highcharts().series[0].addPoint([(new Date()).getTime(), parseFloat(rsp.value)], true, true);
            });
        });
    });
})();