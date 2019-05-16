package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.UsersQuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/")
public class QuestionController
{

    @Autowired
    private UsersQuestionService usersQuestionService ;

    @Autowired
    private  QuestionEntity questionEntity;


    @RequestMapping(value = "/question/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest , @RequestHeader("accessToken") final String accessToken)
            throws AuthorizationFailedException, UserNotFoundException
    {

        String [] token = accessToken.split("Bearer ");
        usersQuestionService.checkUserExistsBasedOnAccessToken(token[1]);
        UserEntity userEntity = usersQuestionService.getUserEntity(token[1]);

        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        String uuid = UUID.randomUUID().toString();


        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(time);
        questionEntity.setUser(userEntity);
        questionEntity.setUuid(uuid);

        String questionUUID = usersQuestionService.addQuestion(questionEntity);
        QuestionResponse questionResponse =null;

        if(questionUUID != null)
        {
             questionResponse = new QuestionResponse()
                    .id(questionUUID)
                    .status("QUESTION CREATED");
        }

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }

    @RequestMapping(value = "/question/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public  ResponseEntity<QuestionResponse> getAllQuestions(@RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException
    {
        String [] token = accessToken.split("Bearer ");
        usersQuestionService.checkUserExistsBasedOnAccessToken(token[1]);

       Map<String,String> questions = usersQuestionService.getAllQustionFromDB();

        QuestionResponse questionResponse = null;

        if(questions.size() > 0 )
        {
            questionResponse = new QuestionResponse()
                    .status(questions.toString());
        }

        return new ResponseEntity<QuestionResponse>(questionResponse ,HttpStatus.OK);

    }

    @RequestMapping(value = "/question/edit/{questionId}" ,method = RequestMethod.PUT ,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> editQuestionById(@RequestHeader("accessToken") final String accessToken ,
                                                             @PathVariable("questionId") final String questionId ,final QuestionRequest questionRequest)
            throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException
    {

        String [] token = accessToken.split("Bearer ");
        usersQuestionService.checkUserExistsBasedOnAccessToken(token[1]);

        UserEntity userEntity = usersQuestionService.getUserEntity(token[1]);

        usersQuestionService.checkIfUserIsmodifingTheQuestion(userEntity.getUuid() ,questionId);

        questionEntity.setUuid(questionId);
        questionEntity.setContent(questionRequest.getContent());

        String uuid =
        usersQuestionService.editQuestionByQuestionId(questionEntity);

        QuestionResponse questionResponse = null;

        if(uuid != null)
        {
          questionResponse =  new QuestionResponse()
                    .id(uuid)
                    .status("QUESTION EDITED");
        }
        return new ResponseEntity<QuestionResponse>(questionResponse ,HttpStatus.OK);
    }

    @RequestMapping(value ="/question/delete/{questionId}" , method = RequestMethod.DELETE ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> deleteQuestion(@RequestHeader("accessToken") final String accessToken ,
                                                           @PathVariable("questionId") final String questionId) throws AuthorizationFailedException,
            UserNotFoundException, InvalidQuestionException
    {
        String [] token = accessToken.split("Bearer ");
        usersQuestionService.checkUserExistsBasedOnAccessToken(token[1]);
        UserEntity userEntity = usersQuestionService.getUserEntity(token[1]);

        usersQuestionService.checkIfUserIsAdminOrHisOwnQuestion(userEntity ,questionId);

        String uuid = usersQuestionService.deleteQuestion(questionId);

        QuestionResponse questionResponse = null;
        if(uuid != null)
        {
            questionResponse =  new QuestionResponse()
                    .id(uuid)
                    .status("QUESTION DELETED");
        }
        return new ResponseEntity<QuestionResponse>(questionResponse ,HttpStatus.OK);

    }

    @RequestMapping(value ="question/all/{userId}" , method = RequestMethod.GET ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> getAllUserByUserId(@RequestHeader("accessToken") final String accessToken
                    , @PathVariable("userId") final String userId) throws AuthorizationFailedException, UserNotFoundException
    {

        String [] token = accessToken.split("Bearer ");
        usersQuestionService.checkUserExistsBasedOnAccessToken(token[1]);
        UserEntity userEntity = usersQuestionService.getUserEntity(token[1]);

        List<QuestionEntity> entity =
        usersQuestionService.getAllQusetionByUserId(userId);

        QuestionResponse questionResponse = null;
        if(entity!= null)
        {
            questionResponse =  new QuestionResponse()
                    .id(entity.toString());
        }
        return new ResponseEntity<QuestionResponse>(questionResponse ,HttpStatus.OK);

    }

}
