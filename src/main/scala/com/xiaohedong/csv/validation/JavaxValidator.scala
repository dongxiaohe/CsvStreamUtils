package com.xiaohedong.csv.validation

import javax.validation.Validation

import com.xiaohedong.csv.base.StreamValidator

object JavaxValidator extends StreamValidator(Validation.buildDefaultValidatorFactory.getValidator)
