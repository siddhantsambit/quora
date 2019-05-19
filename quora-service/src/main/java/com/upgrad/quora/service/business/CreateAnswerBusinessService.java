package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateAnswerBusinessService {

    //Respective Data access objects have been autowired to access the methods defined in respective Dao
    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    //Creates Answer based on input
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity)  {
        return answerDao.createAnswer(answerEntity);
    }

    //Checks user signin status based on accessToken provided
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity verifyAuthToken(final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else if(userAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post an answer");
        }   return userAuthTokenEntity;
    }

    //Returns question based on questionId if exists
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity verifyQuestionId(final String questionUuid)throws InvalidQuestionException {
        QuestionEntity questionEntity= questionDao.getQuestionByQUuid(questionUuid);
        if(questionEntity == null) {
            throw new InvalidQuestionException("QUES-001","The question entered is invalid");
        } else {
            return questionEntity;
        }
    }
}