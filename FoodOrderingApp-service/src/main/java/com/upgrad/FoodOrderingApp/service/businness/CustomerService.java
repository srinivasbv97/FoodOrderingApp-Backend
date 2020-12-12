package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.AppConstants;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.upgrad.FoodOrderingApp.service.common.GenericErrorCode.*;
import static com.upgrad.FoodOrderingApp.service.common.GenericErrorCode.GEN_001;

@Service
public class CustomerService {
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * Method takes CustomerEntity and stores it on the database
     *
     * @param customerEntity New CustomerEntity
     * @return Saved Customer Entity
     * @throws SignUpRestrictedException on invalid email/contact/password on the input customer
     *     entity
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(final CustomerEntity customerEntity)
            throws SignUpRestrictedException {
        // Check if Email is Valid (right format)
        if (!isValidEmail(customerEntity.getEmail())) {
            throw new SignUpRestrictedException(SGR_002.getCode(), SGR_002.getDefaultMessage());
        }

        // Check if Contact Is Valid
        if (!isValidContactNumber(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException(SGR_003.getCode(), SGR_003.getDefaultMessage());
        }

        // Check is password is valid and meets minimum strength requirements
        if (!isStrongPassword(customerEntity.getPassword())) {
            throw new SignUpRestrictedException(SGR_004.getCode(), SGR_004.getDefaultMessage());
        }

        // Encrupt customer password
        final String[] encryptedText =
                passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
        try {
            // Store customer on the database
            return customerDao.saveCustomer(customerEntity);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            if (dataIntegrityViolationException.getCause() instanceof ConstraintViolationException) {
                String constraintName =
                        ((ConstraintViolationException) dataIntegrityViolationException.getCause())
                                .getConstraintName();

                // A customer with the same contact details already exists (Duplicate Customer)
                if (StringUtils.containsIgnoreCase(constraintName, "customer_contact_number_key")) {
                    throw new SignUpRestrictedException(SGR_001.getCode(), SGR_001.getDefaultMessage());
                } else {
                    throw new UnexpectedException(GEN_001, dataIntegrityViolationException);
                }
            } else {
                throw new UnexpectedException(GEN_001, dataIntegrityViolationException);
            }
        } catch (Exception exception) {
            throw new UnexpectedException(GEN_001, exception);
        }
    }

    // This method users regular expressions to guage the strength of a user's
    // password returns password score

    /**
     * Check if password strength meets minimum specifications
     *
     * @param password Customer's password
     * @return true if password meets minimum requirements else false
     */
    private boolean isStrongPassword(final String password) {
        return password.matches(AppConstants.REG_EXP_PASSWD_UPPER_CASE_CHAR)
                && password.matches(AppConstants.REG_EXP_PASSWD_SPECIAL_CHAR)
                && password.matches(AppConstants.REG_EXP_PASSWD_DIGIT)
                && (password.length() > AppConstants.SEVEN_7);
    }

    /**
     * Check's if customer's contact number is valid
     *
     * @param contactNumber Customer's contact number
     * @return true if contact number is numeric and or length 10
     */
    private boolean isValidContactNumber(final String contactNumber) {
        return StringUtils.isNumeric(contactNumber)
                && (contactNumber.length() == AppConstants.NUMBER_10);
    }

    /**
     * Check's if customer's email is valid
     *
     * @param email Customer's email
     * @return true is email format is correct else false
     */
    private boolean isValidEmail(final String email) {
        return email.matches(AppConstants.REG_EXP_VALID_EMAIL);
    }
}

