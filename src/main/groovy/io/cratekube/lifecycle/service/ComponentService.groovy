package io.cratekube.lifecycle.service

import io.cratekube.lifecycle.api.ComponentApi
import io.cratekube.lifecycle.api.GitHubApi
import io.cratekube.lifecycle.api.KubectlApi
import io.cratekube.lifecycle.api.exception.FailedException
import io.cratekube.lifecycle.model.Component
import io.cratekube.lifecycle.modules.annotation.ComponentCache

import javax.inject.Inject

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

class ComponentService implements ComponentApi {
  Map<String, Component> componentCache
  KubectlApi kubectlApi
  GitHubApi gitHubApi

  @Inject
  ComponentService(@ComponentCache Map<String, Component> componentCache, KubectlApi kubectlApi, GitHubApi gitHubApi) {
    this.componentCache = require componentCache, notNullValue()
    this.kubectlApi = require kubectlApi, notNullValue()
    this.gitHubApi = require gitHubApi, notNullValue()
  }

  @Override
  Component getComponent(String name) {
    require name, notEmptyString()

    return null
  }

  @Override
  void applyComponent(String name, String version) throws FailedException {
    require name, notEmptyString()
    require version, notEmptyString()
  }
}
