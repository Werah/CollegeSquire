function iniciarCalendario(config) {
    config = config || {};

    var elGrade = document.getElementById(config.gradeId || 'calendarioGrade');
    var elTitulo = document.getElementById(config.tituloId || 'calendarioTitulo');
    var elMesAnterior = document.getElementById(config.mesAnteriorId || 'mesAnterior');
    var elProximoMes = document.getElementById(config.proximoMesId || 'proximoMes');
    var elDataInput = config.dataInputId ? document.getElementById(config.dataInputId) : null;
    var elDataTexto = config.dataTextoId ? document.getElementById(config.dataTextoId) : null;

    var nomesMes = ['Janeiro', 'Fevereiro', 'Marco', 'Abril', 'Maio', 'Junho',
        'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'];

    var hoje = new Date();
    var mesExibido = hoje.getMonth();
    var anoExibido = hoje.getFullYear();
    var diaSelecionado = new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate());

    function formatarData(d) {
        var ano = d.getFullYear();
        var mes = String(d.getMonth() + 1).padStart(2, '0');
        var dia = String(d.getDate()).padStart(2, '0');
        return ano + '-' + mes + '-' + dia;
    }

    function renderizar() {
        elTitulo.textContent = nomesMes[mesExibido] + ' ' + anoExibido;
        elGrade.innerHTML = '';

        var primeiroDiaSemana = new Date(anoExibido, mesExibido, 1).getDay();
        var totalDias = new Date(anoExibido, mesExibido + 1, 0).getDate();

        for (var i = 0; i < primeiroDiaSemana; i++) {
            var vazio = document.createElement('div');
            vazio.className = 'calendario-dia vazio';
            elGrade.appendChild(vazio);
        }

        for (var dia = 1; dia <= totalDias; dia++) {
            (function (dia) {
                var celula = document.createElement('div');
                celula.className = 'calendario-dia';
                celula.textContent = dia;
                celula.setAttribute('role', 'button');
                celula.setAttribute('tabindex', '0');

                var dataCelula = new Date(anoExibido, mesExibido, dia);

                if (formatarData(dataCelula) === formatarData(hoje)) {
                    celula.classList.add('hoje');
                }
                if (formatarData(dataCelula) === formatarData(diaSelecionado)) {
                    celula.classList.add('selecionado');
                }

                function selecionar() {
                    diaSelecionado = dataCelula;
                    atualizarSelecao();
                }

                celula.addEventListener('click', selecionar);
                celula.addEventListener('keydown', function (evento) {
                    if (evento.key === 'Enter' || evento.key === ' ') {
                        evento.preventDefault();
                        selecionar();
                    }
                });

                elGrade.appendChild(celula);
            })(dia);
        }
    }

    function atualizarSelecao() {
        var dataFormatada = formatarData(diaSelecionado);

        if (elDataInput) {
            elDataInput.value = dataFormatada;
        }
        if (elDataTexto) {
            elDataTexto.textContent = diaSelecionado.getDate() + ' de ' + nomesMes[diaSelecionado.getMonth()] + ' de ' + diaSelecionado.getFullYear();
        }

        renderizar();

        if (typeof config.onSelecionar === 'function') {
            config.onSelecionar(dataFormatada);
        }
    }

    elMesAnterior.addEventListener('click', function () {
        mesExibido--;
        if (mesExibido < 0) {
            mesExibido = 11;
            anoExibido--;
        }
        renderizar();
    });

    elProximoMes.addEventListener('click', function () {
        mesExibido++;
        if (mesExibido > 11) {
            mesExibido = 0;
            anoExibido++;
        }
        renderizar();
    });

    atualizarSelecao();
}
