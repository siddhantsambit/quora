package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//Service class Sign Up logic
@Service
public class SignUpBusinessService {

    //Respective Data access object and passwordcryptogaphyprovider has been autowired to access the method defined in respective Dao
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    //Creates user based on signup request form and also checks for duplicate usernames and email ids while signup
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity, String userName, String email) throws SignUpRestrictedException {
        if (userDao.getUserByUserName(userName) != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        } else if (userDao.getUserByEmail(email) != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        } else {
            //Generates encrypted password and salt based on the password provided by user during signup
            String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
            userEntity.setSalt(encryptedText[0]);
            userEntity.setPassword(encryptedText[1]);
            return userDao.createUser(userEntity);
        }
    }
}
