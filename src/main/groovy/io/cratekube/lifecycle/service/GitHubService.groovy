package io.cratekube.lifecycle.service

import io.cratekube.lifecycle.api.GitHubApi
import io.cratekube.lifecycle.api.exception.NotFoundException

import javax.inject.Inject
import javax.ws.rs.client.Client

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.Assertive.require
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString

class GitHubService implements GitHubApi {
  Client client

  @Inject
  GitHubService(Client client) {
    this.client = require client, notNullValue()
  }

  @Override
  String getLatestVersionFromAtomFeed(String atomFeedUrl) {
    require atomFeedUrl, notEmptyString()
    /**
     * def location = 'https://github.com/cratekube/cluster-mgmt-service/releases.atom'
     * def result = new XmlSlurper().parse(location)
     * result.entry.each { print it.id.toString()[it.id.toString().lastIndexOf('/')+1..-1] }
     */
    return null
  }

  @Override
  String getDeployableComponent(String component, String version) throws NotFoundException {
    require component, notEmptyString()
    require version, notEmptyString()

    return null
  }
}
