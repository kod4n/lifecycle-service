package io.cratekube.lifecycle.job

import io.cratekube.lifecycle.AppConfig
import io.cratekube.lifecycle.api.GitHubApi
import io.cratekube.lifecycle.api.KubectlApi
import io.cratekube.lifecycle.model.Component
import org.valid4j.errors.RequireViolation
import spock.lang.PendingFeature
import spock.lang.Specification
import spock.lang.Subject

import static org.hamcrest.Matchers.anything
import static org.hamcrest.Matchers.hasEntry
import static spock.util.matcher.HamcrestSupport.expect

class UpgradeAvailabilityJobSpec extends Specification {
  @Subject UpgradeAvailabilityJob subject
  Map<String, Component> componentCache
  KubectlApi kubectlApi
  AppConfig config
  GitHubApi gitHubApi
  def setup() {
    componentCache = [:]
    kubectlApi = Mock()
    config = new AppConfig()
    gitHubApi = Mock()
    subject = new UpgradeAvailabilityJob(componentCache, kubectlApi, config, gitHubApi)
  }

  def 'should require valid constructor params'() {
    when:
    new UpgradeAvailabilityJob(cache, kube, conf, git)

    then:
    thrown RequireViolation

    where:
    cache               | kube            | conf        | git
    null                | null            | null        | null
    this.componentCache | null            | null        | null
    this.componentCache | this.kubectlApi | null        | null
    this.componentCache | this.kubectlApi | this.config | null
  }

  @PendingFeature
  def 'doJob should update component cache'() {
    when:
    subject.doJob(_)

    then:
    expect componentCache, hasEntry(anything(), anything())
  }
}
