package validation

import javax.validation.Validation

import base.StreamValidator

object JavaxValidator extends StreamValidator(Validation.buildDefaultValidatorFactory.getValidator)
