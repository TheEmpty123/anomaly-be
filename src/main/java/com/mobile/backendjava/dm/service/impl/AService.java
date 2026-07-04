package com.mobile.backendjava.dm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.backendjava.dm.utils.LogObj;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class AService {
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

    public final LogObj log = new LogObj();
}