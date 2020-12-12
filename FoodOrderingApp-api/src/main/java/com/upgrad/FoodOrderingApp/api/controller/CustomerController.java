package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.upgrad.FoodOrderingApp.service.common.GenericErrorCode.SGR_005;

@CrossOrigin //this annotation helps to share resources across origin
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired private CustomerService customerService;

    /**
     * This method takes Customer Signup request, stores customer information in the system
     *
     * @param request Customer signup request having details like name, email, contact etc.
     * @return ResponseEntity with Customer Id
     * @throws SignUpRestrictedException on invalid signup request or customer already registered
     * @throws UnexpectedException on any other errors
     */

    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SignupCustomerResponse> registerCustomer(
            @RequestBody(required = false) final SignupCustomerRequest request)
            throws SignUpRestrictedException, UnexpectedException {
        // Validate if all necessary information is available in the input request
        validateSignupRequest(request);

        // Map request object to Customer Entity object
        final CustomerEntity newCustomerEntity = new CustomerEntity();
        newCustomerEntity.setUuid(UUID.randomUUID().toString());
        newCustomerEntity.setFirstName(request.getFirstName());
        newCustomerEntity.setLastName(request.getLastName());
        newCustomerEntity.setEmail(request.getEmailAddress());
        newCustomerEntity.setPassword(request.getPassword());
        newCustomerEntity.setContactNumber(request.getContactNumber());
        newCustomerEntity.setSalt(UUID.randomUUID().toString());

        // Store Customer Entity in the database
        final CustomerEntity customerEntity = customerService.saveCustomer(newCustomerEntity);

        // Map persisted Customer Entity to Response Object
        final SignupCustomerResponse response = new SignupCustomerResponse();
        response.id(customerEntity.getUuid()).status("CUSTOMER CREATED SUCCESSFULLY");
        return new ResponseEntity<SignupCustomerResponse>(response, HttpStatus.CREATED);
    }

    private void validateSignupRequest(SignupCustomerRequest request)
            throws SignUpRestrictedException {
        // Throw error if First Name/Password/Email Address/Contact Number are missing or empty
        if ((request.getContactNumber() == null)
                || (request.getFirstName() == null)
                || (request.getPassword() == null)
                || (request.getEmailAddress() == null)
                || (request.getContactNumber().isEmpty())
                || (request.getFirstName().isEmpty())
                || (request.getEmailAddress().isEmpty())
                || (request.getPassword().isEmpty())) {
            throw new SignUpRestrictedException(SGR_005.getCode(), SGR_005.getDefaultMessage());
        }
    }
}
