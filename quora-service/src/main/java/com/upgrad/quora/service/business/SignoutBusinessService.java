package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SignoutBusinessService {

        //Respective Data access object has been autowired to access the method defined in respective Dao
        @Autowired
        private UserDao userDao;

        //Checks whether a user has signed out based on accessToken
        @Transactional(propagation = Propagation.REQUIRED)
        public UserAuthTokenEntity verifyAuthToken(final String accessToken) throws SignOutRestrictedException {
            UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
            if (userAuthTokenEntity == null) {
                throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
            } else {
                final ZonedDateTime now = ZonedDateTime.now();
                userAuthTokenEntity.setLogoutAt(now);
                return userAuthTokenEntity;
            }
        }
}
