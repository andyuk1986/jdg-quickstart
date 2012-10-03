function updateData(isFullReportNeeded) {
    jQuery("#container").block({
        message: null
    });
    jQuery.ajax({
        url: 'rest/freshrep' + (isFullReportNeeded ? "/true" : ""),
        async: false,
        success: function(data) {
               for(var i = 0; i < data.length; i++) {
                   var entry = data[i];
                   var type = entry.elemPrefix

                   jQuery("#userCount_" + type).html(entry.userCount)
                   jQuery("#notifCount_" + type).html(entry.sentNotificationCount)
                   jQuery("#subsCount_" + type).html(entry.subscribtionCount)
                   jQuery("#cancCount_" + type).html(entry.cancellationCount)
               }
           }
       });

    jQuery.ajax({
        url: 'rest/cachestats',
        ajax: false,
        success: function(data) {
           var tableElem = jQuery("<table style='border: 2px grey solid; margin: 20px;' rules='all' cellpadding='12' cellspacing='12' " +
                                         "id='cacheStats'></table>");

           for (var key in data) {
              var elem = jQuery("<tr><td><p>" + key + "</p></td><td><p>" + data[key] + "</p></td></tr>");
                 elem.appendTo(tableElem);
           }

           jQuery("#cacheStats").replaceWith(tableElem);
        }
    });

    jQuery("#container").unblock();
}

jQuery(document).ready(function() {
    //setInterval("updateData()", 7000);
});