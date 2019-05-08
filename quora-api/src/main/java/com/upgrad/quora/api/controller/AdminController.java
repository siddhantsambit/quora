package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.business.UserDeleteBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RestController = @Controller + @ResponseBody
@RestController
@RequestMapping("/")
public class AdminController {
    //Required services are autowired to enable access to methods defined in respective Business services
    @Autowired
    UserDeleteBusinessService userDeleteBusinessService;

    @Autowired
    UserAdminBusinessService userAdminBusinessService;

    //userDelete method takes userId to retrieve  user and accessToken for doing Bearer Authorization and deletes the user whose userId is given if the logged in user is an admin
    @RequestMapping(method= RequestMethod.DELETE,path="/admin/user/{userId}",produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable("userId") final String userUuid,
                                                         @RequestHeader("accessToken") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        String [] bearerToken = accessToken.split("Bearer ");
        final UserEntity signedinUserEntity=userAdminBusinessService.getUser(userUuid, bearerToken[1]);
        final UserEntity userEntityToDelete=userDeleteBusinessService.getUserByUuid(userUuid);
        final String Uuid = userDeleteBusinessService.deleteUser(userEntityToDelete,signedinUserEntity);
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse()
                .id(Uuid)
                .status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }
}
