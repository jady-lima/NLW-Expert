package com.nlwexpert.certification_nlw.modules.students.useCases;

import org.springframework.stereotype.Service;

import com.nlwexpert.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;

@Service
public class verifyIfHasCertificationUseCase {
    
    public boolean execute(VerifyHasCertificationDTO dto) {
        if (dto.getEmail().equals("jk@gmail.com") && dto.getTechnology().equals("JAVA")) {
            return true;
        }
        return false;
    }

}
