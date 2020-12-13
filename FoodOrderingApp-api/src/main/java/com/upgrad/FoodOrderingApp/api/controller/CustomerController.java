package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.common.AppConstants;
import com.upgrad.FoodOrderingApp.service.common.AppUtils;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.StringTokenizer;
import java.util.UUID;

import static com.upgrad.FoodOrderingApp.service.common.GenericErrorCode.ATH_003;
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

    /**
     * This method takes customers username (contact number) and logs the user into the system
     *
     * @param headerParam Basic authorization token with username & password as a request header param
     * @return ResponseEntity with Customer Id, Name, Contact, Email & Access Token
     * @throws AuthenticationFailedException on incorrect/invalid credentials
     * @throws UnexpectedException on any other errors
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> loginCustomer(
            @RequestHeader("authorization") final String headerParam)
            throws AuthenticationFailedException, UnexpectedException {

        // Get Basic Authentication Token
        final String authToken = AppUtils.getBasicAuthToken(headerParam);

        // Validate Basic Authentication Token
        validateLoginRequest(authToken);

        StringTokenizer tokens = new StringTokenizer(authToken, AppConstants.COLON);

        // Login Customer and fetch authorization details
        final CustomerAuthEntity customerAuthEntity =
                customerService.authenticate(tokens.nextToken(), tokens.nextToken());

        // Map customer & access token to Login response object
        final LoginResponse response = new LoginResponse();
        response
                .id(customerAuthEntity.getCustomer().getUuid())
                .firstName(customerAuthEntity.getCustomer().getFirstName())
                .lastName(customerAuthEntity.getCustomer().getLastName())
                .contactNumber(customerAuthEntity.getCustomer().getContactNumber())
                .emailAddress(customerAuthEntity.getCustomer().getEmail())
                .message("LOGGED IN SUCCESSFULLY");

        // Set Http Headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(AppConstants.HTTP_ACCESS_TOKEN_HEADER, customerAuthEntity.getAccessToken());
        headers.setAccessControlExposeHeaders(
                Collections.singletonList(AppConstants.HTTP_ACCESS_TOKEN_HEADER));
        return new ResponseEntity<LoginResponse>(response, headers, HttpStatus.OK);
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

    /**
     * This method validates if Customer sign up request has all necessary information
     *
     * @param authorizationToken Customer Signin Request
     * @throws AuthenticationFailedException on incorrect/invalid basic authentication token
     */
    private void validateLoginRequest(String authorizationToken)
            throws AuthenticationFailedException {
        // Throw error if format of Basic Authentication Token is not right
        if (!authorizationToken.matches(AppConstants.REG_EXP_BASIC_AUTH)) {
            throw new AuthenticationFailedException(ATH_003.getCode(), ATH_003.getDefaultMessage());
        }
    }
}
