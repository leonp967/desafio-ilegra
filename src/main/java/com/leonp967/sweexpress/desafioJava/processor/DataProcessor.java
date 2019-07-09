package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.model.FileDataModel;

import java.util.List;

public interface DataProcessor {
    FileDataModel process(List<String> attributes);
}
