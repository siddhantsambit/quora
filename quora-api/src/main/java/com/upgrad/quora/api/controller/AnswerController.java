package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CreateAnswerBusinessService;
import com.upgrad.quora.service.business.DeleteAnswerBusinessService;
import com.upgrad.quora.service.business.EditAnswerContentBusinessService;
import com.upgrad.quora.service.business.GetAllAnswersToQuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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
public class AnswerController {
    //Required services are autowired to enable access to methods defined in respective Business services
    @Autowired
    private CreateAnswerBusinessService createAnswerBusinessService;

    @Autowired
    private GetAllAnswersToQuestionBusinessService getAllAnswersToQuestionBusinessService;

    @Autowired
    private DeleteAnswerBusinessService deleteAnswerBusinessService;

    @Autowired
    private EditAnswerContentBusinessService editAnswerContentBusinessService;

    //createAnswer method takes questionId to retrieve question and accessToken for doing Bearer Authorization and creates the answer for the corresponding question
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") final String questionId,
                                                       @RequestHeader("accessToken") final String accessToken, final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {
        String [] bearerToken = accessToken.split("Bearer ");
        final UserAuthTokenEntity userAuthTokenEntity=createAnswerBusinessService.verifyAuthToken(bearerToken[1]);
        final AnswerEntity answerEntity=new AnswerEntity();
        answerEntity.setQuestionEntity(createAnswerBusinessService.verifyQuestionId(questionId));
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setUser(userAuthTokenEntity.getUser());
        answerEntity.setAns(answerRequest.getAnswer());
        final ZonedDateTime now = ZonedDateTime.now();
        answerEntity.setDate(now);
        final AnswerEntity createdAnswerEntity = createAnswerBusinessService.createAnswer(answerEntity);

        AnswerResponse answerResponse = new AnswerResponse()
                .id(createdAnswerEntity.getUuid())
                .status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    //getAllAnswersToQuestion method takes questionId to retrieve question and accessToken for doing Bearer Authorization and returns all the answers created for a specific question
    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId")final String questionId,
                                                                               @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        String[] bearerToken = accessToken.split("Bearer ");
        getAllAnswersToQuestionBusinessService.verifyAuthToken(bearerToken[1]);
        List<AnswerEntity> allAnswersByQuestion = new ArrayList<AnswerEntity>();
        allAnswersByQuestion.addAll(getAllAnswersToQuestionBusinessService.getAllAnswersByQuestion(getAllAnswersToQuestionBusinessService.verifyQuestionId(questionId)));
        List<AnswerDetailsResponse> answerDetailsResponseList = new ArrayList<AnswerDetailsResponse>();

        for (AnswerEntity answer : allAnswersByQuestion) {
            AnswerDetailsResponse answerDetailsResponse=new AnswerDetailsResponse();
            answerDetailsResponseList.add(answerDetailsResponse
                    .id(answer.getUuid())
                    .answerContent(answer.getAns())
                    .questionContent(answer.getQuestionEntity().getContent()));
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList,HttpStatus.OK);
    }

    //editAnswerContent method takes answerId to retrieve answer and accessToken for doing Bearer Authorization and returns the edit answer response
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest ,
                                                                @PathVariable("answerId") final String answerId,
                                                                @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        String[] bearerToken = accessToken.split("Bearer ");
        final AnswerEntity answerEntity = editAnswerContentBusinessService.verifyUserStatus(answerId,bearerToken[1]);
        answerEntity.setAns(answerEditRequest.getContent());
        final AnswerEntity updatedAnswerEntity = editAnswerContentBusinessService.updateAnswer(answerEntity);

        AnswerEditResponse answerEditResponse = new AnswerEditResponse()
                .id(updatedAnswerEntity.getUuid())
                .status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    //deleteAnswer method takes answerId to retrieve answer and accessToken for doing Bearer Authorization and allows only the answer owner or admin to delete the answer
    @RequestMapping(method= RequestMethod.DELETE,path="/answer/delete/{answerId}",produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerUuid,
                                                             @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        String [] bearerToken = accessToken.split("Bearer ");
        final AnswerEntity answerEntityToDelete=deleteAnswerBusinessService.verifyAnsUuid(answerUuid);
        final UserEntity signedinUserEntity = deleteAnswerBusinessService.verifyAuthToken(bearerToken[1]);
        final String Uuid = deleteAnswerBusinessService.deleteAnswer(answerEntityToDelete,signedinUserEntity);

        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse()
                .id(Uuid)
                .status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);
    }
}
