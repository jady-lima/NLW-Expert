package com.nlwexpert.certification_nlw.modules.students.useCases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nlwexpert.certification_nlw.modules.questions.entities.QuestionEntity;
import com.nlwexpert.certification_nlw.modules.questions.repositories.QuestionRepository;
import com.nlwexpert.certification_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import com.nlwexpert.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;
import com.nlwexpert.certification_nlw.modules.students.entities.AnswersCertificationsEntity;
import com.nlwexpert.certification_nlw.modules.students.entities.CertificationStudentEntity;
import com.nlwexpert.certification_nlw.modules.students.entities.StudentEntity;
import com.nlwexpert.certification_nlw.modules.students.repositories.CertificationStudentRepository;
import com.nlwexpert.certification_nlw.modules.students.repositories.StudentRepository;

@Service
public class StudentCertificationAnswersUseCase {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

    public CertificationStudentEntity execute(StudentCertificationAnswerDTO studentCertificationAnswerDTO) throws Exception{

        var hasCertification = this.verifyIfHasCertificationUseCase.execute(new VerifyHasCertificationDTO(studentCertificationAnswerDTO.getEmail(), studentCertificationAnswerDTO.getTechnology()));

        if (hasCertification) {
            throw new Exception("Você já possui certificação");
        }

        List<QuestionEntity> questionEntity = questionRepository.findByTechnology(studentCertificationAnswerDTO.getTechnology());
        List<AnswersCertificationsEntity> answersCertificationsEntities = new ArrayList<>();

        AtomicInteger correctAnswers = new AtomicInteger(0);

        studentCertificationAnswerDTO.getQuestionAnswerDTO().stream().forEach(questionAnswer -> {
            var question = questionEntity.stream().filter(q -> q.getId().equals(questionAnswer.getQuestionID())).findFirst().get();

            var findCorrectAlternative = question.getAlternativesEntities().stream().filter(alternative -> alternative.isCorrect()).findFirst().get();

            if (findCorrectAlternative.getId().equals(questionAnswer.getAlternativeID())) {
                questionAnswer.setCorrect(true);
                correctAnswers.incrementAndGet();
            }else{
                questionAnswer.setCorrect(false);
            }

            var answersCertificationEntity = AnswersCertificationsEntity.builder()
                .answerID(questionAnswer.getAlternativeID())
                .questionID(questionAnswer.getQuestionID())
                .isCorrect(questionAnswer.isCorrect())
                .build();

            answersCertificationsEntities.add(answersCertificationEntity);
        });

        var student = studentRepository.findByEmail(studentCertificationAnswerDTO.getEmail());
        UUID studentID;

        if (student.isEmpty()) {
            var studentCreated = StudentEntity.builder().email(studentCertificationAnswerDTO.getEmail()).build();
            studentCreated = studentRepository.save(studentCreated);
            studentID = studentCreated.getId();
        } else {
            studentID = student.get().getId();
        }

        CertificationStudentEntity certificationStudentEntity = CertificationStudentEntity.builder()
            .technology(studentCertificationAnswerDTO.getTechnology())
            .studentID(studentID)
            .grade(correctAnswers.get())
            .build();

        var certificationStudentCreated = certificationStudentRepository.save(certificationStudentEntity);

        answersCertificationsEntities.stream().forEach(answerCertification -> {
            answerCertification.setCertificationID(certificationStudentEntity.getId());
            answerCertification.setCertificationStudentEntity(certificationStudentEntity);
        });

        certificationStudentEntity.setAnswersCertificationsEntities(answersCertificationsEntities);

        certificationStudentRepository.save(certificationStudentEntity);

        return certificationStudentCreated;
    }
}
