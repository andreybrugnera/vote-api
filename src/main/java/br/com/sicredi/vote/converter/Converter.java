package br.com.sicredi.vote.converter;

public interface Converter<E, D> {

    D convertToDto(E e);

    E convertFromDto(D d);
}
