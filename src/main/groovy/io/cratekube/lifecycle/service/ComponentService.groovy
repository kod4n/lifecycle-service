package io.cratekube.lifecycle.service

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import io.cratekube.lifecycle.api.ComponentApi
import io.cratekube.lifecycle.api.GitHubApi
import io.cratekube.lifecycle.api.KubectlApi
import io.cratekube.lifecycle.api.exception.FailedException
import io.cratekube.lifecycle.api.exception.NotFoundException
import io.cratekube.lifecycle.model.Component

import javax.inject.Inject

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

@Slf4j
class ComponentService implements ComponentApi {
  KubectlApi kubectlApi
  GitHubApi gitHubApi
  ObjectMapper objectMapper

  @Inject
  ComponentService(KubectlApi kubectlApi, GitHubApi gitHubApi, ObjectMapper objectMapper) {
    this.kubectlApi = require kubectlApi, notNullValue()
    this.gitHubApi = require gitHubApi, notNullValue()
    this.objectMapper = require objectMapper, notNullValue()
  }

  Component getComponent(String name) {
    require name, notEmptyString()

    def currentVersion = null
    def config = null
    def latestVersion = null
    def stringResource = kubectlApi.getPodJsonByNameSelector(name)
    def jsonResource = objectMapper.readValue(stringResource, Map)
    if (jsonResource.items) {
      // only use a running pod for current version.
      // see https://stackoverflow.com/questions/60045964/kubernetes-how-to-find-pods-that-are-running-and-ready
      for (item in jsonResource.items) {
        def ready = item.status.conditions.status.every {it == 'True'}
        if (ready) {
          //container name needs to match pod name selector
          def image = item.spec.containers.find {it.name == name}.image
          currentVersion = image.split(':')[1] as String
        }
      }
      config = stringResource
    }
    try {
      latestVersion = gitHubApi.getLatestVersion(name)
      return new Component(name, config, currentVersion, latestVersion)
    } catch (FailedException | IOException ex) {
      log.debug(ex.toString())
    }
    if (currentVersion) {
      return new Component(name, config, currentVersion, latestVersion)
    }
    return null
  }

  @Override
  void applyComponent(String name, String version) throws FailedException, NotFoundException {
    require name, notEmptyString()
    require version, notEmptyString()

    def deployableComponent = gitHubApi.getDeployableComponent(name, version)
    kubectlApi.apply(deployableComponent)
  }
}
