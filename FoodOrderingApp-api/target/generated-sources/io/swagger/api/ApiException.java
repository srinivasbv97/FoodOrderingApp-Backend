package io.swagger.api;

<<<<<<< HEAD
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-12-12T20:38:16.621+05:30")
=======
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-12-16T17:05:51.477+05:30")
>>>>>>> REST-API-3

public class ApiException extends Exception{
    private int code;
    public ApiException (int code, String msg) {
        super(msg);
        this.code = code;
    }
}
