/**
 * SOABA Datapoints Page
 *
 * @author João Pinho
 */
"use strict";

(function(){

    function renderDatapointInput(dataType){
        var $cont = $('#contDValue').empty();

        switch(dataType.toLowerCase()){
            case 'bit':
                $('<input type="checkbox" class="switch dpoint-value" />')
                    .appendTo($cont).bootstrapSwitch();
                break;
            case 'percentage':
                $('<div class="slider dpoint-value"></div>')
                    .appendTo($cont).slider({
                        range: "min",
                        animate: true,
                        value:0,
                        min: 0,
                        max: 100,
                        step: 10,
                        slide: function(event, ui) { $cont.find('.slider-text').text(ui.value); }
                    });
                $('<span class="slider-text label label-primary">0</span>').appendTo($cont);
                break;
            case 'tiny_number':
            case 'number':
                $('<input class="updown dpoint-value" value="0" />')
                    .appendTo($cont).spinner({
                        step: 0.01,
                        numberFormat: "n"
                    });
                break;
            case 'string':
            default:
                $cont.append($('<input type="text" class="form-control dpoint-value" />'));
        }
    }

    function getDataTypeDisplayValue(dataType){
        return dataType.toLowerCase().replace(/_/g, ' ').replace('tiny number', 'number (2B)')
    }

    function getAccessTypeDisplayValue(accessType){
        switch(accessType.toUpperCase()){
            case 'READ_ONLY': return 'Read Only';
            case 'WRITE_ONLY': return 'Write Only';
            case 'READ_WRITE': return 'Read/Write';
            default: return '---';
        }
    }

    function trimUnsupportedOperations(accessType){
        $('.btn-group.operations').show();

        switch(accessType){
            case 'READ_ONLY':
                $('#tbDPRead').show().parent().siblings().find('a').hide();
                break;
            case 'WRITE_ONLY':
                $('#tbDPWrite').show().parent().siblings().find('a').hide();
                break;
            case 'READ_WRITE':
                $('#lstDPTabOperations a').show();
                break;
            default:
                $('.btn-group.operations').hide();
        }
    }

    function showResult(){
        $('#dpOperationResult').slideDown();
        soaba.utils.scrollIntoView('#dpOperationResult');
    }

    function hideResult(){
        $('#dpOperationResult').slideUp();
        $('#dpOperationResult .panel-body').empty();
    }

    function setErrorMessage(message){
        $('#dpOperationResult').removeClass('panel-info').addClass('panel-danger');
        $('#dpOperationResult .panel-body').html(
            '<div><b>Unknown Error:</b> ' + message + '</div>'
        );
    }

    function setExceptionMessage(message, cause){
        $('#dpOperationResult .panel').removeClass('panel-info').addClass('panel-danger');
        $('#dpOperationResult .panel-body').html(
            '<div><b>Exception:</b> ' + cause + ' </div><br/>' +
            '<div><b>Message:</b> ' + message + ' </div>'
        );
    }

    function setResultText(message){
        $('#dpOperationResult .panel').removeClass('panel-danger').addClass('panel-info');
        $('#dpOperationResult .panel-body').html(message);
    }

    function dataBind(){
        $('#tblDatapoints').DataTable({
            'ajax': soaba.APP_URL + 'datapoints'
            ,'sAjaxDataProp': null
            ,'fnDrawCallback':function(){ soaba.appLoadingThreads.pop(); }
            ,'columns': [
                { 'title': 'Name', 'data': 'name' }
                ,{ 'title': 'Gateway (IP Address)', 'data': 'gatewayAddress' }
                ,{ 'title': 'Access', 'data': 'accessType' }
                ,{ 'title': 'Data Type', 'data': 'dataType' }
                ,{ 'title': 'Read Address', 'data': 'readAddress' }
                ,{ 'title': 'Write Address', 'data': 'writeAddress' }
            ]
            ,'columnDefs': [{
                    'render': function (data, type, row) {
                        return getAccessTypeDisplayValue(data);
                    }
                    , 'targets': 2 }
                , {
                    'render': function (data, type, row) {
                        return getDataTypeDisplayValue(data);
                    }
                    , 'targets': 3 }
                , {
                    'render': function (data, type, row) {
                        if(typeof data === 'undefined' || data == null || data.length == 0)
                            return '---';
                        return data;
                    }
                    , 'targets': 4 }
                , {
                    'render': function (data, type, row) {
                        if(typeof data === 'undefined' || data == null || data.length == 0)
                            return '---';
                        return data;
                    }
                    , 'targets': 5
                }]
        });

        soaba.appLoadingThreads.push({page: 'datapoints'});
    }

    function attachDataTableEvents(){
        $('#tblDatapoints').DataTable().on('xhr.dt', function ( e, settings, json ) {
            if(typeof json !== 'undefined' || json != null || json.length)
                $('#datapointsCount').text(json.length);
        });

        $('#tblDatapoints tbody').on('click', 'tr', function () {
            $('#tblDatapoints tr').removeClass('warning selected');
            $(this).addClass('warning selected');
            var data = $('#tblDatapoints').DataTable().row(this).data();

            $('.datapointMetaPanel a[href="#tabDatapointInfo"]').tab('show');
            $('.datapointMetaPanel').slideDown();
            $('#txtDatapointName').val(data.name);
            $('#txtGatewayAddress').val(data.gatewayAddress);
            $('#txtDatapointAccess').val(getAccessTypeDisplayValue(data.accessType));
            $('#txtDatapointDataType').val(getDataTypeDisplayValue(data.dataType));

            $('.datapoint-read-address').val(
                typeof data.readAddress === 'undefined' || data.readAddress == null || data.readAddress.length == 0 ?
                    "---" : data.readAddress);

            $('.datapoint-write-address').val(
                typeof data.writeAddress === 'undefined' || data.writeAddress == null || data.writeAddress.length == 0 ?
                    "---" : data.writeAddress);

            renderDatapointInput(data.dataType);
            trimUnsupportedOperations(data.accessType);
            $('.btn-group.operations button, .btn-group.operations li').removeClass('active');

            // scrolls details view into view only if necessary
            if($('body').scrollTop() < $('.datapointMetaPanel').offset().top)
                soaba.utils.scrollIntoView('.datapointMetaPanel');
        });
    }

    function executeDatapointRead(){
        var info = $('#tblDatapoints').DataTable().rows($('#tblDatapoints tr.warning.selected').get(0)).data()[0];
        var url = soaba.APP_URL + 'datapoints/' + info.id;
        var $btn = $(this).button('loading');
        $('#dpOperationResult').slideUp().find('.panel-body').empty();

        $.getJSON(url, function(rsp){
            if(typeof rsp !== "undefined" && typeof rsp.stackTrace === 'object')
                setExceptionMessage(rsp.message, rsp.cause.class);
            else if(typeof rsp !== "undefined" && rsp != null) {
                setResultText(
                    '<div><b>Value:</b> ' + rsp.value + '</div><br/>' +
                    '<div><b>Data Type:</b> ' + rsp.datapoint.dataType + '</div>'
                );
                updateDatapointValue(rsp.datapoint, rsp.value);
            }
            else setResultText('Request sent, but received response is "null"!');
            showResult();
        })
        .always(function(){
            $btn.button('reset');
        })
        .fail(function(xhr, status, message){
            setErrorMessage(message);
            showResult();
        });
    }

    function executeDatapointWrite() {
        var info = $('#tblDatapoints').DataTable().rows($('#tblDatapoints tr.warning.selected').get(0)).data()[0];
        var value = getDatapointValue(info);
        var url = soaba.APP_URL + 'datapoints/' + info.id + '/' + value;
        var $btn = $(this).button('loading');
        $('#dpOperationResult').slideUp().find('.panel-body').empty();

        $.getJSON(url, function (rsp) {
            if (typeof rsp !== "undefined" && rsp != null && typeof rsp.stackTrace === 'object')
                setExceptionMessage(rsp.message, rsp.cause.class);
            else {
                updateDatapointValue(info, value);
                setResultText('Write Request Sent!');
            }
            showResult();
        })
        .always(function () {
            $btn.button('reset');
        })
        .fail(function (xhr, status, message) {
            setErrorMessage(message);
            showResult();
        });
    }

    function updateDatapointValue(datapoint, value){
        var $value = $('#contDValue .dpoint-value');

        switch(datapoint.dataType.toLocaleLowerCase()){
            case 'bit':
                var disabled = $value.bootstrapSwitch('disabled');
                if(disabled) $value.bootstrapSwitch('disabled', false);
                $value.bootstrapSwitch('state', value);
                if(disabled) $value.bootstrapSwitch('disabled', true);
                break;
            case 'percentage':
                $value.slider('option', 'value', value);
                $('#contDValue .slider-text').text(value);
                break;
            case 'tiny_number':
            case 'number':
                $value.spinner('value', value);
                break;
            case 'string':
            default:
                $value.val(value);
        }
    }

    function getDatapointValue(datapoint){
        var $value = $('#contDValue .dpoint-value');

        switch(datapoint.dataType.toLowerCase()){
            case 'bit':
                return $value.bootstrapSwitch('state');
            case 'percentage':
                return $value.slider('option', 'value');
            case 'tiny_number':
            case 'number':
                return $value.spinner('value');
            case 'string':
            default:
                return $value.val();
        }
    }

    function setupTabOperations(){
        $('.tabs a').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
            $(this).parent().siblings().find('.btn-group > button, .btn-group .dropdown-menu li').removeClass('active');
            hideResult();
        });

        $('.tabs .dropdown-menu a').click(function(){
            $(this).parent().siblings().removeClass('active');
            $(this).parents('.btn-group').first().find('button').addClass('active');
        });

        $('.tabs .dropdown-menu #tbDPRead').click(function(){
            var info = $('#tblDatapoints').DataTable().rows($('#tblDatapoints tr.warning.selected').get(0)).data()[0];

            $('#tabDatapointOperations .operations .btn').hide();
            var $value = $('#tabDatapointOperations #contDValue .dpoint-value');

            switch(info.dataType.toLowerCase()){
                case 'bit':
                    $value.bootstrapSwitch('disabled', true);
                    break;
                case 'percentage':
                    $value.slider('disable');
                    break;
                case 'tiny_number':
                case 'number':
                    $value.spinner('disable');
                    break;
                case 'string':
                default:
                    $value.prop('disabled', true);
            }

            $('#btnDatapointRead').show();
        });

        $('.tabs .dropdown-menu #tbDPWrite').click(function(){
            var info = $('#tblDatapoints').DataTable().rows($('#tblDatapoints tr.warning.selected').get(0)).data()[0];

            $('#tabDatapointOperations .operations .btn').hide();
            var $value = $('#tabDatapointOperations #contDValue .dpoint-value');

            switch(info.dataType.toLowerCase()){
                case 'bit':
                    $value.bootstrapSwitch('disabled', false);
                    break;
                case 'percentage':
                    $value.slider('enable');
                    break;
                case 'tiny_number':
                case 'number':
                    $value.spinner('enable');
                    break;
                case 'string':
                default:
                    $value.prop('disabled', false);
            }

            $('#btnDatapointWrite').show();
        });
    }


    /**
     * Page Load
     */
    $(function(){

        /** datapoints page setup **/
        dataBind();
        attachDataTableEvents();


        /** datapoint tabs setup **/
        setupTabOperations();

        /** eventhandlers setup **/
        $('#btnDatapointRead').click(executeDatapointRead);
        $('#btnDatapointWrite').click(executeDatapointWrite);
    });
})();