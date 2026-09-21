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

    var diasPermitidos = config.diasPermitidos;

    function diaPermitido(data) {
        if (!diasPermitidos) {
            return true;
        }
        return diasPermitidos.indexOf(data.getDay()) !== -1;
    }

    var hoje = new Date();
    var mesExibido = hoje.getMonth();
    var anoExibido = hoje.getFullYear();
    var diaHoje = new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate());
    var diaSelecionado = diaPermitido(diaHoje) ? diaHoje : null;

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
        var dataSelecionadaFormatada = diaSelecionado ? formatarData(diaSelecionado) : null;

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

                var dataCelula = new Date(anoExibido, mesExibido, dia);
                var permitido = diaPermitido(dataCelula);

                if (formatarData(dataCelula) === formatarData(hoje)) {
                    celula.classList.add('hoje');
                }
                if (dataSelecionadaFormatada && formatarData(dataCelula) === dataSelecionadaFormatada) {
                    celula.classList.add('selecionado');
                }

                if (!permitido) {
                    celula.classList.add('desabilitado');
                    elGrade.appendChild(celula);
                    return;
                }

                celula.setAttribute('role', 'button');
                celula.setAttribute('tabindex', '0');

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

        if (!diaSelecionado) {
            if (elDataInput) {
                elDataInput.value = '';
            }
            if (elDataTexto) {
                elDataTexto.textContent = 'Nenhuma aula neste mes';
            }

            renderizar();

            if (typeof config.onSelecionar === 'function') {
                config.onSelecionar(null, null);
            }
            return;
        }

        var dataFormatada = formatarData(diaSelecionado);

        if (elDataInput) {
            elDataInput.value = dataFormatada;
        }
        if (elDataTexto) {
            elDataTexto.textContent = diaSelecionado.getDate() + ' de ' + nomesMes[diaSelecionado.getMonth()] + ' de ' + diaSelecionado.getFullYear();
        }

        renderizar();

        if (typeof config.onSelecionar === 'function') {
            config.onSelecionar(dataFormatada, diaSelecionado.getDay());
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
