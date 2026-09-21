document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('tr.linha-clicavel').forEach(function (linha) {
        linha.addEventListener('click', function (evento) {
            if (evento.target.closest('a, button')) {
                return;
            }
            var destino = linha.getAttribute('data-href');
            if (destino) {
                window.location = destino;
            }
        });
    });
});
