package io.cratekube.lifecycle.service

import io.cratekube.lifecycle.AppConfig
import io.cratekube.lifecycle.api.KubectlApi
import io.cratekube.lifecycle.api.ProcessExecutor
import org.valid4j.errors.RequireViolation
import spock.lang.PendingFeature
import spock.lang.Specification
import spock.lang.Subject

import static org.valid4j.matchers.ArgumentMatchers.notEmptyString
import static spock.util.matcher.HamcrestSupport.expect

class KubectlServiceSpec extends Specification {
  @Subject KubectlApi subject
  ProcessExecutor kubectlCmd
  AppConfig config

  def setup() {
    kubectlCmd = Mock()
    config = new AppConfig()
    subject = new KubectlService(kubectlCmd, config)
  }

  def 'should require valid contructor params'() {
    when:
    new KubectlService(cmd, conf)

    then:
    thrown RequireViolation

    where:
    cmd             | conf
    null            | null
    this.kubectlCmd | null
  }
  def 'apply should rquire valid args'() {
    when:
    subject.apply(yaml)

    then:
    thrown RequireViolation

    where:
    yaml << [null, '']
  }

  def 'get should require valid args'() {
    when:
    subject.get(args)

    then:
    thrown RequireViolation

    where:
    args << [null, '']
  }

  @PendingFeature
  def 'get should retrieve the kubernetes resource'() {
    when:
    def result = subject.get('deployments deployment-name')

    then:
    expect result, notEmptyString()
  }
}
