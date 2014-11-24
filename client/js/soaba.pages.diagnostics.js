/**
 * SOABA Diagnostics Page
 *
 * @author João Pinho
 */
"use strict";

(function() {
    var GAUGE_UPD_INTERVAL = 5000, LINECHART_UPD_INTERVAL = 5000;
    var METEO_STATION_PREFIX = 'meteo station';

    /**
     * Page Load
     */
    $(function () {
        var $cont = $('#gaugeContainer');

        $.getJSON(soaba.APP_URL + 'datapoints', function(rsp){
            $.each(rsp, function(i, datapoint){
                if(datapoint.dataType.toLowerCase() == 'percentage' &&
                   datapoint.name.toLowerCase().indexOf(METEO_STATION_PREFIX) >= 0) {

                    var $dpointCont = $('<div class="container soaba-gauge"></div>');
                    $cont.append($dpointCont);

                    soaba.utils.createGauge($dpointCont, datapoint,
                        function (chart) {
                             $.getJSON(soaba.APP_URL + 'datapoints/' + datapoint.id, function(rsp){
                                 var point = chart.series[0].points[0];
                                 debugger;
                                 point.update(rsp.value);
                             });
                        }, GAUGE_UPD_INTERVAL);
                }
                else if((datapoint.dataType.toLowerCase() == 'tiny_number' ||
                        datapoint.dataType.toLowerCase() == 'number') &&
                        datapoint.name.toLowerCase().indexOf(METEO_STATION_PREFIX) >= 0) {

                    var $dpointCont = $('<div class="container soaba-linechart"></div>');
                    $cont.append($dpointCont);

                    soaba.utils.createLineChart($dpointCont, datapoint,
                        function (series) {
                            $.getJSON(soaba.APP_URL + 'datapoints/' + datapoint.id, function(rsp){
                                var x = (new Date()).getTime(), // current time
                                    y = rsp.value;
                                debugger;
                                series.addPoint([x, y], true, true);
                            });
                        }, LINECHART_UPD_INTERVAL);
                }
            });
        });
    });
})();