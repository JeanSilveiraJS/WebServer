$(document).ready(function() {
    let newDiv = "<div class='container-fluid text-start border py-3 mb-3'>" +
                "<label for='nome' class='form-label'>Nome</label>" +
                "<input type='text' name='nome' id='nome' class='form-control' placeholder='Insira aqui o seu nome' required autofocus>" +
                "</div>";
    $("#div-lugares").prepend(newDiv);
});