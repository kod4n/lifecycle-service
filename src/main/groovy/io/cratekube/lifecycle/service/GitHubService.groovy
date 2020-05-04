package io.cratekube.lifecycle.service

import groovy.util.logging.Slf4j
import io.cratekube.lifecycle.GitHubConfig
import io.cratekube.lifecycle.api.GitHubApi
import io.cratekube.lifecycle.api.exception.FailedException
import io.cratekube.lifecycle.api.exception.NotFoundException
import ru.vyarus.dropwizard.guice.module.yaml.bind.Config

import javax.inject.Inject
import javax.ws.rs.ProcessingException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.client.Client

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

@Slf4j
class GitHubService implements GitHubApi {
  Client client
  GitHubConfig gitHubConfig

  @Inject
  GitHubService(Client client, @Config GitHubConfig gitHubConfig) {
    this.client = require client, notNullValue()
    this.gitHubConfig = require gitHubConfig, notNullValue()
  }

  @Override
  String getLatestVersion(String component) {
    require component, notEmptyString()

    String tags = "${gitHubConfig.orgApiRepoHome}/${component}/tags"
    def result = []
    try {
      result = client.target(tags).request().get(List)
    } catch (ProcessingException | WebApplicationException ex) {
      log.debug(ex.toString())
      throw new FailedException("Cannot retrieve the latest version for [${component}].")
    }
    if (!result) {
      throw new FailedException("Cannot retrieve the latest version. There are no releases for [${component}].")
    }
    return result.first().name
  }

  @Override
  String getDeployableComponent(String component, String version) throws NotFoundException {
    require component, notEmptyString()
    require version, notEmptyString()

    String deploymentFileLocation = "${gitHubConfig.orgBaseRawHome}/${component}/${version}/deployment.yml"
    try {
      return client.target(deploymentFileLocation).request().get(String)
    } catch (ProcessingException | WebApplicationException ex) {
      log.debug(ex.toString())
      throw new NotFoundException("Cannot find deployable template for component [${component}] version [${version}].")
    }
  }
}
