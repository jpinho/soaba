/**
 * SOABA Meteo Station Page
 *
 * @author João Pinho
 */
"use strict";

(function() {
    var GAUGE_UPD_INTERVAL = 5 * 1000, LINECHART_UPD_INTERVAL = 2 * 1000;

    /**
     * Page Load
     */
    $(function () {
        var gaugeDatapoints = [
            {address:'0.4.0', min:0, max:1000, plotBands: [
                { from: 0, to: 400, color: '#55BF3B'    /* green */ },
                { from: 400, to: 700, color: '#DDDF0D'  /* yellow */ },
                { from: 700, to: 1000, color: '#DF5353'  /* red */ }]},

            {address:'0.4.1', min:0, max:100, plotBands: [
                { from: 0, to: 30, color: '#DDDF0D'    /* yellow */ },
                { from: 30, to: 80, color: '#55BF3B'  /* green */ },
                { from: 80, to: 90, color: '#DDDF0D'    /* yellow */ },
                { from: 90, to: 100, color: '#DF5353'  /* red */ }]},

            {address:'0.4.3', min:0, max:100, plotBands: [
                { from: 0, to: 15, color: '#DDDF0D'  /* yellow */ },
                { from: 15, to: 30, color: '#55BF3B'    /* green */ },
                { from: 30, to: 40, color: '#DDDF0D'  /* yellow */ },
                { from: 40, to: 100, color: '#DF5353'  /* red */ }]},

            {address:'0.6.20', min:0, max:100, plotBands: [
                { from: 0, to: 15, color: '#DDDF0D'  /* yellow */ },
                { from: 15, to: 30, color: '#55BF3B'    /* green */ },
                { from: 30, to: 40, color: '#DDDF0D'  /* yellow */ },
                { from: 40, to: 100, color: '#DF5353'  /* red */ }]}
        ];

        var lineChartDatapoints = [
            '0.6.27', '0.6.22', '0.6.25','0.6.28'];

        $.each(gaugeDatapoints, function(i, info){
            var $cont = $('#gaugeContainer');
            console.log('Adding datapoint "' + info.address + '" to diagnostics panel.');

            $.getJSON(soaba.APP_URL + 'datapoints/' + info.address, function(rsp){
                console.log('Datapoint "' + info.address+ '" info received.');
                if(typeof rsp.stackTrace !== 'undefined'){
                    console.log('Error: datapoint ' + info.address + ' read exception '
                    + rsp.message + ' -->> ' + rsp.cause.class);
                    return;
                }
                console.log('Datapoint "' + info.address + '" info received was "'+rsp.value+'".');
                var datapoint = rsp.datapoint;

                var $dpointCont = $('<div class="cnt soaba-gauge"></div>');
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
                    }, GAUGE_UPD_INTERVAL, info.min, info.max, info.plotBands);

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

                var $dpointCont = $('<div class="cnt soaba-linechart"></div>');
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