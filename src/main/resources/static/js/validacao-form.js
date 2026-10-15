/*
 * Validacao dos formularios de cadastro (.admin-form form).
 *
 * - Todo campo visivel precisa estar preenchido, exceto:
 *     * campos de senha sem o atributo "required" (edicao: manter a atual);
 *     * campos marcados com data-opcional.
 * - Campos com data-validar="cpf" so aceitam CPF valido (e ganham mascara).
 * - Campos type="email" precisam ter um e-mail valido.
 * - Campo invalido: borda vermelha + aviso "<campo> nao preenchido/invalido"
 *   logo abaixo, e o envio do formulario e bloqueado.
 */
(function () {

    var NAO_PREENCHIDO = ' não preenchido';
    var INVALIDO = ' inválido';

    function cpfValido(valor) {
        var d = String(valor).replace(/\D/g, '');

        if (d.length !== 11 || /^(\d)\1{10}$/.test(d)) {
            return false;
        }

        for (var t = 9; t < 11; t++) {
            var soma = 0;
            for (var i = 0; i < t; i++) {
                soma += parseInt(d.charAt(i), 10) * (t + 1 - i);
            }
            var digito = ((soma * 10) % 11) % 10;
            if (digito !== parseInt(d.charAt(t), 10)) {
                return false;
            }
        }

        return true;
    }

    function mascararCpf(valor) {
        var d = valor.replace(/\D/g, '').slice(0, 11);
        return d
            .replace(/^(\d{3})(\d)/, '$1.$2')
            .replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
            .replace(/\.(\d{3})(\d)/, '.$1-$2');
    }

    function emailValido(valor) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor);
    }

    function nomeDoCampo(campo) {
        var grupo = campo.closest('.input-group');
        var rotulo = grupo ? grupo.querySelector('label') : null;
        return rotulo ? rotulo.textContent.trim() : (campo.name || 'Campo');
    }

    function obrigatorio(campo) {
        if (campo.hasAttribute('data-opcional')) {
            return false;
        }
        if (campo.type === 'password') {
            return campo.required;
        }
        return true;
    }

    // Retorna a mensagem de erro do campo, ou null se estiver ok.
    function erroDoCampo(campo) {
        var valor = campo.value.trim();

        if (valor === '') {
            return obrigatorio(campo) ? nomeDoCampo(campo) + NAO_PREENCHIDO : null;
        }
        if (campo.getAttribute('data-validar') === 'cpf' && !cpfValido(valor)) {
            return nomeDoCampo(campo) + INVALIDO;
        }
        if (campo.type === 'email' && !emailValido(valor)) {
            return nomeDoCampo(campo) + INVALIDO;
        }

        return null;
    }

    function limparErro(campo) {
        campo.classList.remove('invalido');
        campo.removeAttribute('aria-invalid');

        var grupo = campo.closest('.input-group');
        var aviso = grupo ? grupo.querySelector('.msg-erro') : null;
        if (aviso) {
            aviso.remove();
        }
    }

    function mostrarErro(campo, mensagem) {
        limparErro(campo);
        campo.classList.add('invalido');
        campo.setAttribute('aria-invalid', 'true');

        var aviso = document.createElement('div');
        aviso.className = 'msg-erro';
        aviso.setAttribute('role', 'alert');
        aviso.textContent = mensagem;

        var grupo = campo.closest('.input-group');
        (grupo || campo.parentNode).appendChild(aviso);
    }

    function validarCampo(campo) {
        var mensagem = erroDoCampo(campo);
        if (mensagem) {
            mostrarErro(campo, mensagem);
            return false;
        }
        limparErro(campo);
        return true;
    }

    function camposDoFormulario(form) {
        return Array.prototype.filter.call(
            form.querySelectorAll('input, select, textarea'),
            function (campo) {
                return campo.type !== 'hidden' && campo.type !== 'submit' && !campo.disabled;
            }
        );
    }

    function prepararFormulario(form) {
        form.noValidate = true;

        var campos = camposDoFormulario(form);

        campos.forEach(function (campo) {
            if (campo.getAttribute('data-validar') === 'cpf') {
                campo.setAttribute('inputmode', 'numeric');
                campo.setAttribute('maxlength', '14');
                campo.setAttribute('placeholder', '000.000.000-00');
                campo.addEventListener('input', function () {
                    campo.value = mascararCpf(campo.value);
                });
            }

            // Depois de apontado o erro, revalida enquanto o usuario corrige.
            var aoCorrigir = function () {
                if (campo.classList.contains('invalido')) {
                    validarCampo(campo);
                }
            };
            campo.addEventListener('input', aoCorrigir);
            campo.addEventListener('change', aoCorrigir);
        });

        form.addEventListener('submit', function (evento) {
            var primeiroInvalido = null;

            camposDoFormulario(form).forEach(function (campo) {
                if (!validarCampo(campo) && !primeiroInvalido) {
                    primeiroInvalido = campo;
                }
            });

            if (primeiroInvalido) {
                evento.preventDefault();
                primeiroInvalido.focus();
            }
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('.admin-form form').forEach(prepararFormulario);
    });
})();
