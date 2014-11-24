/**
 * SOABA Energy Viewer Page
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
            {address:'e0f2bcacb07b775f53c892bb8765cb0ec17b581d', min:0, max:100, plotBands: [
                { from: 0, to: 30, color: '#DDDF0D'    /* yellow */ },
                { from: 30, to: 80, color: '#55BF3B'   /* green */ },
                { from: 80, to: 90, color: '#DDDF0D'   /* yellow */ },
                { from: 90, to: 100, color: '#DF5353'  /* red */ }]},

            {address:'ebba6a55b3d72bad41f27c1ac552a9ef3aba29c2', min:0, max:100, plotBands: [
                { from: 0, to: 30, color: '#DDDF0D'    /* yellow */ },
                { from: 30, to: 80, color: '#55BF3B'  /* green */ },
                { from: 80, to: 90, color: '#DDDF0D'    /* yellow */ },
                { from: 90, to: 100, color: '#DF5353'  /* red */ }]},

            {address:'74d084d6b7e50a1c5becfbaa950a4616acd7125e', min:0, max:100, plotBands: [
                { from: 0, to: 30, color: '#DDDF0D'    /* yellow */ },
                { from: 30, to: 80, color: '#55BF3B'  /* green */ },
                { from: 80, to: 90, color: '#DDDF0D'    /* yellow */ },
                { from: 90, to: 100, color: '#DF5353'  /* red */ }]},

            {address:'97143a22558c6707d7f9c6fca6764e827326b4a0', min:0, max:100, plotBands: [
                { from: 0, to: 30, color: '#DDDF0D'    /* yellow */ },
                { from: 30, to: 80, color: '#55BF3B'  /* green */ },
                { from: 80, to: 90, color: '#DDDF0D'    /* yellow */ },
                { from: 90, to: 100, color: '#DF5353'  /* red */ }]},

            {address:'55bf71ae9e17042bfda0f8c58bbabc9d7b4b3611', min:0, max:100, plotBands: [
                { from: 0, to: 30, color: '#DDDF0D'    /* yellow */ },
                { from: 30, to: 80, color: '#55BF3B'  /* green */ },
                { from: 80, to: 90, color: '#DDDF0D'    /* yellow */ },
                { from: 90, to: 100, color: '#DF5353'  /* red */ }]},

            {address:'3ca05a0fffd2f48677c788263cf8e4fd1f473cc3', min:0, max:100, plotBands: [
                { from: 0, to: 30, color: '#DDDF0D'    /* yellow */ },
                { from: 30, to: 80, color: '#55BF3B'  /* green */ },
                { from: 80, to: 90, color: '#DDDF0D'    /* yellow */ },
                { from: 90, to: 100, color: '#DF5353'  /* red */ }]},

            {address:'7bc0fcec4fd0a3308ab24d5774dc567a3df242d6', min:0, max:100, plotBands: [
                { from: 0, to: 30, color: '#DDDF0D'    /* yellow */ },
                { from: 30, to: 80, color: '#55BF3B'  /* green */ },
                { from: 80, to: 90, color: '#DDDF0D'    /* yellow */ },
                { from: 90, to: 100, color: '#DF5353'  /* red */ }]},

            {address:'2998280bbfb3ae01c69bf153b755f7e21d13f00f', min:0, max:100, plotBands: [
                { from: 0, to: 30, color: '#DDDF0D'    /* yellow */ },
                { from: 30, to: 80, color: '#55BF3B'  /* green */ },
                { from: 80, to: 90, color: '#DDDF0D'    /* yellow */ },
                { from: 90, to: 100, color: '#DF5353'  /* red */ }]}
        ];

        var lineChartDatapoints = [
            'd6e53cd9291b660bbbe6eed278e9b78046ff5dbb', '6fad55fda63d4be648e13bd29336c0ab809adaff',
            '3d6afcee653f419069fe5134d14b830348b1fa7f'];

        $.each(gaugeDatapoints, function(i, info){
            var $cont = $('#energyGaugeContainer');
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
            var $cont = $('#energyLineChartContainer');
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