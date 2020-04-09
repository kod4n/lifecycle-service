package io.cratekube.lifecycle.resources

import io.cratekube.lifecycle.api.ComponentApi
import io.cratekube.lifecycle.auth.User
import io.cratekube.lifecycle.model.Component
import org.valid4j.errors.RequireViolation
import spock.lang.PendingFeature
import spock.lang.Specification
import spock.lang.Subject
import io.cratekube.lifecycle.resources.ComponentResource.ComponentVersionRequest

import static org.hamcrest.Matchers.endsWith
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasEntry
import static org.hamcrest.Matchers.notNullValue
import static spock.util.matcher.HamcrestSupport.expect

class ComponentResourceSpec extends Specification {
  @Subject ComponentResource subject
  Map<String, Component> componentCache
  ComponentApi componentApi

  def setup() {
    componentCache = [:]
    componentApi = Mock()
    subject = new ComponentResource(componentCache, componentApi)
  }

  def 'should require valid constructor params'() {
    when:
    new ComponentResource(cache, api)

    then:
    thrown RequireViolation

    where:
    cache               | api
    null                | null
    this.componentCache | null
  }

  def 'getComponent should require valid args'() {
    when:
    subject.getComponent(name)

    then:
    thrown RequireViolation

    where:
    name << [null, '']
  }

  @PendingFeature
  def 'getComponent should return valid component'() {
    given:
    def name = 'test-name'

    when:
    def result = subject.getComponent(name)

    then:
    expect result, notNullValue()
    expect result.present, equalTo(true)
  }

  @PendingFeature
  def 'getComponentUpgradeAvailability should return component cache'() {
    given:
    def name = 'test-name'
    def config = 'test-config'
    def currentVersion = 'test-current-version'
    def latestVersion = 'test-latest-version'
    def value = new Component(name, config, currentVersion, latestVersion)
    componentCache = [(name): value]

    when:
    def result = subject.componentUpgradeAvailability

    then:
    expect result, notNullValue()
    expect result, hasEntry(name, value)
  }

  def 'applyComponentVersion should require valid args'() {
    when:
    subject.applyComponentVersion(name, request, user)

    then:
    thrown RequireViolation

    where:
    name        | request                                     | user
    null        | null                                        | null
    ''          | null                                        | null
    'test-name' | null                                        | null
    'test-name' | new ComponentVersionRequest()               | null
    'test-name' | new ComponentVersionRequest('test-version') | null
  }

  @PendingFeature
  def 'applyComponentVersion should return  valid response'() {
    given:
    def name = 'test-name'
    def version = 'test-version'
    def componentVersionRequest = new ComponentVersionRequest(version)
    def user = new User()

    when:
    def response = subject.applyComponentVersion(name, componentVersionRequest, user)

    then:
    expect response, notNullValue()
    expect response.status, equalTo(202)
    expect response.location, notNullValue()
    expect response.location.path, endsWith(name)
  }
}
