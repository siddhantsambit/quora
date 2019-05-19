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

import java.util.List;

@Service
public class GetAllQuestionsBusinessService {

    //Respective Data access objects have been autowired to access the methods defined in respective Dao
    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    //Checks user signedin status based on accessToken
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity verifyAuthToken(final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }   return userAuthTokenEntity;
    }

    //Returns all the questions created by all users
    public List<QuestionEntity> getAllQuestions() { return questionDao.getAllQuestions(); }

}
