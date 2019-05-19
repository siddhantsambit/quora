package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateQuestionBusinessService {

    //Respective Data access objects have been autowired to access the methods defined in respective Dao
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    //Creates question based on input
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {
        return questionDao.createQuestion(questionEntity);
    }

    //Checks user signin status based on accessToken provided
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity verifyAuthToken(final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }   return userAuthTokenEntity;
    }
}
