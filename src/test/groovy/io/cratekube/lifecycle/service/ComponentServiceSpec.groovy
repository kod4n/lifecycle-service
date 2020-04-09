package io.cratekube.lifecycle.service

import io.cratekube.lifecycle.api.ComponentApi
import io.cratekube.lifecycle.api.GitHubApi
import io.cratekube.lifecycle.api.KubectlApi
import io.cratekube.lifecycle.model.Component
import org.valid4j.errors.RequireViolation
import spock.lang.PendingFeature
import spock.lang.Specification
import spock.lang.Subject

import static org.hamcrest.Matchers.notNullValue
import static spock.util.matcher.HamcrestSupport.expect

class ComponentServiceSpec extends Specification {
  @Subject ComponentApi subject
  Map<String, Component> componentCache
  KubectlApi kubectlApi
  GitHubApi gitHubApi

  def setup() {
    componentCache = [:]
    kubectlApi = Mock()
    gitHubApi = Mock()
    subject = new ComponentService(componentCache, kubectlApi, gitHubApi)
  }

  def 'should require valid constructor params'() {
    when:
    new ComponentService(cache, kube, git)

    then:
    thrown RequireViolation

    where:
    cache               | kube            | git
    null                | null            | null
    this.componentCache | null            | null
    this.componentCache | this.kubectlApi | null
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
  def 'getComponent should return component'() {
    given:
    def name = 'test-name'

    when:
    def result = subject.getComponent(name)

    then:
    expect result, notNullValue()
  }

  def 'applyComponent should require valid args'() {
    when:
    subject.applyComponent(name, version)

    then:
    thrown RequireViolation

    where:
    name           | version
    null           | null
    ''             | null
    'test-version' | null
    'test-version' | ''
  }
}
