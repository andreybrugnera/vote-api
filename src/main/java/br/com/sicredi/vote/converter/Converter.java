package br.com.sicredi.vote.converter;

public interface Converter<E, Q, S> {

    S convertToDto(E e);

    E convertFromDto(Q q);
}
