package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    //Named queries created according to the functionality as suggested by the name of the respective methods
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }
    public QuestionEntity getQuestionByQUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("questionByQUuid", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public List<QuestionEntity> getAllQuestionsByUserId(final UserEntity user){
        try {
            return entityManager.createNamedQuery("allQuestionsByUserId", QuestionEntity.class).setParameter("user", user).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public List<QuestionEntity> getAllQuestions(){
        try {
            return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public QuestionEntity updateQuestion(final QuestionEntity questionEntity) {
        return entityManager.merge(questionEntity);
    }
    public String deleteQuestion(final QuestionEntity questionEntity) {
        String uuid=questionEntity.getUuid();
        entityManager.remove(questionEntity);
        return uuid;
    }
}
