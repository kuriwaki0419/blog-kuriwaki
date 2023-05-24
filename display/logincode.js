$("#login-button").click(function(event){
/*    event.preventDefault();*/
/*
$('form').fadeOut(500);
$('.wrapper').addClass('form-success');
});

*/
if ($('form')[0].checkValidity()) {
    $('form').fadeOut(500);
    $('.wrapper').addClass('form-success');
}
});