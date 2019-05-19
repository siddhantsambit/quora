package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetAllQuestionsByUserBusinessService {

    //Respective Data access objects have been autowired to access the methods defined in respective Dao
    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    //Checks user signedin status based on accessToken and also validates entered userId
    @Transactional(propagation = Propagation.REQUIRED)
    public void verifyAuthTokenAndUuid(final String userUuid,final String accessToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if (userDao.getUserByUuid(userUuid) == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        } else if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }
    }

    //Returns all questions created by a specific user
    public List<QuestionEntity> getAllQuestionsByUserId (final UserEntity user){ return questionDao.getAllQuestionsByUserId(user); }

    //Returns UserAuthToken based on userId
    public UserAuthTokenEntity getUserAuthTokenByUuid(final String userUuid){ return userDao.getUserAuthTokenByUuid(userUuid); }

    //Returns UserAuthToken based on TokenId
    public UserAuthTokenEntity getUserAuthTokenByTokenId(final String accessToken){ return userDao.getUserAuthToken(accessToken); }

}



