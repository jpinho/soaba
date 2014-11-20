$(function(){

    var APP_URL = 'http://sb-dev.tagus.ist.utl.pt:9095/soaba/';

    /**
     * Private Functions
     */
    function stripGatewayPackageName(gatewayDriverFullName){
        return gatewayDriverFullName.replace('soaba.core.gateways.drivers.', '');
    }

    function renderDatapointInput(dataType){
        $('#contDValue').empty().append($('<input type="text" class="form-control" />'));
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

    function scrollIntoView(target){
        $('body').animate({scrollTop: $(target).offset().top});
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
        }
        , { 'render': function (data, type, row) { return data.toLowerCase(); }, 'targets': 3 }
        , {
            'render': function (data, type, row) {
                if(typeof data === 'undefined' || data == null || data.length == null)
                    return '---';
                return data;
            }
            , 'targets': 4
        }, {
            'render': function (data, type, row) {
                if(typeof data === 'undefined' || data == null || data.length == null)
                    return '---';
                return data;
            }
            , 'targets': 5
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

        $('.datapoint-read-address').val(
            typeof data.readAddress === 'undefined' || data.readAddress == null || data.readAddress.length == null ?
                "---" : data.readAddress);

        $('.datapoint-write-address').val(
            typeof data.writeAddress === 'undefined' || data.writeAddress == null || data.writeAddress.length == null ?
                "---" : data.writeAddress);

        renderDatapointInput(data.dataType);
        trimUnsupportedOperations(data.accessType);
        $('.btn-group.operations button, .btn-group.operations li').removeClass('active');

        // scrolls details view into view only if necessary
        if($('body').scrollTop() < $('.datapointMetaPanel').offset().top)
            scrollIntoView('.datapointMetaPanel');
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
            'render': function (data, type, row) { return stripGatewayPackageName(data); }, 'targets': 0
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
    });

    $('.tabs .dropdown-menu a').click(function(){
        $(this).parent().siblings().removeClass('active');
        $(this).parents('.btn-group').first().find('button').addClass('active');
    });

    $('.tabs .dropdown-menu #tbDPRead').click(function(){
        $('#tabDatapointOperations .operations .btn').hide();
        $('#tabDatapointOperations #contDValue input').attr('readonly', 'readonly');
        $('#btnDatapointRead').show();
    });
    $('.tabs .dropdown-menu #tbDPWrite').click(function(){
        $('#tabDatapointOperations .operations .btn').hide();
        $('#tabDatapointOperations #contDValue input').removeAttr('readonly');
        $('#btnDatapointWrite').show();
    });

    $('#btnDatapointRead').click(function () {
        var info = $('#tblDatapoints').DataTable().rows('.warning.selected').data()[0];
        var url = APP_URL + 'datapoints/' + info.id;
        var $btn = $(this).button('loading');
        $('#dpOperationResult').slideUp().find('.panel-body').empty();

        $.getJSON(url, function(rsp){
            if(typeof rsp.stackTrace === 'object'){
                $('#dpOperationResult .panel').removeClass('panel-info').addClass('panel-danger');
                $('#dpOperationResult .panel-body').html(
                    '<div><b>Exception:</b> ' + rsp.cause.class + ' </div><br/>' +
                    '<div><b>Message:</b> ' + rsp.message + ' </div>'
                );
                $('#dpReadOperationResult').slideDown().get(0).scrollIntoView();
                return;
            }

            $('#dpOperationResult .panel').removeClass('panel-danger').addClass('panel-info');
            $('#dpOperationResult .panel-body').html(
                '<div><b>Value:</b> ' + rsp.value + '</div><br/>' +
                '<div><b>Data Type:</b> ' + rsp.datapoint.dataType + '</div>'
            );
            $('#dpOperationResult').slideDown();
            scrollIntoView('#dpOperationResult');

            $('#contDValue input').val(rsp.value);
        })
        .always(function(){
            $btn.button('reset');
        })
        .fail(function(xhr, status, message){
            $('#dpOperationResult').removeClass('panel-info').addClass('panel-danger');
            $('#dpOperationResult .panel-body').html(
                '<div><b>Unknown Error:</b> ' + message + '</div>'
            );
            $('#dpOperationResult').slideDown().get(0).scrollIntoView();
        });
    });

    $('#btnDatapointWrite').click(function () {
        var info = $('#tblDatapoints').DataTable().rows('.warning.selected').data()[0];
        var value = $('#contDValue input').val();
        var url = APP_URL + 'datapoints/' + info.id + '/' + value;
        var $btn = $(this).button('loading');
        $('#dpOperationResult').slideUp().find('.panel-body').empty();

        $.getJSON(url, function(rsp){
            if(typeof rsp.stackTrace === 'object'){
                $('#dpOperationResult .panel').removeClass('panel-info').addClass('panel-danger');
                $('#dpOperationResult .panel-body').html(
                    '<div><b>Exception:</b> ' + rsp.cause.class + ' </div><br/>' +
                    '<div><b>Message:</b> ' + rsp.message + ' </div>'
                );
                $('#dpOperationResult').slideDown();
                scrollIntoView('#dpOperationResult');
                return;
            }

            $('#dpOperationResult .panel').removeClass('panel-danger').addClass('panel-info');
            $('#dpOperationResult .panel-body').html(
                '<div><b>Value:</b> ' + rsp.value + '</div><br/>' +
                '<div><b>Data Type:</b> ' + rsp.datapoint.dataType + '</div>'
            );
            $('#dpOperationResult').slideDown();
            scrollIntoView('#dpOperationResult');

            $('#contDValue input').val(rsp.value);
        })
            .always(function(){
                $btn.button('reset');
            })
            .fail(function(xhr, status, message){
                $('#dpOperationResult').removeClass('panel-info').addClass('panel-danger');
                $('#dpOperationResult .panel-body').html(
                    '<div><b>Unknown Error:</b> ' + message + '</div>'
                );
                $('#dpOperationResult').slideDown().get(0).scrollIntoView();
            });
    });
});
