package com.nlwexpert.certification_nlw.modules.questions.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nlwexpert.certification_nlw.modules.questions.dto.AlternativesResultDTO;
import com.nlwexpert.certification_nlw.modules.questions.dto.QuestionResultDTO;
import com.nlwexpert.certification_nlw.modules.questions.entities.AlternativesEntity;
import com.nlwexpert.certification_nlw.modules.questions.entities.QuestionEntity;
import com.nlwexpert.certification_nlw.modules.questions.repositories.QuestionRepository;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    
    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/technology/{technology}")
    public List<QuestionResultDTO> findByTechnology(@PathVariable String technology){
        var result = this.questionRepository.findByTechnology(technology);

        var toMap = result.stream().map(question -> mapQuestionToDTO(question))
        .collect(Collectors.toList());

        return toMap;
    }

    static QuestionResultDTO mapQuestionToDTO(QuestionEntity questions) {
        var questionResultDTO = QuestionResultDTO.builder()
            .id(questions.getId())
            .technology(questions.getTechnology())
            .description(questions.getDescription())
            .build();

        List<AlternativesResultDTO> alternativesResultDTOs = questions.getAlternativesEntities().stream().map(alternative -> mapAlternativesDTO(alternative)).collect(Collectors.toList());
        
        questionResultDTO.setAlternativesResultDTO(alternativesResultDTOs);

        return questionResultDTO;
    }

    static AlternativesResultDTO mapAlternativesDTO(AlternativesEntity alternatives) {
        return AlternativesResultDTO.builder()
            .id(alternatives.getId())
            .description(alternatives.getDescription())
            .build();
    }
}
