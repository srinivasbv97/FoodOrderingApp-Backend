package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method takes a CustomerEntity and stores it in the database
     *
     * @param customerEntity CustomerEntity to persist
     * @return persisted CustomerEntity
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CustomerEntity saveCustomer(final CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        System.out.println(customerEntity.toString());
        return customerEntity;
    }

}
