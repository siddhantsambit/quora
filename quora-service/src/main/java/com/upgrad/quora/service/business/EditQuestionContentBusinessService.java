package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditQuestionContentBusinessService {

    //Respective Data access objects have been autowired to access the methods defined in respective Dao
    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    //Checks user signed in status, validates questionId and checks if the signedin user is the question owner
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity verifyUserStatus(final String questionId, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        QuestionEntity existingQuestionEntity = questionDao.getQuestionByQUuid(questionId);
        if (existingQuestionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (existingQuestionEntity.getUser().getUserName().equalsIgnoreCase(userAuthTokenEntity.getUser().getUserName())) {
            return questionDao.updateQuestion(existingQuestionEntity);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
    }

    //Updates question with the edit question response provided
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity updateQuestion(final QuestionEntity updatedQuestionEntity){
        return questionDao.updateQuestion(updatedQuestionEntity);
    }
}
