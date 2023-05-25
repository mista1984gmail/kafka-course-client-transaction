package com.example.producer.web.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContactNumberValidator implements
    ConstraintValidator<PhoneNumber, String> {
  @Override
  public void initialize(PhoneNumber contactNumber) {
  }

  String patterns ="^\\+(\\d{12})";

  @Override
  public boolean isValid(String contactField,
                         ConstraintValidatorContext cxt) {
    if (contactField == null) {
      return true;
    }
    return contactField.matches(patterns);
  }
}
