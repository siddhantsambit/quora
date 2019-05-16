package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionsDao
{
    @PersistenceContext
    private EntityManager entityManager;

    //Named queries created according to the functionality as suggested by the name of the respective methods
    public QuestionEntity createQuestions(QuestionEntity questionEntity)
    {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestion() {
        try
        {
            return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }


    public QuestionEntity getUserIdFromQuestionId(String questionId)
    {
        QuestionEntity questionEntity = entityManager.createNamedQuery("questionByQUuid" ,QuestionEntity.class).setParameter("uuid" ,questionId).getSingleResult();
        return questionEntity;
    }


    public QuestionEntity editQuestions(QuestionEntity questionEntity)
    {
        entityManager.merge(questionEntity);
        return questionEntity;
    }


    public QuestionEntity deleteQuestion(String questionId)
    {
        QuestionEntity questionEntity =
        entityManager.createNamedQuery("deleteQuestion",QuestionEntity.class).setParameter("uuid" ,questionId).getSingleResult();
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestionByUserId(String userId)
    {
        try
        {
            return entityManager.createNamedQuery("allQuestionsByUserId", QuestionEntity.class).getResultList();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }
}
