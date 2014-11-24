/**
 * SOABA Diagnostics Page
 *
 * @author João Pinho
 */
"use strict";

(function() {
    var GAUGE_UPD_INTERVAL = 10 * 1000, LINECHART_UPD_INTERVAL = 0.5 * 1000;
    var METEO_STATION_PREFIX = 'meteo station';

    /**
     * Page Load
     */
    $(function () {
        var $cont = $('#gaugeContainer');

        var datapoints = [
            '0.6.27', '0.6.5', '0.6.5', '0.6.7', '0.6.8', '0.6.10',
            '0.6.11', '0.6.22', '0.6.25', '0.6.27','0.6.28','0.6.29'
        ];

        $.each(datapoints, function(i, datapointAddress){
            $.getJSON(soaba.APP_URL + 'datapoints/' + datapointAddress, function(rsp){
                if(typeof rsp.stackTrace !== 'undefined'){
                    console.log('Error: datapoint ' + datapointAddress + ' read exception '
                    + rsp.message + ' -->> ' + rsp.cause.class);
                    return;
                }
                var datapoint = rsp.datapoint;

                if((datapoint.dataType.toLowerCase() == 'tiny_number' ||
                   datapoint.dataType.toLowerCase() == 'number' ||
                   datapoint.dataType.toLowerCase() == 'percentage') &&
                   datapoint.name.toLowerCase().indexOf(METEO_STATION_PREFIX) >= 0) {

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
                }
                else if((datapoint.dataType.toLowerCase() == 'tiny_number' ||
                        datapoint.dataType.toLowerCase() == 'number') &&
                        datapoint.name.toLowerCase().indexOf(METEO_STATION_PREFIX) >= 0) {

                    var $dpointCont = $('<div class="container soaba-linechart"></div>');
                    $cont.append($dpointCont);

                    soaba.utils.createLineChart($dpointCont, datapoint,
                        function (series) {
                            $.getJSON(soaba.APP_URL + 'datapoints/' + datapoint.id, function(rsp){
                                if(typeof rsp.stackTrace !== 'undefined'){
                                    console.log('Error: datapoint ' + datapoint.readAddress + ' read exception '
                                        + rsp.message + ' -->> ' + rsp.cause.class);
                                    return;
                                }

                                var x = (new Date()).getTime(), // current time
                                    y = rsp.value;
                                series.addPoint([x, y], true, true);
                            });
                        }, LINECHART_UPD_INTERVAL);

                    $dpointCont.highcharts().series[0].addPoint([(new Date()).getTime(), rsp.value], true, true);
                }
            });
        });
    });
})();