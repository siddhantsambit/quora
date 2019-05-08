package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminBusinessService {

    //Respective Data access object has been autowired to access the method defined in respective Dao
    @Autowired
    private UserDao userDao;

    //Checks user sign in status based on accessToken and also validates the userId of the user to delete
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(final String userUuid, final String accessToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out");
        } else if (userDao.getUserByUuid(userUuid)== null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        } else {
            return userDao.getUserByUuid(userAuthTokenEntity.getUuid());
        }
    }
}




