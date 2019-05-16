package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditAnswerContentBusinessService {

    //Respective Data access objects have been autowired to access the methods defined in respective Dao
    @Autowired
    private UserDao userDao;

    @Autowired
    private AnswerDao answerDao;

    //Checks user signed in status, validates answerId and checks if the signedin user is the answer owner
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity verifyUserStatus(final String answerId, final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }
        AnswerEntity existingAnswerEntity = answerDao.getAnswerByAnsUuid(answerId);
        if (existingAnswerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        if (existingAnswerEntity.getUser().getUserName().equalsIgnoreCase(userAuthTokenEntity.getUser().getUserName())) {
            return answerDao.updateAnswer(existingAnswerEntity);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
    }

    //Updates answer with the edit answer response provided
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer(final AnswerEntity updatedAnswerEntity){
        return answerDao.updateAnswer(updatedAnswerEntity);
    }
}
