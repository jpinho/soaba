/**
 * SOABA Utility Functions
 *
 * @author João Pinho
 */

soaba.utils = Ember.Namespace.create({
    scrollIntoView: function(target){
        $('body').animate({scrollTop: $(target).offset().top});
    }
});