package com.nlwexpert.certification_nlw.modules.students.entities;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswersCertificationsEntity {
    private UUID id;
    private UUID certificationId;
    private UUID studenteId;
    private UUID questionId;
    private UUID answerId;
    private boolean isCorrect;
}
