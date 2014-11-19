$(function(){

    var APP_URL = 'http://localhost:8095/soaba/';

    function stripGatewayPackageName(gatewayDriverFullName){
        return gatewayDriverFullName.replace('soaba.core.gateways.drivers.', '');
    }

    /**
     * Datapoints
     */
    $('#tblDatapoints').DataTable({
         'ajax': APP_URL + 'datapoints'
        ,'sAjaxDataProp': null
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
                switch(data){
                    case 'READ_ONLY': return 'Read Only';
                    case 'WRITE_ONLY': return 'Write Only';
                    case 'READ_WRITE': return 'Read/Write';
                    default: return '---';
                }
            }
            , 'targets': 2
        }, {
            'render': function (data, type, row) {
                if(typeof data === 'undefined' || data == null || data.length == null)
                    return '---';
                return data;
            }
            , 'targets': 4
        }]
    }).on('xhr.dt', function ( e, settings, json ) {
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
        $('#txtDatapointAccess').val(data.accessType);
        $('#txtDatapointDataType').val(data.dataType);
        $('.datapoint-read-address').val(data.readAddress);
        $('#txtDatapointWriteAddress').val(data.writeAddress);
    });


    /**
     * Gateways
     */
    $('#tblGatewayDrivers').DataTable({
        'ajax': APP_URL + 'gateways'
        ,'sAjaxDataProp': null
        ,'columns': [
             { 'title': 'Driver Type', 'data': 'class' }
            ,{ 'title': 'Description', 'data': 'description' }
            ,{ 'title': 'Gateway Address (IP Address)', 'data': 'address' }
            ,{ 'title': 'IsConnected', 'data': 'connected' }
        ]
        ,'columnDefs': [{
            'render': function (data, type, row) {
                return stripGatewayPackageName(data);
            }
            , 'targets': 0
        }]
    }).on('xhr.dt', function ( e, settings, json ) {
        if(typeof json !== 'undefined' || json != null || json.length)
            $('#gatewaysCount').text(json.length);
    });

    $('#tblGatewayDrivers tbody').on('click', 'tr', function () {
        $(this).addClass('warning selected').siblings().removeClass('warning selected');
        var data = $('#tblGatewayDrivers').DataTable().rows('.warning.selected').data()[0];

        $('.gatewayMetaPanel a[href="#tabGatewaysInfo"]').tab('show');
        $('.gatewayMetaPanel').slideDown();
        $('#txtGatewayDriver').val(stripGatewayPackageName(data.class));
        $('#txtGatewayIPAddress').val(data.address);
        $('#txtGatewayIsConnected').val(data.connected);
    });


    /**
     * Navigation :: Page Navigation
     */
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

    $('.tabs a').click(function(e) {
        e.preventDefault();
        $(this).tab('show');
        $(this).parent().siblings().find('.btn-group > button, .btn-group .dropdown-menu li').removeClass('active');
    })

    $('.tabs .dropdown-menu a').click(function(){
        $(this).parent().siblings().removeClass('active');
        $(this).parents('.btn-group').first().find('button').addClass('active');
    });

    $('#btnDatapointRead').click(function () {
        var data = $('#tblDatapoints').DataTable().rows('.warning.selected').data()[0];
        var url = APP_URL + 'datapoints/' + data.id;
        var $btn = $(this).button('loading');
        $('#dpReadOperationResult').slideUp();

        $.getJSON(url, function(data){
            if(typeof data.stackTrace === 'object'){
                $('#dpReadOperationResult .panel').removeClass('panel-info').addClass('panel-danger');
                $('#dpReadOperationResult .panel-body').html(
                    '<div><b>Exception:</b> ' + data.cause.class + ' </div><br/>' +
                    '<div><b>Message:</b> ' + data.message + ' </div>'
                );
                $('#dpReadOperationResult').slideDown().get(0).scrollIntoView();
                return;
            }

            $('#dpReadOperationResult .panel').removeClass('panel-danger').addClass('panel-info');
            $('#dpReadOperationResult .panel-body').html(
                '<div><b>Value:</b> ' + data.value + '</div><br/>' +
                '<div><b>Data Type:</b> ' + data.datatpoint.dataType + '</div>'
            );
            $('#dpReadOperationResult').slideDown().get(0).scrollIntoView();
        })
        .always(function(){
            $btn.button('reset');
        })
        .fail(function(xhr, status, message){
            $('#dpReadOperationResult').removeClass('panel-info').addClass('panel-danger');
            $('#dpReadOperationResult .panel-body').html(
                '<div><b>Unknown Error:</b> ' + message + '</div>'
            );
            $('#dpReadOperationResult').slideDown().get(0).scrollIntoView();
        });
    });
});
