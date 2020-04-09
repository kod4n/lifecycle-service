package io.cratekube.lifecycle.resources

import io.cratekube.lifecycle.BaseIntegrationSpec
import io.cratekube.lifecycle.model.Component
import spock.lang.PendingFeature

import static org.hamcrest.Matchers.notNullValue
import static spock.util.matcher.HamcrestSupport.expect

class ComponentResourceIntegrationSpec extends BaseIntegrationSpec {
  String basePath = '/component'

  @PendingFeature
  def 'should get response when executing GET'() {
    when:
    def result = baseRequest('/test-component').get(Component)

    then:
    expect result, notNullValue()
  }
}
