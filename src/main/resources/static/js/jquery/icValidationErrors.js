
/**
 * Form Validation Errors summary display.
 * 
 * Populates the "summary" error block at the top of a form with a list of validation errors 
 * present in the form, builds the error labels and displays them bellow the input label.
 * See Jira IDG-11.
 * 
 * Requires: - jQuery 1.7.2 or greater.
 */
$(document).ready(function(){
    
    var icErrorList = $('<ul></ul>');

    // The i18n text
    var engText = {
            error: "Error"
        }
      
    var fraText = {
            error: "Erreur"
        }
    
    if ($('html').attr('lang') === 'fr') {
        i18nText = fraText;
    } else {
        i18nText = engText;
    }
    
    // Build the errors summary element.
    $("#ic-errorSummary").attr("tabindex", "-1").append(icErrorList);
    
    // Loop thru the errors.
    $('[id$=".errors"]').each(function(index) {
        
        // Build the error label
        var errorContent = '<span class="prefix">' + i18nText.error + '&nbsp;' + (index + 1) + ': </span>' + $(this).html();
        $(this).html(errorContent)
          //.addClass("label label-danger")
          .wrap("<strong id='" +  this.id + "-error' class='error'></strong>")
          // Update the class for the form-group.
          .closest(".form-group").addClass("has-error");
       
        // Links to error labels
        // ID Team has decided link should go to error message, not the field.
        icErrorList.append('<li><a href="#' +  this.id + '">' + errorContent + '</a></li>');
        
        // Update the class for the input.
        var path =  $(this).attr('id').split(".errors")[0];
        $('#'+path).addClass("error");
        
    });
});

