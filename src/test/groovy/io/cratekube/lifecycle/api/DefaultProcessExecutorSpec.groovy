package io.cratekube.lifecycle.api

import org.valid4j.errors.RequireViolation
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.expect

class DefaultProcessExecutorSpec extends Specification {
  @Subject ProcessExecutor subject
  File f = new File('/')

  def setup() {
    subject = new DefaultProcessExecutor() {
      String executablePath = '/bin/ls'
    }
  }

  @Unroll
  def 'Exec requires valid params: file=#file, opts=#opts'() {
    when:
    subject.exec(file, *opts)

    then:
    thrown RequireViolation

    where:
    file   | opts
    null   | null
    this.f | null
    this.f | ['', ' ', null]
  }

  def 'Exec successfully executes a command'() {
    when:
    def result = subject.exec(f, '--help')
    result.waitForProcessOutput()

    then:
    expect result.exitValue(), equalTo(0)
  }
}
