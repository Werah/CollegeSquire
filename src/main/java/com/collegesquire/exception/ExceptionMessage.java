package com.collegesquire.exception;

public enum ExceptionMessage {

    RESOURCE_NOT_FOUND("Recurso nao encontrado");

    private final String mensagem;

    ExceptionMessage(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    @Override
    public String toString() {
        return mensagem;
    }
}
