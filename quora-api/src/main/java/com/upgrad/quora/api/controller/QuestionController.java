package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.*;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//RestController annotation specifies that this class represents a REST API(equivalent of @Controller + @ResponseBody)
@RestController
@RequestMapping("/")
public class QuestionController {
    //Required services are autowired to enable access to methods defined in respective Business services
    @Autowired
    private CreateQuestionBusinessService createQuestionBusinessService;

    @Autowired
    private GetAllQuestionsBusinessService getAllQuestionsBusinessService;

    @Autowired
    private GetAllQuestionsByUserBusinessService getAllQuestionsByUserBusinessService;

    @Autowired
    EditQuestionContentBusinessService editQuestionContentBusinessService;

    @Autowired
    DeleteQuestionBusinessService deleteQuestionBusinessService;

    //createQuestion method takes accessToken for doing Bearer Authorization and creates the question by the signed in user
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("accessToken") final String accessToken,
                                                           final QuestionRequest questionRequest) throws AuthorizationFailedException {
        String[] bearerToken = accessToken.split("Bearer ");
        final UserAuthTokenEntity userAuthTokenEntity = createQuestionBusinessService.verifyAuthToken(bearerToken[1]);
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUser(userAuthTokenEntity.getUser());
        questionEntity.setContent(questionRequest.getContent());
        final ZonedDateTime now = ZonedDateTime.now();
        questionEntity.setDate(now);
        final QuestionEntity createdQuestionEntity = createQuestionBusinessService.createQuestion(questionEntity);

        QuestionResponse questionResponse = new QuestionResponse()
                .id(createdQuestionEntity.getUuid())
                .status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    //getAllQuestions method takes accessToken for doing Bearer Authorization and returns all the questions created
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException {
        String[] bearerToken = accessToken.split("Bearer ");
        getAllQuestionsBusinessService.verifyAuthToken(bearerToken[1]);
        List<QuestionEntity> allQuestions = new ArrayList<QuestionEntity>();
        allQuestions.addAll(getAllQuestionsBusinessService.getAllQuestions());
        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<QuestionDetailsResponse>();

        for (QuestionEntity question : allQuestions) {
            QuestionDetailsResponse questionDetailsResponse=new QuestionDetailsResponse();
            questionDetailsResponses.add(questionDetailsResponse
                    .id(question.getUuid())
                    .content(question.getContent()));
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses,HttpStatus.OK);
    }

    //getAllQuestionsByUser method takes userId to retrieve user and accessToken for doing Bearer Authorization and returns all the questions created by a specific user
    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId")final String userId,
                                                                               @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        String[] bearerToken = accessToken.split("Bearer ");
        getAllQuestionsByUserBusinessService.verifyAuthTokenAndUuid(userId,bearerToken[1]);
        final UserAuthTokenEntity userAuthTokenEntity= getAllQuestionsByUserBusinessService.getUserAuthTokenByTokenId(bearerToken[1]);
        List<QuestionEntity> allQuestionsByUser = new ArrayList<QuestionEntity>();
        allQuestionsByUser.addAll(getAllQuestionsByUserBusinessService.getAllQuestionsByUserId((userAuthTokenEntity.getUser())));
        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<QuestionDetailsResponse>();

        for (QuestionEntity question : allQuestionsByUser) {
            QuestionDetailsResponse questionDetailsResponse=new QuestionDetailsResponse();
            questionDetailsResponses.add(questionDetailsResponse
                    .id(question.getUuid())
                    .content(question.getContent()));
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses,HttpStatus.OK);
    }

    //editQuestionContent method takes questionId to retrieve question and accessToken for doing Bearer Authorization and returns the edit question response
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest questionEditRequest ,
                                                                    @PathVariable("questionId") final String questionId,
                                                                    @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        String[] bearerToken = accessToken.split("Bearer ");
        final QuestionEntity questionEntity = editQuestionContentBusinessService.verifyUserStatus(questionId,bearerToken[1]);
        questionEntity.setContent(questionEditRequest.getContent());
        final QuestionEntity updatedQuestionEntity = editQuestionContentBusinessService.updateQuestion(questionEntity);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse()
                .id(updatedQuestionEntity.getUuid())
                .status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    //deleteQuestion method takes questionId to retrieve question and accessToken for doing Bearer Authorization and allows only the question owner or admin to delete the question
    @RequestMapping(method= RequestMethod.DELETE,path="/question/delete/{questionId}",produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionUuid,
                                                                 @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        String [] bearerToken = accessToken.split("Bearer ");
        final QuestionEntity questionEntityToDelete=deleteQuestionBusinessService.verifyQUuid(questionUuid);
        final UserEntity signedinUserEntity = deleteQuestionBusinessService.verifyAuthToken(bearerToken[1]);
        final String Uuid = deleteQuestionBusinessService.deleteQuestion( questionEntityToDelete,signedinUserEntity);

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse()
                .id(Uuid)
                .status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }
}

