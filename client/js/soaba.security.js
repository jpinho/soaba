$(function(){
	$.ajaxSetup({
	    beforeSend: function(event, xhr, options) {
			if($.cookie('soaba.auth') != null){
				var userdata = $.parseJSON($.cookie('soaba.auth'));
				xhr.url +=  ((xhr.url.indexOf('?') > 0) ? '&' : '?') + 'token='+userdata.token;
			}
	    }
	});

	if($.cookie('soaba.auth') != null){
		showPreload();

	  	$.ajax({ url: soaba.APP_URL + 'auth/check'
	  		, method: 'POST'
	  		, data: $.cookie('soaba.auth')
  			, success: function(result) { 
  				hidePreload();
				if(result) 
					loadApplication(); 
			  }
	  		, async: true
  		}).always(function(){
  			hidePreload();
  		});
	}
	
	$('#lkSignOut').click(function(){
		$.removeCookie('soaba.auth');
		window.location='';
	});

	$('.login form button[type="submit"]').click(function(e){
		e.preventDefault();
		var $that = $(this);
		$that.attr('disabled','disabled');
		$that.children().toggle();

		$.post(soaba.APP_URL + 'auth', $('.login form').serialize(), function(token){
			$.cookie("soaba.auth", token, { expires : 1, path    : '/' });
			loadApplication();
		}).fail(function(){
			$('.login .alert-box').effect('fade', 500);
		}).always(function(){
			$that.removeAttr('disabled');
			$that.children().toggle();
		});

		return false;
	});
	
	function loadApplication(){
		$('.container-fluid .row').load('views/application.html', function() {
  			$('body').removeClass('login-page');
  			var userdata = $.parseJSON($.cookie('soaba.auth'));
  			$('#username').html(userdata.givenName + ' ' +  userdata.surname);
  			$('.auth').show();
		});
	}

	function hidePreload(){
		$('.app-preloader').hide();
		$('.login h1, .login form').show('fade');
	}

	function showPreload(){
		$('.app-preloader').show();
		$('.login h1, .login form').hide();
	}
});