package io.cratekube.lifecycle.api.exception

import groovy.transform.InheritConstructors

/**
 * Base exception for any errors in API.
 */
@InheritConstructors
class ApiException extends RuntimeException {
  int errorCode = 500
}

@InheritConstructors
class NotFoundException extends ApiException {
  int errorCode = 404
}

@InheritConstructors
class FailedException extends ApiException { }

