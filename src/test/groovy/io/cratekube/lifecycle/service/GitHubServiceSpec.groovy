package io.cratekube.lifecycle.service

import io.cratekube.lifecycle.api.GitHubApi
import org.valid4j.errors.RequireViolation
import spock.lang.PendingFeature
import spock.lang.Specification
import spock.lang.Subject

import javax.ws.rs.client.Client

import static org.hamcrest.Matchers.notNullValue
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString
import static spock.util.matcher.HamcrestSupport.expect

class GitHubServiceSpec extends Specification {
  @Subject GitHubApi subject
  Client client

  def setup() {
    client = Mock()
    subject = new GitHubService(client)
  }

  def 'should require valid contructor params'() {
    when:
    new GitHubService(null)

    then:
    thrown RequireViolation
  }

  def 'getLatestVersionFromAtomFeed should require valid args'() {
    when:
    subject.getLatestVersionFromAtomFeed(feedUrl)

    then:
    thrown RequireViolation

    where:
    feedUrl << [null, '']
  }

  @PendingFeature
  def 'getLatestVersionFromAtomFeed should return the latest version'() {
    given:
    def feedUrl = 'https://github.com/cratekube/dropwizard-groovy-template/releases.atom'

    when:
    def result = subject.getLatestVersionFromAtomFeed(feedUrl)

    then:
    expect result, notNullValue()
  }

  def 'getDeployableComponent should require valid args'() {
    when:
    subject.getDeployableComponent(component, version)

    then:
    thrown RequireViolation

    where:
    component        | version
    null             | null
    ''               | null
    'test-component' | null
    'test-component' | ''
  }

  @PendingFeature
  def 'getDeployableComponent should return deployment configuration'() {
    given:
    def component = 'test-component'
    def version = 'test-version'

    when:
    def result = subject.getDeployableComponent(component, version)

    then:
    expect result, notEmptyString()
  }
}
