package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteAnswerBusinessService {

    //Respective Data access objects have been autowired to access the methods defined in respective Dao
    @Autowired
    private UserDao userDao;

    @Autowired
    private AnswerDao answerDao;

    //Checks user signin status based on accessToken provided
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity verifyAuthToken(final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        } else {
            return userDao.getUserByUuid(userAuthTokenEntity.getUuid());
        }
    }

    //Returns answer based on answerId if exists
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity verifyAnsUuid(final String answerUuid)throws AnswerNotFoundException {
        AnswerEntity answerEntity = answerDao.getAnswerByAnsUuid(answerUuid);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        } else {
            return answerEntity;
        }
    }

    //Deletes answer if signed in user is an admin or answer owner
    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteAnswer(final AnswerEntity answerEntityToDelete, final UserEntity signedinUserEntity ) throws AuthorizationFailedException {
        if (signedinUserEntity.getRole().equalsIgnoreCase("admin")||(answerEntityToDelete.getUser().getUserName()==signedinUserEntity.getUserName())) {
            return answerDao.deleteAnswer(answerEntityToDelete);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
    }
}

