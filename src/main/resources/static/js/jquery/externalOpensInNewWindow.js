/**
 * External links open in new window.
 * 
 * This will crawl all links on a page searching for href's that start with a
 * certain pattern (e.g. http or /eic/) and will: 
 * 
 * 1 - Add a "title" attribute consisting of the link text plus " - opens in a new window" (in French for French version) 
 * 2 - Add an attribute target="_blank"
 * 
 * Requires: - jQuery 1.6 or greater.
 */
(function($) {
    $(function() {
        var opensText = "opens in a new window";
        if ($('html').attr('lang') == 'fr') {
            opensText = "s'ouvre dans une nouvelle fen\u00EAtre";
        }
        var opensTextCaps = opensText.charAt(0).toUpperCase() + opensText.slice(1);
        $('a').each(
                function() {
                    var anchor = $(this);
                    // Current link url.                    
                    var href = String(anchor.attr('href'));
                    // Current link title (will be "undefined" if it doesn't exist).
                    var currentTitle = $.trim(String(anchor.attr('title')));
                    if ((href.indexOf("/eic/") == 0 || href.indexOf("/cgi-bin/") == 0 || href.indexOf("http") == 0) && href.indexOf("mailto:") != 0) {
                        // Default the new title to just "Opens in a new window".
                        var titleText = opensTextCaps;
                        if (currentTitle != "undefined" && currentTitle.length > 0) {
                            titleText = currentTitle + ' - ' + opensText;
                        } else {
                            var linkText = $.trim(String(anchor.text()));
                            if (linkText != "undefined" && linkText.length > 0) {
                                titleText = linkText + ' - ' + opensText;
                            }
                        }
                        // Update the title and set target to "_blank".
                        anchor.attr('title', titleText).attr('target', '_blank');
                    }
                });
    });
})(jQuery);
