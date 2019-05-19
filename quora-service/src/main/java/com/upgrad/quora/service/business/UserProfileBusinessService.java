package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileBusinessService {

    //Respective Data access object has been autowired to access the method defined in respective Dao
    @Autowired
    private UserDao userDao;

    //Checks user sign in status based on accessToken and also validates the userId of the user whose details we wish to see
    public UserEntity getUser(final String userUuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        UserEntity userEntity = userDao.getUserByUuid(userUuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        } else {
            return userDao.getUserByUuid(userUuid);
        }
    }
}
