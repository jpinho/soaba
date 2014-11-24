/**
 * SOABA Gateways Page
 *
 * @author João Pinho
 */

"use strict";

(function(){

    function stripGatewayPackageName(pkg) {
        return pkg.replace('soaba.core.gateways.drivers.', '');
    }

    function dataBind() {
        $('#tblGatewayDrivers').DataTable({
            'ajax': soaba.APP_URL + 'gateways'
            , 'sAjaxDataProp': null
            , 'fnDrawCallback':function(){ soaba.appLoadingThreads.pop(); }
            , 'columns': [
                {'title': 'Driver Type', 'data': 'class'}, {
                    'title': 'Description',
                    'data': 'description'
                }, {'title': 'Gateway Address (IP Address)', 'data': 'address'}, {
                    'title': 'Is Connected',
                    'data': 'connected'
                }
            ], 'columnDefs': [{
                'render': function (data, type, row) {
                    return stripGatewayPackageName(data);
                }, 'targets': 0
            }]
        });

        soaba.appLoadingThreads.push({page: 'gateways'});
    }

    function attachDataTableEvents() {
        $('#tblGatewayDrivers').DataTable().on('xhr.dt', function (e, settings, json) {
            if (typeof json !== 'undefined' || json != null || json.length)
                $('#gatewaysCount').text(json.length);
        });

        $('#tblGatewayDrivers tbody').on('click', 'tr', function () {
            $('#tblGatewayDrivers tr').removeClass('warning selected');
            $(this).addClass('warning selected');
            var data = $('#tblGatewayDrivers').DataTable().row(this).data();

            $('.gatewayMetaPanel a[href="#tabGatewaysInfo"]').tab('show');
            $('.gatewayMetaPanel').slideDown();
            $('#txtGatewayDriver').val(stripGatewayPackageName(data.class));
            $('#txtGatewayIPAddress').val(data.address);
            $('#txtGatewayIsConnected').val(data.connected);
        });

        // scrolls details view into view only if necessary
        if($('body').scrollTop() < $('.gatewayMetaPanel').offset().top)
            soaba.utils.scrollIntoView('.gatewayMetaPanel');
    }


    /**
     * Page Load
     */
    $(function(){
        /** gateways page setup **/
        dataBind();
        attachDataTableEvents();
    });
})();