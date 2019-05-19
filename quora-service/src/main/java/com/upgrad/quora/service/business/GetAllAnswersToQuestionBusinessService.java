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

import java.util.List;

@Service
public class GetAllAnswersToQuestionBusinessService {

    //Respective Data access objects have been autowired to access the methods defined in respective Dao
    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    //Checks user signedin status based on accessToken
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity verifyAuthToken(final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }   return userAuthTokenEntity;
    }

    //Returns question based on questionId if exists
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity verifyQuestionId(final String questionUuid)throws InvalidQuestionException {
        QuestionEntity questionEntity= questionDao.getQuestionByQUuid(questionUuid);
        if(questionEntity == null) {
            throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
        } else {
            return questionEntity;
        }
    }

    //Returns all the answers based on question
    public List<AnswerEntity> getAllAnswersByQuestion(QuestionEntity questionEntity){ return answerDao.getAllAnswersByQuestion(questionEntity); }

}

