package io.cratekube.lifecycle.job

import io.cratekube.lifecycle.AppConfig
import io.cratekube.lifecycle.api.ComponentApi
import io.cratekube.lifecycle.api.exception.FailedException
import io.cratekube.lifecycle.model.Component
import org.quartz.JobExecutionContext
import org.valid4j.errors.RequireViolation
import spock.lang.Specification
import spock.lang.Subject

import static org.hamcrest.Matchers.hasEntry
import static spock.util.matcher.HamcrestSupport.expect

class UpgradeAvailabilityJobSpec extends Specification {
  @Subject UpgradeAvailabilityJob subject
  Map<String, Component> componentCache
  ComponentApi componentApi
  AppConfig config

  def setup() {
    componentCache = [:]
    componentApi = Mock()
    config = new AppConfig(managedComponents: [('test-name'): true, ('test-name-2'): true])
    subject = new UpgradeAvailabilityJob(componentCache, componentApi, config)
  }

  def 'should require valid constructor params'() {
    when:
    new UpgradeAvailabilityJob(cache, comp, conf)

    then:
    thrown RequireViolation

    where:
    cache               | comp              | conf
    null                | null              | null
    this.componentCache | null              | null
    this.componentCache | this.componentApi | null
  }

  def 'doJob should update component cache'() {
    given:
    def nm = 'test-name'
    def curVersion = '1.0.0'
    def cfg = 'test-config'
    def latVersion = '1.0.1'
    def component = new Component(nm, cfg, curVersion, latVersion)
    componentApi.getComponent(_) >> component
    when:
    subject.doJob(_ as  JobExecutionContext)

    then:
    expect componentCache, hasEntry(nm, component)
  }

  def 'doJob should continue updating component cache on failure'() {
    given:
    def nm = 'test-name'
    def curVersion = '1.0.0'
    def cfg = 'test-config'
    def latVersion = '1.0.1'
    def nm2 = 'test-name-2'
    def component2 = new Component(nm2, cfg, curVersion, latVersion)
    componentApi.getComponent(nm) >> {throw new FailedException()}
    componentApi.getComponent(nm2) >> component2

    when:
    subject.doJob(_ as  JobExecutionContext)

    then:
    expect componentCache, hasEntry(nm2, component2)
  }
}
