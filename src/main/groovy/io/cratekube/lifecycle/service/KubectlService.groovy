package io.cratekube.lifecycle.service

import io.cratekube.lifecycle.AppConfig
import io.cratekube.lifecycle.api.KubectlApi
import io.cratekube.lifecycle.api.ProcessExecutor
import io.cratekube.lifecycle.api.exception.FailedException

import javax.inject.Inject

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

class KubectlService implements KubectlApi {
  ProcessExecutor kubectlCmd
  AppConfig config

  @Inject
  KubectlService(ProcessExecutor kubectlCmd, AppConfig config) {
    this.kubectlCmd = require kubectlCmd, notNullValue()
    this.config = require config, notNullValue()
  }

  @Override
  void apply(String yaml) throws FailedException {
    require yaml, notEmptyString()
  }

  @Override
  String get(String arguments) throws FailedException {
    require arguments, notEmptyString()

    return null
  }
}
