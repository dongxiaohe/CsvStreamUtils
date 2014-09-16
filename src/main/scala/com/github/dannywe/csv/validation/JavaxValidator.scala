package com.github.dannywe.csv.validation

import javax.validation.Validation

import com.github.dannywe.csv.base.StreamValidator

object JavaxValidator extends StreamValidator(Validation.buildDefaultValidatorFactory.getValidator)
