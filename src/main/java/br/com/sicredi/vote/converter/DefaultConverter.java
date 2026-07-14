package br.com.sicredi.vote.converter;

import org.modelmapper.ModelMapper;

import lombok.Getter;

@Getter
public abstract class DefaultConverter {

    private final ModelMapper modelMapper;

    protected DefaultConverter() {
        this.modelMapper = new ModelMapper();
    }
}
