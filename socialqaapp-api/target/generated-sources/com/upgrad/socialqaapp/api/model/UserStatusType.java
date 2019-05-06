package com.upgrad.socialqaapp.api.model;

import java.util.Objects;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Status of the User
 */
public enum UserStatusType {
  
  REGISTERED("REGISTERED"),
  
  ACTIVE("ACTIVE"),
  
  INACTIVE("INACTIVE"),
  
  LOCKED("LOCKED");

  private String value;

  UserStatusType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static UserStatusType fromValue(String text) {
    for (UserStatusType b : UserStatusType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

