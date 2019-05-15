package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionsDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UsersQuestionService
{

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionsDao questionsDao;

    /*
    * Checking UserAccessToken Exits In the User_Auth Table.
    * @param String
    * @return none.
    */
    public  void  checkUserExistsBasedOnAccessToken(String authorizationToken) throws  AuthorizationFailedException
    {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if (userAuthTokenEntity.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }


    }

    /*
    * Getting the User Entity by the User AccessToken, InOrder to Add to the Questions Pogo
    * @param : String
    * @return UserEntity.
    */
    public UserEntity getUserEntity(String authToken) throws UserNotFoundException
    {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authToken);
        UserEntity userEntity = userAuthTokenEntity.getUser();
        if(userEntity == null )
        {
            throw  new UserNotFoundException("us-1" ,"User Not Found Exception");
        }
        return userEntity;
    }

    /*
    * Adding the User into the Question Table.
    * @param : QuestionEntity
    * @return : String
    * */
    public String addQuestion(QuestionEntity questionEntity)
    {
        QuestionEntity returnQuestion = questionsDao.createQuestions(questionEntity);
        if(returnQuestion.getUuid()!=null)
        {
            return returnQuestion.getUuid();
        }
        return null;
    }

    /*
    * Getting all the Questions From the Database
    * @param :none
    * @return : Map<String,String>
    */
    public Map<String , String> getAllQustionFromDB()
    {
        List<QuestionEntity> questionEntities = questionsDao.getAllQuestion();
        Map<String , String> question = new HashMap<>();
        for(QuestionEntity questionEntity : questionEntities)
        {
            if (questionEntity.getUuid() != null && questionEntity.getContent() != null)
            {
                question.put(questionEntity.getUuid(), questionEntity.getContent());
            }
        }
        return  question;
    }

    /*
    * Only Original user can Modify the Questions
    * @param : uuid ,questionId
    * @return :none
    */
    public void checkIfUserIsmodifingTheQuestion(String uuid, String questionId) throws AuthorizationFailedException ,
            InvalidQuestionException
    {
        QuestionEntity questionEntity = questionsDao.getUserIdFromQuestionId(questionId);
        if(questionEntity == null)
        {
            throw new InvalidQuestionException("QUES-001" ,"Entered question uuid does not exist");
        }

        if(questionEntity.getUser() != null)
        {
            String userId = questionEntity.getUser().getUuid();
            if ( !userId.equals(uuid))
            {
                throw  new AuthorizationFailedException("ATHR-003" ,"Only the question owner can edit the question");
            }
        }
    }

    /*
    * Editing the Question Based on passed QuestionEntity
    * @param : questionEntity
    * @return String
    * */
    public String editQuestionByQuestionId(QuestionEntity questionEntity)
    {
        QuestionEntity entity = questionsDao.editQuestions(questionEntity);
        return entity.getUuid();
    }
    /*
    * Delete Pre Validation Conditions
    * @param UserEntity ,questionId
    * @return :none
    */
    public void checkIfUserIsAdminOrHisOwnQuestion(UserEntity userEntity , String questionId) throws AuthorizationFailedException,
            InvalidQuestionException
    {
         String role = userEntity.getRole();
         String logedinUser = userEntity.getUuid();
         QuestionEntity questionEntity = questionsDao.getUserIdFromQuestionId(questionId);
         if(questionEntity ==null)
         {
             throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
         }


         if(questionEntity.getUser()!=null)
         {
             String userId = questionEntity.getUser().getUuid();
             if( !userId.equals(logedinUser)
                && !role.equals("admin"))
             {
                 throw  new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
             }

         }
    }
    /*
    * Deleting the Question based on questionId
    * @param String
    * @return String
    */
    public String deleteQuestion(String questionId)
    {
        QuestionEntity questionEntity=
        questionsDao.deleteQuestion(questionId);
        return questionEntity.getUuid();
    }

    /*
    * Getting All the Questions By UserId
    * @param String
    * @return List<QuestionEntity>
    */
    public List<QuestionEntity> getAllQusetionByUserId(String userId)
    {
        List<QuestionEntity> entity = questionsDao.getAllQuestionByUserId(userId);
        return entity;
    }
}
