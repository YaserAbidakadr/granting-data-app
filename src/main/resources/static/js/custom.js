// Don't forget to place the link for this script below the 'footer' fragment since that fragement contains the link for the jQuery script.
$(document).ready(() => {
                
    $('.expd-clps-btn').click(function (event) {
        const btn = $(event.currentTarget);
                   
        if (btn.html() == '+') {
            btn.html('-');
        } else if (btn.html() == '-') {
            btn.html('+');
        }
                
        btn.parent().next().toggle();
    });

});
			