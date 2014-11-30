/**
 * SOABA Control Panel Page
 *
 * @author João Pinho
 */
"use strict";

(function() {
    var UPD_INTERVAL = 15 * 1000;

    /**
     * Page Load
     */
    $(function () {
        soaba.appLoadingThreads.push({page: 'cpanel'});

        var dataLength= 1,
            loadedDataCount= 0,
            updateFunctions = [];

        $('.loader-info').html('loading "Control Panel" app');

        $.getJSON(soaba.APP_URL + 'datapoints', function(rsp){
            if(typeof rsp.stackTrace !== 'undefined') {
                console.log('Error: ' + rsp.message + ' -->> ' + rsp.cause ? rsp.cause.class : '');
                return;
            }

            $.each(rsp, function(i, datapoint){
                var update = null;

                if(datapoint.dataType.toLowerCase() == 'bit' &&
                   datapoint.accessType.toLowerCase() != 'read_only'){
                    var $cont = $('<div class="switch-ctn"><span>'+ datapoint.name +'</span><br/><input type="checkbox" /></div>')
                        .appendTo('#switchControls');

                    $cont.find('input').bootstrapSwitch({size:'small', onSwitchChange: function(evt, state){
                        var value = state;
                        var url = soaba.APP_URL + 'datapoints/' + datapoint.id + '/' + value;

                        $.getJSON(url, function (rsp) {
                            if (typeof rsp !== "undefined" && rsp != null && typeof rsp.stackTrace === 'object')
                                console.log('Error: ' + rsp.message);
                        })
                        .fail(function (xhr, status, message) {
                                console.log('Error: ' + message);
                        });
                    }});

                    updateFunctions.push(function(){
                        $.getJSON(soaba.APP_URL + 'datapoints/' + datapoint.id, function(rsp){
                            if(typeof rsp.stackTrace !== 'undefined'){
                                console.log('Error: ' + rsp.message);
                                return;
                            }
                            $cont.find('input').bootstrapSwitch('state', rsp.value, true);
                        });
                    });
                }
                else if(datapoint.dataType.toLowerCase() == 'percentage' &&
                    datapoint.accessType.toLowerCase() != 'read_only'){

                    var isHVACRelated = datapoint.name.toLowerCase().indexOf('hvac') >= 0;

                    var $cont = $('<div class="percentage-ctn"><span>'+ datapoint.name +'</span><br/><div class="p-control"></div>'
                        + '<span class="slider-text label label-primary">0</span></div>').appendTo(isHVACRelated ? '#hvacControls' : '#percentageControls');

                    $cont.find('.p-control').slider({
                        range: "min",
                        animate: true,
                        value:0,
                        min: 0,
                        max: 100,
                        step: 10,
                        slide: function(event, ui) {
                            $cont.find('.slider-text').text(ui.value);
                            var value = ui.value;
                            var url = soaba.APP_URL + 'datapoints/' + datapoint.id + '/' + value;

                            $.getJSON(url, function (rsp) {
                                if (typeof rsp !== "undefined" && rsp != null && typeof rsp.stackTrace === 'object')
                                    console.log('Error: ' + rsp.message);
                            })
                            .fail(function (xhr, status, message) {
                                console.log('Error: ' + message);
                            });
                        }
                    });

                    updateFunctions.push(function(){
                        $.getJSON(soaba.APP_URL + 'datapoints/' + datapoint.id, function(rsp){
                            if(typeof rsp.stackTrace !== 'undefined'){
                                console.log('Error: ' + rsp.message);
                                return;
                            }
                            $cont.find('.p-control').slider('option', 'value', rsp.value);
                            $cont.find('.slider-text').text(rsp.value);
                        });
                    });
                }
            });
        }).always(function(){
            loadedDataCount++;
            $.each(updateFunctions, function(i, fn){
                setInterval(fn, UPD_INTERVAL);
                fn();
            });
        });

        var loader = setInterval(function(){
            if(loadedDataCount == dataLength){
                $('.loader-info').html('loading of "Control Panel" app completed');
                soaba.appLoadingThreads.pop();
                clearTimeout(loader);
                $('#pageSwitchControlPanel').attr('loaded', 'true');
                $('#loader-wrapper').hide();
            }
        }, 1000);
    });
})();